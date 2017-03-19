package com.bms.mqp.behaviormodelsystem;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.PlacesResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// some code from aware test app on github

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class LocationService extends IntentService {

    public LocationService() {
        super("LocationService");
    }

    private static final String TAG = "LocationService";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 940;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onHandleIntent(Intent intent) {
        setupGoogleApiClient();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Awareness.SnapshotApi.getPlaces(mGoogleApiClient)
                .setResultCallback(new ResultCallback<PlacesResult>() {
                    @Override
                    public void onResult(@NonNull PlacesResult placesResult) {
                        if (!placesResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Could not get places.");
                            return;
                        }
                        List<PlaceLikelihood> placeLikelihoodList = placesResult.getPlaceLikelihoods();
                        // Show the top 5 possible location results.

                        ArrayList<PossiblePlace> possiblePlaces = new ArrayList<>();

                        for (int i = 0; i < 5; i++) {
                            PlaceLikelihood p = placeLikelihoodList.get(i);
                            PossiblePlace place = new PossiblePlace(p.getPlace().getName().toString(), p.getLikelihood());
                            possiblePlaces.add(place);
                            // ExternalSaver.save(p.getPlace().getName().toString() + ", likelihood: " + p.getLikelihood(), "Location.txt\n");
                        }

                        String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());

                        Message msg = Message.obtain();
                        Bundle b = new Bundle();
                        b.putParcelableArrayList("location", (ArrayList<? extends Parcelable>) possiblePlaces);
                        b.putString("time", date);
                        msg.setData(b);


                        try {
                            Log.i("tkeekjkefj","Trying to launch JSON saving");
                            ExternalSaver ex = new ExternalSaver(getApplicationContext());
                            ex.writeMessage(msg);
                        } catch (IOException e) {
                            Log.d("myapp", Log.getStackTraceString(e));

                        }

                    }
                });
        sendNotification("This works");
    }

    // Post a notification indicating whether a doodle was found.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


    private void setupGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();
        mGoogleApiClient.connect();
    }


}
