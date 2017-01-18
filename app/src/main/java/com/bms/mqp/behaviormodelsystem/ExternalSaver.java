package com.bms.mqp.behaviormodelsystem;

/**
 * Created by Stephen on 11/16/2016.
 */

import android.bluetooth.le.ScanResult;
import android.os.Environment;
import android.os.Message;
import android.util.JsonWriter;
import android.util.Log;

import com.google.android.gms.awareness.snapshot.PlacesResult;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

/**
 * Created by Walter on 10/3/2016.
 * "Externally" saves some text in /storage/emulated/0/aaTutorial/savedFile.txt
 */

public class ExternalSaver {
    //location of folder to create and store data in
    public static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BigMoney";
    private static final String TAG = "Writer";

    /** appends text to a textfile, creates the textfile if it doesnt exist
     *
     * @param text string to write to file
     * @param fileName name of textfile to create or append to
     */
    public static void save(String text, String fileName) {
        //check the directory has been created
        File dir = new File(path);
        if(!dir.exists()){
            boolean success = dir.mkdir();
            if(! success){
                Log.e("walter's tag", "In Saver.java, setup() failed to create directory");
                return;
            }
        }

        //save the text file in the directory
        File file = new File(path + "/" + fileName);
        Save(file, text);
    }

    /** appends text to a default text file, called savedFile.txt
     *
     * @param text string to write to file
     */
    public static void save(String text) {
        File file = new File(path + "/savedFile.txt");

        Save(file, text);
    }

    //helper for saving
    private static void Save(File file, String data) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            try {
                fos.write(data.getBytes());
                //fos.write("\n".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeMessage(Message message) throws IOException {
        File file = new File(path + "/savedFile.json");
        OutputStream fos = null;

        try {
            fos = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, "UTF-8"));
        writer.setIndent("  ");

        writer.beginArray();
        writer.beginObject();
        writer.name("time").value(message.getData().getString("time"));
        // writer.name("timestamp").value(message.getData().getString("time"));
        if (message.getData().getDoubleArray("tilt") != null) {
            Log.v(TAG, "tilt write");
            writer.name("tilt");
            writeTiltArray(writer, message.getData().getDoubleArray("tilt"));
        }

        if (message.getData().getParcelableArrayList("usageEvents") != null) {
            Log.v(TAG, "usageEvents write");
            writer.name("usageEvents");
            writeUsageEvents(writer, message);
        }

        if (message.getData().getParcelableArrayList("usageStats") != null) {
            Log.v(TAG, "usageStats write");
            writer.name("usageStats");
            writeUsageStats(writer, message);
        }

        if (message.getData().getParcelableArrayList("wifi") != null) {
            Log.v(TAG, "wifi write");
            writer.name("wifi");
            writeWifiList(writer, message);
        }

        if (message.getData().getParcelableArrayList("location") != null) {
            Log.v(TAG, "location write");
            writer.name("location");
            writeLocations(writer, message);
        }
        writer.endObject();
        writer.endArray();

        writer.close();
    }

    public static void writeLocations(JsonWriter writer, Message m) throws IOException {
        List<PossiblePlace> placeLikelihoodList = m.getData().getParcelableArrayList("location");
        writer.beginArray();
        for (PossiblePlace p : placeLikelihoodList) {
            writeLocation(writer, p);
        }
        writer.endArray();
    }

    public static void writeLocation(JsonWriter writer, PossiblePlace result) throws IOException {
        writer.beginObject();
        writer.name("location").value(result.name);
        writer.name("likelihood").value(result.likelihood);
        writer.endObject();
    }



    public static void writeWifiList(JsonWriter writer, Message m) throws IOException {
        List<WifiResults> results = m.getData().getParcelableArrayList("wifi");
        writer.beginArray();
        for (WifiResults result : results) {
            writeScanResult(writer, result);
        }
        writer.endArray();
    }

    public static void writeScanResult(JsonWriter writer, WifiResults result) throws IOException {
        writer.beginObject();
        writer.name("ssid").value(result.getSSID());
        writer.name("bssid").value(result.getBSSID());
        writer.name("level").value(result.getLevel());
        writer.endObject();
    }

    public static void writeUsageEvents(JsonWriter writer, Message m) throws IOException {
        List<CustomUsageEvents> events = m.getData().getParcelableArrayList("usageEvents");
        writer.beginArray();
        for (CustomUsageEvents event : events) {
            writeUsageEvent(writer, event);
        }
        writer.endArray();
    }

    public static void writeUsageEvent(JsonWriter writer, CustomUsageEvents event) throws IOException {
        writer.beginObject();
        writer.name("name").value(event.usageEvent.getPackageName());
        writer.name("time").value(event.usageEvent.getTimeStamp());
        writer.name("type").value(event.usageEvent.getEventType());
        writer.endObject();
    }


    public static void writeUsageStats(JsonWriter writer, Message m) throws IOException {
        List<CustomUsageStats> stats = m.getData().getParcelableArrayList("usageStats");
        writer.beginArray();
        for (CustomUsageStats stat : stats) {
            writeUsageStat(writer, stat);
        }
        writer.endArray();
    }

    public static void writeUsageStat(JsonWriter writer, CustomUsageStats stat) throws IOException {
        writer.beginObject();
        writer.name("name").value(stat.usageStats.getPackageName());
        writer.name("first").value(stat.usageStats.getFirstTimeStamp());
        writer.name("last").value(stat.usageStats.getLastTimeStamp());
        writer.name("recent").value(stat.usageStats.getLastTimeUsed());
        writer.name("foreground").value(stat.usageStats.getTotalTimeInForeground());
        writer.endObject();
    }

    public static void writeTiltArray(JsonWriter writer, double tiltvalues[]) throws IOException {
        writer.beginArray();
        for (Double value : tiltvalues) {
            writer.value(value);
        }
        writer.endArray();
    }



}