/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.bms.mqp.behaviormodelsystem;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Provide views to RecyclerView with the directory entries.
 */
public class UsageEventsListAdapter extends RecyclerView.Adapter<UsageEventsListAdapter.ViewHolder> {

    private List<CustomUsageEvents> mCustomUsageEventsList = new ArrayList<>();
    private DateFormat mDateFormat = new SimpleDateFormat();
    private DateFormat mElapsedFormat = new SimpleDateFormat("HH:mm:ss.SSS");

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mPackageName;
        private final TextView mTimeStamp;
        private final ImageView mAppIcon;
        private final TextView mClassName;
        private final TextView mEventType;

        public ViewHolder(View v) {
            super(v);
            mPackageName = (TextView) v.findViewById(R.id.textview_package_name);
            mTimeStamp = (TextView) v.findViewById(R.id.textview_timestamp);
            mClassName = (TextView) v.findViewById(R.id.textview_classname);
            mEventType = (TextView) v.findViewById(R.id.textview_eventtype);
            mAppIcon = (ImageView) v.findViewById(R.id.app_icon);
        }

        public TextView getTimeStamp() {
            return mTimeStamp;
        }

        public TextView getPackageName() {
            return mPackageName;
        }

        public ImageView getAppIcon() {
            return mAppIcon;
        }

        public TextView getClassname() {
            return mClassName;
        }

        public TextView getEventType() {
            return mEventType;
        }
    }

    public UsageEventsListAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.usage_row, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getPackageName().setText(
                mCustomUsageEventsList.get(position).usageEvent.getPackageName());
        long timeStamp = mCustomUsageEventsList.get(position).usageEvent.getTimeStamp();
        viewHolder.getTimeStamp().setText(mDateFormat.format(new Date(timeStamp)));
        viewHolder.getClassname().setText(mCustomUsageEventsList.get(position).usageEvent.getClassName());

        String eventTypes = new String();

        if (mCustomUsageEventsList.get(position).usageEvent.getEventType() == 5) {
            eventTypes = eventTypes.concat("Conifguration Change");
        }
        if (mCustomUsageEventsList.get(position).usageEvent.getEventType() == 2) {
            eventTypes = eventTypes.concat("Move to Background");
        }
        if (mCustomUsageEventsList.get(position).usageEvent.getEventType() == 1) {
            eventTypes = eventTypes.concat("Move to Foreground");
        }
        if (mCustomUsageEventsList.get(position).usageEvent.getEventType() == 0) {
            eventTypes = eventTypes.concat("None");
        }
        if (mCustomUsageEventsList.get(position).usageEvent.getEventType() == 7) {
            eventTypes = eventTypes.concat("User Interaction");
        }

        viewHolder.getEventType().setText(eventTypes);
        viewHolder.getAppIcon().setImageDrawable(mCustomUsageEventsList.get(position).appIcon);
    }

    @Override
    public int getItemCount() {
        return mCustomUsageEventsList.size();
    }

    public void setCustomUsageStatsList(List<CustomUsageEvents> customUsageEvents) {
        mCustomUsageEventsList = customUsageEvents;
    }
}