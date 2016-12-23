package cn.edu.nju.dislab.moodexp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import io.yunba.android.manager.YunBaManager;

/**
 * Created by zhantong on 2016/12/23.
 */

public class YunbaReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (YunBaManager.MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {

            String topic = intent.getStringExtra(YunBaManager.MQTT_TOPIC);
            String msg = intent.getStringExtra(YunBaManager.MQTT_MSG);

            //在这里处理从服务器发布下来的消息， 比如显示通知栏， 打开 Activity 等等
            StringBuilder showMsg = new StringBuilder();
            showMsg.append("Received message from server: ")
                    .append(YunBaManager.MQTT_TOPIC)
                    .append(" = ")
                    .append(topic)
                    .append(" ")
                    .append(YunBaManager.MQTT_MSG)
                    .append(" = ")
                    .append(msg);
            Log.i("yunba notification",showMsg.toString());

            Context applicationContext=MainApplication.getContext();
            if (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1) {
                String pkg=applicationContext.getPackageName();
                PowerManager pm=applicationContext.getSystemService(PowerManager.class);

                if (!pm.isIgnoringBatteryOptimizations(pkg)) {
                    Intent i=
                            new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                                    .setData(Uri.parse("package:"+pkg));

                    applicationContext.startActivity(i);
                }
            }
            applicationContext.startService(new Intent(applicationContext,ScheduledService.class));
        }
    }
}
