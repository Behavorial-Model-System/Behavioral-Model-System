package com.bms.mqp.behaviormodelsystem;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created by Walter on 11/12/2016.
 */

public class DriveService extends IntentService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "walter's tag";
    public static final String DRIVEINFO = "DriveStuff";
    public static final String FolderID = "FolderID";
    private GoogleApiClient googleApiClient;
    private String fileName;
    private DriveId driveID;
    private DriveFolder root;
    private SharedPreferences prefs;
    boolean STATUS = true;
    public static boolean wasauth = false;

    public DriveService() {
        super("DriveService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        showMessage("DriveService onStartCommand");

        // apparently this is null sometimes????
        //fileName = intent.getStringExtra("fileName");



        TelephonyManager telephonyManager;

        telephonyManager = (TelephonyManager) getSystemService(Context.
                TELEPHONY_SERVICE);

        fileName = telephonyManager.getDeviceId();



        prefs = getSharedPreferences(DRIVEINFO, MODE_PRIVATE);
        String name = prefs.getString(FolderID, null);

        if(name == null){
            Intent mIntent = new Intent(this, BaseFolderCreationService.class);
            startService(mIntent);
            // wait 30 seconds to make sure the folder has been created
            SystemClock.sleep(30000);
        }


        buildGoogleApiClient();
        googleApiClient.connect();
    }

    public void showMessage(String message) {
        //Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Log.i(TAG, message);
    }

    private void buildGoogleApiClient() {
        Log.i(TAG, "In buildGoogleApiClient()");
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    /*handles connection callbacks*/
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "In onConnected()");
        //query the root folder in drive to see if save file has been created already
        new Thread() {
            @Override
            public void run() {
                query();
            }
        }.start();

    }

    /*handles suspended connection callbacks*/
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "In onConnectionSuspended()");
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "In onConnectionFailed()");
        Intent i = new Intent(this, ResolverActivity.class);
        i.putExtra(ResolverActivity.CONNECT_RESULT_KEY, result);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    /**
     * Checks if the save file has been created yet and creates it if not.
     * Then it calls reads()
     */
    public void query() {
        Log.i(TAG, "In query()");
        Query query = new Query.Builder().addFilter(
                Filters.and(
                        Filters.eq(SearchableField.TITLE, fileName),
                        Filters.eq(SearchableField.TRASHED, false))
        ).build();
        showMessage("The filename is " + fileName);

        //                    SharedPreferences ourSharedPreferences = getSharedPreferences(DRIVEINFO, Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = ourSharedPreferences.edit();
//                    editor.putString(FolderID, result.getDriveFolder().getDriveId());
//                    editor.commit();


        String name = prefs.getString(FolderID, null);

        if(name != null){
            DriveId folderID = DriveId.decodeFromString(name);
            root = folderID.asDriveFolder();
        }
        else{
            // something is wrong and the folder name is still null
          //  root = Drive.DriveApi.getRootFolder(googleApiClient);
            Log.i("Drive Service","something went wrong with getting the base saving folder");
            return;
        }

        root.queryChildren(googleApiClient, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(@NonNull DriveApi.MetadataBufferResult metadataBufferResult) {
                if(metadataBufferResult.getMetadataBuffer() == null){
                    showMessage("null check loop");
                    query();
                }
                else {
                    int count = metadataBufferResult.getMetadataBuffer().getCount();
                    showMessage("found " + count + " file(s) matching the name of the save file");
                    if (count == 0) {
                        //if 0 matching filenames were found, create a new file
                        Drive.DriveApi.newDriveContents(googleApiClient).setResultCallback(driveContentsCallback);
                        showMessage("Authenticated since the file matching the name of the save file was not found");
                        STATUS = true;
                        sendNotification();
                    } else if (count == 1) {
                        //if 1 matching filename was found, write to that file
                        driveID = metadataBufferResult.getMetadataBuffer().get(0).getDriveId();
                        read();

                    } else {
                        showMessage("found too many matching filenames, dont know which one to save to");
                    }
                    metadataBufferResult.release();
                }


            }
        });


    }

    // notify authentication status
    public void sendNotification(){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Authentication Service Status")
                        .setContentText("Hello World!")
                        .setPriority(Notification.PRIORITY_MAX)
                        .setDefaults(Notification.DEFAULT_VIBRATE);

        NotificationManager notificationManager;
        int mNotificationId = 042;
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // only send a notification if the authentication state has changed from last time
        if (STATUS && wasauth == false ) {
            wasauth = true;
            mBuilder.setContentText("Authenticated");
            notificationManager.notify(mNotificationId, mBuilder.build());
        } else if(!STATUS && wasauth == true){
            wasauth = false;
            mBuilder.setContentText("Deauthenticated");
            notificationManager.notify(mNotificationId, mBuilder.build());
        }
    }
    /**
     * reads file
     */
    public void read() {
        Log.i(TAG, "In write()");
        //Log.i(TAG, "driveID: " + driveID);
        DriveFile file = driveID.asDriveFile();
        file.open(googleApiClient, DriveFile.MODE_READ_WRITE, null).setResultCallback(contentsOpenedCallback);
    }

    /**
     * Writes to the file
     */
    public void write() {
        Log.i(TAG, "In write()");
        //Log.i(TAG, "driveID: " + driveID);
        DriveFile file = driveID.asDriveFile();
        file.open(googleApiClient, DriveFile.MODE_READ_WRITE, null).setResultCallback(contentsOpenedCallback2);
    }



    /**
     * Runs when a content is created successfully
     * Creates a new file
     */
    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create new file contents");
                        return;
                    }
                    final DriveContents driveContents = result.getDriveContents();

                    // Perform I/O off the UI thread.
                    new Thread() {
                        @Override
                        public void run() {
                            // write content to DriveContents
                            OutputStream outputStream = driveContents.getOutputStream();
                            Writer writer = new OutputStreamWriter(outputStream);
                            try {
                                writer.write("authenticated\n");
                                writer.close();
                                outputStream.close();
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage());
                            }

                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle(fileName)
                                    .setMimeType("text/plain")
                                    .setStarred(true).build();

                            // create a file on root folder

                            root.createFile(googleApiClient, changeSet, driveContents)
                                .setResultCallback(fileCallback);
                        }
                    }.start();
                }
            };


    /**
     * Runs when file is created successfully
     * Saves the file's drive id to driveID
     */
    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the file");
                        return;
                    }
                    showMessage("Created a file with content: " + result.getDriveFile().getDriveId());
                    driveID = result.getDriveFile().getDriveId();
                    write();
                }
            };
    /**
     * Runs when the file is successfully opened
     * Appends the textToSave string to the file
     */
    final private ResultCallback<DriveApi.DriveContentsResult> contentsOpenedCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.e(TAG, "file could not be opened");
                        return;
                    }
                    // DriveContents object contains pointers
                    // to the actual byte stream

                    try {
                        DriveContents contents = result.getDriveContents();
                        ParcelFileDescriptor parcelFileDescriptor = contents.getParcelFileDescriptor();
                        FileInputStream fileInputStream = new FileInputStream(parcelFileDescriptor
                                .getFileDescriptor());
                        // Read the first line of the file
                        BufferedReader reader = null;
                        //fileInputStream.read(new byte[fileInputStream.available()]);
                        reader = new BufferedReader(new InputStreamReader(fileInputStream));

                        String line = reader.readLine();

                        if(line.toLowerCase().contains("deauthenticated")) {
                            STATUS = false;
                            showMessage("deauthenticated this time");
                            sendNotification();
                        }

                        else {
                            STATUS = true;
                            sendNotification();
                        }
                        fileInputStream.close();
                        contents.commit(googleApiClient, null);
                        Log.e(TAG, "read successful");

                    } catch (IOException e) {
                        STATUS = true;
                        sendNotification();
                        Log.e(TAG, "exception reading file");
                    }

                }
            };

    /**
     * Runs when the file is successfully opened
     * Appends the textToSave string to the file
     */
    final private ResultCallback<DriveApi.DriveContentsResult> contentsOpenedCallback2 =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("file cant be opened");
                        return;
                    }
                    // DriveContents object contains pointers
                    // to the actual byte stream
                    try {
                        DriveContents contents = result.getDriveContents();
                        ParcelFileDescriptor parcelFileDescriptor = contents.getParcelFileDescriptor();
                        FileInputStream fileInputStream = new FileInputStream(parcelFileDescriptor
                                .getFileDescriptor());
                        // Read to the end of the file.
                        fileInputStream.read(new byte[fileInputStream.available()]);

//                        // Append to the file.
//                        FileOutputStream fileOutputStream = new FileOutputStream(parcelFileDescriptor
//                                .getFileDescriptor());
//                       // Writer writer = new OutputStreamWriter(fileOutputStream);
//                       // writer.write(data);
//                       // writer.flush();
//                        //writer.close();
//                        fileOutputStream.close();
//                        fileInputStream.close();
//                        contents.commit(googleApiClient, null);
//                        showMessage("write successful");

                    } catch (IOException e) {
                        showMessage("IOException while appending to the output stream" + e);
                    }
                }
            };
}
