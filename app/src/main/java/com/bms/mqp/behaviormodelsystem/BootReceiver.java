package com.bms.mqp.behaviormodelsystem;

/**
 * Created by Stephen on 11/12/2016.
 * From Google Sample
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * This BroadcastReceiver automatically (re)starts the alarm when the device is
 * rebooted. This receiver is set to be disabled (android:enabled="false") in the
 * application's manifest file. When the user sets the alarm, the receiver is enabled.
 * When the user cancels the alarm, the receiver is disabled, so that rebooting the
 * device will not trigger this receiver.
 */
// BEGIN_INCLUDE(autostart)
public class BootReceiver extends BroadcastReceiver {
    AlarmReceiver alarm = new AlarmReceiver();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
            boolean appUsage = SP.getBoolean("app_usage", false);
            boolean appStats = SP.getBoolean("app_stats", false);
            boolean wifi = SP.getBoolean("wifi_list", false);
            boolean location = SP.getBoolean("location_list", false);
            boolean tilt = SP.getBoolean("phone_tilt", false);
            boolean auth = SP.getBoolean("auth", false);

            if (appUsage) {
                alarm.setAlarm(context, 1);
            }
            if (appStats) {
                alarm.setAlarm(context, 2);
            }
            if (wifi) {
                alarm.setAlarm(context, 3);
            }
            if (location) {
                alarm.setAlarm(context, 4);
            }
            if (tilt) {
                alarm.setAlarm(context, 5);
            }
            if (auth) {
                alarm.setAlarm(context, 6);
            }
        }
    }
}
//END_INCLUDE(autostart)
