package cn.edu.nju.dislab.moodexp.collectors;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.nju.dislab.moodexp.EasyPermissions;
import cn.edu.nju.dislab.moodexp.MainApplication;

/**
 * Created by zhantong on 2016/12/21.
 */

public class WifiCollector {
    private static final String TAG = "WifiCollector";
    private static final String[] PERMISSIONS = {Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE};
    private WifiManager mWifiManager;
    private Context mContext;
    private WifiData result;

    private static final Logger LOG = LoggerFactory.getLogger(WifiCollector.class);

    public WifiCollector() {
        this(MainApplication.getContext());
    }

    public WifiCollector(Context context) {
        mContext = context;
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    }

    public static String[] getPermissions() {
        return PERMISSIONS;
    }

    public int collect() {
        if (!EasyPermissions.hasPermissions(PERMISSIONS)) {
            return Collector.NO_PERMISSION;
        }
        LOG.info("preparing to collect");
        boolean originIsWifiEnabled = mWifiManager.isWifiEnabled();
        if (!originIsWifiEnabled) {
            boolean isEnableWifiSuccess;
            try {
                isEnableWifiSuccess = mWifiManager.setWifiEnabled(true);
            } catch (Exception e) {
                LOG.info("error enable wifi {}", e);
                return Collector.NO_PERMISSION;
            }
            if (!isEnableWifiSuccess) {
                LOG.info("failed to enable wifi");
                return Collector.COLLECT_FAILED;
            }
        }
        LOG.info("start collecting");
        try {
            mWifiManager.startScan();
        } catch (Exception e) {
            LOG.info("error start scan {}", e);
            return Collector.NO_PERMISSION;
        }
        LOG.info("scan started");
        final Object scanFinished = new Object();
        mContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                synchronized (scanFinished) {
                    scanFinished.notify();
                }
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        synchronized (scanFinished) {
            try {
                scanFinished.wait();
            } catch (InterruptedException e) {
                LOG.info("error interrupt {}", e);
                return Collector.COLLECT_FAILED;
            }
            result = new WifiData();
            result.put(mWifiManager.getScanResults());
        }
        LOG.info("finished collect");
        return Collector.COLLECT_SUCCESS;
    }

    public WifiData getResult() {
        return result;
    }
}
