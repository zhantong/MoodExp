package org.graduation.moodexp.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.graduation.moodexp.healthylife.MainApplication;

/**
 * Created by javan on 2016/4/4.
 */
public class HealthyLifeDBHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "HealthyLife.db";
    private static final String CREATE_AUDIO = "create table audio ("
            + "start_time integer,"
            + "volume real)";
    private static final String CREATE_LIGHT = "create table light ("
            + "start_time integer,"
            + "volume real)";
    private static final String CREATE_APP_USAGE = "create table appUsage ("
            + "pkg_name text,"
            + "period integer,"
            + "time integer,"
            + "eno integer)";
    private static final String CREATE_APP = "create table app ("
            + "pkg_name text,"
            + "time integer)";
    private static final String CREATE_WIFI = "create table wifi ("
            + "start_time integer,"
            +"bssid text,"
            +"rssi integer,"
            +"ssid text)";
    private static final String CREATE_ACC = "create table acceleration ("
            + "start_time integer,"//start time means... i do not know too
            + "steps integer,"
            + "x_axis float,"
            + "y_axis float,"
            + "z_axis float)";
    private static final String CREATE_LOCATION = "create table location ("
            + "time integer,"
            + "altitude float,"
            + "longitude float,"
            + "latitude float)";
    private static final String CREATE_EMOTION = "create table emotion ("
            + "eno integer,"
            + "happiness integer,"
            + "sadness integer,"
            + "anger integer,"
            + "surprise integer,"
            + "fear integer,"
            + "disgust integer,"
            + "time varchar(80))";
    private static final String CREATE_MAGNETIC="create table magnetic ("
            +"time integer,"
            +"x_magnetic float,"
            +"y_magnetic float,"
            +"z_magnetic float)";
    private static final String CREATE_GYROSCOPE="create table gyroscope ("
            +"time integer,"
            +"x_gyroscope float,"
            +"y_gyroscope float,"
            +"z_gyroscope float)";
    private static final String CREATE_PhoneInfo="create table phoneInfo ("
            +"IMEI varchar(80),"
            +"SIM_serial varchar(80),"
            +"WLAN_MAC varchar(80),"
            +"IP varchar(80),"
            +"Email varchar(80),"
            +"Phone_Number varchar(80))";
    private static final String CREATE_CONTACTS="create table contacts ("
            +"name integer,"//姓名的hash
            +"phonenum integer)";//号码的hash
    private static final String CREATE_CALLS="create table calls ("
            +"time integer,"//通话开始时间,单位ms
            +"phonenum integer,"//号码的hash
            +"type integer,"//类型,1 is incomming,2 is outgoing,3 is missed
            +"duration integer)";//通话时长，单位s(包括响铃时间)
    private static final String CREATE_SMS="create table sms ("
            +"time integer,"//时间的hash
            +"address integer,"//对方号码的hash
            +"type integer)";//1 is incomming,2 is outgoing
    private static final String CREATE_SCREEN="create table screen ("
            +"time integer,"
            +"state integer)";//0 is off,1 is on
    private static final String CREATE_DAILY_STEP="create table dailyStep ("
            +"time varchar(80),"
            +"stepCount integer)";
    private static final String CREATE_DAILY_VOLUME="create table dailyVolume ("
            +"time varchar(80),"
            +"volume float)";
    private static final String CREATE_DAILY_TIME="create table dailyTime ("
            +"date varchar(80))";
    private static final String CREATE_StudentInfo="create table studentInfo ("
            +"name varchar(80),"
            +"id varchar(80),"
            +"email varchar(80),"
            +"phoneNumber varchar(80),"
            +"version varchar(80))";

    public static final String CREATE_DAILY_HeartBeat="create table if not exists dailyHeartBeat ("
            +"heartBeat varchar(255))";


    public HealthyLifeDBHelper()
    {
        super(MainApplication.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        //确保每次都是同一个数据库
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.e("db","db setup");
        db.execSQL(CREATE_AUDIO);
        db.execSQL(CREATE_LIGHT);
        // appUsage table used for Lollipop MR1 and above
        // while app used for older system
        db.execSQL(CREATE_APP_USAGE);
        db.execSQL(CREATE_APP);
        db.execSQL(CREATE_WIFI);
        db.execSQL(CREATE_ACC);
        db.execSQL(CREATE_LOCATION);
        db.execSQL(CREATE_EMOTION);

        db.execSQL(CREATE_MAGNETIC);
        db.execSQL(CREATE_GYROSCOPE);
        db.execSQL(CREATE_CONTACTS);
        db.execSQL(CREATE_CALLS);
        db.execSQL(CREATE_SMS);
        db.execSQL(CREATE_SCREEN);

        db.execSQL(CREATE_PhoneInfo);
        db.execSQL(CREATE_DAILY_STEP);
        db.execSQL(CREATE_DAILY_VOLUME);
        db.execSQL(CREATE_DAILY_TIME);
        db.execSQL(CREATE_StudentInfo);

        db.execSQL(CREATE_DAILY_HeartBeat);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
