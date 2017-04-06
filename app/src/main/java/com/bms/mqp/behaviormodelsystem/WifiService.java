package com.bms.mqp.behaviormodelsystem;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class WifiService extends IntentService {

    public WifiService() {
        super("WifiService");
    }

    WifiManager wifi;
    ListView lv;
    TextView textStatus;
    Button buttonScan;
    int size = 0;
    List<ScanResult> results;
    WifiResultsAdapter adapter;

    String ITEM_KEY = "key";
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
    ArrayList<WifiResults> networkList=new ArrayList<WifiResults>();

    @Override
    protected void onHandleIntent(Intent intent) {
        wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false)
        {   //Deprecated section, used to turn on wifi if it wasn't on but that negatively effected user experience
            //Toast.makeText(this.getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            //wifi.setWifiEnabled(true);
            return;
        }
        // having trouble with this part
        adapter = new WifiResultsAdapter(this, networkList);

        networkList.clear();
        results = wifi.getScanResults();
        size = results.size();
        //Iterate over all of the wifi results
        for (int i = 0; i < size; i++) {
            WifiResults temp = new WifiResults(results.get(i).SSID, results.get(i).BSSID, results.get(i).level);
            networkList.add(i, temp);
            adapter.notifyDataSetChanged();
            // ExternalSaver.save("SSID: "+results.get(i).SSID+" BSSID: "+results.get(i).BSSID+ " Strength: "+Integer.toString(results.get(i).level)+"\n","Wifi.txt");
        }
        Collections.sort(networkList, Collections.<WifiResults>reverseOrder());
        String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
        Message msg = Message.obtain(); //Create a new instanceOf Message
        Bundle b = new Bundle(); //Create a new instanceOf Bundle
        b.putParcelableArrayList("wifi", networkList);
        b.putString("time", date);
        msg.setData(b);


        try {
            Log.i("tkeekjkefj","Trying to launch JSON saving");
            ExternalSaver ex = new ExternalSaver(getApplication());
            ex.writeMessage(msg);
        } catch (IOException e) {
            Log.d("myapp", Log.getStackTraceString(e));

        }


        wifi.startScan();
    }

}
