package cn.edu.nju.dislab.moodexp.collectors;

import android.Manifest;
import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;

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
        } else {
            mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
            Log.i(TAG, "using ActivityManager");
        }

    }

    public int collect() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (!EasyPermissions.hasPermissions(PERMISSIONS_EG_L)) {
                return Collector.NO_PERMISSION;
            }
            long INTERVAL = 10 * 1000;
            long currentTimeMillis = System.currentTimeMillis();
            UsageEvents usageEvents = mUsageStatsManager.queryEvents(currentTimeMillis - INTERVAL, currentTimeMillis + INTERVAL);
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
                return Collector.COLLECT_FAILED;
            }
            result = new ForegroundAppData(foregroundApp, System.currentTimeMillis());
        } else {
            ActivityManager.RunningTaskInfo foregroundTaskInfo;
            try {
                foregroundTaskInfo = mActivityManager.getRunningTasks(1).get(0);
            } catch (Exception e) {
                Log.i(TAG, "error when getting foregroundTaskInfo");
                e.printStackTrace();
                return Collector.NO_PERMISSION;
            }
            if (foregroundTaskInfo == null) {
                Log.i(TAG, "null foregroundTaskInfo");
                return Collector.COLLECT_FAILED;
            }
            String foregroundApp = foregroundTaskInfo.baseActivity.getPackageName();
            result = new ForegroundAppData(foregroundApp, System.currentTimeMillis());
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
