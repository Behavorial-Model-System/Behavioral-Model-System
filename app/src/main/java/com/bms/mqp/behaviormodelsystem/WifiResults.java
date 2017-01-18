package com.bms.mqp.behaviormodelsystem;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Arun on 10/30/2016.
 */

public class WifiResults implements Parcelable{
    private String SSID = "";
    private String BSSID = "";
    private String level = "";

    public WifiResults(String SSID, String BSSID, String level){
        this.SSID = SSID;
        this.BSSID = BSSID;
        this.level= level;
    }

    public WifiResults(){

    }

    protected WifiResults(Parcel in) {
        SSID = in.readString();
        BSSID = in.readString();
        level = in.readString();
    }

    public static final Creator<WifiResults> CREATOR = new Creator<WifiResults>() {
        @Override
        public WifiResults createFromParcel(Parcel in) {
            return new WifiResults(in);
        }

        @Override
        public WifiResults[] newArray(int size) {
            return new WifiResults[size];
        }
    };

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(SSID);
        dest.writeString(BSSID);
        dest.writeString(level);
    }
}