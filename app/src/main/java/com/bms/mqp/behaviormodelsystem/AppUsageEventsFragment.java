/*
* Copyright 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.bms.mqp.behaviormodelsystem;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/**
 * Fragment that demonstrates how to use App Usage Statistics API.
 */
public class AppUsageEventsFragment extends Fragment {

    private static final String TAG = AppUsageEventsFragment.class.getSimpleName();

    private static final long USAGE_STATS_PERIOD = 1000 * 60 * 60 * 24 * 14;
    private long mLastTime;

    //VisibleForTesting for variables below
    UsageStatsManager mUsageStatsManager;
    UsageEventsListAdapter mUsageEventsListAdapter;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    Button mOpenUsageSettingButton;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment {@link AppUsageEventsFragment}.
     */
    public static AppUsageEventsFragment newInstance() {
        AppUsageEventsFragment fragment = new AppUsageEventsFragment();
        return fragment;
    }

    public AppUsageEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUsageStatsManager = (UsageStatsManager) getActivity()
                .getSystemService(Context.USAGE_STATS_SERVICE); //Context.USAGE_STATS_SERVICE

        mLastTime = System.currentTimeMillis() - USAGE_STATS_PERIOD;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_usage_events, container, false);

        Button mRefreshButton = (Button) view.findViewById(R.id.button_refresh);
        mRefreshButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UsageEvents usageEventsList = getUsageStatistics();
                updateAppsList(usageEventsList);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        mUsageEventsListAdapter = new UsageEventsListAdapter();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_app_usage);
        mLayoutManager = mRecyclerView.getLayoutManager();
        mRecyclerView.scrollToPosition(0);
        mRecyclerView.setAdapter(mUsageEventsListAdapter);
        mOpenUsageSettingButton = (Button) rootView.findViewById(R.id.button_open_usage_setting);

    }

    /**
     * Returns the {@link #mRecyclerView} including the time span specified by the
     * intervalType argument.
     *
     *
     * @return A list of {@link UsageStats}.
     */
    public UsageEvents getUsageStatistics() {
        // Get the app statistics since one year ago from the current time.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);

        long now = System.currentTimeMillis();

        UsageEvents queryUsageStats = mUsageStatsManager
                .queryEvents(mLastTime, now);

        if (!queryUsageStats.hasNextEvent()) {
            Log.i(TAG, "The user may not allow the access to apps usage. ");
            Toast.makeText(getActivity(),
                    getString(R.string.explanation_access_to_appusage_is_not_enabled),
                    Toast.LENGTH_LONG).show();
            mOpenUsageSettingButton.setVisibility(View.VISIBLE);
            mOpenUsageSettingButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                }
            });
        }
        return queryUsageStats;
    }

    /**
     * Updates the {@link #mRecyclerView} with the list of {@link UsageStats} passed as an argument.
     *
     * @param usageEventsList A list of {@link UsageStats} from which update the
     *                       {@link #mRecyclerView}.
     */
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
                Drawable appIcon = getActivity().getPackageManager()
                        .getApplicationIcon(customUsageEvents.usageEvent.getPackageName());
                customUsageEvents.appIcon = appIcon;
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, String.format("App Icon is not found for %s",
                        customUsageEvents.usageEvent.getPackageName()));
                customUsageEvents.appIcon = getActivity()
                        .getDrawable(R.drawable.ic_default_app_launcher);
            }
            customUsageEventsList.add(customUsageEvents);
        }
        Collections.sort(customUsageEventsList, new TimeStampComparator());
        mUsageEventsListAdapter.setCustomUsageStatsList(customUsageEventsList);
        mUsageEventsListAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
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
