package com.brentpanther.bitcoinwidget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager

class WidgetProvider : AppWidgetProvider() {

    override fun onRestored(context: Context, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        oldWidgetIds?.zip(newWidgetIds ?: IntArray(0))?.forEach { (old, new) ->
            Prefs(old).move(new)
        }
    }

    override fun onEnabled(context: Context) {
        DataMigration.migrate(context)
        refreshWidgets(context)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        onEnabled(context)
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
            refreshWidgets(context, widgetIds.filterNot { it == widgetId })
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