package com.brentpanther.bitcoinwidget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import com.brentpanther.bitcoinwidget.db.WidgetDatabase
import com.brentpanther.bitcoinwidget.receiver.PriceBroadcastReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WidgetProvider : AppWidgetProvider() {

    override fun onRestored(context: Context, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        val widgetDao = WidgetDatabase.getInstance(context).widgetDao()
        CoroutineScope(Dispatchers.IO).launch {
            oldWidgetIds?.zip(newWidgetIds ?: IntArray(0))?.forEach { (old, new) ->
                widgetDao.getByWidgetId(old)?.apply {
                    widgetId = new
                    widgetDao.update(this)
                }
            }
        }
    }

    override fun onEnabled(context: Context) {
        DataMigration.migrate()
        refreshWidgets(context)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, widgetIds: IntArray) = onEnabled(context)

    override fun onDeleted(context: Context, widgetIds: IntArray) {
        for (widgetId in widgetIds) {
            val widgetUpdateIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, context, PriceBroadcastReceiver::class.java)
            widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            val pendingIntent = PendingIntent.getBroadcast(context, widgetId, widgetUpdateIntent, 0)
            val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarm.cancel(pendingIntent)
        }
        val widgetDao = WidgetDatabase.getInstance(context).widgetDao()
        CoroutineScope(Dispatchers.IO).launch {
            widgetDao.delete(widgetIds)
            if (widgetDao.config().consistentSize) {
                refreshWidgets(context)
            }
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