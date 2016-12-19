package org.graduation.moodexp.healthylife;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import org.graduation.moodexp.appintro.AppIntro2;
import org.graduation.moodexp.collector.AudioCollector;
import org.graduation.moodexp.collector.ContactCollector;
import org.graduation.moodexp.collector.GpsCollector;
import org.graduation.moodexp.collector.ICollector;
import org.graduation.moodexp.collector.WifiCollector;
import org.graduation.moodexp.slide.FirstSlide;
import org.graduation.moodexp.slide.SecondSlide;
import org.graduation.moodexp.slide.ThirdSlide;

import java.util.ArrayList;
import java.util.List;

public class Guide extends AppIntro2 {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public static boolean isSubmit=false;
    List<ICollector> _collectorList;

    Handler audioHandler;
    int audio=1;

    @Override
    public void init(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        checkPermission();


        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = preferences.edit();

        String isFinish = preferences.getString("finish", "...");
        Log.e("test", isFinish);

        if (isFinish.length() != 0 && isFinish.compareTo("true") == 0)
        {
            Intent intent = new Intent();
            intent.setClass(Guide.this, MainActivity.class);
            Guide.this.startActivity(intent);
        }
        else
        {
            addSlide(new FirstSlide(), getApplicationContext());
            addSlide(new SecondSlide(), getApplicationContext());
            addSlide(new ThirdSlide(), getApplicationContext());

            _collectorList = new ArrayList<>();

            // if (ActivityCompat.checkSelfPermission(MainApplication.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            //   == PackageManager.PERMISSION_GRANTED)
            {
                _collectorList.add(GpsCollector.getCollector());
                Log.e("main collect","gps");
                _collectorList.add(new WifiCollector());
                Log.e("main collect","wifi");
            }

            //if (ActivityCompat.checkSelfPermission(MainApplication.getContext(), Manifest.permission.RECORD_AUDIO)
            // == PackageManager.PERMISSION_GRANTED)


            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Thread.sleep(10);
                        Looper.prepare();

                        for (ICollector g : _collectorList)
                        {
                            g.startCollect();
                            Log.e("collect","collect "+ System.currentTimeMillis());
                        }
                        new ContactCollector().collect();
                        Log.e("loop","1");


                        Log.e("loop","2");
                        List<String> requesting = new ArrayList<>();
                        if (ActivityCompat.checkSelfPermission(MainApplication.getContext(), Manifest.permission.RECORD_AUDIO)
                                != PackageManager.PERMISSION_GRANTED)
                        {
                            requesting.add(Manifest.permission.RECORD_AUDIO);
                            ActivityCompat.requestPermissions(Guide.this, requesting.toArray(new String[requesting.size()]), 0);
                            Log.e("permission","audio");
                        }

                        AudioDemo a = new AudioDemo();
                        a.statAudio();
                        Looper.loop();

                        //audioHandler.sendEmptyMessage(audio);

                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        for (ICollector g : _collectorList)
                        {
                            g.stopCollect();
                        }
                    }
                }
            }).start();

        }//else

        audioHandler=new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                Log.e("Guide handler","recv");
                if(msg.what==audio)
                {
                    _collectorList.add(new AudioCollector(1));
                    Log.e("main collect","audio");
                }
            }
        };


    }


    @Override
    public void onDonePressed()
    {
        // TODO Auto-generated method stub

        if(isSubmit==false)
        {
            Toast.makeText(MainApplication.getContext(), "请点击确定", Toast.LENGTH_SHORT).show();
            return ;
        }

        System.out.println("IntroActivity onDonePressed");

        editor.putString("finish","true");
        editor.commit();

        Intent intent=new Intent();
        intent.setClass(Guide.this, MainActivity.class);
        Guide.this.startActivity(intent);

    }

    private void checkPermission() {
        // Dynamically request permissions on Android 6.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            List<String> requesting = new ArrayList<>();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED)
            {
                requesting.add(Manifest.permission.READ_CONTACTS);

                List<String> permission= new ArrayList<>();
                permission.add(Manifest.permission.READ_CONTACTS);
                //ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), 0);
                Log.e("permission","contacts");
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
            {
                requesting.add(Manifest.permission.ACCESS_FINE_LOCATION);

                List<String> permission= new ArrayList<>();
                permission.add(Manifest.permission.ACCESS_FINE_LOCATION);
                // ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), 0);
                Log.e("permission","gps");
            }

//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
//                    != PackageManager.PERMISSION_GRANTED)
//            {
//                requesting.add(Manifest.permission.RECORD_AUDIO);
//                Log.e("permission","audio");
//            }



            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                    != PackageManager.PERMISSION_GRANTED)
            {
                requesting.add(Manifest.permission.READ_CALL_LOG);

                List<String> permission= new ArrayList<>();
                permission.add(Manifest.permission.READ_CALL_LOG);
                //ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), 0);
                Log.e("permission","call");
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                    != PackageManager.PERMISSION_GRANTED)
            {
                requesting.add(Manifest.permission.READ_SMS);

                List<String> permission= new ArrayList<>();
                permission.add(Manifest.permission.READ_SMS);
                //ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), 0);
                Log.e("permission","sms");
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                requesting.add(Manifest.permission.ACCESS_WIFI_STATE);

                List<String> permission= new ArrayList<>();
                permission.add(Manifest.permission.ACCESS_WIFI_STATE);
                //ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), 0);
                Log.e("permission","wifi");
            }

            if (requesting.isEmpty())
            {
                return;
            }
            ActivityCompat.requestPermissions(this, requesting.toArray(new String[requesting.size()]), 0);
        }
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
            Log.e("collect","audio start");
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
                    for(int j=0;j<10;++j)
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
                                mLock.wait(50);
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