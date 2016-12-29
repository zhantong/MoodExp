package cn.edu.nju.dislab.moodexp.collectors;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhantong on 2016/12/22.
 */

public class SensorsData {
    class Table implements BaseColumns {
        static final String TABLE_NAME = "sensors";
        static final String COLUMN_NAME_TYPE = "type";
        static final String COLUMN_NAME_VALUE_0 = "value_0";
        static final String COLUMN_NAME_VALUE_1 = "value_1";
        static final String COLUMN_NAME_VALUE_2 = "value_2";
        static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }
    static String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + Table.TABLE_NAME + " (" +
                    Table._ID + " INTEGER PRIMARY KEY," +
                    Table.COLUMN_NAME_TYPE + " INTEGER," +
                    Table.COLUMN_NAME_VALUE_0 + " REAL," +
                    Table.COLUMN_NAME_VALUE_1 + " REAL," +
                    Table.COLUMN_NAME_VALUE_2 + " REAL," +
                    Table.COLUMN_NAME_TIMESTAMP + " INTEGER)";
    private Map<Integer, ArrayList<Sensor>> sensors;

    private class Sensor {
        private long timestamp;
        private float[] values;

        public Sensor(long timestamp, float[] values) {
            this.timestamp = timestamp;
            this.values = values;
        }

        @Override
        public String toString() {
            return timestamp + " " + Arrays.toString(values);
        }
    }

    public SensorsData() {
        sensors = new HashMap<>();
    }

    public void put(int type, long timestamp, float[] values) {
        if (!sensors.containsKey(type)) {
            sensors.put(type, new ArrayList<Sensor>());
        }
        sensors.get(type).add(new Sensor(timestamp, values));
    }
    public static void DbInit(SQLiteDatabase db){
        if(db!=null){
            db.execSQL(SQL_CREATE_TABLE);
        }
    }
    public void toDb(SQLiteDatabase db) {
        if(db==null){
            return;
        }
        db.execSQL(SQL_CREATE_TABLE);
        for (Map.Entry<Integer, ArrayList<Sensor>> entry : sensors.entrySet()) {
            int type = entry.getKey();
            ArrayList<Sensor> result = entry.getValue();
            for (Sensor sensor : result) {
                ContentValues values = new ContentValues();
                values.put(Table.COLUMN_NAME_TYPE, type);
                values.put(Table.COLUMN_NAME_VALUE_0, sensor.values[0]);
                values.put(Table.COLUMN_NAME_VALUE_1, sensor.values[1]);
                values.put(Table.COLUMN_NAME_VALUE_2, sensor.values[2]);
                values.put(Table.COLUMN_NAME_TIMESTAMP, sensor.timestamp);
                db.insert(Table.TABLE_NAME, null, values);
            }
        }
    }

    @Override
    public String toString() {
        return sensors.toString();
    }
}
