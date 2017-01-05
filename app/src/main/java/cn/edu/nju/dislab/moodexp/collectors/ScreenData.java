package cn.edu.nju.dislab.moodexp.collectors;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhantong on 2016/12/22.
 */

public class ScreenData {
    static String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + Table.TABLE_NAME + " (" +
                    Table._ID + " INTEGER PRIMARY KEY," +
                    Table.COLUMN_NAME_IS_SCREEN_ON + " INTEGER," +
                    Table.COLUMN_NAME_TIMESTAMP + " INTEGER)";
    private boolean isScreenOn;
    private long timestamp;

    private static final Logger LOG = LoggerFactory.getLogger(ScreenData.class);

    public ScreenData(boolean isScreenOn, long timestamp) {
        this.isScreenOn = isScreenOn;
        this.timestamp = timestamp;
    }

    public static void DbInit(SQLiteDatabase db) {
        LOG.info("start init database");
        if (db != null) {
            db.execSQL(SQL_CREATE_TABLE);
        }
        LOG.info("finished init database");
    }

    public void toDb(SQLiteDatabase db) {
        LOG.info("start write data to database");
        if (db == null) {
            return;
        }
        db.execSQL(SQL_CREATE_TABLE);
        ContentValues values = new ContentValues();
        values.put(Table.COLUMN_NAME_IS_SCREEN_ON, isScreenOn ? 1 : 0);
        values.put(Table.COLUMN_NAME_TIMESTAMP, timestamp);
        db.insert(Table.TABLE_NAME, null, values);
        LOG.info("finished write data to database");
    }

    @Override
    public String toString() {
        return timestamp + " " + isScreenOn;
    }

    class Table implements BaseColumns {
        static final String TABLE_NAME = "screen";
        static final String COLUMN_NAME_IS_SCREEN_ON = "is_screen_on";
        static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }
}
