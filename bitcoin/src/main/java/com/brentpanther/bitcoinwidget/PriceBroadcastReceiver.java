package com.brentpanther.bitcoinwidget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

public class PriceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        boolean manualRefresh = intent.getBooleanExtra(UpdatePriceService.EXTRA_MANUAL_REFRESH, false);
        int restriction = NetworkStatusHelper.getRestriction(context);
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
        Prefs prefs = new Prefs(appWidgetId);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), prefs.getThemeLayout());
        if (restriction != 0) {
            if (manualRefresh) {
                Toast.makeText(context, restriction, Toast.LENGTH_LONG).show();
            }
            return;
        }
        // start loading right away to show that the widget is refreshing in case we get queued
        if (manualRefresh) WidgetViews.setLoading(views);
        appWidgetManager.updateAppWidget(appWidgetId, views);
        Intent i = new Intent(context, UpdatePriceService.class);
        i.putExtras(intent);
        UpdatePriceService.enqueueWork(context, i);
    }

}
