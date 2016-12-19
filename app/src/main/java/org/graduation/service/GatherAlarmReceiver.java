package org.graduation.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GatherAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, CollectingService.class));
        Log.d("Alarm Receiver", "Gather alarm receiver awake.");
    }
}
