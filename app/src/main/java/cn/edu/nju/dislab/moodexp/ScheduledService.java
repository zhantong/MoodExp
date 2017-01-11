package cn.edu.nju.dislab.moodexp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.BaseColumns;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zhantong on 2016/12/23.
 */

public class ScheduledService extends Service implements Runnable {
    private static final String TAG = "ScheduledService";
    private PowerManager.WakeLock mWakeLock;
    private ScheduledExecutorService mScheduledExecutorService;
    private DbHelper mDbHelper;
    private SQLiteDatabase readableDatabase;
    private SQLiteDatabase writableDatabase;

    private static final Logger LOG = LoggerFactory.getLogger(ScheduledService.class);

    public static Thread getCollectorThread(final String type, final SQLiteDatabase db) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                int status = -1;
                switch (type) {
                    case "Audio":
                        LOG.info("preparing {}", type);
                        AudioData.DbInit(db);
                        AudioCollector audioCollector = new AudioCollector();
                        status = audioCollector.collect();
                        LOG.info("{} status {}", type, status);
                        if (status == Collector.COLLECT_SUCCESS) {
                            AudioData result = audioCollector.getResult();
                            //LOG.info("{} result {}",type,result);
                            if (result != null) {
                                if (db != null) {
                                    result.toDb(db);
                                }
                            }
                        }
                        LOG.info("finished {}", type);
                        break;
                    case "CallLog":
                        LOG.info("preparing {}", type);
                        CallLogData.DbInit(db);
                        CallLogCollector callLogCollector = new CallLogCollector();
                        status = callLogCollector.collect();
                        LOG.info("{} status {}", type, status);
                        if (status == Collector.COLLECT_SUCCESS) {
                            CallLogData result = callLogCollector.getResult();
                            //LOG.info("{} result {}",type,result);
                            if (result != null) {
                                if (db != null) {
                                    result.toDb(db);
                                }
                            }
                        }
                        LOG.info("finished {}", type);
                        break;
                    case "Contact":
                        LOG.info("preparing {}", type);
                        ContactData.DbInit(db);
                        ContactCollector contactCollector = new ContactCollector();
                        status = contactCollector.collect();
                        LOG.info("{} status {}", type, status);
                        if (status == Collector.COLLECT_SUCCESS) {
                            ContactData result = contactCollector.getResult();
                            //LOG.info("{} result {}",type,result);
                            if (result != null) {
                                if (db != null) {
                                    result.toDb(db);
                                }
                            }
                        }
                        LOG.info("finished {}", type);
                        break;
                    case "ForegroundApp":
                        LOG.info("preparing {}", type);
                        ForegroundAppData.DbInit(db);
                        ForegroundAppCollector foregroundAppCollector = new ForegroundAppCollector();
                        status = foregroundAppCollector.collect();
                        LOG.info("{} status {}", type, status);
                        if (status == Collector.COLLECT_SUCCESS) {
                            ForegroundAppData result = foregroundAppCollector.getResult();
                            //LOG.info("{} result {}",type,result);
                            if (result != null) {
                                if (db != null) {
                                    result.toDb(db);
                                }
                            }
                        }
                        LOG.info("finished {}", type);
                        break;
                    case "Location":
                        LOG.info("preparing {}", type);
                        LocationData.DbInit(db);
                        LocationCollector locationCollector = new LocationCollector();
                        status = locationCollector.collect();
                        LOG.info("{} status {}", type, status);
                        if (status == Collector.COLLECT_SUCCESS) {
                            LocationData result = locationCollector.getResult();
                            //LOG.info("{} result {}",type,result);
                            if (result != null) {
                                if (db != null) {
                                    result.toDb(db);
                                }
                            }
                        }
                        LOG.info("finished {}", type);
                        break;
                    case "Phone":
                        LOG.info("preparing {}", type);
                        PhoneData.DbInit(db);
                        PhoneCollector phoneCollector = new PhoneCollector();
                        status = phoneCollector.collect();
                        LOG.info("{} status {}", type, status);
                        if (status == Collector.COLLECT_SUCCESS) {
                            PhoneData result = phoneCollector.getResult();
                            //LOG.info("{} result {}",type,result);
                            if (result != null) {
                                if (db != null) {
                                    result.toDb(db);
                                }
                            }
                        }
                        LOG.info("finished {}", type);
                        break;
                    case "RunningApp":
                        LOG.info("preparing {}", type);
                        RunningAppData.DbInit(db);
                        RunningAppCollector runningAppCollector = new RunningAppCollector();
                        status = runningAppCollector.collect();
                        LOG.info("{} status {}", type, status);
                        if (status == Collector.COLLECT_SUCCESS) {
                            RunningAppData result = runningAppCollector.getResult();
                            //LOG.info("{} result {}",type,result);
                            if (result != null) {
                                if (db != null) {
                                    result.toDb(db);
                                }
                            }
                        }
                        LOG.info("finished {}", type);
                        break;
                    case "Screen":
                        LOG.info("preparing {}", type);
                        ScreenData.DbInit(db);
                        ScreenCollector screenCollector = new ScreenCollector();
                        status = screenCollector.collect();
                        LOG.info("{} status {}", type, status);
                        if (status == Collector.COLLECT_SUCCESS) {
                            ScreenData result = screenCollector.getResult();
                            //LOG.info("{} result {}",type,result);
                            if (result != null) {
                                if (db != null) {
                                    result.toDb(db);
                                }
                            }
                        }
                        LOG.info("finished {}", type);
                        break;
                    case "Sensors":
                        LOG.info("preparing {}", type);
                        SensorsData.DbInit(db);
                        int[] typeSensors = new int[]{Sensor.TYPE_GYROSCOPE, Sensor.TYPE_MAGNETIC_FIELD, Sensor.TYPE_LIGHT, Sensor.TYPE_ACCELEROMETER};
                        long[] maxTimes = new long[]{5000000000L, 5000000000L, 5000000000L, 5000000000L};
                        SensorsCollector sensorsCollector = new SensorsCollector(typeSensors, maxTimes);
                        status = sensorsCollector.collect();
                        LOG.info("{} status {}", type, status);
                        if (status == Collector.COLLECT_SUCCESS) {
                            SensorsData result = sensorsCollector.getResult();
                            //LOG.info("{} result {}",type,result);
                            if (result != null) {
                                if (db != null) {
                                    result.toDb(db);
                                }
                            }
                        }
                        LOG.info("finished {}", type);
                        break;
                    case "Sms":
                        LOG.info("preparing {}", type);
                        SmsData.DbInit(db);
                        SmsCollector smsCollector = new SmsCollector();
                        status = smsCollector.collect();
                        LOG.info("{} status {}", type, status);
                        if (status == Collector.COLLECT_SUCCESS) {
                            SmsData result = smsCollector.getResult();
                            //LOG.info("{} result {}",type,result);
                            if (result != null) {
                                if (db != null) {
                                    result.toDb(db);
                                }
                            }
                        }
                        LOG.info("finished {}", type);
                        break;
                    case "Wifi":
                        LOG.info("preparing {}", type);
                        WifiData.DbInit(db);
                        WifiCollector wifiCollector = new WifiCollector();
                        status = wifiCollector.collect();
                        LOG.info("{} status {}", type, status);
                        if (status == Collector.COLLECT_SUCCESS) {
                            WifiData result = wifiCollector.getResult();
                            //LOG.info("{} result {}",type,result);
                            if (result != null) {
                                if (db != null) {
                                    result.toDb(db);
                                }
                            }
                        }
                        LOG.info("finished {}", type);
                        break;
                }
                if (db != null) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(CollectorDbHelper.StatusTable.COLUMN_NAME_TYPE, type);
                    contentValues.put(CollectorDbHelper.StatusTable.COLUMN_NAME_STATUS, status);
                    contentValues.put(CollectorDbHelper.StatusTable.COLUMN_NAME_TIMESTAMP, System.currentTimeMillis());
                    db.insert(CollectorDbHelper.StatusTable.TABLE_NAME, null, contentValues);
                }
            }
        });
    }

    public static boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MainApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static boolean isWifiConnected() {
        WifiManager wifiManager = (WifiManager) MainApplication.getContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null && wifiInfo.getNetworkId() != -1) {
                return true;
            }
        }
        return false;
    }

    public static boolean gzip(String inputFilePath, String outputFilePath) {
        byte[] buffer = new byte[1024];
        try {
            GZIPOutputStream gzos = new GZIPOutputStream(new FileOutputStream(outputFilePath));
            FileInputStream fis = new FileInputStream(inputFilePath);
            int length;
            while ((length = fis.read(buffer)) > 0) {
                gzos.write(buffer, 0, length);
            }
            fis.close();

            gzos.finish();
            gzos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mDbHelper = new DbHelper();
        readableDatabase = mDbHelper.getReadableDatabase();
        writableDatabase = mDbHelper.getWritableDatabase();

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getSimpleName());
        mWakeLock.acquire();
        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        Log.i(TAG, "setting schedule");
        mScheduledExecutorService.scheduleAtFixedRate(this, 0, 60, TimeUnit.SECONDS);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {
        LOG.info("Scheduled Task running");
        String id = MainApplication.getUserId();
        if (id == null || id.equals("")) {
            LOG.info("id not set, Scheduled Task finished");
            return;
        }
        try (Cursor cursorSchedule = readableDatabase.query(DbHelper.ScheduleTable.TABLE_NAME, null, DbHelper.ScheduleTable.COLUMN_NAME_IS_ENABLED + " = ?", new String[]{"1"}, null, null, null)) {
            while (cursorSchedule.moveToNext()) {
                long nextFireTime = cursorSchedule.getLong(cursorSchedule.getColumnIndexOrThrow(DbHelper.ScheduleTable.COLUMN_NAME_NEXT_FIRE_TIME));
                long currentTime = System.currentTimeMillis();
                long level = cursorSchedule.getLong(cursorSchedule.getColumnIndexOrThrow(DbHelper.ScheduleTable.COLUMN_NAME_LEVEL));
                String type = cursorSchedule.getString(cursorSchedule.getColumnIndexOrThrow(DbHelper.ScheduleTable.COLUMN_NAME_TYPE));
                long interval = cursorSchedule.getLong(cursorSchedule.getColumnIndexOrThrow(DbHelper.ScheduleTable.COLUMN_NAME_INTERVAL));
                LOG.info("level {}, type {}, interval {}, current time {}, next fire time {}", level, type, interval, currentTime, nextFireTime);
                if (currentTime < nextFireTime) {
                    LOG.info("{} finished because time isn't up", type);
                    continue;
                }

                switch (type) {
/*                    case "collect":
                        LOG.info("starting {}", type);
                        String collectDbName;
                        String[] actions = new Gson().fromJson(cursorSchedule.getString(cursorSchedule.getColumnIndexOrThrow(DbHelper.ScheduleTable.COLUMN_NAME_ACTIONS)), String[].class);
                        LOG.info("actions {}", Arrays.toString(actions));
                        try (Cursor cursorCollectDb = readableDatabase.query(DbHelper.CollectDbTable.TABLE_NAME, new String[]{DbHelper.CollectDbTable.COLUMN_NAME_NAME}, DbHelper.CollectDbTable.COLUMN_NAME_IS_USING + " = ?", new String[]{"1"}, null, null, null)) {
                            if (cursorCollectDb.getCount() > 0) {
                                cursorCollectDb.moveToFirst();
                                collectDbName = cursorCollectDb.getString(cursorCollectDb.getColumnIndexOrThrow(DbHelper.CollectDbTable.COLUMN_NAME_NAME));
                            } else {
                                break;
                            }
                        }
                        LOG.info("using collector database {}", collectDbName);
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
                        LOG.info("finished {}", type);
                        break;*/
                    case "upload":
                        LOG.info("starting {}", type);
                        if (!isWifiConnected()) {
                            LOG.info("{} finished because no wifi connection", type);
                            break;
                        }
                        final String userId = MainApplication.getUserId();
                        final String version = MainApplication.getVersionName();
                        try (Cursor cursorCheckUpload = readableDatabase.query(DbHelper.CollectDbTable.TABLE_NAME, new String[]{DbHelper.CollectDbTable.COLUMN_NAME_NAME}, DbHelper.CollectDbTable.COLUMN_NAME_IS_USING + " = ? AND " + DbHelper.CollectDbTable.COLUMN_NAME_IS_UPLOADED + " = ?", new String[]{"0", "0"}, null, null, null)) {
                            LOG.info("{} databases need to upload", cursorCheckUpload.getCount());
                            while (cursorCheckUpload.moveToNext()) {
                                final String dbName = cursorCheckUpload.getString(cursorCheckUpload.getColumnIndexOrThrow(DbHelper.CollectDbTable.COLUMN_NAME_NAME));
                                LOG.info("preparing to upload {}", dbName);
                                File dbPath = getDatabasePath(dbName);
                                File tempDir = MainApplication.getContext().getCacheDir();
                                final File gzipFile = new File(tempDir, dbPath.getName() + ".gz");
                                if (gzipFile.exists()) {
                                    gzipFile.delete();
                                }
                                LOG.info("gzip {} to {}", dbPath.getAbsolutePath(), gzipFile.getAbsolutePath());
                                boolean result = gzip(dbPath.getAbsolutePath(), gzipFile.getAbsolutePath());
                                String uploadFilePath;
                                if (result) {
                                    uploadFilePath = gzipFile.getAbsolutePath();
                                } else {
                                    uploadFilePath = dbPath.getAbsolutePath();
                                }
                                LOG.info("uploading {} with id {} version {}", uploadFilePath, userId, version);
                                try {
                                    HttpAPI.upload(uploadFilePath, userId, 0, version, new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            LOG.info("uploading failed {}", e);
                                            if (gzipFile.exists()) {
                                                gzipFile.delete();
                                            }
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            if (!response.isSuccessful()) {
                                                throw new IOException("Unexpected code " + response);
                                            }
                                            JsonElement jsonElement = new JsonParser().parse(response.body().charStream());
                                            if (jsonElement.getAsJsonObject().get("status").getAsBoolean()) {
                                                ContentValues updateValues = new ContentValues();
                                                updateValues.put(DbHelper.CollectDbTable.COLUMN_NAME_IS_UPLOADED, 1);
                                                writableDatabase.update(DbHelper.CollectDbTable.TABLE_NAME, updateValues, DbHelper.CollectDbTable.COLUMN_NAME_NAME + " = ?", new String[]{dbName});
                                                LOG.info("file uploaded successfully");
                                            } else {
                                                LOG.info("upload failed, something wrong in the server side");
                                            }
                                            if (gzipFile.exists()) {
                                                gzipFile.delete();
                                            }
                                        }
                                    });
                                } catch (IOException e) {
                                    LOG.info("uploading failed {}", e);
                                }
                            }
                        }
                        LOG.info("finished {}", type);
                        break;
/*                    case "newDb":
                        LOG.info("starting {}", type);
                        String currentUsingDb = null;
                        try (Cursor cursorCurrentUsingDb = readableDatabase.query(DbHelper.CollectDbTable.TABLE_NAME, new String[]{DbHelper.CollectDbTable.COLUMN_NAME_NAME}, DbHelper.CollectDbTable.COLUMN_NAME_IS_USING + " = ?", new String[]{"1"}, null, null, null)) {
                            if (cursorCurrentUsingDb.getCount() > 0) {
                                cursorCurrentUsingDb.moveToFirst();
                                currentUsingDb = cursorCurrentUsingDb.getString(cursorCurrentUsingDb.getColumnIndexOrThrow(DbHelper.CollectDbTable.COLUMN_NAME_NAME));
                            }
                        }
                        LOG.info("database {} is current using", currentUsingDb);
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
                        LOG.info("changed database successfully");
                        LOG.info("finished {}", type);
                        break;*/
                    case "heartBeat":
                        LOG.info("starting {}", type);
                        if (!isNetworkConnected()) {
                            LOG.info("{} finished because no network", type);
                            break;
                        }
                        writeHeartBeatToDb();
                        LOG.info("sending heartbeat");
                        try {
                            HttpAPI.heartBeat(MainApplication.getUserId(), new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    LOG.info("sending heartbeat failed {}", e);
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    if (!response.isSuccessful()) {
                                        throw new IOException("Unexpected code " + response);
                                    }
                                    JsonElement jsonElement = new JsonParser().parse(response.body().charStream());
                                    if (jsonElement.getAsJsonObject().get("status").getAsBoolean()) {
                                        LOG.info("sent heartbeat successfully");
                                    } else {
                                        LOG.info("send heartbeat failed, something wrong in the server side");
                                    }
                                }
                            });
                        } catch (IOException e) {
                            LOG.info("sending heartbeat failed {}", e);
                        }
                        LOG.info("finished {}", type);
                        break;
                    case "cleanUp":
                        LOG.info("starting {}", type);
                        try (Cursor cursorCheckUpload = readableDatabase.query(DbHelper.CollectDbTable.TABLE_NAME, new String[]{DbHelper.CollectDbTable.COLUMN_NAME_NAME}, DbHelper.CollectDbTable.COLUMN_NAME_IS_USING + " = ? AND " + DbHelper.CollectDbTable.COLUMN_NAME_IS_UPLOADED + " = ? AND " + DbHelper.CollectDbTable.COLUMN_NAME_IS_DELETED + " = ?", new String[]{"0", "1", "0"}, null, null, null)) {
                            while (cursorCheckUpload.moveToNext()) {
                                final String dbName = cursorCheckUpload.getString(cursorCheckUpload.getColumnIndexOrThrow(DbHelper.CollectDbTable.COLUMN_NAME_NAME));
                                File dbPath = getDatabasePath(dbName);
                                LOG.info("deleting {}", dbPath.getAbsolutePath());
                                if ((!dbPath.exists()) || dbPath.delete()) {
                                    ContentValues updateValues = new ContentValues();
                                    updateValues.put(DbHelper.CollectDbTable.COLUMN_NAME_IS_DELETED, 1);
                                    writableDatabase.update(DbHelper.CollectDbTable.TABLE_NAME, updateValues, DbHelper.CollectDbTable.COLUMN_NAME_NAME + " = ?", new String[]{dbName});
                                    LOG.info("{} is deleted successfully", dbPath.getAbsolutePath());
                                }
                            }
                        }
                        LOG.info("finished {}", type);
                        break;
/*                    case "notification":
                        LOG.info("starting {}", type);
                        Calendar calendar = Calendar.getInstance();
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        if (hour < 9 || hour > 21) {
                            LOG.info("{} finished because it's {} (21:00 - 9:00)", type, hour);
                            break;
                        }
                        long lastNotification = 0;
                        try (Cursor cursorLastNotification = readableDatabase.query(DbHelper.MetaTable.TABLE_NAME, new String[]{"MAX(" + DbHelper.MetaTable.COLUMN_NAME_INTEGER_VALUE + ") AS MAX"}, DbHelper.MetaTable.COLUMN_NAME_KEY + " = ? OR " + DbHelper.MetaTable.COLUMN_NAME_KEY + " = ?", new String[]{"last_notification", "last_survey"}, null, null, null)) {
                            if (cursorLastNotification.getCount() > 0) {
                                cursorLastNotification.moveToFirst();
                                lastNotification = cursorLastNotification.getLong(cursorLastNotification.getColumnIndex("MAX"));
                                LOG.info("last notification is at {}", lastNotification);
                            } else {
                                if (true) {
                                    ContentValues values = new ContentValues();
                                    values.put(DbHelper.MetaTable.COLUMN_NAME_KEY, "last_notification");
                                    values.put(DbHelper.MetaTable.COLUMN_NAME_INTEGER_VALUE, System.currentTimeMillis());
                                    writableDatabase.insertWithOnConflict(DbHelper.MetaTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                                }
                                if (true) {
                                    ContentValues values = new ContentValues();
                                    values.put(DbHelper.MetaTable.COLUMN_NAME_KEY, "last_survey");
                                    values.put(DbHelper.MetaTable.COLUMN_NAME_INTEGER_VALUE, System.currentTimeMillis());
                                    writableDatabase.insertWithOnConflict(DbHelper.MetaTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                                }
                            }
                        }
                        if (lastNotification == 0 || currentTime - lastNotification < 5 * 60 * 60 * 1000) {
                            LOG.info("{} finished because last notification is {}, current time is {}", lastNotification, currentTime);
                            break;
                        }
                        LOG.info("preparing to send notification");
                        Context notificationContext = MainApplication.getContext();
                        Notification.Builder builder = new Notification.Builder(notificationContext)
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setPriority(Notification.PRIORITY_MAX)
                                .setAutoCancel(true)
                                .setSmallIcon(R.drawable.icon)
                                .setContentTitle("您现在心情如何呢?")
                                .setContentText("快来告诉我你的心情吧!");
                        Intent resultIntent = new Intent(notificationContext, MainActivity.class);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(notificationContext);
                        stackBuilder.addParentStack(MainActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent =
                                stackBuilder.getPendingIntent(
                                        0,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                );
                        builder.setContentIntent(resultPendingIntent);
                        NotificationManager notificationManager = (NotificationManager) notificationContext.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(new Random().nextInt(), builder.build());
                        LOG.info("notification is sent");

                        ContentValues updateValues = new ContentValues();
                        updateValues.put(DbHelper.MetaTable.COLUMN_NAME_INTEGER_VALUE, System.currentTimeMillis());
                        writableDatabase.update(DbHelper.MetaTable.TABLE_NAME, updateValues, DbHelper.MetaTable.COLUMN_NAME_KEY + " = ?", new String[]{"last_notification"});
                        LOG.info("finished {}", type);
                        break;*/
                }
                if (interval > 0) {
                    while (nextFireTime < System.currentTimeMillis()) {
                        nextFireTime += interval;
                    }
                    LOG.info("next fire time is set to {} with interval {}", nextFireTime, interval);
                    ContentValues updateValues = new ContentValues();
                    updateValues.put(DbHelper.ScheduleTable.COLUMN_NAME_NEXT_FIRE_TIME, nextFireTime);
                    writableDatabase.update(DbHelper.ScheduleTable.TABLE_NAME, updateValues, DbHelper.ScheduleTable.COLUMN_NAME_LEVEL + " = ?", new String[]{Long.toString(level)});
                } else {
                    LOG.info("it's one time task because interval is {}, disabling task", interval);
                    ContentValues updateValues = new ContentValues();
                    updateValues.put(DbHelper.ScheduleTable.COLUMN_NAME_IS_ENABLED, 0);
                    writableDatabase.update(DbHelper.ScheduleTable.TABLE_NAME, updateValues, DbHelper.ScheduleTable.COLUMN_NAME_LEVEL + " = ?", new String[]{Long.toString(level)});
                }
            }
        }
        LOG.info("Scheduled Task stopping");
    }

    private void writeHeartBeatToDb() {
        class HeartBeatTable implements BaseColumns {
            static final String TABLE_NAME = "heart_beat";
            static final String COLUMN_NAME_TIME = "time";
            static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        }
        LOG.info("preparing to write heart beat to database");
        String collectDbName;
        try (Cursor cursorCollectDb = readableDatabase.query(DbHelper.CollectDbTable.TABLE_NAME, new String[]{DbHelper.CollectDbTable.COLUMN_NAME_NAME}, DbHelper.CollectDbTable.COLUMN_NAME_IS_USING + " = ?", new String[]{"1"}, null, null, null)) {
            if (cursorCollectDb.getCount() > 0) {
                cursorCollectDb.moveToFirst();
                collectDbName = cursorCollectDb.getString(cursorCollectDb.getColumnIndexOrThrow(DbHelper.CollectDbTable.COLUMN_NAME_NAME));
            } else {
                Log.i(TAG, "no collect database available");
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
        String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        values.put(HeartBeatTable.COLUMN_NAME_TIME, time);
        values.put(HeartBeatTable.COLUMN_NAME_TIMESTAMP, System.currentTimeMillis());
        db.insert(HeartBeatTable.TABLE_NAME, null, values);
        db.close();
        collectorDbHelper.close();
        LOG.info("write heart beat to database successfully");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mScheduledExecutorService != null) {
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
