package cn.edu.nju.dislab.moodexp;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.BaseColumns;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
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
import cn.edu.nju.dislab.moodexp.httputils.HttpAPI;

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



    @Override
    public void onCreate() {
        super.onCreate();

        mDbHelper=new DbHelper();
        readableDatabase=mDbHelper.getReadableDatabase();
        writableDatabase=mDbHelper.getWritableDatabase();

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
        Cursor cursor=readableDatabase.query(DbHelper.ScheduleTable.TABLE_NAME,null,null,null,null,null,null);
        while(cursor.moveToNext()){
            long nextFireTime= cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.ScheduleTable.COLUMN_NAME_NEXT_FIRE_TIME));
            long currentTime=System.currentTimeMillis();
            if(currentTime<nextFireTime){
                continue;
            }
            int level= cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.ScheduleTable.COLUMN_NAME_LEVEL));
            String type=cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.ScheduleTable.COLUMN_NAME_TYPE));
            int interval= cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.ScheduleTable.COLUMN_NAME_INTERVAL));
            String[] actions= new Gson().fromJson(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.ScheduleTable.COLUMN_NAME_ACTIONS)),String[].class);

            switch (type){
                case "collect":
                    Cursor cursorCollectDb=readableDatabase.query(DbHelper.CollectDbTable.TABLE_NAME, new String[]{DbHelper.CollectDbTable.COLUMN_NAME_NAME}, DbHelper.CollectDbTable.COLUMN_NAME_IS_USING+" = ?",new String[]{"1"},null,null,null);
                    String collectDbName;
                    if(cursorCollectDb.getCount()>0){
                        cursorCollectDb.moveToFirst();
                        collectDbName=cursorCollectDb.getString(cursorCollectDb.getColumnIndexOrThrow(DbHelper.CollectDbTable.COLUMN_NAME_NAME));
                        cursorCollectDb.close();
                    }else{
                        cursorCollectDb.close();
                        continue;
                    }
                    CollectorDbHelper collectorDbHelper=new CollectorDbHelper(collectDbName);
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
                            thread.join(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "upload":
                    if(!isNetworkConnected()){
                        continue;
                    }
                    List<String> dbNames=new ArrayList<>();
                    Cursor cursorCheckUpload=readableDatabase.query(DbHelper.CollectDbTable.TABLE_NAME,new String[]{DbHelper.CollectDbTable.COLUMN_NAME_NAME},DbHelper.CollectDbTable.COLUMN_NAME_IS_USING+" = ? AND "+DbHelper.CollectDbTable.COLUMN_NAME_IS_UPLOADED+" = ?",new String[]{"0","0"},null,null,null);
                    while (cursorCheckUpload.moveToNext()){
                        String dbName=cursorCheckUpload.getString(cursorCheckUpload.getColumnIndexOrThrow(DbHelper.CollectDbTable.COLUMN_NAME_NAME));
                        dbNames.add(dbName);
                    }
                    cursorCheckUpload.close();
                    final String userId=mDbHelper.getUser("id");
                    final String version=getVersionName();
                    for(String dbName:dbNames){
                        final String dbNameFinal=dbName;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                File dbPath=getDatabasePath(dbNameFinal);
                                boolean isUploadSuccess= HttpAPI.upload(dbPath.getAbsolutePath(),userId,0,version);
                                if(isUploadSuccess){
                                    ContentValues updateValues=new ContentValues();
                                    updateValues.put(DbHelper.CollectDbTable.COLUMN_NAME_IS_UPLOADED,1);
                                    writableDatabase.update(DbHelper.CollectDbTable.TABLE_NAME,updateValues,DbHelper.CollectDbTable.COLUMN_NAME_NAME+" = ?",new String[]{dbNameFinal});
                                }
                            }
                        }).start();
                    }
                    break;
                case "newDb":
                    Cursor cursorCurrentUsingDb=readableDatabase.query(DbHelper.CollectDbTable.TABLE_NAME, new String[]{DbHelper.CollectDbTable.COLUMN_NAME_NAME}, DbHelper.CollectDbTable.COLUMN_NAME_IS_USING+" = ?",new String[]{"1"},null,null,null);
                    String currentUsingDb=null;
                    if(cursorCurrentUsingDb.getCount()>0){
                        cursorCurrentUsingDb.moveToFirst();
                        currentUsingDb=cursorCurrentUsingDb.getString(cursorCurrentUsingDb.getColumnIndexOrThrow(DbHelper.CollectDbTable.COLUMN_NAME_NAME));
                    }
                    cursorCurrentUsingDb.close();
                    if(currentUsingDb==null){
                        ContentValues initCollectDb=new ContentValues();
                        initCollectDb.put(DbHelper.CollectDbTable.COLUMN_NAME_NAME,System.currentTimeMillis()+".db");
                        initCollectDb.put(DbHelper.CollectDbTable.COLUMN_NAME_IS_USING,1);
                        initCollectDb.put(DbHelper.CollectDbTable.COLUMN_NAME_IS_UPLOADED,0);
                        initCollectDb.put(DbHelper.CollectDbTable.COLUMN_NAME_TIMESTAMP,System.currentTimeMillis());
                        writableDatabase.insert(DbHelper.CollectDbTable.TABLE_NAME,null,initCollectDb);
                    }else{
                        writableDatabase.beginTransaction();
                        ContentValues updateValues=new ContentValues();
                        updateValues.put(DbHelper.CollectDbTable.COLUMN_NAME_IS_USING,0);
                        writableDatabase.update(DbHelper.CollectDbTable.TABLE_NAME,updateValues,DbHelper.CollectDbTable.COLUMN_NAME_NAME+" = ?",new String[]{currentUsingDb});
                        ContentValues initCollectDb=new ContentValues();
                        initCollectDb.put(DbHelper.CollectDbTable.COLUMN_NAME_NAME,System.currentTimeMillis()+".db");
                        initCollectDb.put(DbHelper.CollectDbTable.COLUMN_NAME_IS_USING,1);
                        initCollectDb.put(DbHelper.CollectDbTable.COLUMN_NAME_IS_UPLOADED,0);
                        initCollectDb.put(DbHelper.CollectDbTable.COLUMN_NAME_TIMESTAMP,System.currentTimeMillis());
                        writableDatabase.insert(DbHelper.CollectDbTable.TABLE_NAME,null,initCollectDb);
                        writableDatabase.setTransactionSuccessful();
                        writableDatabase.endTransaction();
                    }
                    break;
                case "heartBeat":
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpAPI.heartBeat(mDbHelper.getUser("id"));
                        }
                    }).start();
                    break;
            }
            while (nextFireTime<System.currentTimeMillis()){
                nextFireTime+=interval;
            }
            ContentValues updateValues=new ContentValues();
            updateValues.put(DbHelper.ScheduleTable.COLUMN_NAME_NEXT_FIRE_TIME,nextFireTime);
            writableDatabase.update(DbHelper.ScheduleTable.TABLE_NAME,updateValues,DbHelper.ScheduleTable.COLUMN_NAME_LEVEL+" = ?",new String[]{Integer.toString(level)});
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
    public static boolean isNetworkConnected(){
        return ((ConnectivityManager)MainApplication.getContext().getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo()!=null;
    }
    public static String getVersionName(){
        try {
            return MainApplication.getContext().getPackageManager().getPackageInfo(MainApplication.getContext().getPackageName(), 0).versionName;
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
            return "";
        }
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
