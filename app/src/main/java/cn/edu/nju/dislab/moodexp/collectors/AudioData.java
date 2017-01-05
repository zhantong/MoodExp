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

public class AudioData {
    static String SQL_CREATE_TABLE_AUDIO =
            "CREATE TABLE IF NOT EXISTS " + AudioTable.TABLE_NAME + " (" +
                    AudioTable._ID + " INTEGER PRIMARY KEY," +
                    AudioTable.COLUMN_NAME_AMPLITUDE + " REAL," +
                    AudioTable.COLUMN_NAME_TIMESTAMP + " INTEGER)";
    private List<Audio> audios;

    private static final Logger LOG = LoggerFactory.getLogger(AudioData.class);

    public AudioData() {
        audios = new ArrayList<>();
    }

    public static void DbInit(SQLiteDatabase db) {
        LOG.info("start init database");
        if (db != null) {
            db.execSQL(SQL_CREATE_TABLE_AUDIO);
        }
        LOG.info("finished init database");
    }

    public void put(long timestamp, double amplitude) {
        audios.add(new Audio(timestamp, amplitude));
    }

    public void toDb(SQLiteDatabase db) {
        LOG.info("start write data to database");
        if (db == null) {
            return;
        }
        db.execSQL(SQL_CREATE_TABLE_AUDIO);
        for (Audio audio : audios) {
            ContentValues values = new ContentValues();
            values.put(AudioTable.COLUMN_NAME_AMPLITUDE, audio.amplitude);
            values.put(AudioTable.COLUMN_NAME_TIMESTAMP, audio.timestamp);
            db.insert(AudioTable.TABLE_NAME, null, values);
        }
        LOG.info("finished write data to database");
    }

    @Override
    public String toString() {
        return audios.toString();
    }

    class AudioTable implements BaseColumns {
        static final String TABLE_NAME = "audio";
        static final String COLUMN_NAME_AMPLITUDE = "amplitude";
        static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }

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
}
