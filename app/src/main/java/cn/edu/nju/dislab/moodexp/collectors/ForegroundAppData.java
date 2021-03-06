package cn.edu.nju.dislab.moodexp.collectors;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhantong on 2016/12/22.
 */

public class ForegroundAppData {
    static String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + Table.TABLE_NAME + " (" +
                    Table._ID + " INTEGER PRIMARY KEY," +
                    Table.COLUMN_NAME_PACKAGE_NAME + " TEXT," +
                    Table.COLUMN_NAME_TYPE + " TEXT," +
                    Table.COLUMN_NAME_TIMESTAMP + " INTEGER)";
    private List<ForegroundApp> foregroundApps;

    private static final Logger LOG = LoggerFactory.getLogger(ForegroundAppData.class);

    public ForegroundAppData() {
        foregroundApps = new ArrayList<>();
    }

    public static void DbInit(SQLiteDatabase db) {
        LOG.info("start init database");
        if (db != null) {
            db.execSQL(SQL_CREATE_TABLE);
        }
        LOG.info("finished init database");
    }

    public void put(String packageName, String type, long timestamp) {
        foregroundApps.add(new ForegroundApp(packageName, type, timestamp));
    }

    public void toDb(SQLiteDatabase db) {
        LOG.info("start write data to database");
        if (db == null) {
            return;
        }
        db.execSQL(SQL_CREATE_TABLE);
        for (ForegroundApp foregroundApp : foregroundApps) {
            ContentValues values = new ContentValues();
            values.put(Table.COLUMN_NAME_PACKAGE_NAME, foregroundApp.packageName);
            values.put(Table.COLUMN_NAME_TYPE, foregroundApp.type);
            values.put(Table.COLUMN_NAME_TIMESTAMP, foregroundApp.timestamp);
            db.insert(Table.TABLE_NAME, null, values);
        }
        LOG.info("finished write data to database");
    }

    @Override
    public String toString() {
        return foregroundApps.toString();
    }

    class Table implements BaseColumns {
        static final String TABLE_NAME = "foreground_app";
        static final String COLUMN_NAME_PACKAGE_NAME = "package_name";
        static final String COLUMN_NAME_TYPE = "type";
        static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }

    private class ForegroundApp {
        public String packageName;
        public String type;
        public long timestamp;

        public ForegroundApp(String packageName, String type, long timestamp) {
            this.packageName = packageName;
            this.type = type;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return timestamp + " " + packageName + " " + type;
        }
    }
}
