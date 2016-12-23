package cn.edu.nju.dislab.moodexp.collectors;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhantong on 2016/12/23.
 */

public class PhoneData {
    private Map<String, String> data;

    public PhoneData() {
        data = new HashMap<>();
    }

    public void put(String key, String value) {
        data.put(key, value);
    }

    public void toDb(SQLiteDatabase db) {
        class Table implements BaseColumns {
            static final String TABLE_NAME = "phone";
            static final String COLUMN_NAME_NAME = "name";
            static final String COLUMN_NAME_VALUE = "value";
        }
        String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + Table.TABLE_NAME + " (" +
                        Table._ID + " INTEGER PRIMARY KEY," +
                        Table.COLUMN_NAME_NAME + " TEXT," +
                        Table.COLUMN_NAME_VALUE + " TEXT)";
        db.execSQL(SQL_CREATE_TABLE);
        for (Map.Entry<String, String> entry : data.entrySet()) {
            ContentValues values = new ContentValues();
            values.put(Table.COLUMN_NAME_NAME, entry.getKey());
            values.put(Table.COLUMN_NAME_VALUE, entry.getValue());
            db.insert(Table.TABLE_NAME, null, values);
        }
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
