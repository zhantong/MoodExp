package cn.edu.nju.dislab.moodexp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class MainActivity extends Activity {
    private static final String TAG="MainActivity";
    private static final int REQUEST_CODE_INTRO=1;
    private static final int REQUEST_CODE_SURVEY=2;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isFirstStart=preferences.getBoolean("firstStart",true);
        if(isFirstStart){
            startActivityForResult(new Intent(this,IntroActivity.class),REQUEST_CODE_INTRO);
        }else{
            Log.i(TAG,MainApplication.getUserId());
            if(MainApplication.getUserId().isEmpty()) {
                checkRegisterAndLogin();
            }
            startScheduledService();
        }

        Button buttonTest=(Button)findViewById(R.id.btn_test);
        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });
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
    private void test(){
        InputStream inputStream=null;
        try {
            inputStream= getAssets().open("example_survey.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String string=null;
        try {
            string=convertStreamToString(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent=new Intent(this,SurveyActivity.class);
        intent.putExtra("survey",string);
        startActivityForResult(intent,REQUEST_CODE_SURVEY);
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
        startActivity(new Intent(this,RegisterAndLoginActivity.class));
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
        }
        if(requestCode==REQUEST_CODE_SURVEY){
            if(resultCode==Activity.RESULT_OK){
                Map<Integer,Answer> answerMap=new Gson().fromJson(data.getExtras().getString("answers"), new TypeToken<Map<Integer,Answer>>(){}.getType());
                saveSurvryAnswersToDb(answerMap);
                startService(new Intent(this,ScheduledService.class));
            }
        }
    }
    private void saveSurvryAnswersToDb(Map<Integer,Answer> answerMap){
        String surveyAnswersDbName=System.currentTimeMillis()+".db";
        SurveyAnswersDbHelper surveyAnswersDbHelper=new SurveyAnswersDbHelper(surveyAnswersDbName);
        SQLiteDatabase writableSurveyAnswersDb=surveyAnswersDbHelper.getWritableDatabase();
        Gson gson=new Gson();
        for(Map.Entry<Integer,Answer> entry:answerMap.entrySet()){
            int questionId=entry.getKey();
            String answer=gson.toJson(entry.getValue());
            ContentValues contentValues=new ContentValues();
            contentValues.put(SurveyAnswersDbHelper.AnswersTable.COLUMN_NAME_QUESTION_ID,questionId);
            contentValues.put(SurveyAnswersDbHelper.AnswersTable.COLUMN_NAME_ANSWER,answer);
            contentValues.put(SurveyAnswersDbHelper.AnswersTable.COLUMN_NAME_TIMESTAMP,System.currentTimeMillis());
            writableSurveyAnswersDb.insertWithOnConflict(SurveyAnswersDbHelper.AnswersTable.TABLE_NAME,null,contentValues,SQLiteDatabase.CONFLICT_REPLACE);
        }
        writableSurveyAnswersDb.close();
        surveyAnswersDbHelper.close();

        DbHelper dbHelper=new DbHelper();
        SQLiteDatabase writableDb=dbHelper.getWritableDatabase();
        if(true) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DbHelper.CollectDbTable.COLUMN_NAME_NAME, surveyAnswersDbName);
            contentValues.put(DbHelper.CollectDbTable.COLUMN_NAME_IS_USING, 0);
            contentValues.put(DbHelper.CollectDbTable.COLUMN_NAME_IS_UPLOADED, 0);
            contentValues.put(DbHelper.CollectDbTable.COLUMN_NAME_IS_DELETED, 0);
            contentValues.put(DbHelper.CollectDbTable.COLUMN_NAME_TIMESTAMP, System.currentTimeMillis());
            writableDb.insertWithOnConflict(DbHelper.CollectDbTable.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }

        if(true){
            ContentValues valuesSchedule = new ContentValues();
            valuesSchedule.put(DbHelper.ScheduleTable.COLUMN_NAME_LEVEL, System.currentTimeMillis());
            valuesSchedule.put(DbHelper.ScheduleTable.COLUMN_NAME_TYPE, "upload");
            valuesSchedule.put(DbHelper.ScheduleTable.COLUMN_NAME_NEXT_FIRE_TIME, System.currentTimeMillis());
            valuesSchedule.put(DbHelper.ScheduleTable.COLUMN_NAME_INTERVAL, 0);
            valuesSchedule.put(DbHelper.ScheduleTable.COLUMN_NAME_ACTIONS, new Gson().toJson(new String[]{}));
            writableDb.insert(DbHelper.ScheduleTable.TABLE_NAME, null, valuesSchedule);
        }
        writableDb.close();
        dbHelper.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.about:
                startActivity(new Intent(this,AboutActivity.class));
                return true;
            case R.id.intro:
                startActivity(new Intent(this,IntroActivity.class));
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
                                startActivity(new Intent(getBaseContext(),RegisterAndLoginActivity.class));
                            }
                        }).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
