package cn.edu.nju.dislab.moodexp.collectors;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by zhantong on 2016/12/22.
 */

public class ForegroundAppData {
    private String packageName;
    private long timestamp;

    public ForegroundAppData(String packageName, long timestamp) {
        this.packageName = packageName;
        this.timestamp = timestamp;
    }

    public void toDb(SQLiteDatabase db) {
        class Table implements BaseColumns {
            static final String TABLE_NAME = "foreground_app";
            static final String COLUMN_NAME_PACKAGE_NAME = "package_name";
            static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        }
        String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + Table.TABLE_NAME + " (" +
                        Table._ID + " INTEGER PRIMARY KEY," +
                        Table.COLUMN_NAME_PACKAGE_NAME + " TEXT," +
                        Table.COLUMN_NAME_TIMESTAMP + " INTEGER)";
        db.execSQL(SQL_CREATE_TABLE);
        ContentValues values = new ContentValues();
        values.put(Table.COLUMN_NAME_PACKAGE_NAME, packageName);
        values.put(Table.COLUMN_NAME_TIMESTAMP, timestamp);
        db.insert(Table.TABLE_NAME, null, values);
    }

    @Override
    public String toString() {
        return timestamp + " " + packageName;
    }
}
