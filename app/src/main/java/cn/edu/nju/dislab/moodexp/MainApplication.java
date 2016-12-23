package cn.edu.nju.dislab.moodexp;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import io.yunba.android.manager.YunBaManager;

/**
 * Created by zhantong on 2016/12/21.
 */

public class MainApplication extends Application {
    private static final String TAG="MainApplication";
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        YunBaManager.setThirdPartyEnable(getApplicationContext(), true);
        YunBaManager.setXMRegister("2882303761517536019","5531753647019");
        YunBaManager.start(getApplicationContext());

        YunBaManager.subscribe(getApplicationContext(), new String[]{"debug"}, new IMqttActionListener() {

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
}
