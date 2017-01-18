package com.bms.mqp.behaviormodelsystem;

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



import android.app.usage.UsageStats;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Entity class represents usage stats and app icon.
 */
public class CustomUsageStats implements Parcelable {
    public UsageStats usageStats;
    public Drawable appIcon;

    protected CustomUsageStats(Parcel in) {
        usageStats = (UsageStats) in.readValue(UsageStats.class.getClassLoader());
        appIcon = (Drawable) in.readValue(Drawable.class.getClassLoader());
    }

    public CustomUsageStats() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(usageStats);
        dest.writeValue(appIcon);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CustomUsageStats> CREATOR = new Parcelable.Creator<CustomUsageStats>() {
        @Override
        public CustomUsageStats createFromParcel(Parcel in) {
            return new CustomUsageStats(in);
        }

        @Override
        public CustomUsageStats[] newArray(int size) {
            return new CustomUsageStats[size];
        }
    };
}