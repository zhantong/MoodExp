package cn.edu.nju.dislab.moodexp.collectors;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhantong on 2016/12/22.
 */

public class AudioData {
    private List<Audio> audios;

    private class Audio {
        public long timestamp;
        public double amplitude;

        public Audio(long timestamp, double amplitude) {
            this.timestamp = timestamp;
            this.amplitude = amplitude;
        }

        @Override
        public String toString() {
            return timestamp + " " + amplitude;
        }
    }

    public AudioData() {
        audios = new ArrayList<>();
    }

    public void put(long timestamp, double amplitude) {
        audios.add(new Audio(timestamp, amplitude));
    }

    public void toDb(SQLiteDatabase db) {
        class Table implements BaseColumns {
            static final String TABLE_NAME = "audio";
            static final String COLUMN_NAME_AMPLITUDE = "amplitude";
            static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        }
        String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + Table.TABLE_NAME + " (" +
                        Table._ID + " INTEGER PRIMARY KEY," +
                        Table.COLUMN_NAME_AMPLITUDE + " REAL," +
                        Table.COLUMN_NAME_TIMESTAMP + " INTEGER)";
        db.execSQL(SQL_CREATE_TABLE);
        for (Audio audio : audios) {
            ContentValues values = new ContentValues();
            values.put(Table.COLUMN_NAME_AMPLITUDE, audio.amplitude);
            values.put(Table.COLUMN_NAME_TIMESTAMP, audio.timestamp);
            db.insert(Table.TABLE_NAME, null, values);
        }
    }

    @Override
    public String toString() {
        return audios.toString();
    }
}
