package org.graduation.healthylife;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.graduation.R;
import org.graduation.collector.*;
import org.graduation.database.DatabaseManager;
import org.graduation.database.SharedPreferenceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static org.graduation.collector.MyDateManager.getCurrentDate;
import static org.graduation.collector.MyDateManager.getCurrentDateSection;
import static org.graduation.collector.MyDateManager.seA;

public class OptionFragment extends Fragment
{
    final String strGroupA1="请列出你今天想要达成的三个积极的结果：";
    final String strGroupB1="请列出你今天想要回避的、害怕的、不好的三个结果：";

    final String strGroupC1="请列出你今天想要达成的三个积极的结果：";
    final String strGroupC2="你今天想要回避的、害怕的、不好的三个结果：";
    final String strGroupC3="请列出你今天想要达成的三个积极的结果：";

    final String strGroupD1="请列出你今天想要回避的、害怕的、不好的三个结果：";
    final String strGroupD2="请列出你今天想要达成的三个积极的结果：";
    final String strGroupD3="请列出你今天想要回避的、害怕的、不好的三个结果：";

    final static int morning=0,afternoon=1,evening=2;

    static int studentGroup=0;

    //static boolean bIsPingFinish;
    //static boolean bIsPingConnect;
    //static boolean bIsOnPing;
    TableLayout mainLayout;

    public static View view;
    public static int width;
    public static int msgWeek1=MyDateManager.seA;
    public static int msgWeek2=MyDateManager.seB;
    public static int msgWeek3=MyDateManager.seC;
    public static int msgWeek4=MyDateManager.seD;

    public static final int radioIdBase=1000;//five id a group
    public static final int editIdBase=100;

    static ArrayList<Integer> radioIdList;
    static ArrayList<String> radioAnswerList;

    static ArrayList<Integer> editTextIdList;
    static ArrayList<String> edTextAnswerList;

    String className;
    boolean bIsGetStudentGroup=false,bIsFinish=false;
    private static final String HOST = "114.212.80.16";
    private static final int PORT = 9000;

    String check1,check2,check3,check4,check5,check6;
    final int checkId=10000;

    ArrayList<Integer> selectiveRadioId;
    ArrayList<Integer> selectiveEditId;

    int cnt;
    Cursor cursor;
    public ArrayList< Integer> stepList;
    public ArrayList< Float> volumeList;
    final int times=90;
    boolean result;

    Handler changeTextHandler;
    static int subSuccess=0,subFail=1;

