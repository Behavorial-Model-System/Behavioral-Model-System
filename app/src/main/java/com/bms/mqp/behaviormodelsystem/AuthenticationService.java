package com.bms.mqp.behaviormodelsystem;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.content.Context;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static com.google.android.gms.wearable.DataMap.TAG;
import static java.security.AccessController.getContext;

/**
 * Created by Arun on 1/17/2017.
 */

public class AuthenticationService extends IntentService {


    private String fileName = "checker";




    public AuthenticationService() {
        super("AuthenticationService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        // spawn a Drive service thread
        TelephonyManager telephonyManager;

        telephonyManager = (TelephonyManager) getSystemService(Context.
                TELEPHONY_SERVICE);

        fileName = telephonyManager.getDeviceId();

        Intent mIntent = new Intent(this, DriveService.class);
        mIntent.putExtra("fileName", fileName);
        startService(mIntent);

    }
}

