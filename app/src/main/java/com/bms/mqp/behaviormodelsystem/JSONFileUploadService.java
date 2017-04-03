package com.bms.mqp.behaviormodelsystem;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by Arun on 2/24/2017.
 */

public class JSONFileUploadService extends IntentService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "walter's tag";
    public static final String DRIVEINFO = "DriveStuff";
    public static final String FolderID = "FolderID";
    private GoogleApiClient googleApiClient;
    private ArrayList<String> filestoUploadPath;
    private ArrayList<String> filestoUpload;
    private String folderName;
    private DriveFolder root;
    private DriveId tempfiledriveID;


    public JSONFileUploadService() {
        super("JSONFileUploadService");
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        showMessage("JSONFileUploadService onStartCommand");

        Intent mIntent = new Intent(this, BaseFolderCreationService.class);
        startService(mIntent);
        TelephonyManager telephonyManager;

        telephonyManager = (TelephonyManager) getSystemService(Context.
                TELEPHONY_SERVICE);

        folderName = telephonyManager.getDeviceId() +"Data";
        filestoUploadPath = (ArrayList<String>)intent.getSerializableExtra("FILEPATHS");
        filestoUpload = (ArrayList<String>)intent.getSerializableExtra("FILENAMES");
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
     * Checks if folder for JSONS has been created yet, if not then create it
     */
    public void query() {
        Log.i(TAG, "In query()");
        final Query query = new Query.Builder().addFilter(
                Filters.and(
                        Filters.eq(SearchableField.TITLE, folderName),
                        Filters.eq(SearchableField.TRASHED, false))
        ).build();


        SharedPreferences prefs = getSharedPreferences(DRIVEINFO, MODE_PRIVATE);
        String name = prefs.getString(FolderID, null);

        if(name != null){
            DriveId folderID = DriveId.decodeFromString(name);
            root = folderID.asDriveFolder();
        }
        else{
            root = Drive.DriveApi.getRootFolder(googleApiClient);
            return;
        }
        root.queryChildren(googleApiClient, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(@NonNull DriveApi.MetadataBufferResult metadataBufferResult) {
                if(metadataBufferResult.getMetadataBuffer() == null){
                    showMessage("null check loop fileupload");
                    query();
                    return;
                }
                else {
                    int count = metadataBufferResult.getMetadataBuffer().getCount();
                    showMessage("found " + count + " folder(s) matching the name of the save folder");
                    if (count == 0) {
                        //if 0 matching filenames were found, create a new folder
                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle(folderName)
                                .setMimeType("application/vnd.google-apps.folder")
                                .setStarred(true).build();

                        // create a folder on root folder
                        root.createFolder(googleApiClient, changeSet)
                                .setResultCallback(folderCallback);
                        //query();

                    } else if (count == 1) {
//                        SharedPreferences ourSharedPreferences = getSharedPreferences(DRIVEINFO, Context.MODE_PRIVATE);
//                        String folderDriveID = ourSharedPreferences.getString(FolderID,"");
//                        if(folderDriveID.equals("")){
//                            showMessage("DriveID is either not properly being saved or retrieved");
//                        }
//                        else{
//                            showMessage("bleen " + folderDriveID);
//                            DriveId folderID = metadataBufferResult.getMetadataBuffer().get(0).getDriveId();
//                            DriveFolder baseJSON = folderID.asDriveFolder();
//                            writeFiles(baseJSON);
//
//                        }
                        //if 1 matching filename was found, write to that file
                        //driveID = metadataBufferResult.getMetadataBuffer().get(0).getDriveId();
                        //read();

                        DriveId folderID = metadataBufferResult.getMetadataBuffer().get(0).getDriveId();
                        DriveFolder baseJSON = folderID.asDriveFolder();
                        writeFiles(baseJSON);
                    } else {
                        showMessage("found too many matching filenames, dont know which one to save to");
                    }
                    metadataBufferResult.release();
                }

            }
        });


    }

    /**
     * reads file
     */
    public void read() {
        Log.i(TAG, "In write()");
        //Log.i(TAG, "driveID: " + driveID);
        DriveFile file = tempfiledriveID.asDriveFile();
        file.open(googleApiClient, DriveFile.MODE_READ_WRITE, null).setResultCallback(contentsOpenedCallback);
    }

    /**
     * Writes to the file
     */
    public void write() {
        Log.i(TAG, "In write()");
        //Log.i(TAG, "driveID: " + driveID);
        DriveFile file = tempfiledriveID.asDriveFile();
        file.open(googleApiClient, DriveFile.MODE_READ_WRITE, null).setResultCallback(contentsOpenedCallback2);
    }

    public void writeFiles(final DriveFolder df){


        for(int i = 0; i <filestoUpload.size();i++){
            final int temp = i;
            Drive.DriveApi.newDriveContents(googleApiClient).setResultCallback( new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create new file contents");
                        return;
                    }
                    final DriveContents driveContents = result.getDriveContents();


                    new Thread() {
                        @Override
                        public void run() {
                            // write content to DriveContents
                            OutputStream outputStream = driveContents.getOutputStream();
                            Writer writer = new OutputStreamWriter(outputStream);
                            try {
                                FileReader fr = new FileReader(filestoUploadPath.get(temp));
                                int temp=fr.read();
                                while(temp!=-1) {
                                    writer.write(temp);
                                    temp = fr.read();
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle(filestoUpload.get(temp))
                                    .setMimeType("application/json").build();


                            // create a file on root folder
                            df.createFile(googleApiClient, changeSet, driveContents);
                        }

                    }.start();
                }
            });
        }

    }




    /**
     * Runs when a content is created successfully
     * Creates a new file
     */
