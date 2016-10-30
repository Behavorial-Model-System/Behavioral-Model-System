package com.bms.mqp.behaviormodelsystem;

/**
 * Created by Arun on 10/30/2016.
 */

public class WifiResults {
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
}