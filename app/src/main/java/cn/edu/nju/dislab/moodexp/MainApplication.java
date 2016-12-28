package cn.edu.nju.dislab.moodexp;

import android.app.Application;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import java.util.List;

import io.yunba.android.manager.YunBaManager;

/**
 * Created by zhantong on 2016/12/21.
 */

public class MainApplication extends Application {
    private static final String TAG="MainApplication";
    private static Context mContext;
    private static String userId=null;
    private static String versionName=null;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        YunBaManager.setThirdPartyEnable(getApplicationContext(), true);
        YunBaManager.setXMRegister("2882303761517536019","5531753647019");
        YunBaManager.start(getApplicationContext());

        YunBaManager.setAlias(getApplicationContext(),getUserId(),null);
        String topic="user";
        YunBaManager.subscribe(getApplicationContext(), new String[]{topic}, new IMqttActionListener() {

            @Override
            public void onSuccess(IMqttToken arg0) {
                Log.d(TAG, "Subscribe topic succeed");
            }

            @Override
            public void onFailure(IMqttToken arg0, Throwable arg1) {
                Log.d(TAG, "Subscribe topic failed");
            }
        });
    }

    public static Context getContext() {
        return mContext;
    }

    public static boolean isUsageStatsGranted(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            PackageManager packageManager = mContext.getPackageManager();
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            if(list.size()==0){
                return true;
            }
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);
            if(stats==null||stats.isEmpty()){
                return false;
            }
        }
        return true;
    }
    public static String getUserId(){
        return getUserId(false);
    }
    public static String getUserId(boolean forceRefresh){
        if(userId==null||forceRefresh) {
            userId="";
            try (Cursor cursor = new DbHelper().getWritableDatabase().query(DbHelper.UserTable.TABLE_NAME, new String[]{DbHelper.UserTable.COLUMN_NAME_VALUE}, DbHelper.UserTable.COLUMN_NAME_KEY + " = ?", new String[]{"id"}, null, null, null)) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    userId = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.UserTable.COLUMN_NAME_VALUE));
                }
            }
            YunBaManager.setAlias(getContext(),userId,null);
        }
        return userId;
    }
    public static String getVersionName(){
        if(versionName==null) {
            versionName="";
            try {
                versionName= mContext.getPackageManager().getPackageInfo(MainApplication.getContext().getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return versionName;
    }
}
