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
 * Created by javan on 2016/4/26.
 * 步数见函数getStep()
 * 原始数据见line67
 */
public class StepCollector implements ICollector {
    private static boolean isCollectOn=false;
    public static int currentStep = 0;
    private long lastTime=0;
    private static long MIN_TIME=100;
    public static final float SENSITIVITY = 10; // SENSITIVITY灵敏度

    private static final String TAG = "StepRecord";

    private float mLastValues[] = new float[3 * 2];
    private float mScale[] = new float[2];
    private float mYOffset;
    private static long start = 0;

    private static float mLastSensorValues[] = new float[3];

    /**
     * 最后加速度方向
     */
    private float mLastDirections[] = new float[3 * 2];
    private float mLastExtremes[][] = { new float[3 * 2], new float[3 * 2] };
    private float mLastDiff[] = new float[3 * 2];
    private int mLastMatch = -1;

    private static StepCollector self = new StepCollector();
    public static StepCollector getCollector()
    {
        return self;
    }

    private SensorManager sensorManager;
    private StepCollector(){

        int h = 480;
        mYOffset = h * 0.5f;
        mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
        sensorManager = (SensorManager) MainApplication.getContext()
                .getSystemService(Context.SENSOR_SERVICE);
        // 注册传感器，注册监听器
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);

        mLastSensorValues[0] = mLastSensorValues[1] = mLastSensorValues[2] = 0;
    }

    public void collect()
    {
        int step = getStep();
//        Log.d(TAG, "step: " + String.valueOf(step));
//        Log.d(TAG, "x: " + mLastSensorValues[0]
//                + ", y: " + mLastSensorValues[1]
//                + ", z: " + mLastSensorValues[2]);
        DatabaseManager.getDatabaseManager().saveAcc(
                System.currentTimeMillis(),
                step,
                mLastSensorValues[0],
                mLastSensorValues[1],
                mLastSensorValues[2]);
    }

    public static void resetStep(){currentStep=0;}

    //步数
    public int getStep(){

        return currentStep /2*3;
    }

    private SensorEventListener sensorEventListener=new SensorEventListener()
    {

        @Override
        public void onSensorChanged(SensorEvent event)
        {
            Sensor sensor = event.sensor;
            synchronized (this) {
                if (sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                {
                    mLastSensorValues[0] = event.values[0] * mScale[1];
                    mLastSensorValues[1] = event.values[1] * mScale[1];
                    mLastSensorValues[2] = event.values[2] * mScale[1];
                    /**
                     *原始数据是event.values[],三个数字代表三个方向的加速度
                     */
                    float vSum = 0;
                    for (int i = 0; i < 3; i++) {
                        final float v = mYOffset + event.values[i] * mScale[1];
                        vSum += v;
                    }
                    int k = 0;
                    float v = vSum / 3;

                    float direction = (v > mLastValues[k] ? 1
                            : (v < mLastValues[k] ? -1 : 0));
                    if (direction == -mLastDirections[k]) {
                        // Direction changed
                        int extType = (direction > 0 ? 0 : 1); // minumum or
                        // maximum?
                        mLastExtremes[extType][k] = mLastValues[k];
                        float diff = Math.abs(mLastExtremes[extType][k]
                                - mLastExtremes[1 - extType][k]);

                        if (diff > SENSITIVITY) {
                            boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
                            boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
                            boolean isNotContra = (mLastMatch != 1 - extType);

                            if (isAlmostAsLargeAsPrevious
                                    && isPreviousLargeEnough && isNotContra) {
                                long end = System.currentTimeMillis();
                                if (end - start > 500)
                                {// 此时判断为走了一步
                                    Log.i("step1", "current step:"+ currentStep);
                                    currentStep++;
                                    mLastMatch = extType;
                                    start = end;
                                }
                            } else {
                                mLastMatch = -1;
                            }
                        }
                        mLastDiff[k] = diff;
                    }
                    mLastDirections[k] = direction;
                    mLastValues[k] = v;
                    long current=System.currentTimeMillis();
                    if(current-lastTime>MIN_TIME && isCollectOn)
                    {
                        lastTime = current;
                        collect();
                    }
                }

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    public void startCollect() {
        isCollectOn=true;
    }

    @Override
    public void stopCollect() {
        isCollectOn=false;
    }
}
