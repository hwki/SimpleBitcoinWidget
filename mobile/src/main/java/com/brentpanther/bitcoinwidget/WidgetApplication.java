package com.brentpanther.bitcoinwidget;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;

/**
 * Created by brentpanther on 3/11/17.
 */

public class WidgetApplication extends Application {

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this, WidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        ComponentName cm = new ComponentName(this, WidgetProvider.class);
        int[] appWidgetIds = manager.getAppWidgetIds(cm);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        sendBroadcast(intent);
    }
}
