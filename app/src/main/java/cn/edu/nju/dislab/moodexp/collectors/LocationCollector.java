package cn.edu.nju.dislab.moodexp.collectors;


import android.Manifest;
import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import cn.edu.nju.dislab.moodexp.EasyPermissions;
import cn.edu.nju.dislab.moodexp.MainApplication;

/**
 * Created by zhantong on 2016/12/21.
 */

public class LocationCollector {
    private static final String TAG = "LocationCollector";
    private static final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};
    private AMapLocationClient mLocationClient = null;
    private Context mContext;
    private final Object LOCK = new Object();
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

    public LocationCollector() {
        this(MainApplication.getContext());
    }

    public LocationCollector(Context context) {
        mContext = context;
        mLocationClient = new AMapLocationClient(mContext);
        mLocationClient.setLocationListener(mLocationListener);
    }

    public int collect() {
        if (!EasyPermissions.hasPermissions(PERMISSIONS)) {
            return Collector.NO_PERMISSION;
        }
        AMapLocationClientOption locationClientOption = new AMapLocationClientOption();
        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //locationClientOption.setOnceLocation(true);
        locationClientOption.setOnceLocationLatest(true);
        locationClientOption.setHttpTimeOut(6000);
        locationClientOption.setLocationCacheEnable(false);

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
        if (result == null || result.getLocation().getErrorCode() != 0) {
            return Collector.COLLECT_FAILED;
        }
        return Collector.COLLECT_SUCCESS;
    }

    public LocationData getResult() {
        return result;
    }

    public static String[] getPermissions() {
        return PERMISSIONS;
    }
}
