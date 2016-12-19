package org.graduation.collector;

import android.content.Context;
import android.os.Build;
import android.os.PowerManager;

import org.graduation.database.DatabaseManager;
import org.graduation.healthylife.MainApplication;

/**
 * Created by javan on 2016/6/10.
 */
public class ScreenCollector implements ICollector{
    @Override
    public void startCollect() {
        PowerManager pm=(PowerManager)MainApplication.getContext().getSystemService(Context.POWER_SERVICE);
        DatabaseManager dm=DatabaseManager.getDatabaseManager();
        if(Build.VERSION.SDK_INT>=20){
            if(pm.isInteractive()){
                dm.saveScreen(1);
            }
            else dm.saveScreen(0);
        }
        else{
            if(pm.isScreenOn()){
                dm.saveScreen(1);
            }
            else dm.saveScreen(0);
        }
    }

    @Override
    public void stopCollect() {

    }
}
