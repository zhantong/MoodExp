package cn.edu.nju.dislab.moodexp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Random;

import io.yunba.android.manager.YunBaManager;

/**
 * Created by zhantong on 2016/12/23.
 */

public class YunbaReceiver extends BroadcastReceiver {
    private static final String TAG = "YunbaReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (YunBaManager.MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {

            String topic = intent.getStringExtra(YunBaManager.MQTT_TOPIC);
            String msgJson = intent.getStringExtra(YunBaManager.MQTT_MSG);

            //在这里处理从服务器发布下来的消息， 比如显示通知栏， 打开 Activity 等等
            StringBuilder showMsg = new StringBuilder();
            showMsg.append("Received message from server: ")
                    .append(YunBaManager.MQTT_TOPIC)
                    .append(" = ")
                    .append(topic)
                    .append(" ")
                    .append(YunBaManager.MQTT_MSG)
                    .append(" = ")
                    .append(msgJson);
            Log.i("yunba notification", showMsg.toString());
            JsonElement jsonElement = new JsonParser().parse(msgJson);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String type = jsonObject.get("type").getAsString();
            if (type.equals("notification")) {
                JsonObject message = jsonObject.get("message").getAsJsonObject();
                Notification.Builder builder = new Notification.Builder(context)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle(message.get("title").getAsString())
                        .setContentText(message.get("message").getAsString());
                Intent resultIntent = new Intent(context, MainActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                builder.setContentIntent(resultPendingIntent);
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(new Random().nextInt(), builder.build());
            }


            Context applicationContext = MainApplication.getContext();
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                String pkg = applicationContext.getPackageName();
                PowerManager pm = applicationContext.getSystemService(PowerManager.class);

                if (!pm.isIgnoringBatteryOptimizations(pkg)) {
                    Intent i =
                            new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                                    .setData(Uri.parse("package:" + pkg));

                    applicationContext.startActivity(i);
                }
            }
            applicationContext.startService(new Intent(applicationContext, ScheduledService.class));
        }
    }
}
