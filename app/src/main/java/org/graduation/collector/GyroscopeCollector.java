package org.graduation.collector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.graduation.database.DatabaseManager;
import org.graduation.healthylife.MainApplication;

/**
 * Created by javan on 2016/6/9.
 */
public class GyroscopeCollector implements ICollector
{
    private static final String TAG="GyroscopeCollector";
    private SensorManager sensorManager;
    private static float gyroscope[]=new float[3];
    private long lastTime=0;
    private static long MIN_TIME=100;
    private static GyroscopeCollector self = new GyroscopeCollector();

    public static GyroscopeCollector getCollector()
    {
        return self;
    }

    private GyroscopeCollector(){
        sensorManager=(SensorManager) MainApplication.getContext()
                .getSystemService(Context.SENSOR_SERVICE);
    }
    public void collect() {
        //x,y,z三个方向的磁场强度
//        Log.d(TAG, String.valueOf(gyroscope[0]) + " " + gyroscope[1] + " " + gyroscope[2]);
        DatabaseManager.getDatabaseManager().saveGyroscope(gyroscope);
    }
    private SensorEventListener sensorEventListener=new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            long current=System.currentTimeMillis();
            if(current-lastTime>MIN_TIME) {
                lastTime=current;
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    gyroscope[0] = event.values[0];
                    gyroscope[1] = event.values[1];
                    gyroscope[2] = event.values[2];
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
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void stopCollect() {
        sensorManager.unregisterListener(sensorEventListener);
    }
}