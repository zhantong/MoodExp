package org.graduation.collector;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.format.Time;
import android.util.Log;

import org.graduation.database.DatabaseManager;
import org.graduation.healthylife.MainActivity;
import org.graduation.healthylife.MainApplication;
import org.graduation.slide.ThirdSlide;

import java.util.List;

public class WifiCollector implements ICollector {
    private static final String TAG = "WifiRecord";
    public static int day=0;

    public void collect()
    {
        Log.e("collect", "WiFi recording...");
        WifiManager wifiManager = (WifiManager) MainApplication.getContext()
                .getSystemService(Context.WIFI_SERVICE);
        boolean on = wifiManager.isWifiEnabled();
        if (!on)
        {
            //wifiManager.setWifiEnabled(true);

            Time t=new Time("GMT+8");
            t.setToNow(); // 取得系统时间。
            int year = t.year;
            int month = t.month;
            int date = t.monthDay;
            int hour = t.hour; // 0-23
            int minute = t.minute;
            int second = t.second;

            //String oldData= MainActivity.shared.getString("date","-1");

            if( day != date )
            {
                day=date;
                MainActivity.recvHandler.sendEmptyMessage(MainActivity.msgWlanRequest);
            }

            return ;
        }


        List<ScanResult> results=null;
        while(results==null||results.size()==0)
        {
            wifiManager.startScan();
            results = wifiManager.getScanResults();
        }
        if (!on)
        {
            wifiManager.setWifiEnabled(false);
        }
        DatabaseManager databaseManager = DatabaseManager.getDatabaseManager();
        for (ScanResult result : results)
        {
            Log.e(TAG, "WiFi ssid " + result.SSID);
            databaseManager.saveWifi(System.currentTimeMillis(), result.SSID,result.BSSID,result.level);
        }
    }

    @Override
    public void startCollect() {
        collect();
    }

    @Override
    public void stopCollect() {

    }
}
