package com.brentpanther.bitcoinwidget.receiver

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.brentpanther.bitcoinwidget.WidgetApplication

class ConfigChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val widgetUpdateIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, context, WidgetProvider::class.java)
        val appWidgetIds = WidgetApplication.instance.widgetIds
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        context.sendBroadcast(widgetUpdateIntent)
    }

}