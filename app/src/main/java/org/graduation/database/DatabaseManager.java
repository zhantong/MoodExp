package org.graduation.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;
import android.util.Log;

import org.graduation.collector.StepCollector;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by javan on 2016/3/8.
 */
public class DatabaseManager {
    public SQLiteDatabase db;
    private static DatabaseManager self = new DatabaseManager();

    public static DatabaseManager getDatabaseManager() {
        return self;
    }

    private DatabaseManager() {
        db = new HealthyLifeDBHelper().getWritableDatabase();
    }


    public SQLiteDatabase getDatabase(){
        return db;
    }

    public void refresh()
    {
        db.execSQL("delete from audio");
        db.execSQL("delete from light");
        db.execSQL("delete from appusage");
        db.execSQL("delete from app");
        db.execSQL("delete from wifi");
        db.execSQL("delete from acceleration");
        db.execSQL("delete from location");
        db.execSQL("delete from magnetic");
        db.execSQL("delete from gyroscope");
        db.execSQL("delete from contacts");
        db.execSQL("delete from calls");
        db.execSQL("delete from sms");
        db.execSQL("delete from screen");

        //db.execSQL("delete from dailyAnswer");
        db.execSQL("delete from appUsage");
        db.execSQL("delete from dailyHeartBeat");

        Log.e("sub","delete table");

        StepCollector.resetStep();
    }/**/

    public void refresh(long time)
    {
        db.execSQL("delete from audio");
        db.execSQL("delete from light");
        db.execSQL("delete from appusage");
        db.execSQL("delete from app");
        db.execSQL("delete from wifi");
        db.execSQL("delete from acceleration");
        db.execSQL("delete from location");
        db.execSQL("delete from magnetic");
        db.execSQL("delete from gyroscope");
        db.execSQL("delete from contacts");
        db.execSQL("delete from calls");
        db.execSQL("delete from sms");
        db.execSQL("delete from screen");

        //db.execSQL("delete from dailyAnswer");
        db.execSQL("delete from appUsage");
        db.execSQL("delete from dailyHeartBeat");

        Log.e("sub","delete table");

        StepCollector.resetStep();
    }/**/

    public static void copyfile(File fromFile, File toFile, Boolean rewrite )
    {

        if (!fromFile.exists()) {

            return;

            }

        if (!fromFile.isFile()) {

            return ;

            }

        if (!fromFile.canRead()) {

            return ;

            }

        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }

        if (toFile.exists() && rewrite) {
            toFile.delete();
        }

        //当文件不存时，canWrite一直返回的都是false

        // if (!toFile.canWrite()) {

        // MessageDialog.openError(new Shell(),"错误信息","不能够写将要复制的目标文件" + toFile.getPath());

        // Toast.makeText(this,"不能够写将要复制的目标文件", Toast.LENGTH_SHORT);

        // return ;

        // }

        try
        {
            java.io.FileInputStream fosfrom = new java.io.FileInputStream(fromFile);
            java.io.FileOutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];

            int c;

            while ((c = fosfrom.read(bt)) > 0)
            {
                fosto.write(bt, 0, c); //将内容写到新文件当中
            }
            fosfrom.close();
            fosto.close();
        }
        catch (Exception ex)
        {
            Log.e("readfile", ex.getMessage());
        }
    }


