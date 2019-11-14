package com.brentpanther.bitcoinwidget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast

class PriceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val manualRefresh = intent.getBooleanExtra(UpdatePriceService.EXTRA_MANUAL_REFRESH, false)
        val restriction = NetworkStatusHelper.getRestriction(context)
        val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0)
        val prefs = Prefs(appWidgetId)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val views = RemoteViews(context.packageName, prefs.themeLayout)
        if (restriction != 0) {
            if (manualRefresh) {
                Toast.makeText(context, restriction, Toast.LENGTH_LONG).show()
            }
            return
        }
        // start loading right away to show that the widget is refreshing in case we get queued
        if (manualRefresh) WidgetViews.setLoading(views)
        appWidgetManager.updateAppWidget(appWidgetId, views)
        val i = Intent(context, UpdatePriceService::class.java)
        i.putExtras(intent)
        UpdatePriceService.enqueueWork(context, i)
    }

}
