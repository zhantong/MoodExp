package cn.edu.nju.dislab.moodexp.collectors;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhantong on 2016/12/23.
 */

public class WifiData {
    private List<ScanResult> scanResults;

    public WifiData() {
        scanResults = new ArrayList<>();
    }

    public void put(List<ScanResult> scanResults) {
        this.scanResults = scanResults;
    }

    public void toDb(SQLiteDatabase db) {
        class Table implements BaseColumns {
            static final String TABLE_NAME = "wifi";
            static final String COLUMN_NAME_BSSID = "bssid";
            static final String COLUMN_NAME_SSID = "ssid";
            static final String COLUMN_NAME_LEVEL = "level";
            static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        }
        String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + Table.TABLE_NAME + " (" +
                        Table._ID + " INTEGER PRIMARY KEY," +
                        Table.COLUMN_NAME_BSSID + " TEXT," +
                        Table.COLUMN_NAME_SSID + " TEXT," +
                        Table.COLUMN_NAME_LEVEL + " INTEGER," +
                        Table.COLUMN_NAME_TIMESTAMP + " INTEGER)";
        db.execSQL(SQL_CREATE_TABLE);
        for (ScanResult scanResult : scanResults) {
            ContentValues values = new ContentValues();
            values.put(Table.COLUMN_NAME_BSSID, scanResult.BSSID);
            values.put(Table.COLUMN_NAME_SSID, scanResult.SSID);
            values.put(Table.COLUMN_NAME_LEVEL, scanResult.level);
            values.put(Table.COLUMN_NAME_TIMESTAMP, scanResult.timestamp);
            db.insert(Table.TABLE_NAME, null, values);
        }
    }

    @Override
    public String toString() {
        return scanResults.toString();
    }
}
