package com.brentpanther.bitcoinwidget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;


class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent widgetUpdateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, context, WidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName cm = new ComponentName(context, WidgetProvider.class);
        int[] appWidgetIds = WidgetApplication.getInstance().getWidgetIds();
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        context.sendBroadcast(widgetUpdateIntent);
    }
}
