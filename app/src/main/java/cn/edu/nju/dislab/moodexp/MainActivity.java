package cn.edu.nju.dislab.moodexp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.edu.nju.dislab.moodexp.httputils.HttpAPI;
import cn.edu.nju.dislab.moodexp.permissionintro.PermissionIntroActivity;
import cn.edu.nju.dislab.moodexp.registerandlogin.RegisterAndLoginActivity;
import cn.edu.nju.dislab.moodexp.survey.SurveyActivity;
import cn.edu.nju.dislab.moodexp.survey.SurveyAnswer;

public class MainActivity extends Activity {
    private static final String TAG="MainActivity";
    private static final int REQUEST_CODE_INTRO=1;
    private static final int REQUEST_CODE_SURVEY=2;
    private static final int REQUEST_CODE_REGISTER_AND_LOGIN=3;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isFirstStart=preferences.getBoolean("firstStart",true);
        if(isFirstStart){
            startActivityForResult(new Intent(this,FirstTimeIntroActivity.class),REQUEST_CODE_INTRO);
        }else{
            Log.i(TAG,MainApplication.getUserId());
            if(MainApplication.getUserId().isEmpty()) {
                checkRegisterAndLogin();
            }
            startScheduledService();
            new CheckUpdate(MainActivity.this,false).execute();
        }

