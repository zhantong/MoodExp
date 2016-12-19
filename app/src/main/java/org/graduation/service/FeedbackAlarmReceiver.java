package org.graduation.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class FeedbackAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, NotificationService.class));
        Log.d("Alarm Receiver", "Feedback alarm receiver awake.");
    }
}
