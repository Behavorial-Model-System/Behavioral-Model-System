package com.bms.mqp.behaviormodelsystem;

import android.app.IntentService;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AppUsageEventsService extends IntentService {


    public AppUsageEventsService() {
        super("AppUsageEventsService");
    }

    //VisibleForTesting for variables below
    UsageStatsManager mUsageStatsManager;
    Button mOpenUsageSettingButton;
    private static final String TAG = AppUsageEventsFragment.class.getSimpleName();

    private static final long USAGE_STATS_PERIOD = 1000 * 60 * 60 * 1;
    private long mLastTime;

    @Override
    protected void onHandleIntent(Intent intent) {
        mUsageStatsManager = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE); //Context.USAGE_STATS_SERVICE

        mLastTime = System.currentTimeMillis() - USAGE_STATS_PERIOD;


        UsageEvents usageEventsList = getUsageStatistics();
        updateAppsList(usageEventsList);
    }


    public UsageEvents getUsageStatistics() {
        // Get the app statistics since one year ago from the current time.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);

        long now = System.currentTimeMillis();

        UsageEvents queryUsageStats = mUsageStatsManager
                .queryEvents(mLastTime, now);

        if (!queryUsageStats.hasNextEvent()) {
            Log.i(TAG, "The user may not allow the access to apps usage. ");
            Toast.makeText(this,
                    getString(R.string.explanation_access_to_appusage_is_not_enabled),
                    Toast.LENGTH_LONG).show();
            mOpenUsageSettingButton.setVisibility(View.VISIBLE);
            mOpenUsageSettingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                }
            });
        }
        return queryUsageStats;
    }

    //VisibleForTesting
    void updateAppsList(UsageEvents usageEventsList) {
        List<CustomUsageEvents> customUsageEventsList = new ArrayList<>();
        while (usageEventsList.hasNextEvent()) {
            CustomUsageEvents customUsageEvents = new CustomUsageEvents();
            UsageEvents.Event event;
            event = new UsageEvents.Event();
            usageEventsList.getNextEvent(event);
            customUsageEvents.usageEvent = event;
            try {
                Drawable appIcon = this.getPackageManager()
                        .getApplicationIcon(customUsageEvents.usageEvent.getPackageName());
                customUsageEvents.appIcon = appIcon;
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, String.format("App Icon is not found for %s",
                        customUsageEvents.usageEvent.getPackageName()));
                customUsageEvents.appIcon = this
                        .getDrawable(R.drawable.ic_default_app_launcher);
            }
            customUsageEventsList.add(customUsageEvents);
            ExternalSaver.save("App: "+event.getPackageName()+" Time Stamp: "+event.getTimeStamp(),"UsageEvents.txt\n");
        }
    }

    /**
     * The {@link Comparator} to sort a collection of {@link UsageStats} sorted by the timestamp
     * last time the app was used in the descendant order.
     */
    private static class TimeStampComparator implements Comparator<CustomUsageEvents> {

        @Override
        public int compare(CustomUsageEvents left, CustomUsageEvents right) {
            return Long.compare(right.usageEvent.getTimeStamp(), left.usageEvent.getTimeStamp());
        }
    }
}
