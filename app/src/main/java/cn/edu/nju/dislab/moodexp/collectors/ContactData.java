package cn.edu.nju.dislab.moodexp.collectors;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhantong on 2016/12/22.
 */

public class ContactData {
    static String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + Table.TABLE_NAME + " (" +
                    Table.COLUMN_NAME_NAME + " INTEGER," +
                    Table.COLUMN_NAME_NUMBER + " INTEGER," +
                    "PRIMARY KEY (" + Table.COLUMN_NAME_NAME + ", " + Table.COLUMN_NAME_NUMBER + ")" +
                    ")";
    private List<Contact> contacts;

    public ContactData() {
        contacts = new ArrayList<>();
    }

    public static void DbInit(SQLiteDatabase db) {
        if (db != null) {
            db.execSQL(SQL_CREATE_TABLE);
        }
    }

    public void put(String name, String number) {
        contacts.add(new Contact(name, number));
    }

    public void toDb(SQLiteDatabase db) {
        if (db == null) {
            return;
        }
        db.execSQL(SQL_CREATE_TABLE);
        for (Contact contact : contacts) {
            ContentValues values = new ContentValues();
            values.put(Table.COLUMN_NAME_NAME, contact.name == null ? null : contact.name.hashCode());
            values.put(Table.COLUMN_NAME_NUMBER, contact.number == null ? null : contact.number.hashCode());
            db.insertWithOnConflict(Table.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        }
    }

    @Override
    public String toString() {
        return contacts.toString();
    }

    class Table implements BaseColumns {
        static final String TABLE_NAME = "contact";
        static final String COLUMN_NAME_NAME = "name";
        static final String COLUMN_NAME_NUMBER = "number";
    }

    private class Contact {
        public String name;
        public String number;

        public Contact(String name, String number) {
            this.name = name;
            this.number = number;
        }

        @Override
        public String toString() {
            return name + " " + number;
        }
    }
}
