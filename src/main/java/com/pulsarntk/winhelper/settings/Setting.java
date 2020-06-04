package com.pulsarntk.winhelper.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONException;
import org.json.JSONObject;

public class Setting extends JSONObject {
    public static final File settingsFile = new File("Settings.json");
    private static JSONObject json = new JSONObject();
    private static FileWriter fileWriter;
    private static Scanner scanner;
    private String label;

    static {
        readFromFile();
    }

    public Setting(String label) {
        super(json.isNull(label) ? "{}" : json.getJSONObject(label).toString());
        this.label = label;
        json.put(label, this);
    }

    private Setting(String label, String json) {
        super(json);
        this.label = label;
    }

    private Setting(String label, Object o) {
        this.label = label;
    }

    public Setting newSettings(String label) {
        String json = optString(label);
        if (json.compareTo("") == 0)
            json = "{}";
        Setting temp = new Setting(label, json);
        put(label, temp);
        return temp;
    }

    @Override
    public JSONObject put(String key, Object value) {
        super.put(key, value);
        writeToFile();
        return this;
    }

    public static void writeToFile() {
        if (isFileValid()) {
            try {
                fileWriter = new FileWriter(settingsFile);
                fileWriter.write(json.toString());
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void readFromFile() {
        if (isFileValid()) {
            try {
                scanner = new Scanner(settingsFile);
                StringBuilder data = new StringBuilder();
                while (scanner.hasNextLine()) {
                    data.append(scanner.nextLine());
                }
                scanner.close();
                if (data.length() == 0)
                    data.append("{}");
                json = new JSONObject(data.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isFileValid() {
        if (settingsFile.exists()) {
            if (!settingsFile.isDirectory()) {
                return true;
            }
        } else {
            try {
                if (settingsFile.createNewFile())
                    return isFileValid();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
