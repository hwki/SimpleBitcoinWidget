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
        for (widgetId in appWidgetIds) {
            val prefs = Prefs(widgetId)
            val layout = prefs.themeLayout
            val views = RemoteViews(context.packageName, layout)
            appWidgetManager.updateAppWidget(widgetId, views)
            val i = Intent(context, PriceBroadcastReceiver::class.java)
            i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            val pi = PendingIntent.getBroadcast(context, widgetId, i, 0)
            views.setOnClickPendingIntent(R.id.parent, pi)
            setAlarm(context, widgetId)
        }
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle) {
        val prefs = Prefs(appWidgetId)
        val layout = prefs.themeLayout
        val views = RemoteViews(context.packageName, layout)
        WidgetViews.setLastText(context, views, prefs)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (widgetId in appWidgetIds) {
            val prefs = Prefs(widgetId)
            prefs.delete()
            val i = Intent(context, PriceBroadcastReceiver::class.java)
            val pi = PendingIntent.getBroadcast(context, widgetId + 1000, i, 0)
            val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarm.cancel(pi)
        }
        refreshIfFixedSize(context)
    }

    private fun refreshIfFixedSize(context: Context) {
        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.key_fixed_size), false)) return
        refreshWidgets(context, -1)
    }

    private fun setAlarm(context: Context, widgetId: Int) {
        val prefs = Prefs(widgetId)
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val i = Intent(context, PriceBroadcastReceiver::class.java)
        i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        val pi = PendingIntent.getBroadcast(context, widgetId + 1000, i, 0)
        val update = prefs.interval
        context.sendBroadcast(i)
        alarm.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis() + 1000, (update * 60000).toLong(), pi)
    }

    companion object {

        fun refreshWidgets(context: Context, widgetId: Int) {
            val widgetIds = WidgetApplication.instance.widgetIds
            val refreshWidgetIds = ArrayList<Int>()
            for (appWidgetId in widgetIds) {
                if (appWidgetId == widgetId) continue
                refreshWidgetIds.add(appWidgetId)
            }
            refreshWidgets(context, refreshWidgetIds)
        }

        private fun refreshWidgets(context: Context, widgetIds: List<Int>) {
            for (widgetId in widgetIds) {
                val widgetUpdateIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, context, PriceBroadcastReceiver::class.java)
                widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                context.sendBroadcast(widgetUpdateIntent)
            }
        }
    }


}
