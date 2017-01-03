package cn.edu.nju.dislab.moodexp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by zhantong on 2016/12/23.
 */

public class CollectorDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    static String SQL_CREATE_TABLE_STATUS =
            "CREATE TABLE IF NOT EXISTS " + StatusTable.TABLE_NAME + " (" +
                    StatusTable._ID + " INTEGER PRIMARY KEY," +
                    StatusTable.COLUMN_NAME_TYPE + " TEXT," +
                    StatusTable.COLUMN_NAME_STATUS + " INTEGER," +
                    StatusTable.COLUMN_NAME_TIMESTAMP + " INTEGER)";

    public CollectorDbHelper(String name) {
        this(MainApplication.getContext(), name, null, DATABASE_VERSION);
    }

    public CollectorDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_STATUS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public class StatusTable implements BaseColumns {
        static final String TABLE_NAME = "status";
        static final String COLUMN_NAME_TYPE = "type";
        static final String COLUMN_NAME_STATUS = "status";
        static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }
}
