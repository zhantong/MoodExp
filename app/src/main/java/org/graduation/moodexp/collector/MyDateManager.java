
package org.graduation.moodexp.collector;

import android.text.format.Time;

import org.graduation.moodexp.database.SharedPreferenceManager;
import org.graduation.moodexp.healthylife.MyDate;

public class MyDateManager
{
    public static MyDate baseDate=new MyDate("11","16","22","00");

    public static int seA=0,seB=1,seC=2,seD=3,seE=4;

    public static int getCurrentDateSection()
    {
        MyDate d=new MyDate(getMyDateString());
        int interval=Math.abs(d.theDaysCount-baseDate.theDaysCount);

        SharedPreferenceManager sm=SharedPreferenceManager.getManager();
        boolean morningSub=sm.getBoolean("morningSubmit",false);
        boolean afternoonSub=sm.getBoolean("afternoonSubmit",false);
        boolean eveningSub=sm.getBoolean("eveningSubmit",false);

        if(0<= interval && interval<=6)
        {
//            if(interval==6 && morningSub==true && afternoonSub==true && eveningSub==true)
//            {
//                return seB;
//            }
            return seA;
        }
        else if(7<=interval && interval<=13)
        {
//            if(interval==13 && morningSub==true && afternoonSub==true && eveningSub==true)
//            {
//                return seC;
//            }
            return seB;
        }
        else if(14<=interval && interval<=20)
        {
//            if(interval==20 && morningSub==true && afternoonSub==true && eveningSub==true)
//            {
//                return seD;
//            }
            return seC;
        }
        else if(21<=interval && interval<=27)
        {
//            if(interval==20 && morningSub==true && afternoonSub==true && eveningSub==true)
//            {
//                return seD;
//            }
            return seD;
        }
        else
        {
            return seE;
        }
    }

    public static MyDate getCurrentDate()
    {
        return new MyDate(getMyDateString());
    }

    public static int getIntervalDaysFromBase()
    {
        return getIntervalDays(baseDate,getCurrentDate());
    }

    public static int getIntervalDays(MyDate d1,MyDate d2)
    {
        return Math.abs(d1.theDaysCount-d2.theDaysCount);
    }

    public static String getMyDateString()
    {
        SharedPreferenceManager sm=SharedPreferenceManager.getManager();

        Time t=new Time();//"GMT+8"
        t.setToNow(); // 取得系统时间。
        int year = t.year;

        //int month=sm.getInt("testMonth",11)-1;
        //int day=sm.getInt("testDay",16);
        //int hour=sm.getInt("testDay",18);

        int month = t.month;
        int day = t.monthDay;
        int hour = t.hour;
        int minute = t.minute;
        int second = t.second;

        String myDate="";

        if(minute<10) myDate=(month+1)+"/"+day+" "+hour +":"+"0"+minute ;
        else myDate=(month+1)+"/"+day+" "+hour +":"+minute ;

        return myDate;
    }




    public MyDateManager(){}


}