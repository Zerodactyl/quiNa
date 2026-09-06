package com.exteragram.messenger;

import android.content.Context;
import android.content.SharedPreferences;

import org.telegram.messenger.ApplicationLoader;

public final class ExteraConfig {
    private static final Object sync = new Object();
    private static boolean configLoaded;
    private static boolean initialized;

    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;
    public static boolean showFeedTab;
    public static boolean showFeedUnreadCounter = true;

    private ExteraConfig() {
    }

    public static void loadConfig() {
        synchronized (sync) {
            if (configLoaded) {
                return;
            }
            Context context = ApplicationLoader.applicationContext;
            if (context == null) {
                return;
            }
            preferences = context.getSharedPreferences("exteraconfig", Context.MODE_PRIVATE);
            editor = preferences.edit();
            showFeedTab = preferences.getBoolean("showFeedTab", false);
            showFeedUnreadCounter = preferences.getBoolean("showFeedUnreadCounter", true);
            configLoaded = true;
        }
    }

    public static void reloadConfig() {
        synchronized (sync) {
            configLoaded = false;
        }
        loadConfig();
    }

    public static boolean getShowFeedTab() {
        loadConfig();
        return showFeedTab;
    }

    public static void setShowFeedTab(boolean value) {
        loadConfig();
        showFeedTab = value;
        if (editor != null) {
            editor.putBoolean("showFeedTab", value).apply();
        }
    }

    public static boolean getShowFeedUnreadCounter() {
        loadConfig();
        return showFeedUnreadCounter;
    }

    public static void setShowFeedUnreadCounter(boolean value) {
        loadConfig();
        showFeedUnreadCounter = value;
        if (editor != null) {
            editor.putBoolean("showFeedUnreadCounter", value).apply();
        }
    }

    public static void init() {
        synchronized (sync) {
            if (initialized) {
                return;
            }
            initialized = true;
        }
        loadConfig();
    }
}
