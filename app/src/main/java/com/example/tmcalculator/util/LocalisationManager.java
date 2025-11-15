package com.example.tmcalculator.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class LocalisationManager {

    private Map<String, String> warnings;
    private Map<String, String> action;
    private Map<String, String> character;
    private Map<String, String> bonus;
    private Map<String, String> favor;
    private Map<String, String> scoring;
    private static final String WARNING_PATH = "json/localisation/CHS/warning.json";
    private static final String ACTION_PATH = "json/localisation/CHS/action.json";
    private static final String CHARACTER_PATH = "json/localisation/CHS/character.json";
    private static final String SETTING_PATH = "json/localisation/CHS/setting.json";
    private Context context;
    private static LocalisationManager instance;

    private LocalisationManager() {
        this.context = ContextManager.getContext();
        loadLocalisation(WARNING_PATH, map -> warnings = map);
        loadLocalisation(ACTION_PATH, map -> action = map);
        loadLocalisation(CHARACTER_PATH, map -> character = map);
        loadLocalisation(SETTING_PATH, map -> {
            bonus = new HashMap<>();
            favor = new HashMap<>();
            scoring = new HashMap<>();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value.startsWith("BON")) {
                    bonus.put(key, value);
                } else if (value.startsWith("FAV")) {
                    favor.put(key, value);
                } else if (value.startsWith("SCO")) {
                    scoring.put(key, value);
                }
            }
        });
    }

    public static synchronized LocalisationManager getInstance() {
        if (instance == null) {
            instance = new LocalisationManager();
        }
        return instance;
    }

    public void loadLocalisation(String path, Consumer<Map<String, String>> consumer) {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, String>>() {}.getType();
        try {
            InputStream inputStream = context.getAssets().open(path);
            InputStreamReader reader = new InputStreamReader(inputStream);
            Map<String, String> map = gson.fromJson(reader, type);
            consumer.accept(map);
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

    public String getCharacterLocalisation(String key) {
        if (character == null) return null;
        return character.get(key);
    }

    public String getBonusLocalisation(String key) {
        if (bonus == null) return null;
        return bonus.get(key);
    }

    public String getFavorLocalisation(String key) {
        if (favor == null) return null;
        return favor.get(key);
    }

    public String getScoringLocalisation(String key) {
        if (scoring == null) return null;
        return scoring.get(key);
    }

    public List<String> getBonusList() {
        if (bonus == null) return null;
        return new ArrayList<>(bonus.values());
    }

    public List<String> getFavorList() {
        if (favor == null) return null;
        return new ArrayList<>(favor.values());
    }

    public List<String> getScoringList() {
        if (scoring == null) return null;
        return new ArrayList<>(scoring.values());
    }
}
