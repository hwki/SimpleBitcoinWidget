package com.brentpanther.cryptowidget;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by brentpanther on 3/11/17.
 */

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

    public Ids getIds() {
        return null;
    }

    public Prefs getPrefs(int widgetId) {
        return null;
    }

    public Class getWidgetProvider() {
        return WidgetProvider.class;
    }
}