//    private static final String CREATE_StudentInfo="create table studentInfo ("
//            +"name varchar(80),"
//            +"id varchar(80),"
//            +"email varchar(80),"
//            +"phoneNumber varchar(80))";
    public void saveStudentInfo(String name,String id,String email,String phoneNumber)
    {

        ContentValues values = new ContentValues();
        values.put("name",name);
        values.put("id",id);
        values.put("email",email);
        values.put("phoneNumber",phoneNumber);
        values.put("version","3.0");
        db.insert("studentInfo", null, values);
    }

    public void savePhoneInfo(String IMEI,String SIM_Serial,String WLAN_MAC,String IP,String Email,String PhoneNumber)
    {


        ContentValues values = new ContentValues();
        values.put("IMEI",IMEI);
        values.put("SIM_serial",SIM_Serial);
        values.put("WLAN_MAC",WLAN_MAC);
        values.put("IP",IP);
        values.put("Email",Email);
        values.put("Phone_Number",PhoneNumber);
        db.insert("phoneInfo", null, values);
    }

    public void saveAnswer(ArrayList<String> l)
    {


        ContentValues values = new ContentValues();

        for(int i=0;i<l.size();++i)
        {
            values.put("answer",l.get(i));
            db.insert("dailyAnswer", null, values);
        }
        Log.e("show","save answer");


    }

    public Cursor queryStudentInfo()
    {
        return db.rawQuery("select * from studentInfo",null);
    }
    public Cursor queryPhoneInfo()
    {
        return db.rawQuery("select * from phoneInfo",null);
    }
    public Cursor queryAcceleration()
    {
        return db.rawQuery("select * from acceleration", null);
    }
    public Cursor queryAudio()
    {
        return db.rawQuery("select * from audio",null);
    }
    public Cursor queryDailyTime()
    {
        return db.rawQuery("select * from dailyTime",null);
    }
    public Cursor queryEmotion(){
        return db.rawQuery("select * from emotion",null);
    }
    public Cursor queryDailyStep(){
        return db.rawQuery("select * from dailyStep",null);
    }
    public Cursor queryDailyVolume(){
        return db.rawQuery("select * from dailyVolume",null);
    }
    public Cursor queryDailyAnswer(){
        return db.rawQuery("select * from dailyAnswer",null);
    }

    public String getStudentId()
    {
        int id=1;
        Cursor c=queryStudentInfo();
        while(c.moveToNext()==true)
        {
            return c.getString(id);
        }

        return null;
    }

    public void saveDailyTime()
    {


        ContentValues values = new ContentValues();

        Time t=new Time();//"GMT+8"
        t.setToNow(); // 取得系统时间。
        int year = t.year;
        int month = t.month;
        int date = t.monthDay;
        int hour = t.hour;
        int minute = t.minute;
        int second = t.second;

        if(minute<10) values.put("date",(month+1)+"/"+date+" "+hour +":"+"0"+minute) ;//forget to add 1
        else values.put("date",(month+1)+"/"+date+" "+hour +":"+minute) ;

        db.insert("dailyTime", null, values);
    }

    public void saveDailyStep(int stepCount) {


        ContentValues values = new ContentValues();
        values.put("time",System.currentTimeMillis()+"");
        values.put("stepCount",stepCount);
        db.insert("dailyStep", null, values);
    }

    public void saveDailyVolume(float volume) {



        ContentValues values = new ContentValues();
        values.put("time",System.currentTimeMillis()+"");
        values.put("volume",volume);
        db.insert("dailyVolume", null, values);
    }




    public void saveAudio(final long startTime,final double volume) {

        new Thread()
        {
            public void run()
            {
                ContentValues values = new ContentValues();
                values.put("start_time",startTime);
                values.put("volume",volume);
                db.insert("audio", null, values);
            }
        }.start();


    }


    public void saveLight(final long startTime,final  double volume) {

        new Thread()
        {
            public void run()
            {
                ContentValues values = new ContentValues();
                values.put("start_time",startTime);
                values.put("volume",volume);
                db.insert("light", null, values);
            }
        }.start();


    }

    public void saveHeartBeat(final String time) {

        new Thread()
        {
            public void run()
            {
                db.execSQL(HealthyLifeDBHelper.CREATE_DAILY_HeartBeat);


                ContentValues values = new ContentValues();
                values.put("heartBeat", time);
                db.insert("dailyHeartBeat", null, values);
                Log.e("sub","heart beat time:"+time);
            }
        }.start();

    }


    public void saveAppUsage(final String pkgName) {

        new Thread()
        {
            public void run()
            {
                ContentValues values = new ContentValues();
                values.put("pkg_name", pkgName);
                values.put("time", System.currentTimeMillis());
                db.insert("app", null, values);
            }
        }.start();


    }

    public void saveWifi(final long startTime,final  String ssid,final String bssid,final int rssi) {

        new Thread()
        {
            public void run()
            {
                ContentValues values = new ContentValues();
                values.put("start_time", startTime);
                values.put("ssid", ssid);
                values.put("bssid",bssid);
                values.put("rssi",rssi);
                db.insert("wifi", null, values);
            }
        }.start();


    }

    public void saveAcc(final long startTime,final  int step,final  float xAxis,final  float yAxis,final  float zAxis) {


        new Thread()
        {
            public void run()
            {
                ContentValues values = new ContentValues();
                values.put("start_time", startTime);
                values.put("steps", step);
                values.put("x_axis", xAxis);
                values.put("y_axis", yAxis);
                values.put("z_axis", zAxis);
                db.insert("acceleration", null, values);
            }
        }.start();



    }

    public void saveLocation(final double altitude, final double longitude, final double latitude) {

        new Thread()
        {
            public void run()
            {
                ContentValues values = new ContentValues();
                values.put("time", System.currentTimeMillis());
                values.put("altitude", altitude);
                values.put("longitude", longitude);
                values.put("latitude", latitude);
                db.insert("location", null, values);
            }
        }.start();



    }

    public void saveEmotion(final int emotionNo, final int happiness, final int sadness,
                            final int anger, final int surprise, final int fear, final int disgust) {

        new Thread()
        {
            public void run()
            {
                ContentValues values = new ContentValues();
                values.put("eno", emotionNo);
                values.put("happiness", happiness);
                values.put("sadness", sadness);
                values.put("anger", anger);
                values.put("surprise", surprise);
                values.put("fear", fear);
                values.put("disgust", disgust);
                values.put("time", System.currentTimeMillis()+"");
                db.insert("emotion", null, values);
            }
        }.start();




    }
    public void saveMagnetic(final float[] magnetic){

        new Thread()
        {
            public void run()
            {
                ContentValues values = new ContentValues();
                values.put("x_magnetic",magnetic[0]);
                values.put("y_magnetic",magnetic[1]);
                values.put("z_magnetic",magnetic[2]);
                values.put("time", System.currentTimeMillis());
                db.insert("magnetic", null, values);
            }
        }.start();


    }
    public void saveGyroscope(final float[] magnetic){

        new Thread()
        {
            public void run()
            {
                ContentValues values = new ContentValues();
                values.put("x_gyroscope",magnetic[0]);
                values.put("y_gyroscope",magnetic[1]);
                values.put("z_gyroscope",magnetic[2]);
                values.put("time", System.currentTimeMillis());
                db.insert("gyroscope", null, values);
            }
        }.start();


    }
    public void saveContacts(long name,long phonenum){



        ContentValues values = new ContentValues();
        values.put("name",name);
        values.put("phonenum",phonenum);
        db.insert("contacts", null, values);
    }
    public void saveCalls(long time,long phonenum,int type,int duration){



        ContentValues values = new ContentValues();
        values.put("time",time);
        values.put("phonenum",phonenum);
        values.put("type",type);
        values.put("duration", duration);
        db.insert("calls", null, values);
    }
    public void saveSms(long time,long address,int type){



        ContentValues values = new ContentValues();
        values.put("time",time);
        values.put("address",address);
        values.put("type",type);
        db.insert("sms", null, values);
    }
    public void saveScreen(final int state){

        new Thread()
        {
            public void run()
            {
                ContentValues values = new ContentValues();
                values.put("time", System.currentTimeMillis());
                values.put("state",state);
                db.insert("screen", null, values);
            }
        }.start();


    }
}
