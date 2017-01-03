package cn.edu.nju.dislab.moodexp.collectors;

import android.Manifest;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import cn.edu.nju.dislab.moodexp.EasyPermissions;

/**
 * Created by zhantong on 2016/12/21.
 */

public class AudioCollector {
    private static final String TAG = "AudioCollector";
    private static final String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO};
    private static final int POLL_INTERVAL = 100;
    private static final int MAX_TICKS = 50;
    private final Object LOCK = new Object();
    private Handler mHandler;
    private MediaRecorder mRecorder;
    private int mTickCount;
    private AudioData result;
    private Runnable mPollTask = new Runnable() {
        @Override
        public void run() {
            double amp = getAmplitude();
            result.put(System.currentTimeMillis(), amp);
            mTickCount++;
            if (mTickCount > MAX_TICKS) {
                stop();
            } else {
                mHandler.postDelayed(mPollTask, POLL_INTERVAL);
            }
        }
    };

    public AudioCollector() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static String[] getPermissions() {
        return PERMISSIONS;
    }

    private void stop() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        synchronized (LOCK) {
            LOCK.notify();
        }
    }

    public int collect() {
        if (!EasyPermissions.hasPermissions(PERMISSIONS)) {
            return Collector.NO_PERMISSION;
        }
        try {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            mRecorder.prepare();
            mRecorder.start();
        } catch (Exception e) {
            Log.i(TAG, "unable to record audio");
            e.printStackTrace();
            return Collector.NO_PERMISSION;
        }
        mTickCount = 0;
        result = new AudioData();
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
        synchronized (LOCK) {
            try {
                LOCK.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return Collector.COLLECT_FAILED;
            }
        }
        return Collector.COLLECT_SUCCESS;
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return (mRecorder.getMaxAmplitude() / 2700.0);
        else
            return 0;
    }

    public AudioData getResult() {
        return result;
    }
}
