package com.bms.mqp.behaviormodelsystem;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;
/**
 * Created by Stephen on 11/12/2016.
 * From Google Sample
 */

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent
 * and then starts the IntentService {@code SampleSchedulingService} to do some work.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    PendingIntent AppUsagealarmIntent;
    PendingIntent AppStatsalarmIntent;
    PendingIntent LocationalarmIntent;
    PendingIntent TiltalarmIntent;
    PendingIntent wifialarmIntent;
    PendingIntent AuthIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        // BEGIN_INCLUDE(alarm_onreceive)
        /*
         * If your receiver intent includes extras that need to be passed along to the
         * service, use setComponent() to indicate that the service should handle the
         * receiver's intent. For example:
         *
         * ComponentName comp = new ComponentName(context.getPackageName(),
         *      MyService.class.getName());
         *
         * // This intent passed in this call will include the wake lock extra as well as
         * // the receiver intent contents.
         * startWakefulService(context, (intent.setComponent(comp)));
         *
         * In this example, we simply create a new intent to deliver to the service.
         * This intent holds an extra identifying the wake lock.
         */
        Intent location = new Intent(context, LocationService.class);
        Intent appusage = new Intent(context, AppUsageEventsService.class);
        Intent appstats = new Intent(context, AppUsageStatisticsService.class);
        Intent tilt = new Intent(context, TiltService.class);
        Intent wifi = new Intent(context, WifiService.class);
        Intent authentication = new Intent(context, AuthenticationService.class);

        // Start the service, keeping the device awake while it is launching.
        if (intent.getStringExtra("service").equals("app_usage")) {
            startWakefulService(context, appusage);
        }
        if (intent.getStringExtra("service").equals("app_stats")) {
            startWakefulService(context, appstats);
        }
        if (intent.getStringExtra("service").equals("wifi")) {
            startWakefulService(context, wifi);
        }
        if (intent.getStringExtra("service").equals("location")) {
            startWakefulService(context, location);
        }
        if (intent.getStringExtra("service").equals("tilt")) {
            startWakefulService(context, tilt);
        }
        if (intent.getStringExtra("service").equals("authentication")) {
            startWakefulService(context, authentication);
        }
        // END_INCLUDE(alarm_onreceive)
    }

    // BEGIN_INCLUDE(set_alarm)
    /**
     * Sets a repeating alarm that runs once a day at approximately 8:30 a.m. When the
     * alarm fires, the app broadcasts an Intent to this WakefulBroadcastReceiver.
     * @param context
     */
    public void setAlarm(Context context, int id) {
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);



        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // Set the alarm's trigger time to 8:30 a.m.

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        // Set the alarm to fire at approximately 8:30 a.m., according to the device's
        // clock, and to repeat once a day.
        if (id == 1) { // app usage
            intent.putExtra("service", "app_usage");
            AppUsagealarmIntent = PendingIntent.getBroadcast(context, id, intent, 0);
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, Integer.valueOf(SP.getString("app_usage_interval", "60000")), Integer.valueOf(SP.getString("app_usage_interval", "60000")), AppUsagealarmIntent);
        }
        if (id == 2) { // app stats
            intent.putExtra("service", "app_stats");
            AppStatsalarmIntent = PendingIntent.getBroadcast(context, id, intent, 0);
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, Integer.valueOf(SP.getString("app_stats_interval", "60000")), Integer.valueOf(SP.getString("app_stats_interval", "60000")), AppStatsalarmIntent);
        }
        if (id == 3) { // wifi
            intent.putExtra("service", "wifi");
            wifialarmIntent = PendingIntent.getBroadcast(context, id, intent, 0);
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, Integer.valueOf(SP.getString("wifi_interval", "60000")), Integer.valueOf(SP.getString("wifi_interval", "60000")), wifialarmIntent);
        }
        if (id == 4) { // location
            intent.putExtra("service", "location");
            LocationalarmIntent = PendingIntent.getBroadcast(context, id, intent, 0);
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, Integer.valueOf(SP.getString("location_interval", "60000")), Integer.valueOf(SP.getString("location_interval", "60000")), LocationalarmIntent);
        }
        if (id == 5) { // tilt
            intent.putExtra("service", "tilt");
            TiltalarmIntent = PendingIntent.getBroadcast(context, id, intent, 0);
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, Integer.valueOf(SP.getString("tilt_interval", "60000")), Integer.valueOf(SP.getString("tilt_interval", "60000")), TiltalarmIntent);
        }
        if (id == 6) { // authentication
            intent.putExtra("service", "authentication");
            AuthIntent = PendingIntent.getBroadcast(context, id, intent, 0);
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, Integer.valueOf(SP.getString("tilt_interval", "10000")), Integer.valueOf(SP.getString("tilt_interval", "10000")), AuthIntent);
        }


        // Enable {@code SampleBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
    // END_INCLUDE(set_alarm)


    /**
     * Cancels the alarm.
     * @param context
     */
    // BEGIN_INCLUDE(cancel_alarm)
    public void cancelAlarm(Context context, int id) {
        // If the alarm has been set, cancel it.
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent;

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        // Set the alarm to fire at approximately 8:30 a.m., according to the device's
        // clock, and to repeat once a day.

        if (alarmMgr != null) {
            if (id == 1) {
                alarmMgr.cancel(AppUsagealarmIntent);
            }
            if (id == 2) {
                alarmMgr.cancel(AppStatsalarmIntent);
            }
            if (id == 3) {
                alarmMgr.cancel(wifialarmIntent);
            }
            if (id == 4) {
                alarmMgr.cancel(LocationalarmIntent);
            }
            if (id == 5) {
                alarmMgr.cancel(TiltalarmIntent);
            }
            if (id == 6) {
                alarmMgr.cancel(AuthIntent);
            }
        }
    }
    // END_INCLUDE(cancel_alarm)
}
