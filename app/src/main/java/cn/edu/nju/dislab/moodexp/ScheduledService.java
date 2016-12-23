package cn.edu.nju.dislab.moodexp;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.BaseColumns;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.edu.nju.dislab.moodexp.collectors.AudioCollector;
import cn.edu.nju.dislab.moodexp.collectors.AudioData;
import cn.edu.nju.dislab.moodexp.collectors.CallLogCollector;
import cn.edu.nju.dislab.moodexp.collectors.CallLogData;
import cn.edu.nju.dislab.moodexp.collectors.Collector;
import cn.edu.nju.dislab.moodexp.collectors.ContactCollector;
import cn.edu.nju.dislab.moodexp.collectors.ContactData;
import cn.edu.nju.dislab.moodexp.collectors.ForegroundAppCollector;
import cn.edu.nju.dislab.moodexp.collectors.ForegroundAppData;
import cn.edu.nju.dislab.moodexp.collectors.LocationCollector;
import cn.edu.nju.dislab.moodexp.collectors.LocationData;
import cn.edu.nju.dislab.moodexp.collectors.PhoneCollector;
import cn.edu.nju.dislab.moodexp.collectors.PhoneData;
import cn.edu.nju.dislab.moodexp.collectors.RunningAppCollector;
import cn.edu.nju.dislab.moodexp.collectors.RunningAppData;
import cn.edu.nju.dislab.moodexp.collectors.ScreenCollector;
import cn.edu.nju.dislab.moodexp.collectors.ScreenData;
import cn.edu.nju.dislab.moodexp.collectors.SensorsCollector;
import cn.edu.nju.dislab.moodexp.collectors.SensorsData;
import cn.edu.nju.dislab.moodexp.collectors.SmsCollector;
import cn.edu.nju.dislab.moodexp.collectors.SmsData;
import cn.edu.nju.dislab.moodexp.collectors.WifiCollector;
import cn.edu.nju.dislab.moodexp.collectors.WifiData;

/**
 * Created by zhantong on 2016/12/23.
 */

public class ScheduledService extends Service implements Runnable{
    private static final String TAG="ScheduledService";
    private PowerManager.WakeLock mWakeLock;
    private ScheduledExecutorService mScheduledExecutorService;
    private DbHelper mDbHelper;
    private SQLiteDatabase readableDatabase;
    private SQLiteDatabase writableDatabase;

    public static class ScheduleTable implements BaseColumns {
        static final String TABLE_NAME = "schedule";
        static final String COLUMN_NAME_LEVEL = "level";
        static final String COLUMN_NAME_TYPE = "type";
        static final String COLUMN_NAME_NEXT_FIRE_TIME = "next_fire_time";
        static final String COLUMN_NAME_INTERVAL = "interval";
        static final String COLUMN_NAME_ACTIONS = "actions";
    }
    public static class CollectDbTable implements BaseColumns{
        static final String TABLE_NAME = "collect_db";
        static final String COLUMN_NAME_NAME = "name";
        static final String COLUMN_NAME_IS_USING = "is_using";
        static final String COLUMN_NAME_IS_UPLOADED = "is_uploaded";
        static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mDbHelper=new DbHelper();
        readableDatabase=mDbHelper.getReadableDatabase();
        writableDatabase=mDbHelper.getWritableDatabase();


        String SQL_CREATE_TABLE_SCHEDULE=
                "CREATE TABLE IF NOT EXISTS "+ ScheduleTable.TABLE_NAME+" ("+
                        ScheduleTable.COLUMN_NAME_LEVEL+" INTEGER PRIMARY KEY,"+
                        ScheduleTable.COLUMN_NAME_TYPE+" TEXT,"+
                        ScheduleTable.COLUMN_NAME_NEXT_FIRE_TIME+" INTEGER,"+
                        ScheduleTable.COLUMN_NAME_INTERVAL+" INTEGER,"+
                        ScheduleTable.COLUMN_NAME_ACTIONS +" TEXT)";
        writableDatabase.execSQL(SQL_CREATE_TABLE_SCHEDULE);
        ContentValues valuesSchedule=new ContentValues();
        valuesSchedule.put(ScheduleTable.COLUMN_NAME_LEVEL,1);
        valuesSchedule.put(ScheduleTable.COLUMN_NAME_TYPE,"collect");
        valuesSchedule.put(ScheduleTable.COLUMN_NAME_NEXT_FIRE_TIME,System.currentTimeMillis());
        valuesSchedule.put(ScheduleTable.COLUMN_NAME_INTERVAL,10*1000);
        valuesSchedule.put(ScheduleTable.COLUMN_NAME_ACTIONS,new Gson().toJson(new String[]{"CallLog","Contact"}));
        writableDatabase.insert(ScheduleTable.TABLE_NAME,null,valuesSchedule);


