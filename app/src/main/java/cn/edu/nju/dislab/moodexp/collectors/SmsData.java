package cn.edu.nju.dislab.moodexp.collectors;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhantong on 2016/12/22.
 */

public class SmsData {
    private List<Sms> smses;

    private class Sms {
        public String address;
        public String type;
        public String date;
        public String person;

        public Sms(String address, String type, String date, String person) {
            this.address = address;
            this.type = type;
            this.date = date;
            this.person = person;
        }

        @Override
        public String toString() {
            return address + " " + type + " " + date + " " + person;
        }
    }


    public SmsData() {
        smses = new ArrayList<>();
    }

    public void put(String address, String type, String date, String person) {
        smses.add(new Sms(address, type, date, person));
    }

    public void toDb(SQLiteDatabase db) {
        class Table implements BaseColumns {
            static final String TABLE_NAME = "sms";
            static final String COLUMN_NAME_ADDRESS = "address";
            static final String COLUMN_NAME_TYPE = "type";
            static final String COLUMN_NAME_DATE = "date";
            static final String COLUMN_NAME_PERSON = "person";
        }
        String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + Table.TABLE_NAME + " (" +
                        Table.COLUMN_NAME_ADDRESS + " TEXT," +
                        Table.COLUMN_NAME_TYPE + " TEXT," +
                        Table.COLUMN_NAME_DATE + " TEXT," +
                        Table.COLUMN_NAME_PERSON + " TEXT," +
                        "PRIMARY KEY (" + Table.COLUMN_NAME_ADDRESS + ", " + Table.COLUMN_NAME_DATE + ")" +
                        ")";
        db.execSQL(SQL_CREATE_TABLE);
        for (Sms sms : smses) {
            ContentValues values = new ContentValues();
            values.put(Table.COLUMN_NAME_ADDRESS, sms.address);
            values.put(Table.COLUMN_NAME_TYPE, sms.type);
            values.put(Table.COLUMN_NAME_DATE, sms.date);
            values.put(Table.COLUMN_NAME_PERSON, sms.person);
            db.insertWithOnConflict(Table.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        }
    }

    @Override
    public String toString() {
        return smses.toString();
    }
}
