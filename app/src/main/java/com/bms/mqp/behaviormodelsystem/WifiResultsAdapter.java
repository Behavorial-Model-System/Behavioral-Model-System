package com.bms.mqp.behaviormodelsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Arun on 10/30/2016.
 */

public class WifiResultsAdapter extends BaseAdapter {
    private static ArrayList<WifiResults> searchArrayList;

    private LayoutInflater mInflater;

    public WifiResultsAdapter(Context context, ArrayList<WifiResults> results) {
        searchArrayList = results;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return searchArrayList.size();
    }

    public Object getItem(int position) {
        return searchArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.wifirow, null);
            holder = new ViewHolder();
            holder.txtSSID = (TextView) convertView.findViewById(R.id.SSID);
            holder.txtBSSID = (TextView) convertView.findViewById(R.id.BSSID);
            holder.txtLevel = (TextView) convertView.findViewById(R.id.level);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtSSID.setText(searchArrayList.get(position).getSSID());
        holder.txtBSSID.setText(searchArrayList.get(position).getBSSID());
        holder.txtLevel.setText(searchArrayList.get(position).getLevel());

        return convertView;
    }

    static class ViewHolder {
        TextView txtSSID;
        TextView txtBSSID;
        TextView txtLevel;
    }
}