        Button buttonDoSurvey=(Button)findViewById(R.id.btn_do_survey);
        buttonDoSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DoSurvey(MainActivity.this).execute();
            }
        });

        Button buttonViewHistory=(Button)findViewById(R.id.btn_view_history);
        buttonViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetSurveyHistory(MainActivity.this).execute();
            }
        });
    }
    private class GetSurveyHistory extends AsyncTask<Void,Void,JsonObject> {
        private ProgressDialog mProgressDialog;
        private Context mContext;
        public GetSurveyHistory(Context context){
            mContext =context;
            mProgressDialog=new ProgressDialog(mContext);
        }
        @Override
        protected void onPreExecute() {
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("正在加载...");
            mProgressDialog.show();
        }
        @Override
        protected JsonObject doInBackground(Void... params) {
            String id=MainApplication.getUserId();
            JsonObject result= HttpAPI.getSurveyCount(id);
            return result;
        }

        @Override
        protected void onPostExecute(final JsonObject result) {
            mProgressDialog.dismiss();
            if(result==null) {
                Toast.makeText(mContext, "未知错误，请检查网络连接是否正常", Toast.LENGTH_SHORT).show();
            } else{
                new AlertDialog.Builder(mContext)
                        .setTitle("提交历史")
                        .setMessage(result.get("message").getAsString()+"\n\n如需转跳浏览器中查看详细历史信息，请点击『确定』。")
                        .setNegativeButton("取消",null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String url=result.get("url").getAsString();
                                Intent intent=new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(url));
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }
    public static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
    private class DoSurvey extends AsyncTask<Void,Void,JsonObject>{
        private ProgressDialog mProgressDialog;
        private Context mContext;
        public DoSurvey(Context context){
            mContext=context;
            mProgressDialog=new ProgressDialog(mContext);
        }
        @Override
        protected void onPreExecute() {
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("加载中...");
            mProgressDialog.show();
        }
        @Override
        protected JsonObject doInBackground(Void... params) {
            return HttpAPI.getSurvey(MainApplication.getUserId());
        }

        @Override
        protected void onPostExecute(JsonObject result) {
            mProgressDialog.dismiss();
            if(result==null){
                Toast.makeText(mContext,"加载失败，请重试",Toast.LENGTH_SHORT).show();
            }else if(!result.get("status").getAsBoolean()){
                Toast.makeText(mContext,"错误，"+result.get("message").getAsString(),Toast.LENGTH_SHORT).show();
            }
            else{
                Intent intent=new Intent(mContext,SurveyActivity.class);
                intent.putExtra("survey",result.get("survey").getAsString());
                startActivityForResult(intent,REQUEST_CODE_SURVEY);
            }
        }
    }
    private void startScheduledService(){
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1) {
            String pkg=getPackageName();
            PowerManager pm=getSystemService(PowerManager.class);

            if (!pm.isIgnoringBatteryOptimizations(pkg)) {
                Intent i=
                        new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                                .setData(Uri.parse("package:"+pkg));

                startActivity(i);
            }
        }
        startService(new Intent(this,ScheduledService.class));
    }
    private void checkRegisterAndLogin(){
        startActivityForResult(new Intent(this,RegisterAndLoginActivity.class),REQUEST_CODE_REGISTER_AND_LOGIN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE_INTRO){
            SharedPreferences.Editor editor=preferences.edit();
            editor.putBoolean("firstStart",false);
            editor.apply();
            new FirstTimeRunPermissionIssure().execute();
            if(MainApplication.getUserId().isEmpty()) {
                checkRegisterAndLogin();
            }
        }
        if(requestCode==REQUEST_CODE_SURVEY){
            if(resultCode==Activity.RESULT_OK){
                String surveyAnswer=data.getExtras().getString("answer");
                saveSurveyAnswerToDb(surveyAnswer);
                new UploadSurveyAnswer(MainActivity.this).execute(surveyAnswer);
            }
        }
        if(requestCode==REQUEST_CODE_REGISTER_AND_LOGIN){
            if(resultCode==Activity.RESULT_OK){
                String studentId=data.getStringExtra("id");
                String studentName=data.getStringExtra("name");
                String studentClass=data.getStringExtra("class");
                String studentPhone=data.getStringExtra("phone");

                DbHelper dbHelper=new DbHelper();
                SQLiteDatabase writableDb=dbHelper.getWritableDatabase();
                if(studentId!=null){
                    ContentValues values=new ContentValues();
                    values.put(DbHelper.UserTable.COLUMN_NAME_KEY,"id");
                    values.put(DbHelper.UserTable.COLUMN_NAME_VALUE,studentId);
                    values.put(DbHelper.UserTable.COLUMN_NAME_TIMESTAMP,System.currentTimeMillis());
                    writableDb.insertWithOnConflict(DbHelper.UserTable.TABLE_NAME,null,values,SQLiteDatabase.CONFLICT_REPLACE);
                }
                if(studentClass!=null){
                    ContentValues values=new ContentValues();
                    values.put(DbHelper.UserTable.COLUMN_NAME_KEY,"class");
                    values.put(DbHelper.UserTable.COLUMN_NAME_VALUE,studentClass);
                    values.put(DbHelper.UserTable.COLUMN_NAME_TIMESTAMP,System.currentTimeMillis());
                    writableDb.insertWithOnConflict(DbHelper.UserTable.TABLE_NAME,null,values,SQLiteDatabase.CONFLICT_REPLACE);
                }
                if(studentName!=null){
                    ContentValues values=new ContentValues();
                    values.put(DbHelper.UserTable.COLUMN_NAME_KEY,"name");
                    values.put(DbHelper.UserTable.COLUMN_NAME_VALUE,studentName);
                    values.put(DbHelper.UserTable.COLUMN_NAME_TIMESTAMP,System.currentTimeMillis());
                    writableDb.insertWithOnConflict(DbHelper.UserTable.TABLE_NAME,null,values,SQLiteDatabase.CONFLICT_REPLACE);
                }
                if(studentPhone!=null){
                    ContentValues values=new ContentValues();
                    values.put(DbHelper.UserTable.COLUMN_NAME_KEY,"phone");
                    values.put(DbHelper.UserTable.COLUMN_NAME_VALUE,studentPhone);
                    values.put(DbHelper.UserTable.COLUMN_NAME_TIMESTAMP,System.currentTimeMillis());
                    writableDb.insertWithOnConflict(DbHelper.UserTable.TABLE_NAME,null,values,SQLiteDatabase.CONFLICT_REPLACE);
                }
                MainApplication.getUserId(true);
                if(studentName!=null) {
                    Toast.makeText(this, "欢迎你，" +studentName+"。",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private class FirstTimeRunPermissionIssure extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            checkAndRequestPermission();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            startScheduledService();
            new CheckUpdate(MainActivity.this,false).execute();
        }
    }
    private class UploadSurveyAnswer extends AsyncTask<String, Void,JsonObject>{
        private ProgressDialog mProgressDialog;
        private Context mContext;
        public UploadSurveyAnswer(Context context){
            mContext =context;
            mProgressDialog=new ProgressDialog(mContext);
        }
        @Override
        protected void onPreExecute() {
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("提交中...");
            mProgressDialog.show();
        }
        @Override
        protected JsonObject doInBackground(String... params) {
            String id=MainApplication.getUserId();
            String answer=params[0];
            SurveyAnswer surveyAnswer=new Gson().fromJson(answer,SurveyAnswer.class);
            String session=surveyAnswer.getSession();
            JsonObject result=HttpAPI.submitSurvey(id,session,answer);
            return result;
        }

        @Override
        protected void onPostExecute(JsonObject result) {
            mProgressDialog.dismiss();
            if(result==null){
                Toast.makeText(mContext, "未知错误，请检查网络连接是否正常", Toast.LENGTH_SHORT).show();
            }else if(!result.get("status").getAsBoolean()){
                Toast.makeText(mContext, "错误："+result.get("message"), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(mContext,"问卷已提交",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class UploadSurveyAnswersManually extends AsyncTask<Void, Void,JsonObject>{
        private ProgressDialog mProgressDialog;
        private Context mContext;
        AsyncTask self;
        public UploadSurveyAnswersManually(Context context){
            mContext =context;
            mProgressDialog=new ProgressDialog(mContext);
            self=this;
        }
        @Override
        protected void onPreExecute() {
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("提交中...");
            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    self.cancel(true);
                    mProgressDialog.dismiss();
                }
            });
            mProgressDialog.show();
        }
        @Override
        protected JsonObject doInBackground(Void... params) {
            String id=MainApplication.getUserId();
            SurveyAnswersDbHelper surveyAnswersDbHelper=new SurveyAnswersDbHelper();
            SQLiteDatabase readableDb=surveyAnswersDbHelper.getReadableDatabase();
            try(Cursor cursor=readableDb.query(SurveyAnswersDbHelper.AnswersTable.TABLE_NAME,null,null,null,null,null,null)){
                while (cursor.moveToNext()){
                    String answer=cursor.getString(cursor.getColumnIndexOrThrow(SurveyAnswersDbHelper.AnswersTable.COLUMN_NAME_ANSWER));
                    if(answer!=null){
                        SurveyAnswer surveyAnswer=new Gson().fromJson(answer,SurveyAnswer.class);
                        String session=surveyAnswer.getSession();
                        HttpAPI.submitSurvey(id,session,answer);
                    }
                }
            }
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("status",true);
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JsonObject result) {
            mProgressDialog.dismiss();
            if(result==null){
                Toast.makeText(mContext, "未知错误，请检查网络连接是否正常", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(mContext,"提交成功",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void saveSurveyAnswerToDb(String answer){
        SurveyAnswersDbHelper surveyAnswersDbHelper=new SurveyAnswersDbHelper();
        SQLiteDatabase writableSurveyAnswersDb=surveyAnswersDbHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(SurveyAnswersDbHelper.AnswersTable.COLUMN_NAME_ANSWER,answer);
        writableSurveyAnswersDb.insert(SurveyAnswersDbHelper.AnswersTable.TABLE_NAME,null,contentValues);
        writableSurveyAnswersDb.close();
        surveyAnswersDbHelper.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.first_time_intro_page:
                startActivity(new Intent(this,FirstTimeIntroActivity.class));
                return true;
            case R.id.feedback:
                startActivity(new Intent(this,FeedbackActivity.class));
                return true;
            case R.id.about:
                startActivity(new Intent(this,AboutActivity.class));
                return true;
            case R.id.permission_intro:
                startActivity(new Intent(this,PermissionIntroActivity.class));
                return true;
            case R.id.log_out:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.confirm_logout)
                        .setNegativeButton(R.string.cancel,null)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DbHelper dbHelper=new DbHelper();
                                SQLiteDatabase writableDb=dbHelper.getWritableDatabase();
                                if(true) {
                                    ContentValues values=new ContentValues();
                                    values.put(DbHelper.UserTable.COLUMN_NAME_KEY, "id");
                                    values.put(DbHelper.UserTable.COLUMN_NAME_VALUE, "");
                                    values.put(DbHelper.UserTable.COLUMN_NAME_TIMESTAMP, System.currentTimeMillis());
                                    writableDb.insertWithOnConflict(DbHelper.UserTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                                }
                                if(true) {
                                    ContentValues values=new ContentValues();
                                    values.put(DbHelper.UserTable.COLUMN_NAME_KEY, "class");
                                    values.put(DbHelper.UserTable.COLUMN_NAME_VALUE, "");
                                    values.put(DbHelper.UserTable.COLUMN_NAME_TIMESTAMP, System.currentTimeMillis());
                                    writableDb.insertWithOnConflict(DbHelper.UserTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                                }
                                if(true) {
                                    ContentValues values=new ContentValues();
                                    values.put(DbHelper.UserTable.COLUMN_NAME_KEY, "name");
                                    values.put(DbHelper.UserTable.COLUMN_NAME_VALUE, "");
                                    values.put(DbHelper.UserTable.COLUMN_NAME_TIMESTAMP, System.currentTimeMillis());
                                    writableDb.insertWithOnConflict(DbHelper.UserTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                                }
                                if(true) {
                                    ContentValues values=new ContentValues();
                                    values.put(DbHelper.UserTable.COLUMN_NAME_KEY, "phone");
                                    values.put(DbHelper.UserTable.COLUMN_NAME_VALUE, "");
                                    values.put(DbHelper.UserTable.COLUMN_NAME_TIMESTAMP, System.currentTimeMillis());
                                    writableDb.insertWithOnConflict(DbHelper.UserTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                                }
                                MainApplication.getUserId(true);
                                checkRegisterAndLogin();
                            }
                        }).show();
                return true;
            case R.id.check_update:
                new CheckUpdate(MainActivity.this,true).execute();
                return true;
            case R.id.submit_survey_answers_manually:
                new AlertDialog.Builder(this)
                        .setTitle("确定手动提交问卷回答吗？")
                        .setMessage("仅当问卷提交失败或遇到其他意外情况时，你才需要手动提交问卷回答。继续吗？")
                        .setNegativeButton(R.string.cancel,null)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new UploadSurveyAnswersManually(MainActivity.this).execute();
                            }
                        }).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void checkAndRequestPermission(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            final String[] collectors=new String[]{"Screen","CallLog","Phone","Sms","Contact","Audio","Location","Wifi","Sensors","ForegroundApp","RunningApp"};
            for(String collector:collectors){
                Thread thread=ScheduledService.getCollectorThread(collector,null);
                thread.start();
                try {
                    thread.join(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }else {
            String[] PERMS = new String[]{android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.READ_CALL_LOG,
                    android.Manifest.permission.READ_SMS, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE};
            List<String> shouldRequestPerms = new ArrayList<>();
            for (String perm : PERMS) {
                if (!(ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED)) {
                    shouldRequestPerms.add(perm);
                }
            }
            if (shouldRequestPerms.size() != 0) {
                ActivityCompat.requestPermissions(this, shouldRequestPerms.toArray(new String[0]), new Random().nextInt());
            }
        }
    }
}
