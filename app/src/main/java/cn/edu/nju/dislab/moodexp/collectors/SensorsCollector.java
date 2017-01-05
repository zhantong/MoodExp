package cn.edu.nju.dislab.moodexp.collectors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.nju.dislab.moodexp.MainApplication;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by zhantong on 2016/12/22.
 */

public class SensorsCollector {
    private static final String TAG = "SensorCollector";
    private static final String[] PERMISSIONS = {};
    private final Object LOCK = new Object();
    private SensorsData result;
    private Context mContext;
    private int[] mTypeSensors;
    private SensorManager mSensorManager;
    private Map<Integer, Long> mMaxTimes;
    private Map<Integer, Long> mStartTimes;
    private Map<Integer, Boolean> mIsDone;
    private boolean mIsAllDone;
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            int type = event.sensor.getType();
            long timestamp = event.timestamp;
            if (!mStartTimes.containsKey(type)) {
                mStartTimes.put(type, timestamp);
            }
            if (!mIsDone.get(type)) {
                if (timestamp - mStartTimes.get(type) > mMaxTimes.get(type)) {
                    mIsAllDone = true;
                    mIsDone.put(type, true);
                } else {
                    result.put(type, event.timestamp, event.values.clone());
                }
            }
            if (mIsAllDone || (!mIsDone.containsValue(false))) {
                synchronized (LOCK) {
                    LOCK.notify();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private static final Logger LOG = LoggerFactory.getLogger(SensorsCollector.class);

    public SensorsCollector(int[] typeSensors, long[] maxTimes) {
        this(MainApplication.getContext(), typeSensors, maxTimes);
    }

    public SensorsCollector(Context context, int[] typeSensors, long[] maxTimes) {
        mContext = context;
        mTypeSensors = typeSensors;
        mSensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
        mStartTimes = new HashMap<>();
        mMaxTimes = new HashMap<>();
        for (int i = 0; i < typeSensors.length; i++) {
            int typeSensor = mTypeSensors[i];
            mMaxTimes.put(typeSensor, maxTimes[i]);
        }
    }

    public static String[] getPermissions() {
        return PERMISSIONS;
    }

    public int collect() {
        LOG.info("preparing to collect");
        result = new SensorsData();
        List<Sensor> sensors = new ArrayList<>();
        mIsDone = new HashMap<>();
        mIsAllDone = false;
        LOG.info("start collecting");
        for (int typeSensor : mTypeSensors) {
            Sensor sensor = mSensorManager.getDefaultSensor(typeSensor);
            if (sensor != null) {
                mIsDone.put(typeSensor, false);
                sensors.add(sensor);
                mSensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                LOG.info("sensor {} not exist", typeSensor);
            }
        }
        synchronized (LOCK) {
            try {
                LOCK.wait();
            } catch (InterruptedException e) {
                LOG.info("error interrupted {}", e);
                return Collector.COLLECT_FAILED;
            }
        }
        mSensorManager.unregisterListener(sensorEventListener);
        LOG.info("finished collect");
        return Collector.COLLECT_SUCCESS;
    }

    public SensorsData getResult() {
        return result;
    }
}
