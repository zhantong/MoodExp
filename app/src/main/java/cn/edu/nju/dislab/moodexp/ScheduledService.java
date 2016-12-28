package cn.edu.nju.dislab.moodexp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.BaseColumns;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
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
        Log.i(TAG,"setting schedule");
        mScheduledExecutorService.scheduleAtFixedRate(this,0,5, TimeUnit.SECONDS);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {
        Log.i(TAG,"running started");
        try(Cursor cursorSchedule=readableDatabase.query(DbHelper.ScheduleTable.TABLE_NAME,null,DbHelper.ScheduleTable.COLUMN_NAME_IS_ENABLED+" = ?",new String[]{"1"},null,null,null)) {
            while (cursorSchedule.moveToNext()) {
                long nextFireTime = cursorSchedule.getLong(cursorSchedule.getColumnIndexOrThrow(DbHelper.ScheduleTable.COLUMN_NAME_NEXT_FIRE_TIME));
                long currentTime = System.currentTimeMillis();
                if (currentTime < nextFireTime) {
                    continue;
                }
                long level = cursorSchedule.getLong(cursorSchedule.getColumnIndexOrThrow(DbHelper.ScheduleTable.COLUMN_NAME_LEVEL));
                String type = cursorSchedule.getString(cursorSchedule.getColumnIndexOrThrow(DbHelper.ScheduleTable.COLUMN_NAME_TYPE));
                long interval = cursorSchedule.getLong(cursorSchedule.getColumnIndexOrThrow(DbHelper.ScheduleTable.COLUMN_NAME_INTERVAL));
                String[] actions = new Gson().fromJson(cursorSchedule.getString(cursorSchedule.getColumnIndexOrThrow(DbHelper.ScheduleTable.COLUMN_NAME_ACTIONS)), String[].class);
                Log.i(TAG,"level: "+level+" type: "+type+" interval: "+interval);

                switch (type) {
                    case "collect":
                        String collectDbName;
                        try(Cursor cursorCollectDb = readableDatabase.query(DbHelper.CollectDbTable.TABLE_NAME, new String[]{DbHelper.CollectDbTable.COLUMN_NAME_NAME}, DbHelper.CollectDbTable.COLUMN_NAME_IS_USING + " = ?", new String[]{"1"}, null, null, null)) {
                            if (cursorCollectDb.getCount() > 0) {
                                cursorCollectDb.moveToFirst();
                                collectDbName = cursorCollectDb.getString(cursorCollectDb.getColumnIndexOrThrow(DbHelper.CollectDbTable.COLUMN_NAME_NAME));
                            } else {
                                break;
                            }
                        }
                        CollectorDbHelper collectorDbHelper = new CollectorDbHelper(collectDbName);
                        final SQLiteDatabase collectorDb = collectorDbHelper.getWritableDatabase();
                        List<Thread> threads = new ArrayList<>();
                        for (String action : actions) {
                            threads.add(getCollectorThread(action, collectorDb));
                        }
                        for (Thread thread : threads) {
                            thread.start();
                        }
                        for (Thread thread : threads) {
                            try {
                                thread.join(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        collectorDb.close();
                        collectorDbHelper.close();
                        break;
                    case "upload":
                        if (!isWifiConnected()) {
                            Log.i(TAG,"no wifi connection");
                            break;
                        }
                        final String userId = MainApplication.getUserId();
                        final String version = MainApplication.getVersionName();
                        try(Cursor cursorCheckUpload = readableDatabase.query(DbHelper.CollectDbTable.TABLE_NAME, new String[]{DbHelper.CollectDbTable.COLUMN_NAME_NAME}, DbHelper.CollectDbTable.COLUMN_NAME_IS_USING + " = ? AND " + DbHelper.CollectDbTable.COLUMN_NAME_IS_UPLOADED + " = ?", new String[]{"0", "0"}, null, null, null)) {
                            Log.i(TAG,cursorCheckUpload.getCount()+" databases need upload"+" at thread "+Thread.currentThread().getId());
                            while (cursorCheckUpload.moveToNext()) {
                                final String dbName = cursorCheckUpload.getString(cursorCheckUpload.getColumnIndexOrThrow(DbHelper.CollectDbTable.COLUMN_NAME_NAME));
                                Log.i(TAG,"uploading "+dbName+" at thread "+Thread.currentThread().getId());
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        File dbPath = getDatabasePath(dbName);
                                        boolean isUploadSuccess = HttpAPI.upload(dbPath.getAbsolutePath(), userId, 0, version);
                                        if (isUploadSuccess) {
                                            ContentValues updateValues = new ContentValues();
                                            updateValues.put(DbHelper.CollectDbTable.COLUMN_NAME_IS_UPLOADED, 1);
                                            writableDatabase.update(DbHelper.CollectDbTable.TABLE_NAME, updateValues, DbHelper.CollectDbTable.COLUMN_NAME_NAME + " = ?", new String[]{dbName});
                                        }
                                    }
                                }).start();
                            }
                        }
                        break;
                    case "newDb":
                        String currentUsingDb = null;
                        try(Cursor cursorCurrentUsingDb = readableDatabase.query(DbHelper.CollectDbTable.TABLE_NAME, new String[]{DbHelper.CollectDbTable.COLUMN_NAME_NAME}, DbHelper.CollectDbTable.COLUMN_NAME_IS_USING + " = ?", new String[]{"1"}, null, null, null)) {
                            if (cursorCurrentUsingDb.getCount() > 0) {
                                cursorCurrentUsingDb.moveToFirst();
                                currentUsingDb = cursorCurrentUsingDb.getString(cursorCurrentUsingDb.getColumnIndexOrThrow(DbHelper.CollectDbTable.COLUMN_NAME_NAME));
                            }
                        }
                        if (currentUsingDb == null) {
                            ContentValues initCollectDb = new ContentValues();
                            initCollectDb.put(DbHelper.CollectDbTable.COLUMN_NAME_NAME, System.currentTimeMillis() + ".db");
                            initCollectDb.put(DbHelper.CollectDbTable.COLUMN_NAME_IS_USING, 1);
                            initCollectDb.put(DbHelper.CollectDbTable.COLUMN_NAME_IS_UPLOADED, 0);
                            initCollectDb.put(DbHelper.CollectDbTable.COLUMN_NAME_TIMESTAMP, System.currentTimeMillis());
                            writableDatabase.insert(DbHelper.CollectDbTable.TABLE_NAME, null, initCollectDb);
                        } else {
                            writableDatabase.beginTransaction();
                            ContentValues updateValues = new ContentValues();
                            updateValues.put(DbHelper.CollectDbTable.COLUMN_NAME_IS_USING, 0);
                            writableDatabase.update(DbHelper.CollectDbTable.TABLE_NAME, updateValues, DbHelper.CollectDbTable.COLUMN_NAME_NAME + " = ?", new String[]{currentUsingDb});
                            ContentValues initCollectDb = new ContentValues();
                            initCollectDb.put(DbHelper.CollectDbTable.COLUMN_NAME_NAME, System.currentTimeMillis() + ".db");
                            initCollectDb.put(DbHelper.CollectDbTable.COLUMN_NAME_IS_USING, 1);
                            initCollectDb.put(DbHelper.CollectDbTable.COLUMN_NAME_IS_UPLOADED, 0);
                            initCollectDb.put(DbHelper.CollectDbTable.COLUMN_NAME_TIMESTAMP, System.currentTimeMillis());
                            writableDatabase.insert(DbHelper.CollectDbTable.TABLE_NAME, null, initCollectDb);
                            writableDatabase.setTransactionSuccessful();
                            writableDatabase.endTransaction();
                        }
                        break;
                    case "heartBeat":
                        writeHeartBeatToDb();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HttpAPI.heartBeat(MainApplication.getUserId());
                            }
                        }).start();
                        break;
                    case "cleanUp":
                        try(Cursor cursorCheckUpload = readableDatabase.query(DbHelper.CollectDbTable.TABLE_NAME, new String[]{DbHelper.CollectDbTable.COLUMN_NAME_NAME}, DbHelper.CollectDbTable.COLUMN_NAME_IS_USING + " = ? AND " + DbHelper.CollectDbTable.COLUMN_NAME_IS_UPLOADED + " = ? AND "+DbHelper.CollectDbTable.COLUMN_NAME_IS_DELETED+" = ?", new String[]{"0", "1","0"}, null, null, null)) {
                            while (cursorCheckUpload.moveToNext()) {
                                final String dbName = cursorCheckUpload.getString(cursorCheckUpload.getColumnIndexOrThrow(DbHelper.CollectDbTable.COLUMN_NAME_NAME));
                                File dbPath = getDatabasePath(dbName);
                                if((!dbPath.exists())||dbPath.delete()){
                                    ContentValues updateValues = new ContentValues();
                                    updateValues.put(DbHelper.CollectDbTable.COLUMN_NAME_IS_DELETED, 1);
                                    writableDatabase.update(DbHelper.CollectDbTable.TABLE_NAME, updateValues, DbHelper.CollectDbTable.COLUMN_NAME_NAME + " = ?", new String[]{dbName});
                                }
                            }
                        }
                        break;
                    case "notification":
                        if(currentTime-nextFireTime>60*60*1000){
                            Log.i(TAG,"time gone too long, no notification");
                            break;
                        }
                        Context notificationContext=MainApplication.getContext();
                        Notification.Builder builder=new Notification.Builder(notificationContext)
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setPriority(Notification.PRIORITY_MAX)
                                .setAutoCancel(true)
                                .setSmallIcon(R.drawable.icon)
                                .setContentTitle("您现在心情如何呢?")
                                .setContentText("快来告诉我你的心情吧!");
                        Intent resultIntent=new Intent(notificationContext,MainActivity.class);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(notificationContext);
                        stackBuilder.addParentStack(MainActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent =
                                stackBuilder.getPendingIntent(
                                        0,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                );
                        builder.setContentIntent(resultPendingIntent);
                        NotificationManager notificationManager=(NotificationManager)notificationContext.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(new Random().nextInt(),builder.build());
                }
                if(interval>0) {
                    while (nextFireTime < System.currentTimeMillis()) {
                        nextFireTime += interval;
                    }
                    ContentValues updateValues = new ContentValues();
                    updateValues.put(DbHelper.ScheduleTable.COLUMN_NAME_NEXT_FIRE_TIME, nextFireTime);
                    writableDatabase.update(DbHelper.ScheduleTable.TABLE_NAME, updateValues, DbHelper.ScheduleTable.COLUMN_NAME_LEVEL + " = ?", new String[]{Long.toString(level)});
                }else{
                    ContentValues updateValues = new ContentValues();
                    updateValues.put(DbHelper.ScheduleTable.COLUMN_NAME_IS_ENABLED, 0);
                    writableDatabase.update(DbHelper.ScheduleTable.TABLE_NAME, updateValues, DbHelper.ScheduleTable.COLUMN_NAME_LEVEL + " = ?", new String[]{Long.toString(level)});
                }
            }
        }
        Log.i(TAG,"running at thread "+Thread.currentThread().getId());
    }
    private void writeHeartBeatToDb(){
        class HeartBeatTable implements BaseColumns {
            static final String TABLE_NAME = "heart_beat";
            static final String COLUMN_NAME_TIME = "time";
            static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        }
        String collectDbName;
        try(Cursor cursorCollectDb = readableDatabase.query(DbHelper.CollectDbTable.TABLE_NAME, new String[]{DbHelper.CollectDbTable.COLUMN_NAME_NAME}, DbHelper.CollectDbTable.COLUMN_NAME_IS_USING + " = ?", new String[]{"1"}, null, null, null)) {
            if (cursorCollectDb.getCount() > 0) {
                cursorCollectDb.moveToFirst();
                collectDbName = cursorCollectDb.getString(cursorCollectDb.getColumnIndexOrThrow(DbHelper.CollectDbTable.COLUMN_NAME_NAME));
            } else {
                Log.i(TAG,"no collect database available");
                return;
            }
        }
        CollectorDbHelper collectorDbHelper = new CollectorDbHelper(collectDbName);
        final SQLiteDatabase db = collectorDbHelper.getWritableDatabase();
        String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + HeartBeatTable.TABLE_NAME + " (" +
                        HeartBeatTable._ID + " INTEGER PRIMARY KEY," +
                        HeartBeatTable.COLUMN_NAME_TIME + " TEXT," +
                        HeartBeatTable.COLUMN_NAME_TIMESTAMP + " INTEGER)";
        db.execSQL(SQL_CREATE_TABLE);
        ContentValues values = new ContentValues();
        String time=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        values.put(HeartBeatTable.COLUMN_NAME_TIME, time);
        values.put(HeartBeatTable.COLUMN_NAME_TIMESTAMP, System.currentTimeMillis());
        db.insert(HeartBeatTable.TABLE_NAME, null, values);
        db.close();
        collectorDbHelper.close();
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
    public static boolean isWifiConnected(){
        NetworkInfo activeNetwork=((ConnectivityManager)MainApplication.getContext().getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return (activeNetwork!=null)&&(activeNetwork.getType()==ConnectivityManager.TYPE_WIFI);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mScheduledExecutorService!=null){
            mScheduledExecutorService.submit(this);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mScheduledExecutorService.shutdownNow();
        mWakeLock.release();
        super.onDestroy();
    }
}
