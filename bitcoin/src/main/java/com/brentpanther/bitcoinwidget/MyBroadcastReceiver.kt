package com.brentpanther.bitcoinwidget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


internal class MyBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val restriction = NetworkStatusHelper.getRestriction(context)
        if (restriction != 0) {
            return
        }
        val widgetUpdateIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, context, WidgetProvider::class.java)
        val appWidgetIds = WidgetApplication.instance.widgetIds
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        context.sendBroadcast(widgetUpdateIntent)
    }
}
