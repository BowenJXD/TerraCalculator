package com.example.tmcalculator.game;

import android.content.Context;

import com.example.tmcalculator.util.ContextManager;

/**
 * BONUS, FAVOR & SCORING tiles are all managed here.
 */
public class SettingManager {
    private static SettingManager instance;
    private Context context;

    private SettingManager(){
        this.context = ContextManager.getContext();
    }

    public static synchronized SettingManager getInstance() {
        if (instance == null) {
            instance = new SettingManager();
        }
        return instance;
    }
}
