package org.graduation.healthylife;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import org.graduation.R;
import org.graduation.database.DatabaseManager;

//private static final String CREATE_EMOTION = "create table emotion ("
//        + "eno integer,"
//        + "happiness integer,"
//        + "sadness integer,"
//        + "anger integer,"
//        + "surprise integer,"
//        + "fear integer,"
//        + "disgust integer,"
//        + "time integer)";


public class ChartActivity extends Activity
{
    public static int emotionNum=6;

    public LineChart stepLineChart;
    public ArrayList<String> stepLineXVals;
    public ArrayList<Entry> stepLineYVals;
    public LineDataSet stepLineDataSet;
    public LineData stepLineData;

    public LineChart volumeLineChart;
    public ArrayList<String> volumeLineXVals;
    public ArrayList<Entry> volumeLineYVals;
    public LineDataSet volumeLineDataSet;
    public LineData volumeLineData;

    //private LineChart lineChart;
    //private LineData lineData;
    //private ArrayList<String> lineXVals;
    //public ArrayList<LineDataSet> lineDataSetList;
    //private LineDataSet lineDataSet[];
    //private ArrayList< ArrayList<Entry> > lineYVals;

    private BarChart barChart;
    private BarData barData;
    private ArrayList<String> barXVals;
    public ArrayList<BarDataSet> barDataSetList;
    private BarDataSet barDataSet[];
    private ArrayList< ArrayList<BarEntry> > barYVals;


    public RadarChart radarChart;
    public ArrayList<String> radarXVals;
    public ArrayList<Entry> radarYVals;
    public RadarDataSet radarDataSet;
    public RadarData radarData;

    public ArrayList< MyEmotionSet > emotionList;
    public ArrayList< Integer> stepList;
    public ArrayList< Float> volumeList;
    public ArrayList< String> dateList;

