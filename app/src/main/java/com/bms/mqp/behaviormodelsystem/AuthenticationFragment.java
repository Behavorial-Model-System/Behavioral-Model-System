package com.bms.mqp.behaviormodelsystem;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Arun on 1/17/2017.
 */

public class AuthenticationFragment extends Fragment implements View.OnClickListener {
    Button checkButton;
    TextView status;
    // notification in notification bar


    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_authentication, container, false);
        checkButton = (Button) view.findViewById(R.id.checkButton);
        checkButton.setText("Recheck Status");
        checkButton.setOnClickListener(this);
        status = (TextView) view.findViewById(R.id.authtext);
        return view;
    }

    public void checkStatus(){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Authentication Service Status")
                        .setContentText("Hello World!");
        NotificationManager notificationManager;
        int mNotificationId = 001;
        notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);

        if(SystemClock.currentThreadTimeMillis()%2 == 0) {
            status.setText("Authenticated");
            mBuilder.setContentText("Authenticated");
            notificationManager.notify(mNotificationId, mBuilder.build());
        }
        else{
            status.setText("Deauthenticated");
            mBuilder.setContentText("Deauthenticated");
            notificationManager.notify(mNotificationId, mBuilder.build());
        }

    }

    public void onClick(View view)
    {
        status.setText("Checking...");
        Toast.makeText(getActivity(), "Checking Status....", Toast.LENGTH_SHORT).show();
        checkStatus();


    }

}

