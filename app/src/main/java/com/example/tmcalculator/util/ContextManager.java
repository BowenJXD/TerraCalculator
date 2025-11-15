package com.example.tmcalculator.util;

import android.content.Context;

/**
 * Provide context for singletons
 */
public class ContextManager {
    private static ContextManager instance;
    private Context context;
    private ContextManager() {
    }
    public static ContextManager getInstance() {
        if (instance == null) {
            instance = new ContextManager();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    public static Context getContext() {
        return getInstance().context;
    }

}
