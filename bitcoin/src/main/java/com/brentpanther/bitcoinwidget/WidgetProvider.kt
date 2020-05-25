package com.brentpanther.bitcoinwidget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import androidx.preference.PreferenceManager
import java.util.*

class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        DataMigration.migrate(context)
        refreshWidgets(context)
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle) {
        val prefs = Prefs(appWidgetId)
        val layout = prefs.themeLayout
        val views = RemoteViews(context.packageName, layout)
        WidgetViews.setText(context, views, prefs, prefs.lastValue)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (widgetId in appWidgetIds) {
            Prefs(widgetId).delete()
            val widgetUpdateIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, context, PriceBroadcastReceiver::class.java)
            widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            val pendingIntent = PendingIntent.getBroadcast(context, widgetId, widgetUpdateIntent, 0)
            val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarm.cancel(pendingIntent)
        }
        refreshIfFixedSize(context, appWidgetIds)
    }

    private fun refreshIfFixedSize(context: Context, appWidgetIds: IntArray) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        if (prefs.getBoolean(context.getString(R.string.key_fixed_size), false)) {
            refreshWidgets(context, appWidgetIds.toList())
        }
    }

    companion object {

        fun refreshWidgets(context: Context, widgetId: Int? = null) {
            val widgetIds = WidgetApplication.instance.widgetIds
            val refreshWidgetIds = ArrayList<Int>()
            for (appWidgetId in widgetIds) {
                if (appWidgetId == widgetId) continue
                refreshWidgetIds.add(appWidgetId)
            }
            refreshWidgets(context, refreshWidgetIds)
        }

        internal fun refreshWidgets(context: Context, widgetIds: List<Int>) {
            for (widgetId in widgetIds) {
                val widgetUpdateIntent = Intent(context, PriceBroadcastReceiver::class.java)
                widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                context.sendBroadcast(widgetUpdateIntent)
            }
        }
    }

}