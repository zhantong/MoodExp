package org.graduation.moodexp.service;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.graduation.moodexp.collector.AudioCollector;
import org.graduation.moodexp.collector.GpsCollector;
import org.graduation.moodexp.collector.GyroscopeCollector;
import org.graduation.moodexp.collector.ICollector;
import org.graduation.moodexp.collector.LightCollector;
import org.graduation.moodexp.collector.MagneticCollector;
import org.graduation.moodexp.collector.MyDateManager;
import org.graduation.moodexp.collector.ScreenCollector;
import org.graduation.moodexp.collector.StepCollector;
import org.graduation.moodexp.collector.UsageCollector;
import org.graduation.moodexp.collector.WifiCollector;
import org.graduation.moodexp.database.DatabaseManager;
import org.graduation.moodexp.database.SharedPreferenceManager;
import org.graduation.moodexp.healthylife.FtpUploader;
import org.graduation.moodexp.healthylife.HttpRequest;
import org.graduation.moodexp.healthylife.MainActivity;
import org.graduation.moodexp.healthylife.MainApplication;
import org.graduation.moodexp.healthylife.MyUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectingService extends Service {
    private static final String TAG = "CollectingService";

    private List<ICollector> _collectorList;
    public static Handler recvHandler=new Handler();
    public static int msgUpdateApp=2;
    public static int msgDownloadFileFinish=3;

    String updateUrl="https://github.com/Songcheng-Gao/MoodExp/releases/download/r3.0/moodexp.apk";

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d(TAG, "Service starting.");

        DatabaseManager manager = DatabaseManager.getDatabaseManager();
        manager.saveHeartBeat("Service starting time:"+MyDateManager.getMyDateString());

        _collectorList = new ArrayList<>();

        subInfo();
        //getLatestAppVersion();

        if(MainActivity.recvHandler!=null)
        {
            Log.e("update","main handler is not null");
            //MainActivity.recvHandler.sendEmptyMessage(MainActivity.msgGetLatestVersion);
        }
        else
        {
            Log.e("update","main handler is null");
        }



        recvHandler =new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {

                if(msg.what==msgUpdateApp)
                {
                    downFile(updateUrl);
                }
                else if(msg.what==msgDownloadFileFinish)
                {
                    pBar.cancel();
                    update();
                }
            }//public void handleMessage(Message msg)

        };

