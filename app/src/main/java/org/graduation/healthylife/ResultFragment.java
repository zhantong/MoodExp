package org.graduation.healthylife;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.graduation.R;
import org.graduation.collector.ContactCollector;
import org.graduation.collector.MyDateManager;
import org.graduation.database.DatabaseManager;
import org.graduation.database.SharedPreferenceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.UUID;

public class ResultFragment extends Fragment
{//upload ftp and the final view

    int cnt;
    Cursor cursor;
    public ArrayList< Integer> stepList;
    public ArrayList< Float> volumeList;
    final int times=90;
    boolean result;

    Handler changeTextHandler;
    static int subSuccess=0,subFail=1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view=inflater.inflate(R.layout.content_result, container, false);
        final TextView tv= (TextView) view.findViewById(R.id.textView3);


        cnt= SharedPreferenceManager.getManager().getInt("emotionCnt",0);
        Log.d("resultFragment","emotion"+cnt);

        stepList=new ArrayList<Integer>();
        volumeList=new ArrayList<Float>();

        changeTextHandler=new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what==subSuccess)
                {

                    tv.setText("上传成功");
                }
                else if(msg.what==subFail)
                {
                    tv.setText("上传失败");
                }

            }
        };





        if(cnt<times) tv.setText("感谢提交，请不要立刻关闭网络");
        else tv.setText("您已经提交了足够的次数。\n您的ID是"+SharedPreferenceManager.getManager().getString("phoneID", null)
                +"。\n联系张啸(tobexiao1@gmail.com)确认上传成功后即可领取奖励。");

        result=false;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {

                saveDailyTime();
                readDB();
                saveDailyStep();
                saveDailyVolume();

                DatabaseManager manager = DatabaseManager.getDatabaseManager();

                try
                {
                    new ContactCollector().collect();

                    result=new FtpUploader().upload();

                    if(result==false)
                    {
                        changeTextHandler.sendEmptyMessage(subFail);
                    }

                    else if(result==true)
                    {
                        changeTextHandler.sendEmptyMessage(subSuccess);
                    }


                    //Toast.makeText(MainApplication.getContext(), "read contacts", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    e.printStackTrace();

                    new FtpUploader().upload();

                    //Toast.makeText(MainApplication.getContext(), "fail contacts", Toast.LENGTH_SHORT).show();
                }





            }
        }).start();


        SharedPreferenceManager sm=SharedPreferenceManager.getManager();
        boolean morningSub=sm.getBoolean("morningSubmit",false);
        boolean afternoonSub=sm.getBoolean("afternoonSubmit",false);
        boolean eveningSub=sm.getBoolean("eveningSubmit",false);


        return view;
    }



    public void saveDailyTime()
    {

        DatabaseManager manager = DatabaseManager.getDatabaseManager();
        manager.saveDailyTime();
    }

    public void saveDailyStep()
    {
        if(stepList.size()==0) return ;

        DatabaseManager manager = DatabaseManager.getDatabaseManager();
        manager.saveDailyStep(stepList.get(stepList.size()-1));
    }

    public void saveDailyVolume()
    {
        if(volumeList.size()==0) return;

        DatabaseManager manager = DatabaseManager.getDatabaseManager();
        double average=0;
        for(int i=0;i<volumeList.size();++i)
        {
            average+=volumeList.get(i);
        }
        average=average/(double)volumeList.size();
        manager.saveDailyVolume((float) average);

        Log.e("vol","saved");
    }
    public void readDB()
    {
        cursor=DatabaseManager.getDatabaseManager().queryAudio();
        //private static final String CREATE_AUDIO = "create table audio ("
        //        + "start_time integer,"
        //        + "volume real)";

        volumeList=new ArrayList<Float>();
        //volumeList.clear();
        while(cursor.moveToNext())
        {
            volumeList.add(cursor.getFloat(1));
        }

        cursor=DatabaseManager.getDatabaseManager().queryAcceleration();
        //private static final String CREATE_ACC = "create table acceleration ("
        //        + "start_time integer,"
        //        + "steps integer,"
        //        + "x_axis float,"
        //        + "y_axis float,"
        //        + "z_axis float)";

        stepList=new ArrayList<Integer>();
        //stepList.clear();
        while(cursor.moveToNext())
        {
            stepList.add(cursor.getInt(1));
        }
    }

}
