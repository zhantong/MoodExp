package cn.edu.nju.dislab.moodexp.collectors;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.nju.dislab.moodexp.MainApplication;

import static android.content.Context.POWER_SERVICE;

/**
 * Created by zhantong on 2016/12/22.
 */

public class ScreenCollector {
    private static final String TAG = "ScreenCollector";
    private static final String[] PERMISSIONS = {};
    private Context mContext;
    private DisplayManager mDisplayManager;
    private PowerManager mPowerManager;
    private ScreenData result;

    private static final Logger LOG = LoggerFactory.getLogger(ScreenCollector.class);

    public ScreenCollector() {
        this(MainApplication.getContext());
    }

    public ScreenCollector(Context context) {
        mContext = context;
        mDisplayManager = (DisplayManager) mContext.getSystemService(Context.DISPLAY_SERVICE);
        mPowerManager = (PowerManager) mContext.getSystemService(POWER_SERVICE);
        if (mDisplayManager == null) {
            Log.i(TAG, "null DisplayManager");
            throw new RuntimeException();
        }
    }

    public static String[] getPermissions() {
        return PERMISSIONS;
    }

    public int collect() {
        LOG.info("preparing to collect");
        boolean isScreenOn = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            for (Display display : mDisplayManager.getDisplays()) {
                if (display.getState() != Display.STATE_OFF) {
                    isScreenOn = true;
                }
            }
        } else {
            isScreenOn = mPowerManager.isScreenOn();
        }
        LOG.info("start collecting");
        result = new ScreenData(isScreenOn, System.currentTimeMillis());
        LOG.info("finished collect");
        return Collector.COLLECT_SUCCESS;
    }

    public ScreenData getResult() {
        return result;
    }
}