    Cursor cursor;
    public int cnt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        	WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_chart);

        cursor= DatabaseManager.getDatabaseManager().queryEmotion();

        //lineChart=(LineChart)findViewById(R.id.lineChart);
        //lineDataSetList=new ArrayList<LineDataSet>();

        barChart=(BarChart)findViewById(R.id.barChart);
        barDataSetList=new ArrayList<BarDataSet>();

        radarChart=(RadarChart)findViewById(R.id.radarChart);
        stepLineChart=(LineChart)findViewById(R.id.stepLineChart);
        volumeLineChart=(LineChart)findViewById(R.id.volumeLineChart);

        emotionList=new ArrayList<MyEmotionSet>();
        stepList=new ArrayList<Integer>();
        volumeList=new ArrayList<Float>();
        dateList=new ArrayList<String>();

        getDBEmotionValue();

        //Log.e("chart","1");

        setBarChart();//Log.e("chart","2");
        setRadarChart();//Log.e("chart","3");
        setStepLineChart();//Log.e("chart","4");
        setVolumeLineChart();//Log.e("chart","5");


    }

    public void getDBEmotionValue()
    {
        Cursor cursor= DatabaseManager.getDatabaseManager().queryEmotion();
        cnt=0;

        while(cursor.moveToNext())
        {
            emotionList.add(new MyEmotionSet());
            //text+="  "+cnt;
            for(int i=1;i<=emotionNum;i++)
            {
                emotionList.get(cnt).emotion[i-1]=cursor.getInt(i);
            }
            cnt++;
        }

        cursor=DatabaseManager.getDatabaseManager().queryDailyVolume();
//        private static final String CREATE_DAILY_VOLUME="create table dailyVolume ("
//                +"time varchar(80),"
//                +"volume float)";
        while(cursor.moveToNext())
        {
            volumeList.add(cursor.getFloat(1));
        }

        cursor=DatabaseManager.getDatabaseManager().queryDailyStep();
//        private static final String CREATE_DAILY_STEP="create table dailyStep ("
//                +"time varchar(80),"
//                +"stepCount integer)";
        while(cursor.moveToNext())
        {
            stepList.add(cursor.getInt(1));
        }

        cursor=DatabaseManager.getDatabaseManager().queryDailyTime();
//        private static final String CREATE_DAILY_TIME="create table dailyTime ("
//                +"date varchar(80))";
        while(cursor.moveToNext())
        {
            dateList.add(cursor.getString(0));
        }

    }

    public double getLineAverageLevel(int index)
    {
        double sum=0;

        for(int i=0;i<cnt;++i)
        {
            switch(index)
            {
                case 0:
                    sum+=emotionList.get(i).emotion[index];
                    break;

                case 1:
                    sum+=emotionList.get(i).emotion[index];
                    break;

                case 2:
                    sum+=emotionList.get(i).emotion[index];
                    break;

                case 3:
                    sum+=emotionList.get(i).emotion[index];
                    break;

                case 4:
                    sum+=emotionList.get(i).emotion[index];
                    break;

                case 5:
                    sum+=emotionList.get(i).emotion[index];
                    break;

                default:
                    break;
            }//switch(j)
        }//for(int i=0;i<MainActivity.userEmotionList.get(MainActivity.selectedDBIndex).happinessList.size();++i)

        return sum/((double)cnt );
    }

    public void setBarChart()
    {
        barXVals=new ArrayList<String>();//横坐标描述词
        for(int i=0;i<cnt;i++)
        {
            //barXVals.add(""+i);
            barXVals.add(dateList.get(i));
        }

        barYVals=new ArrayList< ArrayList<BarEntry> >();
        for(int i=0;i<emotionNum;++i)
        {
            barYVals.add( new ArrayList<BarEntry>() );
        }
        float level = 0;
        for(int j=0;j<emotionNum;++j)
        {
            for(int i=0;i<cnt;i++)
            {
                switch(j)
                {
                    case 0:
                        level=emotionList.get(i).emotion[j];
                        break;

                    case 1:
                        level=emotionList.get(i).emotion[j];
                        break;

                    case 2:
                        level=emotionList.get(i).emotion[j];
                        break;

                    case 3:
                        level=emotionList.get(i).emotion[j];
                        break;

                    case 4:
                        level=emotionList.get(i).emotion[j];
                        break;

                    case 5:
                        level=emotionList.get(i).emotion[j];
                        break;

                    default:
                        break;
                }//switch(j)
                barYVals.get(j).add(new BarEntry(level,i));

            }//for(int i=0;i<MainActivity.userEmotionList.get(MainActivity.selectedDBIndex).happinessList.size();i++)

        }//for(int j=0;j<emotionNum;++j)

        barDataSet=new BarDataSet[emotionNum];
        for(int i=0;i<emotionNum;++i)
        {
            switch(i)
            {
                case 0:
                    barDataSet[i]=new BarDataSet(barYVals.get(i),"Happiness");
                    barDataSet[i].setColor(ColorTemplate.JOYFUL_COLORS[0]);
                    break;

                case 1:
                    barDataSet[i]=new BarDataSet(barYVals.get(i),"Sadness");
                    barDataSet[i].setColor(ColorTemplate.JOYFUL_COLORS[1]);
                    break;

                case 2:
                    barDataSet[i]=new BarDataSet(barYVals.get(i),"Anger");
                    barDataSet[i].setColor(ColorTemplate.JOYFUL_COLORS[2]);
                    break;

                case 3:
                    barDataSet[i]=new BarDataSet(barYVals.get(i),"Surprise");
                    barDataSet[i].setColor(ColorTemplate.JOYFUL_COLORS[3]);
                    break;

                case 4:
                    barDataSet[i]=new BarDataSet(barYVals.get(i),"Fear");
                    barDataSet[i].setColor(ColorTemplate.JOYFUL_COLORS[4]);
                    break;

                case 5:
                    barDataSet[i]=new BarDataSet(barYVals.get(i),"Disgust");
                    barDataSet[i].setColor(Color.BLACK);
                    break;
            }

            barDataSetList.add(barDataSet[i]);
        }

        barData=new BarData(barXVals,barDataSetList);//横纵对应
        barData.setDrawValues(false);

        barChart.setData(barData);//设置数据
        barChart.setDescription("");//情绪累计调查
        barChart.getLegend().setFormSize(5f);
        //barChart.setDescriptionPosition(x, y)

        //barChart.getXAxis().setSpaceBetweenLabels(1);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setScaleYEnabled(false);
        //barChart.setEnabled(false);
        //barChart.setDrawBarShadow(false);
        barChart.animateY(3000);//向上动画
    }

    public void setRadarChart()
    {
        radarChart.setDescription("");

        radarXVals=new ArrayList<String>();
        radarYVals=new ArrayList<Entry>();

        radarXVals.add("Happiness");radarXVals.add("Sadness");radarXVals.add("Anger");radarXVals.add("Surprise");
        radarXVals.add("Fear");radarXVals.add("Disgust");

        for(int i=0;i<radarXVals.size();++i)
        {
            radarYVals.add(new Entry(  (float) (getLineAverageLevel(i)),i)  );
        }

        radarDataSet=new RadarDataSet(radarYVals,"average level");
        radarDataSet.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        radarDataSet.setDrawFilled(true);

        radarData=new RadarData(radarXVals,radarDataSet);
        radarData.setDrawValues(true);

        radarChart.setData(radarData);
        radarChart.getXAxis().setTextSize(12f);
        //radarChart.getYAxis().setShowOnlyMinMax(true);
        radarChart.getYAxis().setEnabled(false);
        radarChart.getYAxis().setSpaceTop(0);


        radarChart.animateY(3000);
    }

    public void setStepLineChart()
    {
        //if(stepList.size()<cnt) return;

        stepLineChart.setDescription("");
        //stepLineChart.setDescriptionPosition(0,0);

        stepLineXVals=new ArrayList<String>();//横坐标描述词
        for(int i=0;i<stepList.size();i++)
        //for(int i=0;i<cnt;i++)
        {
            //stepLineXVals.add(""+i);
            stepLineXVals.add(dateList.get(i));
        }

        stepLineYVals=new ArrayList<Entry>();

        for(int i=0;i<stepLineXVals.size();++i)
        {
            stepLineYVals.add(new Entry(stepList.get(i),i)  );
        }

        stepLineDataSet=new LineDataSet(stepLineYVals,"step count");
        stepLineDataSet.setColor(ColorTemplate.VORDIPLOM_COLORS[2]);
        stepLineDataSet.setDrawFilled(true);

        stepLineData=new LineData(stepLineXVals,stepLineDataSet);
        stepLineData.setDrawValues(true);

        stepLineChart.setData(stepLineData);
        stepLineChart.getXAxis().setTextSize(12f);
        //radarChart.getYAxis().setShowOnlyMinMax(true);
        //stepLineChart.getYAxis().setEnabled(false);
        //stepLineChart.getYAxis().setSpaceTop(0);

        stepLineChart.setDoubleTapToZoomEnabled(false);
        stepLineChart.setScaleYEnabled(false);

        stepLineChart.animateY(3000);
    }

    public void setVolumeLineChart()
    {
        //if(volumeList.size()<cnt) return;


        volumeLineChart.setDescription("");
        //volumeLineChart.setDescriptionPosition(0,0);

        Log.e("chart","1");
        volumeLineXVals=new ArrayList<String>();//横坐标描述词
        for(int i=0;i<volumeList.size();i++)
        //for(int i=0;i<cnt;i++)
        {
            //volumeLineXVals.add(""+i);
            volumeLineXVals.add(dateList.get(i));
        }
        Log.e("chart","2");

        volumeLineYVals=new ArrayList<Entry>();

        //DecimalFormat df=new DecimalFormat("0.000");
        for(int i=0;i<volumeList.size();++i)
        {
            //volumeLineYVals.add(new Entry( Float.valueOf(df.format(volumeList.get(i)) ),i) );
            volumeLineYVals.add(new Entry( Float.valueOf( volumeList.get(i).toString().substring(0,4) ) ,i) );
        }
        Log.e("chart","3");


        volumeLineDataSet=new LineDataSet(volumeLineYVals,"volume");
        volumeLineDataSet.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        volumeLineDataSet.setDrawFilled(true);

        volumeLineData=new LineData(volumeLineXVals,volumeLineDataSet);
        volumeLineData.setDrawValues(true);



        volumeLineChart.setData(volumeLineData);
        volumeLineChart.getXAxis().setTextSize(12f);
        //radarChart.getYAxis().setShowOnlyMinMax(true);
        //volumeLineChart.getYAxis().setEnabled(false);
        //volumeLineChart.getYAxis().setSpaceTop(0);


        volumeLineChart.setDoubleTapToZoomEnabled(false);
        volumeLineChart.setScaleYEnabled(false);
        volumeLineChart.animateY(3000);
    }


