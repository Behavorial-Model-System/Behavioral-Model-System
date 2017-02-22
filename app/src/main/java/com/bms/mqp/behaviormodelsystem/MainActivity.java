package com.bms.mqp.behaviormodelsystem;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.telephony.TelephonyManager;
import android.content.Context;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TiltFragment.OnFragmentInteractionListener {
    AlarmReceiver alarm = new AlarmReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String PREFS_NAME = "MyPrefsFile";

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("my_first_time", true)) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 1);

            new AlertDialog.Builder(this)
                    .setTitle("Open Usage Access")
                    .setMessage("You will now have to give usage access permission")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();

            settings.edit().putBoolean("my_first_time", false).commit();
        }



        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        displayView(R.id.nav_apps);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean appUsage = SP.getBoolean("app_usage", false);
        boolean appStats = SP.getBoolean("app_stats", false);
        boolean wifi = SP.getBoolean("wifi_list", false);
        boolean location = SP.getBoolean("location_list", false);
        boolean tilt = SP.getBoolean("phone_tilt", false);
        boolean auth = SP.getBoolean("auth", false);


        if (appUsage) {
            alarm.setAlarm(this, 1);
        }
        if (appStats) {
            alarm.setAlarm(this, 2);
        }
        if (wifi) {
            alarm.setAlarm(this, 3);
        }
        if (location) {
            alarm.setAlarm(this, 4);
        }
        if (tilt) {
            alarm.setAlarm(this, 5);
        }
        if (auth) {
            alarm.setAlarm(this, 6);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // Menu options to set and cancel the alarm.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // When the user clicks START ALARM, set the alarm.
            case R.id.settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
        }
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displayView(item.getItemId());
        return true;
    }

    public void displayView(int viewId) {

        Fragment fragment = null;
        String title = getString(R.string.app_name);

        switch (viewId) {
            case R.id.nav_apps:
                fragment = new AppUsageEventsFragment();
                title  = "App Usage Events";
                break;
            case R.id.nav_stats:
                fragment = new AppUsageStatisticsFragment();
                title = "App Usage Stats";
                break;
            case R.id.nav_location:
                // fragment = new EventsFragment();
                title = "Location";
                break;
            case R.id.nav_touch:
                // fragment = new EventsFragment();
                title = "Touch";
                break;
            case R.id.nav_accelerometer:
                // fragment = new EventsFragment();
                fragment = new TiltFragment();
                title = "Accelerometer";
                break;
            case R.id.nav_wifi:
                // fragment = new EventsFragment();
                fragment = new WifiFragment();
                title = "WiFi";
                break;
            case R.id.nav_auth:
                // fragment = new EventsFragment();
                fragment = new AuthenticationFragment();
                title = "Authentication Checker";
                break;

        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
