package com.bms.mqp.behaviormodelsystem;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

import static android.hardware.SensorManager.*;
import static com.bms.mqp.behaviormodelsystem.SchedulingService.NOTIFICATION_ID;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class TiltService extends IntentService implements SensorEventListener {

    public TiltService() {
        super("TiltService");
    }

    private SensorManager mSensorManager;
    Sensor mAccelerometer;
    Sensor mMagnometer;
    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];

    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];

    public int counter = 0;
    public int max_amount_per = 5;
    public static final String TAG = "Scheduling Demo";
    // An ID used to post the notification.
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    @Override
    protected void onHandleIntent(Intent intent) {
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnometer= mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorManager.registerListener(this, mAccelerometer,
                SENSOR_DELAY_NORMAL, SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagnometer,
                SENSOR_DELAY_NORMAL, SENSOR_DELAY_UI);

        // BEGIN_INCLUDE(service_onhandle)
        // The URL from which to fetch content.
        // Log.v(TAG, notificationString);
        //sendNotification("Tilt Notification");
        SystemClock.sleep(7000);


        // Release the wake lock provided by the BroadcastReceiver.
        mSensorManager.unregisterListener(this);
        AlarmReceiver.completeWakefulIntent(intent);
        // END_INCLUDE(service_onhandle)
    }


    // Post a notification indicating whether a doodle was found.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        mSensorManager.getRotationMatrix(mRotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);

        // "mRotationMatrix" now has up-to-date information.

        mSensorManager.getOrientation(mRotationMatrix, mOrientationAngles);

        // "mOrientationAngles" now has up-to-date information.
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mMagnetometerReading,
                    0, mMagnetometerReading.length);
        }
        updateOrientationAngles();
        if(counter < max_amount_per) {
            saveInfo();
            counter++;
        }
    }

    public void saveInfo(){

        String test = "results: " + mOrientationAngles[0]*180/Math.PI +" "+mOrientationAngles[1]*180/Math.PI+ " "+ mOrientationAngles[2]*180/Math.PI+"\n";
        String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
        double x = mOrientationAngles[0]*180/Math.PI;
        double y = mOrientationAngles[1]*180/Math.PI;
        double z = mOrientationAngles[2]*180/Math.PI;

        double array[] = {x, y, z};

        Message msg = Message.obtain();
        Bundle b = new Bundle();
        b.putDoubleArray("tilt", array);
        b.putString("time", date);
        msg.setData(b);

        try {
            Log.i("tkeekjkefj","Trying to launch JSON saving");
            ExternalSaver ex = new ExternalSaver(getApplication());
            ex.writeMessage(msg);
        } catch (IOException e) {
            Log.d("myapp", Log.getStackTraceString(e));
        }


        // ExternalSaver.save(test, "tiltService");
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
