package cn.edu.nju.dislab.moodexp.collectors;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhantong on 2016/12/22.
 */

public class RunningAppData {
    class Table implements BaseColumns {
        static final String TABLE_NAME = "running_app";
        static final String COLUMN_NAME_PACKAGE_NAME = "package_name";
        static final String COLUMN_NAME_TYPE = "type";
        static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }
    static String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + Table.TABLE_NAME + " (" +
                    Table._ID + " INTEGER PRIMARY KEY," +
                    Table.COLUMN_NAME_PACKAGE_NAME + " TEXT," +
                    Table.COLUMN_NAME_TYPE + " TEXT," +
                    Table.COLUMN_NAME_TIMESTAMP + " INTEGER)";
    private List<RunningApp> runningApps;

    private class RunningApp {
        public String packageName;
        public String type;
        public long timestamp;

        public RunningApp(String packageName,String type, long timestamp) {
            this.packageName = packageName;
            this.type=type;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return timestamp + " " + packageName+" "+type;
        }
    }


    public RunningAppData() {
        runningApps = new ArrayList<>();
    }

    public void put(String packageName,String type, long timestamp) {
        runningApps.add(new RunningApp(packageName,type, timestamp));
    }
    public static void DbInit(SQLiteDatabase db){
        if(db!=null){
            db.execSQL(SQL_CREATE_TABLE);
        }
    }
    public void toDb(SQLiteDatabase db) {
        if(db==null){
            return;
        }

        db.execSQL(SQL_CREATE_TABLE);
        for (RunningApp runningApp : runningApps) {
            ContentValues values = new ContentValues();
            values.put(Table.COLUMN_NAME_PACKAGE_NAME, runningApp.packageName);
            values.put(Table.COLUMN_NAME_TYPE, runningApp.type);
            values.put(Table.COLUMN_NAME_TIMESTAMP, runningApp.timestamp);
            db.insert(Table.TABLE_NAME, null, values);
        }
    }

    @Override
    public String toString() {
        return runningApps.toString();
    }
}
