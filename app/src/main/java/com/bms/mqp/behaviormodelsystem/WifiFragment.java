package com.bms.mqp.behaviormodelsystem;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.*;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Arun on 10/29/2016.
 */

public class WifiFragment extends Fragment implements View.OnClickListener {
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



    public WifiFragment(){

    }

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
        View view = inflater.inflate(R.layout.fragment_wifi, container, false);
        buttonScan = (Button) view.findViewById(R.id.buttonScan);
        buttonScan.setText("Rescan Networks");
        buttonScan.setOnClickListener(this);
        lv = (ListView) view.findViewById(R.id.list);



        wifi = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false)
        {
            Toast.makeText(getActivity().getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
        // having trouble with this part
        adapter = new WifiResultsAdapter(getActivity(),networkList);
        lv.setAdapter(adapter);


        getActivity().registerReceiver(new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context c, Intent intent)
            {
                networkList.clear();
                results = wifi.getScanResults();
                size = results.size();
                for(int i =0 ; i<size;i++){
                    WifiResults temp = new WifiResults(results.get(i).SSID,results.get(i).BSSID,Integer.toString(results.get(i).level));
                    networkList.add(i,temp);
                    adapter.notifyDataSetChanged();
                }
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        return view;
    }


    public void onClick(View view)
    {
        //arraylist.clear();
        wifi.startScan();

        Toast.makeText(getActivity(), "Scanning...." + size, Toast.LENGTH_SHORT).show();
       /*
        try
        {
            size = size - 1;
            while (size >= 0)
            {
                //HashMap<String, String> item = new HashMap<String, String>();
                //item.put(ITEM_KEY, results.get(size).SSID + "  " + results.get(size).capabilities);

                //arraylist.add(item);
                size--;
                adapter.notifyDataSetChanged();
            }
        }
        catch (Exception e)
        { }
        */
    }

}