/*
    public void setLineChart()
    {
        lineYVals=new ArrayList< ArrayList<Entry> >();
        for(int i=0;i<emotionNum;++i)
        {
            lineYVals.add( new ArrayList<Entry>() );
        }
        float level = 0;
        for(int j=0;j<emotionNum;++j)
        {
            for(int i=0;i<cnt;i++)
            {
                switch(j)
                {
                    case 0:
                        level=emotionList.get(i).emotion[j];
                        break;

                    case 1:
                        level=emotionList.get(i).emotion[j];
                        break;

                    case 2:
                        level=emotionList.get(i).emotion[j];
                        break;

                    case 3:
                        level=emotionList.get(i).emotion[j];
                        break;

                    case 4:
                        level=emotionList.get(i).emotion[j];
                        break;

                    case 5:
                        level=emotionList.get(i).emotion[j];
                        break;

                    default:
                        break;
                }//switch(j)
                lineYVals.get(j).add(new Entry(level,i));

            }//for(int i=0;i<MainActivity.userEmotionList.get(MainActivity.selectedDBIndex).happinessList.size();i++)

        }//for(int j=0;j<emotionNum;++j)

        lineDataSet=new LineDataSet[6];
        for(int i=0;i<emotionNum;++i)
        {
            switch(i)
            {
                case 0:
                    lineDataSet[i]=new LineDataSet(lineYVals.get(i),"Happiness");
                    lineDataSet[i].setColor(ColorTemplate.JOYFUL_COLORS[0]);
                    break;

                case 1:
                    lineDataSet[i]=new LineDataSet(lineYVals.get(i),"Sadness");
                    lineDataSet[i].setColor(ColorTemplate.JOYFUL_COLORS[1]);
                    break;

                case 2:
                    lineDataSet[i]=new LineDataSet(lineYVals.get(i),"Anger");
                    lineDataSet[i].setColor(ColorTemplate.JOYFUL_COLORS[2]);
                    break;

                case 3:
                    lineDataSet[i]=new LineDataSet(lineYVals.get(i),"Surprise");
                    lineDataSet[i].setColor(ColorTemplate.JOYFUL_COLORS[3]);
                    break;

                case 4:
                    lineDataSet[i]=new LineDataSet(lineYVals.get(i),"Fear");
                    lineDataSet[i].setColor(ColorTemplate.JOYFUL_COLORS[4]);
                    break;

                case 5:
                    lineDataSet[i]=new LineDataSet(lineYVals.get(i),"Disgust");
                    lineDataSet[i].setColor(Color.BLACK);
                    break;
            }

            lineDataSetList.add(lineDataSet[i]);
        }

        lineData=new LineData(lineXVals,lineDataSetList);//横纵对应

        lineChart.setData(lineData);//设置数据
        lineChart.setDescription("");//情绪累计调查
        //lineChart.setDescriptionPosition(x, y)

        //lineChart.getXAxis().setSpaceBetweenLabels(1);
        lineChart.animateY(3000);//向上动画

    }*/

    class MyEmotionSet
    {
        //public int happy=0;
        public int emotion[];

        public MyEmotionSet()
        {
            emotion=new int [ChartActivity.emotionNum];
        }

    }

}





















