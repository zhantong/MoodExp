package cn.edu.nju.dislab.moodexp.collectors;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhantong on 2016/12/22.
 */

public class CallLogData {
    private List<CallLog> callLogs;

    private class CallLog {
        public String number;
        public String type;
        public String date;
        public String duration;

        public CallLog(String number, String type, String date, String duration) {
            this.number = number;
            this.type = type;
            this.date = date;
            this.duration = duration;
        }

        @Override
        public String toString() {
            return date + " " + number + " " + type + " " + duration;
        }
    }


    public CallLogData() {
        callLogs = new ArrayList<>();
    }

    public void put(String number, String type, String date, String duration) {
        callLogs.add(new CallLog(number, type, date, duration));
    }

    public void toDb(SQLiteDatabase db) {
        class Table implements BaseColumns {
            static final String TABLE_NAME = "call_log";
            static final String COLUMN_NAME_NUMBER = "number";
            static final String COLUMN_NAME_TYPE = "type";
            static final String COLUMN_NAME_DATE = "date";
            static final String COLUMN_NAME_DURATION = "duration";
        }
        String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + Table.TABLE_NAME + " (" +
                        Table.COLUMN_NAME_NUMBER + " TEXT," +
                        Table.COLUMN_NAME_TYPE + " TEXT," +
                        Table.COLUMN_NAME_DATE + " TEXT," +
                        Table.COLUMN_NAME_DURATION + " TEXT," +
                        "PRIMARY KEY (" + Table.COLUMN_NAME_NUMBER + ", " + Table.COLUMN_NAME_DATE + ")" +
                        ")";
        db.execSQL(SQL_CREATE_TABLE);
        for (CallLog callLog : callLogs) {
            ContentValues values = new ContentValues();
            values.put(Table.COLUMN_NAME_NUMBER, callLog.number);
            values.put(Table.COLUMN_NAME_TYPE, callLog.type);
            values.put(Table.COLUMN_NAME_DATE, callLog.date);
            values.put(Table.COLUMN_NAME_DURATION, callLog.duration);
            db.insertWithOnConflict(Table.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        }
    }

    @Override
    public String toString() {
        return callLogs.toString();
    }
}
