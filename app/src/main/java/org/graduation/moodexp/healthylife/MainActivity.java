package org.graduation.moodexp.healthylife;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.graduation.moodexp.R;
import org.graduation.moodexp.database.HealthyLifeDBHelper;
import org.graduation.moodexp.database.SharedPreferenceManager;
import org.graduation.moodexp.service.FeedbackAlarmReceiver;
import org.graduation.moodexp.service.GatherAlarmReceiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity
{
    private static AlarmManager alarmManager;
    private static PendingIntent gatherPendingIntent;
    private static AlarmManager alarmManager2;
    private static PendingIntent feedbackPendingIntent;
//    TextView introTv;
//    TextView submitTv;
//    TextView queryTv;

    public static SharedPreferences shared;
    public static SharedPreferences.Editor editor;

    public static Handler recvHandler=null;
    public static int msgGpsRequest=0;
    public static int msgWlanRequest=1;
    public static int msgUpdateApp=2;
    public static int msgDownloadFileFinish=3;
    public static int msgGetLatestVersion=4;


    private long exitTime = 0;

    NotificationManager nm;

    private static final String HOST = "114.212.80.16";
    private static final int PORT = 9000;

    String updatePath;
    //String url="https://github.com/Songcheng-Gao/MoodExp/releases/download/v2.5.8/app-debug.apk";
    String updateUrl="https://github.com/Songcheng-Gao/MoodExp/releases/download/r3.0/moodexp.apk";

    //static boolean isUpdating=false;

    @Override
    protected void onResume(){
        super.onResume();

        Log.e("version","app resume");
        getLatestAppVersion();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("version","app create");
        updatePath=Environment.getExternalStorageDirectory()+"/Download/";

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        getFragmentManager().beginTransaction()
                .replace(R.id.testFragment, new OptionFragment())
                .commit();//创建情绪选择fragment界面
        getFragmentManager().executePendingTransactions();
        Log.i("fragment","done");

        shared=getSharedPreferences("user", Context.MODE_PRIVATE);
        editor=shared.edit();editor.putString("date","-1");

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                setVersion(MyUtil.version);
//            }
//        }).start();


        //getLatestAppVersion();


        nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        recvHandler =new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what==msgGpsRequest)
                {
                    Toast.makeText(MainApplication.getContext(), "请激活GPS", Toast.LENGTH_SHORT).show();
                    Intent intent1=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent pi= PendingIntent.getActivity(MainActivity.this, 0, intent1,0);

                    Notification notify=new Notification.Builder( getApplicationContext() )
                            .setAutoCancel(true)
                            .setTicker("请打开GPS开关")

                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon))
                            .setSmallIcon(R.mipmap.icon)
                            .setContentTitle( "请打开GPS开关"  )
                            .setContentText("GPS数据是实验数据重要的一部分，请打开GPS开关")
                            //.setVisibility(Notification.VISIBILITY_PUBLIC )
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                            .setWhen(System.currentTimeMillis())
                            .setContentIntent(pi)
                            .build();

                    int id= msgGpsRequest ;
                    nm.notify(id,notify);/**/
                }

                else if(msg.what==msgUpdateApp)
                {
                    //isUpdating=true;
                    downFile(updateUrl);
                }
                else if(msg.what==msgDownloadFileFinish)
                {
                    pBar.cancel();
                    update();
                    //isUpdating=false;
                }
                else if(msg.what==msgGetLatestVersion)
                {
                    //if(isUpdating==false)
                    {
                        getLatestAppVersion();
                    }
                    //else
                    {
                        //Log.e("version","version updating");
                    }
                }
            }//public void handleMessage(Message msg)

        };

        checkPermission();//开启权限
        prepareServices();//开始服务

        if(!hasOpened())
        {


            Log.e("version",MyUtil.version);
            handleFirstOpen();//确认第一次开启
        }
    }



    String version;
    public void getLatestAppVersion()
    {
        version="";
        new Thread(new Runnable() {
            @Override
            public void run() {

//                String Null="https://sojump.com/jq/10606867.aspx";
//                String pro="https://ks.sojump.hk/jq/10677681.aspx";
//                String pre="https://ks.sojump.hk/jq/10678216.aspx";
//
//                Map<String, String> questionnaireUrls = new HashMap<>();
//                questionnaireUrls.put("0", Null);
//                questionnaireUrls.put("1", Null);
//                questionnaireUrls.put("2", Null);
//                questionnaireUrls.put("3", Null);
//
//                for (Map.Entry<String, String> entry : questionnaireUrls.entrySet()) {
//                    String group = entry.getKey();
//                    String url = entry.getValue();
//                    boolean resultBoolean = setQuestionnaireUrl(group, url);
//                    if (resultBoolean) {
//                        Log.d("url", "set group " + group + " questionnaire url success");
//                    }
//                }

                //setDebugVersion(MyUtil.debugVersion);
                setReleaseVersion(MyUtil.version);
                version=getReleaseVersion();
                Log.e("version","get version:"+version);

                if(version==null)
                {
                    Log.e("version","version id null");
                    return;
                }

                if(version.compareTo(MyUtil.version)!=0)
                {
                    Log.e("version","version update");
                    recvHandler.sendEmptyMessage(msgUpdateApp);
                }
                else
                {
                    Log.e("version","version needn't update");
                }

            }
        }).start();
    }

    public String getQuestionnaireUrl(String group) {
        Map<String, String> params = new HashMap<>();
        params.put("group", group);

        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.getReturnJson(HOST, PORT, "questionnaireurl", params);
            JsonObject result = element.getAsJsonObject();
            if (result.get("status").getAsBoolean()) {
                return result.get("url").getAsString();
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean setQuestionnaireUrl(String group, String url) {
        Map<String, String> params = new HashMap<>();
        params.put("group", group);
        params.put("url", url);

        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.postReturnJson(HOST, PORT, "questionnaireurl", params, null);
            JsonObject result = element.getAsJsonObject();
            return result.get("status").getAsBoolean();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    ProgressDialog pBar;
    void downFile(final String url)
    {
        pBar = new ProgressDialog(MainActivity.this);    //进度条，在下载的时候实时更新进度，提高用户友好度
        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pBar.setTitle("正在下载");
        pBar.setMessage("应用正在更新中，请稍候...");
        pBar.setProgress(0);
        pBar.setCanceledOnTouchOutside(false);
        pBar.show();
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

    //安装文件，一般固定写法
    void update()
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile( new File(updatePath, "moodexp.apk") ),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }



    public String getReleaseVersion() {
        return getVersion("release");
    }

    public String getDebugVersion() {
        return getVersion("debug");
    }

    private String getVersion(String type) {
        Map<String, String> params = new HashMap<>();
        params.put("type", type);

        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.getReturnJson(HOST, PORT, "version", params);
            JsonObject result = element.getAsJsonObject();
            if (result.get("status").getAsBoolean()) {
                return result.get("version").getAsString();
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean setReleaseVersion(String version) {
        return setVersion("release", version);
    }

    public boolean setDebugVersion(String version) {
        return setVersion("debug", version);
    }

    private boolean setVersion(String type, String version) {
        Map<String, String> params = new HashMap<>();
        params.put("type", type);
        params.put("version", version);

        HttpRequest request = new HttpRequest();
        try {
            JsonElement element = request.postReturnJson(HOST, PORT, "version", params, null);
            JsonObject result = element.getAsJsonObject();
            return result.get("status").getAsBoolean();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
    boolean hasOpened()
    {
        SharedPreferenceManager sm=SharedPreferenceManager.getManager();
        return sm.getBoolean("opened", false);
    }

    void handleFirstOpen()
    {
        SharedPreferenceManager sm=SharedPreferenceManager.getManager();
        //sm.put("studentGroup","3");
        sm.put("opened",true);
        sm.put("phoneID",""+(long)(Math.random()*Long.MAX_VALUE));//手机序列号随机 有可能重复
        sm.put("morningSubmit",true);
        sm.put("afternoonSubmit",true);
        sm.put("eveningSubmit",false);

        sm.put("testMonth",11);
        sm.put("testDay",16);
        //sm.put("testHour",18);
    }

    private void prepareServices()
    {
        alarmManager= (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        gatherPendingIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(this, GatherAlarmReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                1*60*1000,
                //5 * 60 * 1000,//5 min
                gatherPendingIntent);

        alarmManager.cancel(gatherPendingIntent);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                1*60*1000,
                //5 * 60 * 1000,
                gatherPendingIntent);




        alarmManager2 = (AlarmManager)this
                .getSystemService(Context.ALARM_SERVICE);
        feedbackPendingIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(this, FeedbackAlarmReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar clock = Calendar.getInstance();
        clock.setTimeInMillis(System.currentTimeMillis());
        clock.set(Calendar.HOUR_OF_DAY, 10);
        clock.set(Calendar.MINUTE, 0);
        clock.set(Calendar.SECOND, 0);
        alarmManager2.cancel(feedbackPendingIntent);

        alarmManager2.setRepeating(AlarmManager.RTC_WAKEUP,
                clock.getTimeInMillis(),
                //AlarmManager.INTERVAL_HALF_DAY,
                AlarmManager.INTERVAL_HALF_DAY,
                feedbackPendingIntent);

        Calendar clock2 = Calendar.getInstance();
        clock2.setTimeInMillis(System.currentTimeMillis());
        clock2.set(Calendar.HOUR_OF_DAY, 16);
        clock2.set(Calendar.MINUTE, 0);
        clock2.set(Calendar.SECOND, 0);

        alarmManager2.setRepeating(AlarmManager.RTC_WAKEUP,
                clock2.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                feedbackPendingIntent);
        Log.d("Service Preparation", "done.");
    }

    private void checkPermission() {
        // Dynamically request permissions on Android 6.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            List<String> requesting = new ArrayList<>();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED)
            {
                requesting.add(Manifest.permission.READ_CONTACTS);

                List<String> permission= new ArrayList<>();
                permission.add(Manifest.permission.READ_CONTACTS);
                //ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), 0);
                Log.e("permission","contacts");
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
            {
                requesting.add(Manifest.permission.ACCESS_FINE_LOCATION);

                List<String> permission= new ArrayList<>();
                permission.add(Manifest.permission.ACCESS_FINE_LOCATION);
               // ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), 0);
                Log.e("permission","gps");
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED)
            {
                requesting.add(Manifest.permission.RECORD_AUDIO);

                List<String> permission= new ArrayList<>();
                permission.add(Manifest.permission.RECORD_AUDIO);
                //ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), 0);
                Log.e("permission","audio");
            }



            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                    != PackageManager.PERMISSION_GRANTED)
            {
                requesting.add(Manifest.permission.READ_CALL_LOG);

                List<String> permission= new ArrayList<>();
                permission.add(Manifest.permission.READ_CALL_LOG);
                //ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), 0);
                Log.e("permission","call");
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                    != PackageManager.PERMISSION_GRANTED)
            {
                requesting.add(Manifest.permission.READ_SMS);

                List<String> permission= new ArrayList<>();
                permission.add(Manifest.permission.READ_SMS);
                //ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), 0);
                Log.e("permission","sms");
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                requesting.add(Manifest.permission.ACCESS_WIFI_STATE);

                List<String> permission= new ArrayList<>();
                permission.add(Manifest.permission.ACCESS_WIFI_STATE);
                //ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), 0);
                Log.e("permission","wifi");
            }

            if (requesting.isEmpty())
            {
                return;
            }
            ActivityCompat.requestPermissions(this, requesting.toArray(new String[requesting.size()]), 0);
        }


        // Ask for usage permission on available system
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
        {
            if (!checkUsagePermission())
            {
                Toast.makeText(getBaseContext(),
                        "我们的实验需要您打开权限开关,谢谢", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private boolean checkUsagePermission()
    {
        UsageStatsManager manager = (UsageStatsManager) getSystemService(
                Context.USAGE_STATS_SERVICE);
        Map<String, UsageStats> results = manager.queryAndAggregateUsageStats(
                System.currentTimeMillis() - SystemClock.elapsedRealtime(),
                System.currentTimeMillis());
        return !results.isEmpty();
    }

    private void dumpDatabase()
    {
        final String SRC_DATABASE = this.getDatabasePath(HealthyLifeDBHelper.DATABASE_NAME).toString();
        final String DEST_FILE = new File(Environment.getExternalStorageDirectory() ,HealthyLifeDBHelper.DATABASE_NAME).getAbsolutePath();
        try {
            InputStream fin = new FileInputStream(new File(SRC_DATABASE));
            OutputStream fout = new FileOutputStream(new File(DEST_FILE));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fin.read(buffer)) > 0)
            {
                fout.write(buffer, 0, length);
            }
            fout.flush();
            fin.close();
            fout.close();
            Toast.makeText(getBaseContext(), "数据库准备完毕", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "数据库复制失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
