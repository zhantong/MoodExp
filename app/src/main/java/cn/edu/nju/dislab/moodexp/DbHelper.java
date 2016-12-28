package cn.edu.nju.dislab.moodexp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.google.gson.Gson;

/**
 * Created by zhantong on 2016/12/22.
 */

public class DbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MoodExp.db";

    private SQLiteDatabase mReadableDb;

    public static class ScheduleTable implements BaseColumns {
        static final String TABLE_NAME = "schedule";
        static final String COLUMN_NAME_LEVEL = "level";
        static final String COLUMN_NAME_TYPE = "type";
        static final String COLUMN_NAME_NEXT_FIRE_TIME = "next_fire_time";
        static final String COLUMN_NAME_INTERVAL = "interval";
        static final String COLUMN_NAME_ACTIONS = "actions";
        static final String COLUMN_NAME_IS_ENABLED = "is_enabled";
    }
    public static class CollectDbTable implements BaseColumns{
        static final String TABLE_NAME = "collect_db";
        static final String COLUMN_NAME_NAME = "name";
        static final String COLUMN_NAME_IS_USING = "is_using";
        static final String COLUMN_NAME_IS_UPLOADED = "is_uploaded";
        static final String COLUMN_NAME_IS_DELETED = "is_deleted";
        static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }
    public static class UserTable implements BaseColumns{
        static final String TABLE_NAME = "user";
        static final String COLUMN_NAME_KEY = "key";
        static final String COLUMN_NAME_VALUE = "value";
        static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }
    private static final String SQL_CREATE_TABLE_SCHEDULE=
            "CREATE TABLE "+ ScheduleTable.TABLE_NAME+" ("+
                    ScheduleTable.COLUMN_NAME_LEVEL+" INTEGER PRIMARY KEY,"+
                    ScheduleTable.COLUMN_NAME_TYPE+" TEXT,"+
                    ScheduleTable.COLUMN_NAME_NEXT_FIRE_TIME+" INTEGER,"+
                    ScheduleTable.COLUMN_NAME_INTERVAL+" INTEGER,"+
                    ScheduleTable.COLUMN_NAME_ACTIONS +" TEXT,"+
                    ScheduleTable.COLUMN_NAME_IS_ENABLED+" INTEGER DEFAULT 1)";
    private static final String SQL_CREATE_TABLE_COLLECT_DB=
            "CREATE TABLE "+ CollectDbTable.TABLE_NAME+" ("+
                    CollectDbTable.COLUMN_NAME_NAME+" TEXT PRIMARY KEY,"+
                    CollectDbTable.COLUMN_NAME_IS_USING+" INTEGER,"+
                    CollectDbTable.COLUMN_NAME_IS_UPLOADED+" INTEGER,"+
                    CollectDbTable.COLUMN_NAME_IS_DELETED+" INTEGER DEFAULT 0,"+
                    CollectDbTable.COLUMN_NAME_TIMESTAMP+" INTEGER)";
    private static final String SQL_CREATE_TABLE_USER=
            "CREATE TABLE "+ UserTable.TABLE_NAME+" ("+
                    UserTable.COLUMN_NAME_KEY+" TEXT PRIMARY KEY,"+
                    UserTable.COLUMN_NAME_VALUE+" TEXT,"+
                    UserTable.COLUMN_NAME_TIMESTAMP+" INTEGER)";


    public DbHelper() {
        this(MainApplication.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DbHelper(String name){
        this(MainApplication.getContext(),name,null,1);
    }

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mReadableDb=getReadableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_SCHEDULE);
        db.execSQL(SQL_CREATE_TABLE_COLLECT_DB);
        db.execSQL(SQL_CREATE_TABLE_USER);

        /*
        ContentValues userName=new ContentValues();
        userName.put(UserTable.COLUMN_NAME_KEY,"name");
        userName.put(UserTable.COLUMN_NAME_VALUE,"test0");
        userName.put(UserTable.COLUMN_NAME_TIMESTAMP,System.currentTimeMillis());
        db.insert(UserTable.TABLE_NAME,null,userName);

        ContentValues userId=new ContentValues();
        userId.put(UserTable.COLUMN_NAME_KEY,"id");
        userId.put(UserTable.COLUMN_NAME_VALUE,"test0");
        userId.put(UserTable.COLUMN_NAME_TIMESTAMP,System.currentTimeMillis());
        db.insert(UserTable.TABLE_NAME,null,userId);
        */

        ContentValues initCollectDb=new ContentValues();
        initCollectDb.put(CollectDbTable.COLUMN_NAME_NAME,System.currentTimeMillis()+".db");
        initCollectDb.put(CollectDbTable.COLUMN_NAME_IS_USING,1);
        initCollectDb.put(CollectDbTable.COLUMN_NAME_IS_UPLOADED,0);
        initCollectDb.put(CollectDbTable.COLUMN_NAME_TIMESTAMP,System.currentTimeMillis());
        db.insert(CollectDbTable.TABLE_NAME,null,initCollectDb);
        if(true) {
            ContentValues valuesSchedule = new ContentValues();
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_LEVEL, 1);
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_TYPE, "collect");
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_NEXT_FIRE_TIME, System.currentTimeMillis());
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_INTERVAL, 10 * 1000);
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_ACTIONS, new Gson().toJson(new String[]{"CallLog", "Contact","Audio","ForegroundApp","Location","Phone","RunningApp","Screen","Sensors","Sms","Wifi"}));
            db.insert(ScheduleTable.TABLE_NAME, null, valuesSchedule);
        }
        if(true) {
            ContentValues valuesSchedule = new ContentValues();
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_LEVEL, 2);
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_TYPE, "heartBeat");
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_NEXT_FIRE_TIME, System.currentTimeMillis());
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_INTERVAL, 30 * 1000);
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_ACTIONS, new Gson().toJson(new String[]{}));
            db.insert(ScheduleTable.TABLE_NAME, null, valuesSchedule);
        }
        if(true) {
            ContentValues valuesSchedule = new ContentValues();
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_LEVEL, 3);
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_TYPE, "newDb");
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_NEXT_FIRE_TIME, System.currentTimeMillis());
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_INTERVAL, 2*60 * 1000);
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_ACTIONS, new Gson().toJson(new String[]{}));
            db.insert(ScheduleTable.TABLE_NAME, null, valuesSchedule);
        }
        if(true) {
            ContentValues valuesSchedule = new ContentValues();
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_LEVEL, 4);
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_TYPE, "upload");
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_NEXT_FIRE_TIME, System.currentTimeMillis());
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_INTERVAL, 2*60 * 1000);
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_ACTIONS, new Gson().toJson(new String[]{}));
            db.insert(ScheduleTable.TABLE_NAME, null, valuesSchedule);
        }
        if(true) {
            ContentValues valuesSchedule = new ContentValues();
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_LEVEL, 5);
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_TYPE, "cleanUp");
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_NEXT_FIRE_TIME, System.currentTimeMillis());
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_INTERVAL, 4*60 * 1000);
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_ACTIONS, new Gson().toJson(new String[]{}));
            db.insert(ScheduleTable.TABLE_NAME, null, valuesSchedule);
        }
        if(true){
            ContentValues valuesSchedule = new ContentValues();
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_LEVEL, 6);
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_TYPE, "notification");
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_NEXT_FIRE_TIME, 1482832800000L);
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_INTERVAL, 24*60*60 * 1000L);
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_ACTIONS, new Gson().toJson(new String[]{}));
            db.insert(ScheduleTable.TABLE_NAME, null, valuesSchedule);
        }
        if(true){
            ContentValues valuesSchedule = new ContentValues();
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_LEVEL, 7);
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_TYPE, "notification");
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_NEXT_FIRE_TIME, 1482850800000L);
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_INTERVAL, 24*60*60 * 1000L);
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_ACTIONS, new Gson().toJson(new String[]{}));
            db.insert(ScheduleTable.TABLE_NAME, null, valuesSchedule);
        }
        if(true){
            ContentValues valuesSchedule = new ContentValues();
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_LEVEL, 8);
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_TYPE, "notification");
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_NEXT_FIRE_TIME, 1482868800000L);
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_INTERVAL, 24*60*60 * 1000L);
            valuesSchedule.put(ScheduleTable.COLUMN_NAME_ACTIONS, new Gson().toJson(new String[]{}));
            db.insert(ScheduleTable.TABLE_NAME, null, valuesSchedule);
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
