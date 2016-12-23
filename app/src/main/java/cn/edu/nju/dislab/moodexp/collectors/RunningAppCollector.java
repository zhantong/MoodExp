package cn.edu.nju.dislab.moodexp.collectors;

import android.Manifest;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

import cn.edu.nju.dislab.moodexp.EasyPermissions;
import cn.edu.nju.dislab.moodexp.MainApplication;

/**
 * Created by zhantong on 2016/12/22.
 */

public class RunningAppCollector {
    private static final String TAG = "RunningAppCollector";
    private static final String[] PERMISSIONS_LESS_L = {Manifest.permission.GET_TASKS};
    private static final String[] PERMISSIONS_EG_L = {Manifest.permission.PACKAGE_USAGE_STATS};
    private Context mContext;
    private ActivityManager mActivityManager;
    private UsageStatsManager mUsageStatsManager;
    private RunningAppData result;

    public RunningAppCollector() {
        this(MainApplication.getContext());
    }

    public RunningAppCollector(Context context) {
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
        result = new RunningAppData();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (!EasyPermissions.hasPermissions(PERMISSIONS_EG_L)) {
                return Collector.NO_PERMISSION;
            }
            long INTERVAL = 30 * 60 * 1000;
            long currentTimeMillis = System.currentTimeMillis();
            List<UsageStats> usageStatses = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, currentTimeMillis - INTERVAL, currentTimeMillis + INTERVAL);
            if (usageStatses == null || usageStatses.isEmpty()) {
                return Collector.COLLECT_FAILED;
            }
            for (UsageStats usageStats : usageStatses) {
                result.put(usageStats.getPackageName(), System.currentTimeMillis());
            }
        } else {
            List<ActivityManager.RunningTaskInfo> runningTaskInfos;
            try {
                runningTaskInfos = mActivityManager.getRunningTasks(Integer.MAX_VALUE);
            } catch (Exception e) {
                Log.i(TAG, "error when getting runningTaskInfos");
                e.printStackTrace();
                return Collector.NO_PERMISSION;
            }
            if (runningTaskInfos == null) {
                Log.i(TAG, "null runningTaskInfos");
                return Collector.NO_PERMISSION;
            }
            if (runningTaskInfos.isEmpty()) {
                return Collector.COLLECT_FAILED;
            }
            for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfos) {
                result.put(runningTaskInfo.baseActivity.getPackageName(), System.currentTimeMillis());
            }
        }
        return Collector.COLLECT_SUCCESS;
    }

    public RunningAppData getResult() {
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