        String SQL_CREATE_TABLE_COLLECT_DB=
                "CREATE TABLE IF NOT EXISTS "+ CollectDbTable.TABLE_NAME+" ("+
                        CollectDbTable.COLUMN_NAME_NAME+" TEXT PRIMARY KEY,"+
                        CollectDbTable.COLUMN_NAME_IS_USING+" INTEGER,"+
                        CollectDbTable.COLUMN_NAME_IS_UPLOADED+" INTEGER,"+
                        CollectDbTable.COLUMN_NAME_TIMESTAMP+" INTEGER)";
        writableDatabase.execSQL(SQL_CREATE_TABLE_COLLECT_DB);
        ContentValues valuesCollectDb=new ContentValues();
        valuesCollectDb.put(CollectDbTable.COLUMN_NAME_NAME,System.currentTimeMillis()+".db");
        valuesCollectDb.put(CollectDbTable.COLUMN_NAME_IS_USING,1);
        valuesCollectDb.put(CollectDbTable.COLUMN_NAME_IS_UPLOADED,0);
        valuesCollectDb.put(CollectDbTable.COLUMN_NAME_TIMESTAMP,System.currentTimeMillis());
        writableDatabase.insert(CollectDbTable.TABLE_NAME,null,valuesCollectDb);


        PowerManager powerManager=(PowerManager)getSystemService(POWER_SERVICE);

