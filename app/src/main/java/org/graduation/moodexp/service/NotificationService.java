package org.graduation.moodexp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.graduation.moodexp.R;
import org.graduation.moodexp.database.SharedPreferenceManager;
import org.graduation.moodexp.healthylife.MainActivity;

public class NotificationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("Notification Service", "created notification service");

        long time = System.currentTimeMillis();
        SharedPreferenceManager sm = SharedPreferenceManager.getManager();
        long lastTime = sm.getLong("lasttime", 0);
        if (time - lastTime <= 1000)//5 * 60 * 60 * 1000
        {
            return;
        }
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("您现在心情如何呢?")
                .setContentText("快来告诉我你的心情吧!")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.newcoin)
                .setVisibility(Notification.VISIBILITY_PUBLIC )
                .setAutoCancel(true);
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity
                (this, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        nBuilder.setContentIntent(pendingIntent);

        ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(0, nBuilder.build());
        this.stopSelf();
    }
}
