package org.graduation.moodexp.collector;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.graduation.moodexp.database.DatabaseManager;
import org.graduation.moodexp.healthylife.MainApplication;

import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by javan on 2016/4/25.
 */
public class AudioCollector implements ICollector {
    private static final String TAG = "AudioRecord";
    AudioRecord mAudioRecord = null;
    private static boolean isGetVoiceRun=false;
    //private static boolean collectFlag=false;
    private static int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100 };
    private int bufferSize = 0;
    static AudioDemo audio;
    static boolean bIsAudioDemoCreate=false;

    int count=1;

    public AudioCollector(int c)
    {
        Log.e("collect","audio start");
        //if(bIsAudioDemoCreate==false)
        {
            audio = new AudioDemo();
            audio.statAudio();
            bIsAudioDemoCreate = true;
        }
        count=c;
    };

    /**
     * 判断程序是否在前台运行
     * @param context
     * @return
     */
    private boolean isAppIsInBackground(Context context)
    {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH)
        {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses)
            {
                //前台程序
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
                {
                    Log.d("appication","111111111111111"+processInfo.pkgList.length);
                    for (String activeProcess : processInfo.pkgList)
                    {
                        Log.d("appication","111111111111111"+activeProcess);
                        if (activeProcess.equals(context.getPackageName()))
                        {
                            isInBackground = false;
                        }
                    }
                }
            }
        }
        else
        {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public void collect()
    {
        Log.e("collect","audio:"+audio.getVolume()+"");
        DatabaseManager.getDatabaseManager().saveAudio(System.currentTimeMillis(), audio.getVolume());
    }


    @Override
    public void startCollect()
    {
        if (ActivityCompat.checkSelfPermission(MainApplication.getContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (isGetVoiceRun)
        {
            return;
        }
        //collectFlag=true;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                for(int i=0;i<count;++i)
                {
                    try
                    {
                        if(i==0) Thread.sleep(300);
                        else Thread.sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    Log.e("collect audio",""+i);
                    collect();
                }
            }
        }).start();
    }



    @Override
    public void stopCollect() {
        //collectFlag=false;
    }


    public class AudioDemo
    {

        private static final String TAG = "AudioRecord";
        static final int SAMPLE_RATE_IN_HZ = 8000;
        final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
                AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord mAudioRecord;
        boolean isGetVoiceRun;
        Object mLock;

        double volume=20;

        public AudioDemo()
        {
            mLock = new Object();
        }

        public double getVolume()
        {
            //Log.e("test",volume+"");
            if(volume>100) return 100;
            else  return volume;
        }

        public void statAudio()
        {
            if (ActivityCompat.checkSelfPermission(MainApplication.getContext(), Manifest.permission.RECORD_AUDIO)
             != PackageManager.PERMISSION_GRANTED)
            {
                return;
            }



            if (isGetVoiceRun)
            {
                Log.e(TAG, "还在录着呢");
                return;
            }
            mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
                    AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);

            if (mAudioRecord == null)
            {
                Log.e("sound", "mAudioRecord初始化失败");
            }
            isGetVoiceRun = true;

            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    mAudioRecord.startRecording();
                    short[] buffer = new short[BUFFER_SIZE];
                    for(int j=0;j<15;++j)
                    {
                        //r是实际读取的数据长度，一般而言r会小于buffersize
                        int r = mAudioRecord.read(buffer, 0, BUFFER_SIZE);
                        long v = 0;
                        // 将 buffer 内容取出，进行平方和运算
                        for (int i = 0; i < buffer.length; i++)
                        {
                            v += buffer[i] * buffer[i];
                        }
                        // 平方和除以数据总长度，得到音量大小。
                        double mean = v / (double) r;
                        volume = 10 * Math.log10(mean);
                        //Log.d("test", "分贝值:" + volume);
                        // 大概一秒十次
                        synchronized (mLock)
                        {
                            try {
                                mLock.wait(80);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    mAudioRecord.stop();
                    mAudioRecord.release();
                    mAudioRecord = null;


                }
            }).start();
        }
    }



}
