package com.bms.mqp.behaviormodelsystem;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Arun on 1/17/2017.
 */

public class AuthenticationService extends IntentService {

    public AuthenticationService(){super("AuthenticationService");}

    @Override
    protected void onHandleIntent(Intent intent) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Authentication Service Status")
                        .setContentText("Hello World!");
        NotificationManager notificationManager;
        int mNotificationId = 042;
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(SystemClock.currentThreadTimeMillis()%2 == 0) {
            mBuilder.setContentText("Authenticated");
            notificationManager.notify(mNotificationId, mBuilder.build());
        }
        else{
            mBuilder.setContentText("Deauthenticated");
            notificationManager.notify(mNotificationId, mBuilder.build());
        }
    }
}
