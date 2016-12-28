package cn.edu.nju.dislab.moodexp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import cn.edu.nju.dislab.moodexp.httputils.HttpAPI;
import cn.edu.nju.dislab.moodexp.permissionintro.PermissionIntroActivity;
import cn.edu.nju.dislab.moodexp.registerandlogin.RegisterAndLoginActivity;
import cn.edu.nju.dislab.moodexp.survey.Answer;
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
            JsonObject result= HttpAPI.getSurveyStat(id);
            return result;
        }

        @Override
        protected void onPostExecute(JsonObject result) {
            mProgressDialog.dismiss();
            if(result==null) {
                Toast.makeText(mContext, "未知错误，请检查网络连接是否正常", Toast.LENGTH_SHORT).show();
            } else{
                String countHasSubmit=result.get("count").getAsString();
                String countShouldSubmit=result.get("max_count").getAsString();
                new AlertDialog.Builder(mContext)
                        .setTitle("提交历史")
                        .setMessage("当前已提交 "+countHasSubmit+" 次，还需提交 "+countShouldSubmit+" 次。")
                        .setPositiveButton("确定", null)
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
    private class DoSurvey extends AsyncTask<Void,Void,String>{
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
        protected String doInBackground(Void... params) {
            return HttpAPI.getSurvey(MainApplication.getUserId());
        }

        @Override
        protected void onPostExecute(String s) {
            mProgressDialog.dismiss();
            if(s==null){
                Toast.makeText(mContext,"加载失败，请重试",Toast.LENGTH_SHORT).show();
            }else{
                Intent intent=new Intent(mContext,SurveyActivity.class);
                intent.putExtra("survey",s);
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

            if(MainApplication.getUserId().isEmpty()) {
                checkRegisterAndLogin();
            }
            startScheduledService();
            new CheckUpdate(MainActivity.this,false).execute();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
