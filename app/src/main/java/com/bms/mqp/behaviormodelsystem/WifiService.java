package com.bms.mqp.behaviormodelsystem;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
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
        {
            Toast.makeText(this.getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
        // having trouble with this part
        adapter = new WifiResultsAdapter(this, networkList);

        networkList.clear();
        results = wifi.getScanResults();
        size = results.size();
        for(int i =0 ; i<size;i++){
            WifiResults temp = new WifiResults(results.get(i).SSID,results.get(i).BSSID,Integer.toString(results.get(i).level));
            networkList.add(i,temp);
            adapter.notifyDataSetChanged();
        }

        wifi.startScan();
    }

}
