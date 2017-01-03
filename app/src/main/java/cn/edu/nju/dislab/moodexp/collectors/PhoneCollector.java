package cn.edu.nju.dislab.moodexp.collectors;

import android.Manifest;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import cn.edu.nju.dislab.moodexp.EasyPermissions;
import cn.edu.nju.dislab.moodexp.MainApplication;

/**
 * Created by zhantong on 2016/12/23.
 */

public class PhoneCollector {
    private static final String TAG = "PhoneCollector";
    private static final String[] PERMISSIONS = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_WIFI_STATE};
    private Context mContext;
    private TelephonyManager mTelecomManager;
    private WifiManager mWifiManager;
    private PhoneData result;

    public PhoneCollector() {
        this(MainApplication.getContext());
    }

    public PhoneCollector(Context context) {
        mContext = context;
        mTelecomManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    }

    public static String[] getPermissions() {
        return PERMISSIONS;
    }

    public int collect() {
        if (!EasyPermissions.hasPermissions(PERMISSIONS)) {
            return Collector.NO_PERMISSION;
        }
        try {
            int dataState = mTelecomManager.getDataState();
            String deviceId = mTelecomManager.getDeviceId();
            String deviceSoftwareVersion = mTelecomManager.getDeviceSoftwareVersion();
            String groupIdLevel1 = mTelecomManager.getGroupIdLevel1();
            String line1Number = mTelecomManager.getLine1Number();
            String mmsUserAgent = mTelecomManager.getMmsUserAgent();
            String networkCountryIso = mTelecomManager.getNetworkCountryIso();
            String networkOperator = mTelecomManager.getNetworkOperator();
            String networkOperatorName = mTelecomManager.getNetworkOperatorName();
            int networkType = mTelecomManager.getNetworkType();
            int phoneType = mTelecomManager.getPhoneType();
            String simCountryIso = mTelecomManager.getSimCountryIso();
            String simOperator = mTelecomManager.getSimOperator();
            String simOperatorName = mTelecomManager.getSimOperatorName();
            String simSerialNumber = mTelecomManager.getSimSerialNumber();
            int simState = mTelecomManager.getSimState();

            result = new PhoneData();
            result.put("data_state", Integer.toString(dataState));
            result.put("device_id", deviceId);
            result.put("device_software_version", deviceSoftwareVersion);
            result.put("group_id_level_1", groupIdLevel1);
            result.put("line_1_number", line1Number);
            result.put("mms_user_agent", mmsUserAgent);
            result.put("network_country_iso", networkCountryIso);
            result.put("network_operator", networkOperator);
            result.put("network_operator_name", networkOperatorName);
            result.put("network_type", Integer.toString(networkType));
            result.put("phone_type", Integer.toString(phoneType));
            result.put("sim_country_iso", simCountryIso);
            result.put("sim_operator", simOperator);
            result.put("sim_operator_name", simOperatorName);
            result.put("sim_serial_number", simSerialNumber);
            result.put("sim_state", Integer.toString(simState));
        } catch (Exception e) {
            e.printStackTrace();
            return Collector.NO_PERMISSION;
        }
        try {
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            String macAddress = wifiInfo.getMacAddress();

            result.put("mac_address", macAddress);
        } catch (Exception e) {
            e.printStackTrace();
            return Collector.NO_PERMISSION;
        }
        return Collector.COLLECT_SUCCESS;
    }

    public PhoneData getResult() {
        return result;
    }
}
