package com.bms.mqp.behaviormodelsystem;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class SettingsActivity extends PreferenceActivity {
    public static final String KEY_APP_USAGE = "app_usage";
    public static final String KEY_APP_USAGE_INTERVAL = "app_usage_interval";
    public static final String KEY_APP_STATS = "app_stats";
    public static final String KEY_APP_STATS_INTERVAL = "app_stats_interval";
    public static final String KEY_WIFI_LIST = "wifi_list";
    public static final String KEY_WIFI_INTERVAL = "wifi_interval";
    public static final String KEY_LOCATION = "location_list";
    public static final String KEY_LOCATION_INTERVAL = "location_interval";
    public static final String KEY_PHONE_TILT = "phone_tilt";
    public static final String KEY_TILT_INTERVAL = "phone_tilt_interval";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        AlarmReceiver alarm = new AlarmReceiver();

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                              String key) {
            if (key.equals(KEY_APP_USAGE)) {
                Preference pref = findPreference(key);
                // Set summary to be the user-description for the selected value
                if (sharedPreferences.getBoolean(key, false)) {
                    alarm.setAlarm(getActivity().getApplication(), 1);
                }
                else {
                    alarm.cancelAlarm(getActivity().getApplication(), 1);
                }
            }
            if (key.equals(KEY_APP_STATS)) {
                Preference pref = findPreference(key);
                // Set summary to be the user-description for the selected value
                if (sharedPreferences.getBoolean(key, false)) {
                    alarm.setAlarm(getActivity().getApplication(), 2);
                }
                else {
                    alarm.cancelAlarm(getActivity().getApplication(), 2);
                }
            }
            if (key.equals(KEY_WIFI_LIST)) {
                Preference pref = findPreference(key);
                // Set summary to be the user-description for the selected value
                if (sharedPreferences.getBoolean(key, false)) {
                    alarm.setAlarm(getActivity().getApplication(), 3);
                }
                else {
                    alarm.cancelAlarm(getActivity().getApplication(), 3);
                }
            }
            if (key.equals(KEY_LOCATION)) {
                Preference pref = findPreference(key);
                // Set summary to be the user-description for the selected value
                if (sharedPreferences.getBoolean(key, false)) {
                    alarm.setAlarm(getActivity().getApplication(), 4);
                }
                else {
                    alarm.cancelAlarm(getActivity().getApplication(), 4);
                }
            }
            if (key.equals(KEY_PHONE_TILT)) {
                Preference pref = findPreference(key);
                // Set summary to be the user-description for the selected value
                if (sharedPreferences.getBoolean(key, false)) {
                    alarm.setAlarm(getActivity().getApplication(), 5);
                }
                else {
                    alarm.cancelAlarm(getActivity().getApplication(), 5);
                }
            }
            if (key.equals(KEY_APP_USAGE_INTERVAL)) {
                Preference pref = findPreference(key);
                // Set summary to be the user-description for the selected value
                pref.setSummary(sharedPreferences.getString(key, ""));
                if (sharedPreferences.getBoolean(KEY_APP_USAGE, false)) {
                    alarm.cancelAlarm(getActivity().getApplication(), 1);
                    alarm.setAlarm(getActivity().getApplication(), 1);
                }

            }
            if (key.equals(KEY_APP_STATS_INTERVAL)) {
                Preference pref = findPreference(key);
                // Set summary to be the user-description for the selected value
                pref.setSummary(sharedPreferences.getString(key, ""));
                if (sharedPreferences.getBoolean(KEY_APP_STATS, false)) {
                    alarm.cancelAlarm(getActivity().getApplication(), 2);
                    alarm.setAlarm(getActivity().getApplication(), 2);
                }

            }
            if (key.equals(KEY_WIFI_INTERVAL)) {
                Preference pref = findPreference(key);
                // Set summary to be the user-description for the selected value
                pref.setSummary(sharedPreferences.getString(key, ""));
                if (sharedPreferences.getBoolean(KEY_WIFI_LIST, false)) {
                    alarm.cancelAlarm(getActivity().getApplication(), 3);
                    alarm.setAlarm(getActivity().getApplication(), 3);
                }
            }
            if (key.equals(KEY_LOCATION_INTERVAL)) {
                Preference pref = findPreference(key);
                // Set summary to be the user-description for the selected value
                pref.setSummary(sharedPreferences.getString(key, ""));
                if (sharedPreferences.getBoolean(KEY_LOCATION, false)) {
                    alarm.cancelAlarm(getActivity().getApplication(), 4);
                    alarm.setAlarm(getActivity().getApplication(), 4);
                }

            }
            if (key.equals(KEY_TILT_INTERVAL)) {
                Preference pref = findPreference(key);
                // Set summary to be the user-description for the selected value
                pref.setSummary(sharedPreferences.getString(key, ""));
                if (sharedPreferences.getBoolean(KEY_PHONE_TILT, false)) {
                    alarm.cancelAlarm(getActivity().getApplication(), 5);
                    alarm.setAlarm(getActivity().getApplication(), 5);
                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }
    }



}
