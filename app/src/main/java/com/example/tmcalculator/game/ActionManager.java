// ActionManager.java - REFACTORED
package com.example.tmcalculator.game;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Singleton that manages the localisation of actions, as well as abbreviation.
 * Supports identifying the exact action given the action type and the snapshot.
 * E.g. user inputs UPGRADE_SHIPPING in a snapshot that has shipping level of 2, it would identify
 * the action as UPGRADE_SHIPPING_L2_L3 so the vp added would be 4.
 */
public class ActionManager {
    private static ActionManager instance;
    private final Context context;

    private Map<String, Map<String, String>> actionTree;
    private Map<String, String> actionMap;
    private String[] prefixes = {
            "UPGRADE_SHOVEL",
            "UPGRADE_SHIPPING",
            "SHOVEL_ONCE",
            "SHOVEL_TWICE",
            "SHOVEL_THRICE",
            "POWER_GAIN",
            "LEECH_POWER"
    };

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

    public String mapToActionBySS(String actionPrefix, GameSnapshot ss) {
        String action = actionPrefix;
        if (Objects.equals(actionPrefix, "TOWN_SHIPPING")) {
            switch (ss.shipping) {
                case 0:
                    action = "TOWN_SHIPPING_L0_L1";
                    break;
                case 1:
                    action = "TOWN_SHIPPING_L1_L2";
                    break;
                case 2:
                    action = "TOWN_SHIPPING_L2_L3";
                    break;
                default:
                    break;
            }
        } else if (Objects.equals(actionPrefix, "UPGRADE_SHIPPING")) {
            switch (ss.shipping) {
                case 0:
                    action = "UPGRADE_SHIPPING_L0_L1";
                    break;
                case 1:
                    action = "UPGRADE_SHIPPING_L1_L2";
                    break;
                case 2:
                    action = "UPGRADE_SHIPPING_L2_L3";
                    break;
                default:
                    break;
            }
        } else if (Objects.equals(actionPrefix, "UPGRADE_SHOVEL")) {
            switch (ss.shovel) {
                case 1:
                    action = "UPGRADE_SHOVEL_L1_L2";
                    break;
                case 2:
                    action = "UPGRADE_SHOVEL_L2_L3";
                    break;
                default:
                    break;
            }
        } else if (Objects.equals(actionPrefix, "SHOVEL_ONCE")) {
            switch (ss.shovel) {
                case 1:
                    action = "SHOVEL_ONCE_L1";
                    break;
                case 2:
                    action = "SHOVEL_ONCE_L2";
                    break;
                case 3:
                    action = "SHOVEL_ONCE_L3";
                    break;
                default:
                    break;
            }
        } else if (Objects.equals(actionPrefix, "SHOVEL_TWICE")) {
            switch (ss.shovel) {
                case 1:
                    action = "SHOVEL_TWICE_L1";
                    break;
                case 2:
                    action = "SHOVEL_TWICE_L2";
                    break;
                case 3:
                    action = "SHOVEL_TWICE_L3";
                    break;
                default:
                    break;
            }
        } else if (Objects.equals(actionPrefix, "SHOVEL_THIRCE")) {
            switch (ss.shovel) {
                case 1:
                    action = "SHOVEL_THIRCE_L1";
                    break;
                case 2:
                    action = "SHOVEL_THIRCE_L2";
                    break;
                case 3:
                    action = "SHOVEL_THIRCE_L3";
                    break;
                default:
            }
        }

        return action;
    }

    public String mapBack(String action){
        String result = action;
        for (String prefix : prefixes) {
            if (action.startsWith(prefix)) {
                result = prefix;
                break;
            }
        }
        return result;
    }
}
