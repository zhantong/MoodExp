package cn.edu.nju.dislab.moodexp.collectors;

import android.Manifest;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOG = LoggerFactory.getLogger(RunningAppCollector.class);

    public RunningAppCollector() {
        this(MainApplication.getContext());
    }

    public RunningAppCollector(Context context) {
        mContext = context;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mUsageStatsManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
            Log.i(TAG, "using UsageStatsManager");
        }
        mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        Log.i(TAG, "using ActivityManager");
    }

    public static String[] getPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            return PERMISSIONS_EG_L;
        } else {
            return PERMISSIONS_LESS_L;
        }
    }

    public int collect() {
        LOG.info("preparing to collect");
        result = new RunningAppData();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (!EasyPermissions.hasPermissions(PERMISSIONS_EG_L)) {
                LOG.info("no usage stat permission");
            } else {
                LOG.info("using UsageEvents");
                long INTERVAL = 30 * 60 * 1000;
                long currentTimeMillis = System.currentTimeMillis();
                List<UsageStats> usageStatses = null;
                try {
                    usageStatses = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, currentTimeMillis - INTERVAL, currentTimeMillis + INTERVAL);
                } catch (Exception e) {
                    LOG.info("error getting UsageEvents {}", e);
                }
                if (usageStatses == null || usageStatses.isEmpty()) {
                    LOG.info("null or empty usageStatses");
                } else {
                    LOG.info("start collecting");
                    for (UsageStats usageStats : usageStatses) {
                        result.put(usageStats.getPackageName(), "UsageStats", System.currentTimeMillis());
                    }
                }
            }
        }
        LOG.info("using RunningTaskInfo");
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = null;
        try {
            runningTaskInfos = mActivityManager.getRunningTasks(Integer.MAX_VALUE);
        } catch (Exception e) {
            LOG.info("error getting RunningTaskInfo {}", e);
            e.printStackTrace();
        }
        if (runningTaskInfos == null) {
            LOG.info("null RunningTaskInfo");
        } else {
            LOG.info("start collecting");
            for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfos) {
                result.put(runningTaskInfo.baseActivity.getPackageName(), "RunningTasks", System.currentTimeMillis());
            }
        }
        LOG.info("using RunningAppProcessInfo");
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = null;
        try {
            runningAppProcessInfos = mActivityManager.getRunningAppProcesses();
        } catch (Exception e) {
            LOG.info("error getting RunningAppProcessInfo {}", e);
        }
        if (runningAppProcessInfos == null) {
            LOG.info("null RunningAppProcessInfo");
        } else {
            LOG.info("start collecting");
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos) {
                if (runningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    result.put(runningAppProcessInfo.processName, "RunningAppProcesses", System.currentTimeMillis());
                }
            }
        }
        LOG.info("using RunningServiceInfo");
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = null;
        try {
            runningServiceInfos = mActivityManager.getRunningServices(Integer.MAX_VALUE);
        } catch (Exception e) {
            LOG.info("error getting RunningServiceInfo {}", e);
        }
        if (runningServiceInfos == null) {
            LOG.info("null RunningServiceInfo");
        } else {
            LOG.info("start collecting");
            for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServiceInfos) {
                result.put(runningServiceInfo.process, "RunningService", System.currentTimeMillis());
            }
        }
        LOG.info("finished collect");
        return Collector.COLLECT_SUCCESS;
    }

    public RunningAppData getResult() {
        return result;
    }
}
