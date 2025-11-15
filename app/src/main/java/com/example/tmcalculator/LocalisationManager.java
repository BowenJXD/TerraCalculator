package com.example.tmcalculator;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class LocalisationManager {

    private Map<String, String> warnings;
    private Map<String, String> action;
    private static final String WARNING_PATH = "json/localization/CHS/warning.json";
    private static final String ACTION_PATH = "json/localization/CHS/action.json";
    private Context context;
    private static LocalisationManager instance;

    private LocalisationManager(Context context) {
        this.context = context.getApplicationContext();
        loadWarningLocalisation();
        loadActionLocalisation();
    }

    public static synchronized LocalisationManager getInstance(Context context) {
        if (instance == null) {
            instance = new LocalisationManager(context);
        }
        return instance;
    }

    public void loadWarningLocalisation() {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, String>>() {}.getType();
        try {
            InputStream inputStream = context.getAssets().open(WARNING_PATH);
            InputStreamReader reader = new InputStreamReader(inputStream);
            warnings = gson.fromJson(reader, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadActionLocalisation() {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, String>>() {}.getType();
        try {
            InputStream inputStream = context.getAssets().open(ACTION_PATH);
            InputStreamReader reader = new InputStreamReader(inputStream);
            action = gson.fromJson(reader, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getWarningLocalisation(String key) {
        return warnings.get(key);
    }

    public String getActionLocalisation(String key) {
        if (action == null) return null;
        return action.get(key);
    }
}
