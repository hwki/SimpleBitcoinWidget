package com.brentpanther.cryptowidget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by brentpanther on 4/24/17.
 */

class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        WidgetApplication app = WidgetApplication.getInstance();
        Class widgetProvider = app.getWidgetProvider();
        Intent intent2 = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, context, widgetProvider);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName cm = new ComponentName(context, widgetProvider);
        int[] appWidgetIds = manager.getAppWidgetIds(cm);
        intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        context.sendBroadcast(intent2);
    }
}
