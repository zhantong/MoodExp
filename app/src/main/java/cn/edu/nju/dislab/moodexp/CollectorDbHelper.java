package cn.edu.nju.dislab.moodexp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhantong on 2016/12/23.
 */

public class CollectorDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public CollectorDbHelper(String name) {
        this(MainApplication.getContext(), name, null, DATABASE_VERSION);
    }

    public CollectorDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
