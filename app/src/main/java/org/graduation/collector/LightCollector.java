package org.graduation.collector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import org.graduation.database.DatabaseManager;
import org.graduation.healthylife.MainApplication;

/**
 * Created by javan on 2016/4/25.
 */
public class LightCollector implements ICollector {
    private static final String TAG = "LightRecord";
    private SensorManager sensorManager;
    private long lastTime=0;
    private static long MIN_TIME=100;
    float light;

    private static LightCollector self = new LightCollector();
    public static LightCollector getCollector() {
        return self;
    }

    private LightCollector(){
        sensorManager=(SensorManager) MainApplication.getContext()
                .getSystemService(Context.SENSOR_SERVICE);
    }
    public void collect() {
        //这是光照强度
//        Log.d(TAG, String.valueOf(light));
        DatabaseManager.getDatabaseManager().saveLight(System.currentTimeMillis(), light);
    }
    private SensorEventListener sensorEventListener=new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            long current=System.currentTimeMillis();
            if(current-lastTime>MIN_TIME) {
                lastTime = current;
                if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                    light = event.values[0];
                    collect();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    public void startCollect() {
        Log.d(TAG,"startCollect");
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void stopCollect() {
        Log.d(TAG, "stopCollect");
        sensorManager.unregisterListener(sensorEventListener);
    }
}
