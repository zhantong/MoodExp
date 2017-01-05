package cn.edu.nju.dislab.moodexp.collectors;


import android.Manifest;
import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.nju.dislab.moodexp.EasyPermissions;
import cn.edu.nju.dislab.moodexp.MainApplication;

/**
 * Created by zhantong on 2016/12/21.
 */

public class LocationCollector {
    private static final String TAG = "LocationCollector";
    private static final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};
    private final Object LOCK = new Object();
    private AMapLocationClient mLocationClient = null;
    private Context mContext;
    private LocationData result;
    private AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            result = new LocationData(aMapLocation);
            synchronized (LOCK) {
                LOCK.notify();
            }
        }
    };

    private static final Logger LOG = LoggerFactory.getLogger(LocationCollector.class);

    public LocationCollector() {
        this(MainApplication.getContext());
    }

    public LocationCollector(Context context) {
        mContext = context;
        mLocationClient = new AMapLocationClient(mContext);
        mLocationClient.setLocationListener(mLocationListener);
    }

    public static String[] getPermissions() {
        return PERMISSIONS;
    }

    public int collect() {
        if (!EasyPermissions.hasPermissions(PERMISSIONS)) {
            return Collector.NO_PERMISSION;
        }
        LOG.info("preparing to collect");
        AMapLocationClientOption locationClientOption = new AMapLocationClientOption();
        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //locationClientOption.setOnceLocation(true);
        locationClientOption.setOnceLocationLatest(true);
        locationClientOption.setHttpTimeOut(6000);
        locationClientOption.setLocationCacheEnable(false);

        LOG.info("start collecting");
        mLocationClient.setLocationOption(locationClientOption);
        mLocationClient.startLocation();

        synchronized (LOCK) {
            try {
                LOCK.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return Collector.COLLECT_FAILED;
            }
        }

        mLocationClient.unRegisterLocationListener(mLocationListener);
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();
        if (result == null) {
            LOG.info("null location");
            return Collector.COLLECT_FAILED;
        }
        LOG.info("finished collect");
        return Collector.COLLECT_SUCCESS;
    }

    public LocationData getResult() {
        return result;
    }
}
