package cn.edu.nju.dislab.moodexp.collectors;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.amap.api.location.AMapLocation;

/**
 * Created by zhantong on 2016/12/23.
 */

public class LocationData {
    private AMapLocation aMapLocation;

    public LocationData(AMapLocation aMapLocation) {
        this.aMapLocation = aMapLocation;
    }

    public AMapLocation getLocation() {
        return aMapLocation;
    }

    public void toDb(SQLiteDatabase db) {
        class Table implements BaseColumns {
            static final String TABLE_NAME = "location";
            static final String COLUMN_NAME_TYPE = "type";
            static final String COLUMN_NAME_LATITUDE = "latitude";
            static final String COLUMN_NAME_LONGITUDE = "longitude";
            static final String COLUMN_NAME_ACCURACY = "accuracy";
            static final String COLUMN_NAME_COUNTRY = "country";
            static final String COLUMN_NAME_PROVINCE = "province";
            static final String COLUMN_NAME_CITY = "city";
            static final String COLUMN_NAME_DISTRICT = "district";
            static final String COLUMN_NAME_STREET = "street";
            static final String COLUMN_NAME_STREET_NUMBER = "street_number";
            static final String COLUMN_NAME_CITY_CODE = "city_code";
            static final String COLUMN_NAME_ADDRESS_CODE = "address_code";
            static final String COLUMN_NAME_BUILDING_ID = "building_id";
            static final String COLUMN_NAME_FLOOR = "floor";
            static final String COLUMN_NAME_TIME = "time";
            static final String COLUMN_NAME_AOI = "aoi";
        }
        String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + Table.TABLE_NAME + " (" +
                        Table._ID + " INTEGER PRIMARY KEY," +
                        Table.COLUMN_NAME_TYPE + " INTEGER," +
                        Table.COLUMN_NAME_LATITUDE + " REAL," +
                        Table.COLUMN_NAME_LONGITUDE + " REAL," +
                        Table.COLUMN_NAME_ACCURACY + " REAL," +
                        Table.COLUMN_NAME_COUNTRY + " TEXT," +
                        Table.COLUMN_NAME_PROVINCE + " TEXT," +
                        Table.COLUMN_NAME_CITY + " TEXT," +
                        Table.COLUMN_NAME_DISTRICT + " TEXT," +
                        Table.COLUMN_NAME_STREET + " TEXT," +
                        Table.COLUMN_NAME_STREET_NUMBER + " TEXT," +
                        Table.COLUMN_NAME_CITY_CODE + " TEXT," +
                        Table.COLUMN_NAME_ADDRESS_CODE + " TEXT," +
                        Table.COLUMN_NAME_BUILDING_ID + " TEXT," +
                        Table.COLUMN_NAME_FLOOR + " TEXT," +
                        Table.COLUMN_NAME_TIME + " INTEGER," +
                        Table.COLUMN_NAME_AOI + " TEXT)";
        db.execSQL(SQL_CREATE_TABLE);
        ContentValues values = new ContentValues();
        values.put(Table.COLUMN_NAME_TYPE, aMapLocation.getLocationType());
        values.put(Table.COLUMN_NAME_LATITUDE, aMapLocation.getLatitude());
        values.put(Table.COLUMN_NAME_LONGITUDE, aMapLocation.getLongitude());
        values.put(Table.COLUMN_NAME_ACCURACY, aMapLocation.getAccuracy());
        values.put(Table.COLUMN_NAME_COUNTRY, aMapLocation.getCountry());
        values.put(Table.COLUMN_NAME_PROVINCE, aMapLocation.getProvince());
        values.put(Table.COLUMN_NAME_CITY, aMapLocation.getCity());
        values.put(Table.COLUMN_NAME_DISTRICT, aMapLocation.getDistrict());
        values.put(Table.COLUMN_NAME_STREET, aMapLocation.getStreet());
        values.put(Table.COLUMN_NAME_STREET_NUMBER, aMapLocation.getStreetNum());
        values.put(Table.COLUMN_NAME_CITY_CODE, aMapLocation.getCityCode());
        values.put(Table.COLUMN_NAME_ADDRESS_CODE, aMapLocation.getAdCode());
        values.put(Table.COLUMN_NAME_BUILDING_ID, aMapLocation.getBuildingId());
        values.put(Table.COLUMN_NAME_FLOOR, aMapLocation.getFloor());
        values.put(Table.COLUMN_NAME_TIME, aMapLocation.getTime());
        values.put(Table.COLUMN_NAME_AOI, aMapLocation.getAoiName());
        db.insert(Table.TABLE_NAME, null, values);
    }

    @Override
    public String toString() {
        return aMapLocation.toString();
    }
}