        mWakeLock=powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,getClass().getSimpleName());
        mWakeLock.acquire();
        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        mScheduledExecutorService.scheduleAtFixedRate(this,0,5, TimeUnit.SECONDS);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {
        Cursor cursor=readableDatabase.query(ScheduleTable.TABLE_NAME,null,null,null,null,null,null);
        while(cursor.moveToNext()){
            long nextFireTime= cursor.getInt(cursor.getColumnIndexOrThrow(ScheduleTable.COLUMN_NAME_NEXT_FIRE_TIME));
            long currentTime=System.currentTimeMillis();
            if(currentTime<nextFireTime){
                continue;
            }
            int level= cursor.getInt(cursor.getColumnIndexOrThrow(ScheduleTable.COLUMN_NAME_LEVEL));
            String type=cursor.getString(cursor.getColumnIndexOrThrow(ScheduleTable.COLUMN_NAME_TYPE));
            int interval= cursor.getInt(cursor.getColumnIndexOrThrow(ScheduleTable.COLUMN_NAME_INTERVAL));
            String[] actions= new Gson().fromJson(cursor.getString(cursor.getColumnIndexOrThrow(ScheduleTable.COLUMN_NAME_ACTIONS)),String[].class);

            switch (type){
                case "collect":
                    Cursor cursorCollectDb=readableDatabase.query(CollectDbTable.TABLE_NAME, new String[]{CollectDbTable.COLUMN_NAME_NAME}, CollectDbTable.COLUMN_NAME_IS_USING+" = ?",new String[]{"1"},null,null,null);
                    String collctDbName;
                    if(cursorCollectDb.getCount()>0){
                        cursorCollectDb.moveToFirst();
                        collctDbName=cursorCollectDb.getString(cursorCollectDb.getColumnIndexOrThrow(CollectDbTable.COLUMN_NAME_NAME));
                    }else{
                        throw new RuntimeException();
                    }
                    cursorCollectDb.close();
                    CollectorDbHelper collectorDbHelper=new CollectorDbHelper(collctDbName);
                    final SQLiteDatabase collectorDb=collectorDbHelper.getWritableDatabase();
                    List<Thread> threads=new ArrayList<>();
                    for(String action:actions){
                        threads.add(getCollectorThread(action,collectorDb));
                    }
                    for(Thread thread:threads){
                        thread.start();
                    }
                    for(Thread thread:threads){
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
            while (nextFireTime<System.currentTimeMillis()){
                nextFireTime+=interval;
            }
            ContentValues updateValues=new ContentValues();
            updateValues.put(ScheduleTable.COLUMN_NAME_NEXT_FIRE_TIME,nextFireTime);
            writableDatabase.update(ScheduleTable.TABLE_NAME,updateValues,ScheduleTable.COLUMN_NAME_LEVEL+" = ?",new String[]{Integer.toString(level)});
        }
        cursor.close();
        Log.i(TAG,"running");
    }
    private Thread getCollectorThread(final String type,final SQLiteDatabase db){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                switch (type){
                    case "Audio":
                        AudioCollector audioCollector=new AudioCollector();
                        if(audioCollector.collect()==Collector.COLLECT_SUCCESS){
                            AudioData result=audioCollector.getResult();
                            if(result!=null){
                                result.toDb(db);
                                Log.i(TAG, result.toString());
                            }
                        }
                        break;
                    case "CallLog":
                        CallLogCollector callLogCollector=new CallLogCollector();
                        if(callLogCollector.collect()==Collector.COLLECT_SUCCESS){
                            CallLogData result=callLogCollector.getResult();
                            if(result!=null){
                                result.toDb(db);
                                Log.i(TAG, result.toString());
                            }
                        }
                        break;
                    case "Contact":
                        ContactCollector contactCollector=new ContactCollector();
                        if(contactCollector.collect()==Collector.COLLECT_SUCCESS){
                            ContactData result=contactCollector.getResult();
                            if(result!=null){
                                result.toDb(db);
                                Log.i(TAG, result.toString());
                            }
                        }
                        break;
                    case "ForegroundApp":
                        ForegroundAppCollector foregroundAppCollector=new ForegroundAppCollector();
                        if(foregroundAppCollector.collect()==Collector.COLLECT_SUCCESS){
                            ForegroundAppData result=foregroundAppCollector.getResult();
                            if(result!=null){
                                result.toDb(db);
                                Log.i(TAG, result.toString());
                            }
                        }
                        break;
                    case "Location":
                        LocationCollector locationCollector=new LocationCollector();
                        if(locationCollector.collect()==Collector.COLLECT_SUCCESS){
                            LocationData result=locationCollector.getResult();
                            if(result!=null){
                                result.toDb(db);
                                Log.i(TAG, result.toString());
                            }
                        }
                        break;
                    case "Phone":
                        PhoneCollector phoneCollector=new PhoneCollector();
                        if(phoneCollector.collect()==Collector.COLLECT_SUCCESS){
                            PhoneData result=phoneCollector.getResult();
                            if(result!=null){
                                result.toDb(db);
                                Log.i(TAG, result.toString());
                            }
                        }
                        break;
                    case "RunningApp":
                        RunningAppCollector runningAppCollector=new RunningAppCollector();
                        if(runningAppCollector.collect()==Collector.COLLECT_SUCCESS){
                            RunningAppData result=runningAppCollector.getResult();
                            if(result!=null){
                                result.toDb(db);
                                Log.i(TAG, result.toString());
                            }
                        }
                        break;
                    case "Screen":
                        ScreenCollector screenCollector=new ScreenCollector();
                        if(screenCollector.collect()==Collector.COLLECT_SUCCESS){
                            ScreenData result=screenCollector.getResult();
                            if(result!=null){
                                result.toDb(db);
                                Log.i(TAG, result.toString());
                            }
                        }
                        break;
                    case "Sensors":
                        int[] typeSensors = new int[]{Sensor.TYPE_GYROSCOPE, Sensor.TYPE_MAGNETIC_FIELD, Sensor.TYPE_LIGHT, Sensor.TYPE_ACCELEROMETER};
                        long[] maxTimes = new long[]{100000000, 100000000, 100000000, 100000000};
                        SensorsCollector sensorsCollector=new SensorsCollector(typeSensors,maxTimes);
                        if(sensorsCollector.collect()==Collector.COLLECT_SUCCESS){
                            SensorsData result=sensorsCollector.getResult();
                            if(result!=null){
                                result.toDb(db);
                                Log.i(TAG, result.toString());
                            }
                        }
                        break;
                    case "Sms":
                        SmsCollector smsCollector=new SmsCollector();
                        if(smsCollector.collect()==Collector.COLLECT_SUCCESS){
                            SmsData result=smsCollector.getResult();
                            if(result!=null){
                                result.toDb(db);
                                Log.i(TAG, result.toString());
                            }
                        }
                        break;
                    case "Wifi":
                        WifiCollector wifiCollector=new WifiCollector();
                        if(wifiCollector.collect()==Collector.COLLECT_SUCCESS){
                            WifiData result=wifiCollector.getResult();
                            if(result!=null){
                                result.toDb(db);
                                Log.i(TAG, result.toString());
                            }
                        }
                        break;
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mScheduledExecutorService.shutdownNow();
        mWakeLock.release();

        super.onDestroy();
    }
}
