package com.brentpanther.bitcoinwidget;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;


public class WidgetApplication extends Application {

    private static WidgetApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        registerReceiver(new MyBroadcastReceiver(), new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED));
    }

    public static WidgetApplication getInstance() {
        return instance;
    }

    public int[] getWidgetIds() {
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        ComponentName cm = new ComponentName(this, WidgetProvider.class);
        return manager.getAppWidgetIds(cm);
    }

    public boolean useAutoSizing() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean fixedSize = sharedPrefs.getBoolean(getString(R.string.key_fixed_size), false);
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !fixedSize;
    }

}