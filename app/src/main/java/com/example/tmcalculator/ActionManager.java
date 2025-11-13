// ActionManager.java - REFACTORED
package com.example.tmcalculator;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ActionManager {
    private static ActionManager instance;
    private final Context context;

    private Map<String, Map<String, String>> actionTree;
    private Map<String, String> actionMap;

    private ActionManager(Context context) {
        this.context = context.getApplicationContext();
        loadActionMap();
    }

    public static synchronized ActionManager getInstance(Context context) {
        if (instance == null) {
            instance = new ActionManager(context);
        }
        return instance;
    }

    public Map<String, Map<String, String>> getActionTree() {
        return actionTree;
    }

    public Map<String, String> getActionMap() {
        return actionMap;
    }

    private void loadActionMap() {
        if (actionTree != null) { // Don't reload if already loaded
            return;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Map<String, String>>>() {}.getType();

        try {
            InputStream inputStream = context.getAssets().open("json/localization/CHS/action.json");
            InputStreamReader reader = new InputStreamReader(inputStream);
            actionTree = gson.fromJson(reader, type);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error loading actions", Toast.LENGTH_SHORT).show();
        }

        actionMap = new HashMap<>();
        if (actionTree != null) {
            for (Map<String, String> category : actionTree.values()) {
                actionMap.putAll(category);
            }
        }
    }

    public String getActionName(String actionId) {
        return actionMap.get(actionId);
    }
}
