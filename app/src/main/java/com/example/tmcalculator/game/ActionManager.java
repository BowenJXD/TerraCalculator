package com.example.tmcalculator.game;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Singleton that manages the structure of the action menu.
 * Supports identifying the exact action given the action type and the snapshot.
 * E.g. user inputs UPGRADE_SHIPPING in a snapshot that has shipping level of 2, it would identify
 * the action as UPGRADE_SHIPPING_L2_L3 so the vp added would be 4.
 */
public class ActionManager {
    private static ActionManager instance;
    private final Context context;

    private Map<String, List<String>> actionTree;
    private static final String ACTION_TREE_PATH = "json/localization/CHS/actionTree.json";
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
        loadActionTree();
    }

    public static synchronized ActionManager getInstance(Context context) {
        if (instance == null) {
            instance = new ActionManager(context);
        }
        return instance;
    }

    public Map<String, List<String>> getActionTree() {
        return actionTree;
    }

    private void loadActionTree() {
        if (actionTree != null) { // Don't reload if already loaded
            return;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, List<String>>>() {}.getType();

        try {
            InputStream inputStream = context.getAssets().open(ACTION_TREE_PATH);
            InputStreamReader reader = new InputStreamReader(inputStream);
            actionTree = gson.fromJson(reader, type);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error loading action tree", Toast.LENGTH_SHORT).show();
        }
    }

    public String mapToActionBySS(String actionPrefix, GameSnapshot ss) {
        String action = actionPrefix;
        if (Objects.equals(actionPrefix, "TOWN_SHIPPING")) {
            action = String.format("%s_L%d_L%d", actionPrefix, ss.shipping, ss.shipping + 1);
        } else if (Objects.equals(actionPrefix, "UPGRADE_SHIPPING")) {
            action = String.format("%s_L%d_L%d", actionPrefix, ss.shipping, ss.shipping + 1);
        } else if (Objects.equals(actionPrefix, "UPGRADE_SHOVEL")) {
            action = String.format("%s_L%d_L%d", actionPrefix, ss.shovel, ss.shovel + 1);
        } else if (actionPrefix.startsWith("SHOVEL_")) {
            action = String.format("%s_L%d",actionPrefix, ss.shovel);
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
