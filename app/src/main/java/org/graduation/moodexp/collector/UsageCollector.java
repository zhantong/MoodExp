package org.graduation.moodexp.collector;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import org.graduation.moodexp.database.DatabaseManager;
import org.graduation.moodexp.healthylife.*;

import java.util.ArrayList;
import java.util.List;

public class UsageCollector implements ICollector//before 5.0
{//app使用时间搜集

    private static final String TAG = "UsageRecord";

    public void collect()
    {
        ActivityManager activityManager = (ActivityManager) MainApplication.getContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcessList
                = activityManager.getRunningAppProcesses();
        String[] running = null;

        for (ActivityManager.RunningAppProcessInfo info : runningProcessList)
        {
            if (info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && info.importanceReasonCode == 0)
            {
                running = info.pkgList;
                break;
            }
        }
        if (running == null)
        {
            Log.d(TAG, "Nothing found this time");
            //return;
        }

        DatabaseManager manager = DatabaseManager.getDatabaseManager();
        List<ActivityManager.RunningAppProcessInfo> runningAppsInfo = new ArrayList<ActivityManager.RunningAppProcessInfo>();
        PackageManager pm = MainApplication.getContext().getPackageManager();
        ActivityManager am = (ActivityManager)MainApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am
                .getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : runningServices)
        {

            String pkgName = service.process.split(":")[0];
            try
            {
                ActivityManager.RunningAppProcessInfo item = new ActivityManager.RunningAppProcessInfo();
                item.pkgList = new String[] { pkgName };
                item.pid = service.pid;
                item.processName = service.process;
                item.uid = service.uid;


                runningAppsInfo.add(item);

                Log.e("process",""+item.processName);
                manager.saveAppUsage(item.processName);

            }
            catch (Exception e) {

            }
        }

//        //ProcessManager m=new ProcessManager();
//        List<ProcessManager.Process> list=ProcessManager.getRunningApps();
//        for(int i=0;i<list.size();++i)
//        {
//            Log.e("pro","name: "+list.get(i).name+"  cpu:"+list.get(i).cpu);
//        }


    }

    @Override
    public void startCollect() {
        collect();
    }

    @Override
    public void stopCollect()
    {
    }

}