    public static Handler recvHandler;
    public static int msgSubInfo=0;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.content_new_main, container, false);

        WindowManager wm = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();

        TextView textVersion=(TextView)view.findViewById(R.id.textViewVersion);
        textVersion.setText("当前app版本:"+MyUtil.version);

        final WebView webView = (WebView) view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.requestFocus();

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });

        final ImageView im=(ImageView)view.findViewById(R.id.imageView);
        im.setImageResource(R.mipmap.new_welcome);

        SharedPreferenceManager sm=SharedPreferenceManager.getManager();
        studentGroup=Integer.parseInt( sm.getString("studentGroup","test0") );


        recvHandler =new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what==msgSubInfo)
                {
                    bIsGetStudentGroup=false;
                    bIsFinish=false;
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            //Looper.prepare();
                            className=studentClass("test0");
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

                        return;
                    }

                    subInfo();
                }
            }
        };

        final String str1="填写问卷";
        final String str2="返回欢迎页面";

        view.findViewById(R.id.button_submit).setVisibility(View.INVISIBLE);//text = welcome back to
        view.findViewById(R.id.button_submit).setOnClickListener(new View.OnClickListener()
        {
            boolean bIsClick=false;
            boolean bIsReturn =false;
            @Override
            public void onClick(View v)
            {
                final Button btn=(Button)view.findViewById(R.id.button_submit);
                final Button btn2=(Button)view.findViewById(R.id.button_upload);
                final Button btnBelowWelcome=(Button)view.findViewById(R.id.button_welcome);


                bIsClick=false;
                bIsReturn =false;

                new AlertDialog.Builder(view.getContext())
                        .setTitle("提示")
                        .setMessage("确定"+str2+"?")
                        //.setIcon(R.drawable.quit)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
//                                    subInfo();
//                                    webView.loadUrl("https://sojump.com/jq/10606867.aspx");
//                                    im.setVisibility(View.INVISIBLE);
//
//                                    btn.setText("返回问卷首页");

                                 //subInfo();
                                im.setVisibility(View.VISIBLE);
                                webView.setVisibility(View.INVISIBLE);



                                btn2.setVisibility(View.VISIBLE);
                                btnBelowWelcome.setVisibility(View.VISIBLE);
                                btn.setVisibility(View.INVISIBLE);

                                bIsClick=true;
                                bIsReturn=true;
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                bIsClick=true;
                                bIsReturn=false;
                            }
                        })
                        .show();
            }
        });

        view.findViewById(R.id.button_welcome).setOnClickListener(new View.OnClickListener()
        {
            boolean bIsClick=false;
            boolean bIsReturn =false;
            @Override
            public void onClick(View v)
            {
                if(MainActivity.recvHandler!=null)
                {
                    Log.e("update","clicked , main handler is not null");
                    //MainActivity.recvHandler.sendEmptyMessage(MainActivity.msgGetLatestVersion);
                }
                else
                {
                    Log.e("update","clicked , main handler is null");
                }

                String url=getUrl();

                webView.loadUrl(url);
                webView.setVisibility(View.VISIBLE);

                Button btnWelcome=(Button)view.findViewById(R.id.button_welcome);
                btnWelcome.setVisibility(View.INVISIBLE);

                Button btnQuestion=(Button)view.findViewById(R.id.button_submit);
                btnQuestion.setVisibility(View.VISIBLE);

                Button btnUpload=(Button)view.findViewById(R.id.button_upload);
                btnUpload.setVisibility(View.INVISIBLE);

                im.setVisibility(View.INVISIBLE);

                //subInfo();
            }
        });

        view.findViewById(R.id.button_upload).setOnClickListener(new View.OnClickListener()
        {
            boolean bIsClick=false;
            boolean bIsReturn =false;
            @Override
            public void onClick(View v)
            {
                subInfoClicked();
            }
        });

        return view;
    }

    public void subInfoClicked()
    {
        bIsGetStudentGroup=false;
        bIsFinish=false;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //Looper.prepare();
                className=studentClass("test0");
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
            Toast.makeText(MainApplication.getContext(),"网络没有连接",Toast.LENGTH_SHORT).show();

            return;
        }

        long time = System.currentTimeMillis();
        SharedPreferenceManager sm = SharedPreferenceManager.getManager();
        long lastTime = sm.getLong("lasttime", 0);
        if (time - lastTime <=  1 * 60 * 60 * 1000)
        {//1 * 60 * 60 * 1000
            Toast.makeText(getActivity(), "数据正在上传，请稍后...", Toast.LENGTH_SHORT).show();
            Log.e("sub","sub not until 1 hour");
            return;
        }


        if(MainActivity.recvHandler!=null)
        {
            Log.e("update","main handler is not null");
            MainActivity.recvHandler.sendEmptyMessage(MainActivity.msgGetLatestVersion);
        }
        else
        {
            Log.e("update","main handler is null");
        }


        DatabaseManager manager = DatabaseManager.getDatabaseManager();
        final String id=manager.getStudentId();
        Log.e("sub","heart beat id:"+id);

        sm.put("lasttime", time);
        //SharedPreferenceManager sm = SharedPreferenceManager.getManager();
        int emotionCnt = sm.getInt("emotionCnt", 0);
        emotionCnt++;
        sm.put("emotionCnt", emotionCnt);

        new Thread(new ResultRecord()
                .setContextParam(getActivity().getApplicationContext())
                .setEmotions(0, 0, 0, 0, 0, 0)).start();


        new Thread()
        {
            DatabaseManager manager = DatabaseManager.getDatabaseManager();
            public void run()
            {
                manager.saveHeartBeat("send heartbeat time:"+MyDateManager.getMyDateString());
                boolean bHeart=heartBeat(id);
                if(bHeart==false)
                {
                    return;
                }
                else
                {//

                    manager.saveHeartBeat("send heartbeat time successfully:"+MyDateManager.getMyDateString());
                }
            }
        }.start();


        stepList=new ArrayList<Integer>();
        volumeList=new ArrayList<Float>();
        result=false;
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
                    new ContactCollector().collect();

                    manager.db.execSQL("BEGIN EXCLUSIVE TRANSACTION");

                    result=new FtpUploader().upload();
                    if(result==false)
                    {
                        Log.e("sub","submit fail");
                    }

                    else if(result==true)
                    {
                        //DatabaseManager.getDatabaseManager().refresh();
                        Log.e("sub","submit success");
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
                className=studentClass("test0");
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

            return;
        }

        long time = System.currentTimeMillis();
        SharedPreferenceManager sm = SharedPreferenceManager.getManager();
        long lastTime = sm.getLong("lasttime", 0);
        if (time - lastTime <=  1 * 60 * 60 * 1000)
        {//1 * 60 * 60 * 1000
            //Toast.makeText(getActivity(), "version "+MyUtil.version, Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseManager manager = DatabaseManager.getDatabaseManager();
        final String id=manager.getStudentId();
        Log.e("sub","heart beat id:"+id);

        sm.put("lasttime", time);
        //SharedPreferenceManager sm = SharedPreferenceManager.getManager();
        int emotionCnt = sm.getInt("emotionCnt", 0);
        emotionCnt++;
        sm.put("emotionCnt", emotionCnt);

        new Thread(new ResultRecord()
                .setContextParam(getActivity().getApplicationContext())
                .setEmotions(0, 0, 0, 0, 0, 0)).start();

        new Thread()
        {
            DatabaseManager manager = DatabaseManager.getDatabaseManager();
            public void run()
            {
                manager.saveHeartBeat("send heartbeat time:"+MyDateManager.getMyDateString());
                boolean bHeart=heartBeat(id);
                if(bHeart==false)
                {
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
        result=false;
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
                    new ContactCollector().collect();

                    manager.db.execSQL("BEGIN EXCLUSIVE TRANSACTION");

                    result=new FtpUploader().upload();
                    if(result==false)
                    {
                        Log.e("sub","submit fail");
                    }

                    else if(result==true)
                    {
                        //DatabaseManager.getDatabaseManager().refresh();
                        Log.e("sub","submit success");
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

    public String studentClass(String id){
        JsonObject info=studentInfo(id);
        if(info!=null&&info.get("status").getAsBoolean()){
            return info.get("class").getAsString();
        }
        return null;
    }
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
            return null;
        }
    }

    private static class ResultRecord implements Runnable
    {
        int happiness = -1;
        int sadness = -1;
        int anger = -1;
        int surprise = -1;
        int fear = -1;
        int disgust = -1;
        Context contextParam = null;
        private static final String LAST_RECORD_TIME = "LastRecordAt";

        @Override
        public void run()
        {
            SharedPreferenceManager sm=SharedPreferenceManager.getManager();
            boolean morningSub=sm.getBoolean("morningSubmit",false);
            boolean afternoonSub=sm.getBoolean("afternoonSubmit",false);
            boolean eveningSub=sm.getBoolean("eveningSubmit",false);

            if(morningSub==false && afternoonSub==false && eveningSub==false)
            {
                sm.put("morningSubmit",true);
            }
            else if(morningSub==true && afternoonSub==false && eveningSub==false)
            {
                sm.put("afternoonSubmit",true);
                Log.e("show","sub:"+sm.getBoolean("afternoonSubmit",false));
            }
            else if(morningSub==true && afternoonSub==true && eveningSub==false)
            {
                sm.put("eveningSubmit",true);
            }
            if(morningSub==true && afternoonSub==true && eveningSub==true)
            {
                sm.put("morningSubmit",false);
                sm.put("afternoonSubmit",false);
                sm.put("eveningSubmit",false);
            }

            int emotionId = UUID.randomUUID().toString().hashCode();
            DatabaseManager manager = DatabaseManager.getDatabaseManager();
            manager.saveEmotion(emotionId, happiness, sadness, anger, surprise, fear, disgust);


            Log.d("Result Record", "save " + emotionId + "(" + happiness + ", " + anger + ", "
                    + surprise + ", " + fear + ", " + disgust + ")");

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            {
                return;
            }
//            long last = SharedPreferenceManager.getManager().getLong(LAST_RECORD_TIME, 0);
//            long now = System.currentTimeMillis();
//            SharedPreferenceManager.getManager().put(LAST_RECORD_TIME, now);
//
//            Log.e("time",last+" "+now+"");
//
//            Map<String, UsageStats> usage = new UsageInfo(contextParam).getAppUsageInfo(last, now);
//
//            if (usage != null)
//            {
//                for (Map.Entry<String, UsageStats> entry : usage.entrySet())
//                {
//                    manager.saveAppUsage(entry.getKey(),entry.getValue().getTotalTimeInForeground(), emotionId);
//                }//time is the current time
//            }
        }

        public ResultRecord setEmotions(int happiness, int sadness, int anger,
                                        int surprise, int fear, int disgust) {
            this.happiness = happiness;
            this.sadness = sadness;
            this.anger = anger;
            this.surprise = surprise;
            this.fear = fear;
            this.disgust = disgust;
            return this;
        }
        public ResultRecord setContextParam(Context context)
        {
            this.contextParam = context;
            return this;
        }
    }
    String url;
    public String getUrl()
    {
        String Null="https://sojump.com/jq/10606867.aspx";
        String pro="https://ks.sojump.hk/jq/10677681.aspx";
        String pre="https://ks.sojump.hk/jq/10678216.aspx";

        bIsGetStudentGroup=false;
        bIsFinish=false;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //Looper.prepare();
                className=studentClass(DatabaseManager.getDatabaseManager().getStudentId());
                if(className!=null)
                {
                    Log.e("group","id: "+DatabaseManager.getDatabaseManager().getStudentId()+", class: "+className);
                    //Toast.makeText(MainApplication.getContext(),"学生组别"+className,Toast.LENGTH_SHORT).show();
                    bIsGetStudentGroup=true;
                    bIsFinish=true;

                }
                else
                {
                    Log.e("group","id: "+DatabaseManager.getDatabaseManager().getStudentId()+", class not found, you may try again");
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
            Toast.makeText(MainApplication.getContext(),"网络没有连接",Toast.LENGTH_SHORT).show();

            return Null;
        }




        bIsGetStudentGroup=false;
        bIsFinish=false;

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {

                url = getQuestionnaireUrl(className);
                if (url != null)
                {
                    Log.d("url"," questionnaire url: " + url);
                    bIsFinish=true;
                    bIsGetStudentGroup=true;
                }
                else
                {
                    bIsFinish=true;
                    bIsGetStudentGroup=false;
                }

            }
        }).start();

        while(bIsFinish==false);

        if(bIsGetStudentGroup==false)
        {
            Toast.makeText(MainApplication.getContext(),"网络没有连接",Toast.LENGTH_SHORT).show();

            return Null;
        }

        //String url = getQuestionnaireUrl(className);
        if (url != null)
        {
            Log.d("url"," questionnaire url: " + url);
            return url;
        }

        return Null;
    }

    public static int getTimeSection()
    {
        MyDate d=MyDateManager.getCurrentDate();
        int hour=Integer.valueOf(d.hour);
        int minute=Integer.valueOf(d.minute);

        if(7<=hour && hour<=13)
        {
            return morning;//0
        }
        else if(14<=hour && hour<=18)
        {
            return afternoon;//1
        }
        else if(19<=hour && hour<=23)
        {
            return evening;//2
        }
        else
        {
            return morning;//used to be evening
        }
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
}