//    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new
//            ResultCallback<DriveApi.DriveContentsResult>() {
//                @Override
//                public void onResult(DriveApi.DriveContentsResult result) {
//                    if (!result.getStatus().isSuccess()) {
//                        showMessage("Error while trying to create new file contents");
//                        return;
//                    }
//                    final DriveContents driveContents = result.getDriveContents();
//
//
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            // write content to DriveContents
//                            OutputStream outputStream = driveContents.getOutputStream();
//                            Writer writer = new OutputStreamWriter(outputStream);
//                            try {
//                                writer.write("authenticated\n");
//                                writer.close();
//                                outputStream.close();
//                            } catch (IOException e) {
//                                Log.e(TAG, e.getMessage());
//                            }
//
//                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
//                                    .setTitle(filestoUpload.get(i))
//                                    .setMimeType("application/json").build();
//
//
//                                // create a folder on root folder
//                                Drive.DriveApi.getRootFolder(googleApiClient)
//                                        .createFile(googleApiClient, changeSet, driveContents)
//                                        .setResultCallback(fileCallback);
//                        }
//                    }.start();
//                }
//            };
//
//    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
//            ResultCallback<DriveFolder.DriveFileResult>() {
//                @Override
//                public void onResult(DriveFolder.DriveFileResult result) {
//                    if (!result.getStatus().isSuccess()) {
//                        showMessage("Error while trying to create the file");
//                        return;
//                    }
//                    showMessage("Created a file with content: " + result.getDriveFile().getDriveId());
//                    tempfiledriveID = result.getDriveFile().getDriveId();
//                    write();
//                }
//            };

    /**
     * Runs when file is created successfully
     * Saves the file's drive id to driveID
     */
    final private ResultCallback<DriveFolder.DriveFolderResult> folderCallback = new
            ResultCallback<DriveFolder.DriveFolderResult>() {
                @Override
                public void onResult(DriveFolder.DriveFolderResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the file");
                        return;
                    }
                    showMessage("Created a folder with content: " + result.getDriveFolder().getDriveId());
                    showMessage("Created a folder with resourceID: " + result.getDriveFolder().getDriveId().encodeToString());
//
//                    SharedPreferences ourSharedPreferences = getSharedPreferences(DRIVEINFO, Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = ourSharedPreferences.edit();
//                    editor.putString(FolderID, result.getDriveFolder().getDriveId());
//                    editor.commit();

                    // need to put a for loop for writing files here
                    writeFiles(result.getDriveFolder().getDriveId().asDriveFolder());
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

                        fileInputStream.close();
                        contents.commit(googleApiClient, null);
                        Log.e(TAG, "read successful");

                    } catch (IOException e) {

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
//                        Writer writer = new OutputStreamWriter(fileOutputStream);
//                        writer.write(data);
//                        writer.flush();
//                        writer.close();
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