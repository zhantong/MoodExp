package cn.edu.nju.dislab.moodexp.collectors;

import android.Manifest;
import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

import cn.edu.nju.dislab.moodexp.EasyPermissions;
import cn.edu.nju.dislab.moodexp.MainApplication;


/**
 * Created by zhantong on 2016/12/22.
 */

public class ForegroundAppCollector {
    private static final String TAG = "ForegroundAppCollector";
    private static final String[] PERMISSIONS_LESS_L = {Manifest.permission.GET_TASKS};
    private static final String[] PERMISSIONS_EG_L = {Manifest.permission.PACKAGE_USAGE_STATS};
    private Context mContext;
    private ActivityManager mActivityManager;
    private UsageStatsManager mUsageStatsManager;
    private ForegroundAppData result;

    public ForegroundAppCollector() {
        this(MainApplication.getContext());
    }

    public ForegroundAppCollector(Context context) {
        mContext = context;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mUsageStatsManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
            Log.i(TAG, "using UsageStatsManager");
        }
        mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        Log.i(TAG, "using ActivityManager");
    }

    public int collect() {
        result=new ForegroundAppData();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (!EasyPermissions.hasPermissions(PERMISSIONS_EG_L)) {
                Log.i(TAG,"no usage stat permission");
            }else {
                long INTERVAL = 10 * 1000;
                long currentTimeMillis = System.currentTimeMillis();
                UsageEvents usageEvents=null;
                try {
                    usageEvents = mUsageStatsManager.queryEvents(currentTimeMillis - INTERVAL, currentTimeMillis + INTERVAL);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(usageEvents!=null) {
                    UsageEvents.Event event = new UsageEvents.Event();
                    String foregroundApp = null;
                    long latestTime = 0;
                    while (usageEvents.hasNextEvent()) {
                        usageEvents.getNextEvent(event);
                        long time = event.getTimeStamp();
                        if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND && time > latestTime) {
                            latestTime = time;
                            foregroundApp = event.getPackageName();
                        }
                    }
                    if (foregroundApp == null) {
                        Log.i(TAG, "no foreground app");
                    } else {
                        result.put(foregroundApp, "UsageStats", System.currentTimeMillis());
                    }
                }
            }
        }
        ActivityManager.RunningTaskInfo foregroundTaskInfo=null;
        try {
            foregroundTaskInfo = mActivityManager.getRunningTasks(1).get(0);
        } catch (Exception e) {
            Log.i(TAG, "error when getting foregroundTaskInfo");
            e.printStackTrace();
        }
        if (foregroundTaskInfo == null) {
            Log.i(TAG, "null foregroundTaskInfo");
        }else {
            String foregroundApp = foregroundTaskInfo.baseActivity.getPackageName();
            result.put(foregroundApp,"RunningTasks", System.currentTimeMillis());
        }
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos=null;
        try {
            runningAppProcessInfos = mActivityManager.getRunningAppProcesses();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(runningAppProcessInfos==null){
            Log.i(TAG, "null runningAppProcessInfos");
        }else{
            for(ActivityManager.RunningAppProcessInfo runningAppProcessInfo:runningAppProcessInfos){
                if(runningAppProcessInfo.importance== ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                    result.put(runningAppProcessInfo.processName,"RunningAppProcesses",System.currentTimeMillis());
                }
            }
        }
        return Collector.COLLECT_SUCCESS;
    }

    public ForegroundAppData getResult() {
        return result;
    }

    public static String[] getPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            return PERMISSIONS_EG_L;
        } else {
            return PERMISSIONS_LESS_L;
        }
    }
}
