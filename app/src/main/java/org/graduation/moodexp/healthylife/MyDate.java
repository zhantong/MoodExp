
package org.graduation.moodexp.healthylife;


public class MyDate
{
    public String month;
    public String day;
    public String hour;
    public String minute;

    public int theDaysCount;

    public void setTheDaysCount()
    {
        if(Integer.valueOf(month)==11)
        {
            theDaysCount=Integer.valueOf(day);
        }
        else
        {
            theDaysCount=30+Integer.valueOf(day);
        }
    }

    public MyDate(String mon,String d,String h,String min)
    {
        month=mon;day=d;hour=h;minute=min;
        setTheDaysCount();
    }

    public MyDate()
    {
        month="";day="";hour="";minute="";
    }

    public MyDate(String d)
    {
        setDate(d);

    }


    public void setDate(String date)
    {
        String s1[]=date.split(" ");
        String s2[]=s1[0].split("/");
        String s3[]=s1[1].split(":");

        month=s2[0];day=s2[1];
        hour=s3[0];minute=s3[1];

        setTheDaysCount();
    }


}