package org.graduation.moodexp.collector;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import org.graduation.moodexp.database.DatabaseManager;
import org.graduation.moodexp.healthylife.MainActivity;
import org.graduation.moodexp.healthylife.MainApplication;

public class GpsCollector implements ICollector
{
    private static final String TAG = "GPSRecord";
    private LocationManager manager = null;
    private Location location = null;
    public static int day=0;


    public static GpsCollector self = new GpsCollector();

    //SharedPreferences shared;
    //SharedPreferences.Editor editor;

    public static GpsCollector getCollector()
    {


        return self;
    }

    private GpsCollector() {
        manager = (LocationManager) MainApplication.getContext()
                .getSystemService(Context.LOCATION_SERVICE);
    }
    public void collect()
    {
        System.out.println("collect gps");
        Log.e(TAG, "location: altitude: " + (location == null ? 0 : location.getAltitude()) + ", "
                + "longitude: " + (location == null ? 0 : location.getLongitude()) + ", "
                + "latitude: " + (location == null ? 0 : location.getLatitude()));
//        try
//        {
//            location=manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        }catch (Exception e){}


        if (location == null)
        {
            DatabaseManager.getDatabaseManager().saveLocation(0, 0, 0);
        }
        else
        {
            DatabaseManager.getDatabaseManager().saveLocation(
                    location.getAltitude(), location.getLongitude(), location.getLatitude());
        }
    }

    private LocationListener listener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location loc) {
            location = loc;
            collect();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider)
        {
            //Toast.makeText(MainApplication.getContext(), "请激活GPS", Toast.LENGTH_SHORT).show();

            //Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。

            //ThirdSlide.shared

            Time t=new Time("GMT+8");
            t.setToNow(); // 取得系统时间。
            int year = t.year;
            int month = t.month;
            int date = t.monthDay;
            int hour = t.hour; // 0-23
            int minute = t.minute;
            int second = t.second;

            //String oldData=MainActivity.shared.getString("date","");

            if( day != date )
            {
                day=date;
                MainActivity.recvHandler.sendEmptyMessage(MainActivity.msgGpsRequest);
            }

            //MainApplication.getContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    //.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));//open location view
        }
    };

    @Override
    public void startCollect()
    {
        try
        {
            if (ActivityCompat.checkSelfPermission(MainApplication.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, listener);
            location=manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            collect();

            Log.e(TAG, "location: altitude: " + (location == null ? 0 : location.getAltitude()) + ", "
                    + "longitude: " + (location == null ? 0 : location.getLongitude()) + ", "
                    + "latitude: " + (location == null ? 0 : location.getLatitude()));

            if (location == null)
            {
                DatabaseManager.getDatabaseManager().saveLocation(0, 0, 0);
            }
            else
            {
                DatabaseManager.getDatabaseManager().saveLocation(
                        location.getAltitude(), location.getLongitude(), location.getLatitude());
            }

        }
        catch (SecurityException e)
        {
            Toast.makeText(MainApplication.getContext(), "请授予我们权限", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void stopCollect()
    {
        if ( Build.VERSION.SDK_INT >= 23)
        {
            MainApplication.getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(manager!=null)
        {
            manager.removeUpdates(listener);
        }

    }
}
