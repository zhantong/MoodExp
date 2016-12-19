package org.graduation.moodexp.collector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.graduation.moodexp.database.DatabaseManager;
import org.graduation.moodexp.healthylife.MainApplication;

/**
 * Created by javan on 2016/6/9.
 * 硬件上一般没有独立的磁力传感器，磁力数据由电子罗盘传感器提供
 */

public class MagneticCollector implements ICollector {
    private static final String TAG="MagneticCollector";
    private SensorManager sensorManager;
    private static float magnetic[]=new float[3];
    private long lastTime=0;
    private static long MIN_TIME=100;

    private static MagneticCollector self = new MagneticCollector();
    public static MagneticCollector getCollector() {
        return self;
    }

    private MagneticCollector(){
        sensorManager=(SensorManager) MainApplication.getContext()
                .getSystemService(Context.SENSOR_SERVICE);
    }
    public void collect() {
        //x,y,z三个方向的磁场强度
//        Log.d(TAG, String.valueOf(magnetic[0])+" "+magnetic[1]+" "+magnetic[2]);
        DatabaseManager.getDatabaseManager().saveMagnetic(magnetic);
    }
    private SensorEventListener sensorEventListener=new SensorEventListener()
    {
        @Override
        public void onSensorChanged(SensorEvent event)
        {
            long current=System.currentTimeMillis();
            if(current-lastTime>MIN_TIME) {
                lastTime = current;
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    magnetic[0] = event.values[0];
                    magnetic[1] = event.values[1];
                    magnetic[2] = event.values[2];
                    collect();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    public void startCollect()
    {
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void stopCollect() {
        sensorManager.unregisterListener(sensorEventListener);
    }
}