//        if(OptionFragment.recvHandler==null)
//        {
//            Log.e("sub","handler is null");
//        }
//        else
//        {
//            OptionFragment.recvHandler.sendEmptyMessage(OptionFragment.msgSubInfo);
//        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED)
        {
            _collectorList.add(new AudioCollector(5));Log.e("collect","audio");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            _collectorList.add(GpsCollector.getCollector());
            Log.e("collect","gps");
            _collectorList.add(new WifiCollector());
            Log.e("collect","wifi");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)
                == PackageManager.PERMISSION_GRANTED)
        {
            //_collectorList.add(new WifiCollector());
            //Log.e("collect","wifi");
        }



        _collectorList.add(new ScreenCollector());//Log.e("collect","screen");
        _collectorList.add(LightCollector.getCollector());//Log.e("collect","light");
        _collectorList.add(StepCollector.getCollector());//Log.e("collect","step");

        _collectorList.add(MagneticCollector.getCollector());//Log.e("collect","magnetic");
        _collectorList.add(GyroscopeCollector.getCollector());//Log.e("collect","gyr");
        _collectorList.add(new UsageCollector());

        /*
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1)
        {

        }
        else
        {
            final String LAST_RECORD_TIME = "LastRecordAt";
            DatabaseManager manager = DatabaseManager.getDatabaseManager();
            long last = SharedPreferenceManager.getManager().getLong(LAST_RECORD_TIME, 0);
            long now = System.currentTimeMillis();
            SharedPreferenceManager.getManager().put(LAST_RECORD_TIME, now);

            Map<String, UsageStats> usage = new UsageInfo(MainApplication.getContext()).getAppUsageInfo(last, now);

            if (usage != null)
            {
                Log.e("collect","usage info get");
                for (Map.Entry<String, UsageStats> entry : usage.entrySet())
                {
                    manager.saveAppUsage(entry.getKey(),entry.getValue().getTotalTimeInForeground(), 0);
                }//time is the current time
            }
            else
            {
                Log.e("collect","usage info not get");
            }
        }
        */

        Log.d(TAG, "Service started.");
        collect();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(5000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    for (ICollector g : _collectorList)
                    {
                        g.stopCollect();
                    }
                    stopSelf();
                }
            }
        }).start();

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        DatabaseManager manager=DatabaseManager.getDatabaseManager();
        //manager.saveHeartBeat("Service stopped time:"+MyDateManager.getMyDateString());
        Log.d(TAG, "Service stopped.");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    private void collect()
    {
        for (ICollector g : _collectorList)
        {
            g.startCollect();
        }

        SharedPreferenceManager sm=SharedPreferenceManager.getManager();
        boolean morningSub=sm.getBoolean("morningSubmit",false);
        boolean afternoonSub=sm.getBoolean("afternoonSubmit",false);
        boolean eveningSub=sm.getBoolean("eveningSubmit",false);

        if(morningSub==true && afternoonSub==true && eveningSub==true)
        {
            sm.put("morningSubmit",false);
            sm.put("afternoonSubmit",false);
            sm.put("eveningSubmit",false);
        }

    }


    boolean bIsGetStudentGroup=false;
    boolean bIsFinish=false;
    boolean result=false;

    public void subInfo()
    {
        bIsGetStudentGroup=false;
        bIsFinish=false;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //Looper.prepare();
                String className=studentClass("test0");
                if(className!=null)
                {
                    Log.e("group","id: "+"test0"+", class: "+className);
                    //Toast.makeText(MainApplication.getContext(),"学生组别"+className,Toast.LENGTH_SHORT).show();
                    bIsGetStudentGroup=true;
                    bIsFinish=true;

                }
                else
                {
                    Log.e("group","id: "+"test0"+", class not found, you may try again");
                    bIsGetStudentGroup=false;
                    bIsFinish=true;

                    return;
                }
                //Looper.loop();
            }
        }).start();


        while(bIsFinish==false);

        if(bIsGetStudentGroup==false)
        {
            //Toast.makeText(MainApplication.getContext(),"网络没有连接",Toast.LENGTH_SHORT).show();

            DatabaseManager manager = DatabaseManager.getDatabaseManager();
            manager.saveHeartBeat("网络没有连接");

            return;
        }

        long time = System.currentTimeMillis();
        SharedPreferenceManager sm = SharedPreferenceManager.getManager();
        long lastTime = sm.getLong("lasttime", 0);
        /*
        if (time - lastTime <=  60 * 60 * 10000)
        {//1 * 60 * 60 * 1000
            //Toast.makeText(getActivity(), "version "+MyUtil.version, Toast.LENGTH_SHORT).show();
            return;
        }
        */
        DatabaseManager manager = DatabaseManager.getDatabaseManager();
        final String id=manager.getStudentId();
        Log.e("sub","heart beat id:"+id);

        sm.put("lasttime", time);
        //SharedPreferenceManager sm = SharedPreferenceManager.getManager();
        int emotionCnt = sm.getInt("emotionCnt", 0);
        emotionCnt++;
        sm.put("emotionCnt", emotionCnt);

        new Thread()
        {
            DatabaseManager manager = DatabaseManager.getDatabaseManager();
            public void run()
            {
                manager.saveHeartBeat("send heartbeat time:"+MyDateManager.getMyDateString());
                boolean bHeart=heartBeat(id);
                if(bHeart==false)
                {
                    manager.saveHeartBeat("send heartbeat time unsuccessfully:"+MyDateManager.getMyDateString());
                    return;
                }
                else
                {//
                    manager.saveHeartBeat("send heartbeat time successfully:"+MyDateManager.getMyDateString());
                }
            }
        }.start();


        boolean bIsWifiConnected=isNetworkAvailable(MainApplication.getContext());

        if(bIsWifiConnected==false)
        {


            Log.e("sub","wifi not connect submit stop");
            return;
        }


        stepList=new ArrayList<Integer>();
        volumeList=new ArrayList<Float>();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {

                saveDailyTime();
                readDB();
                saveDailyStep();
                saveDailyVolume();

                DatabaseManager manager = DatabaseManager.getDatabaseManager();

                try
                {
                    //new ContactCollector().collect();

                    manager.db.execSQL("BEGIN EXCLUSIVE TRANSACTION");

                    result=new FtpUploader().upload();
                    if(result==false)
                    {
                        Log.e("sub","service submit fail");
                    }

                    else if(result==true)
                    {
                        //DatabaseManager.getDatabaseManager().refresh();
                        Log.e("sub","service submit success");
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally {

                    manager.db.execSQL("ROLLBACK TRANSACTION");

                    if(result==true)
                    {
                        DatabaseManager.getDatabaseManager().refresh();
                        Log.e("sub","submit success");
                    }
                }


            }
        }).start();

    }

    public String studentClass(String id){
        JsonObject info=studentInfo(id);
        if(info!=null&&info.get("status").getAsBoolean()){
            return info.get("class").getAsString();
        }
        return null;
    }
    private static final String HOST = "114.212.80.16";
    private static final int PORT = 9000;

    private JsonObject studentInfo(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);

        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.getReturnJson(HOST, PORT, "info", params);
            JsonObject result = element.getAsJsonObject();
            return result;
        } catch (IOException e) {
            e.printStackTrace();

            DatabaseManager manager = DatabaseManager.getDatabaseManager();
            manager.saveHeartBeat(e.toString());

            return null;
        }
    }

    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
        } else {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null&&networkInfo.length>0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean heartBeat(String id)
    {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);

        HttpRequest request = new HttpRequest();
        try {

            JsonElement element = request.getReturnJson(HOST, PORT, "heartbeat", params);
            JsonObject result = element.getAsJsonObject();
            return result.get("status").getAsBoolean();
        }
        catch (IOException e) {
            e.printStackTrace();


            return false;
        }
    }

    public void saveDailyTime()
    {

        DatabaseManager manager = DatabaseManager.getDatabaseManager();
        manager.saveDailyTime();
    }

    Cursor cursor;
    public ArrayList< Integer> stepList;
    public ArrayList< Float> volumeList;
    public void saveDailyStep()
    {
        if(stepList.size()==0) return ;

        DatabaseManager manager = DatabaseManager.getDatabaseManager();
        manager.saveDailyStep(stepList.get(stepList.size()-1));
    }

    public void saveDailyVolume()
    {
        if(volumeList.size()==0) return;

        DatabaseManager manager = DatabaseManager.getDatabaseManager();
        double average=0;
        for(int i=0;i<volumeList.size();++i)
        {
            average+=volumeList.get(i);
        }
        average=average/(double)volumeList.size();
        manager.saveDailyVolume((float) average);

        Log.e("vol","saved");
    }
    public void readDB()
    {
        cursor=DatabaseManager.getDatabaseManager().queryAudio();
        //private static final String CREATE_AUDIO = "create table audio ("
        //        + "start_time integer,"
        //        + "volume real)";

        volumeList=new ArrayList<Float>();
        //volumeList.clear();
        while(cursor.moveToNext())
        {
            volumeList.add(cursor.getFloat(1));
        }

        cursor=DatabaseManager.getDatabaseManager().queryAcceleration();
        //private static final String CREATE_ACC = "create table acceleration ("
        //        + "start_time integer,"
        //        + "steps integer,"
        //        + "x_axis float,"
        //        + "y_axis float,"
        //        + "z_axis float)";

        stepList=new ArrayList<Integer>();
        //stepList.clear();
        while(cursor.moveToNext())
        {
            stepList.add(cursor.getInt(1));
        }
    }

    String version;

    public void getLatestAppVersion()
    {
        version="";
        new Thread(new Runnable() {
            @Override
            public void run() {
                version=getVersion();
                Log.e("version","get version:"+version);

                if(version==null) return;

                if(version.compareTo(MyUtil.version)!=0)
                {
                    recvHandler.sendEmptyMessage(msgUpdateApp);
                }

            }
        }).start();
    }

    public String getVersion()
    {
        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.getReturnJson(HOST, PORT, "version", null);
            JsonObject result = element.getAsJsonObject();
            return result.get("version").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    ProgressDialog pBar;
    String updatePath=Environment.getExternalStorageDirectory()+"/Download/";
    void downFile(final String url)
    {
        Log.e("update","service down file");
        pBar = new ProgressDialog(CollectingService.this);    //进度条，在下载的时候实时更新进度，提高用户友好度
        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pBar.setTitle("正在下载");
        pBar.setMessage("应用正在更新中，请稍候...");
        pBar.setProgress(0);
        pBar.setCanceledOnTouchOutside(false);
        //pBar.show();
        /*
        new Thread()
        {
            public void run()
            {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try
                {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    int length = (int) entity.getContentLength();   //获取文件大小
                    pBar.setMax(length);                            //设置进度条的总长度
                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null)
                    {
                        File file = new File(
                                //Environment.getExternalStorageDirectory(),
                                updatePath,
                                "moodexp.apk");
                        fileOutputStream = new FileOutputStream(file);
                        byte[] buf = new byte[1024];   //这个是缓冲区，即一次读取10个比特，我弄的小了点，因为在本地，所以数值太大一 下就下载完了，看不出progressbar的效果。
                        int ch = -1;
                        int process = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            process += ch;
                            pBar.setProgress(process);       //这里就是关键的实时更新进度了！
                        }

                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }


                    recvHandler.sendEmptyMessage(msgDownloadFileFinish);

                }
                catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        */
    }//down file

    void update()
    {
        Log.e("update","service update");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile( new File(updatePath, "moodexp.apk") ),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }





}
