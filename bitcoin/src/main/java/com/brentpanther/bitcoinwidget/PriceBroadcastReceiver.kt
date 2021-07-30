package com.brentpanther.bitcoinwidget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.JobIntentService
import com.brentpanther.bitcoinwidget.db.Configuration
import com.brentpanther.bitcoinwidget.db.WidgetDatabase
import com.brentpanther.bitcoinwidget.db.WidgetSettings
import kotlinx.coroutines.*

class PriceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val manualRefresh = intent.getBooleanExtra(EXTRA_MANUAL_REFRESH, false)
        val widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0)
        val dao = WidgetDatabase.getInstance(context).widgetDao()

        CoroutineScope(Dispatchers.IO).launch {
            val config = dao.config()
            val widget = dao.getByWidgetId(widgetId) ?: return@launch
            val views = RemoteViews(context.packageName, widget.theme.layout)

            // we don't always need to download a new price, sometimes just the layout can be updated.
            // only update the price if its a manual refresh, or if we are close to the interval
            val widgetSettings = WidgetSettings(widget, config, dao.getSmallestSizes())
            val updatePrice = manualRefresh || widgetSettings.shouldRefresh()

            val widgetViews = WidgetViews(context, views, widgetSettings)

            if (updatePrice) {
                val restriction = NetworkStatusHelper.getRestriction(context)
                if (restriction != 0) {
                    if (manualRefresh) {
                        Toast.makeText(context, restriction, Toast.LENGTH_LONG).show()
                        return@launch
                    }
                    setAlarm(context, config, widgetId)
                    return@launch
                }
                if (manualRefresh) {
                    widgetViews.setLoading(views)
                    val throttled = NetworkStatusHelper.checkThrottled(context)
                    if (throttled || widgetSettings.throttled()) {
                        // we are either throttled by the system, or are trying to refresh too often
                        // and then we will not get scheduled
                        // so don't enqueue any work, just "finish" after a short time
                        CoroutineScope(Dispatchers.IO).launch {
                            delay(750)
                            widgetViews.setText(widget.lastValue, false)
                            setOnClick(context, widgetId, views)
                        }
                        val appWidgetManager = AppWidgetManager.getInstance(context)
                        appWidgetManager.updateAppWidget(widgetId, views)
                        return@launch
                    }
                }
                val i = Intent(context.applicationContext, UpdatePriceService::class.java)
                i.putExtras(intent)
                JobIntentService.enqueueWork(context.applicationContext, UpdatePriceService::class.java, 7483, i)
                setAlarm(context, config, widgetId)
            } else {
                widgetViews.setText(widget.lastValue, false)
            }
            setOnClick(context, widgetId, views)
        }

    }

    private fun setAlarm(context: Context, configuration: Configuration, widgetId: Int) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val widgetUpdateIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, context, PriceBroadcastReceiver::class.java)
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        val pendingIntent = PendingIntent.getBroadcast(context, widgetId, widgetUpdateIntent, 0)

        // cancel any existing alarms
        alarm.cancel(pendingIntent)

        val update = configuration.refresh * 60000
        alarm.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + update, pendingIntent)
    }

    companion object {

        private val TAG = PriceBroadcastReceiver::class.java.simpleName
        private const val EXTRA_MANUAL_REFRESH = "manualRefresh"

        internal fun setOnClick(context: Context, widgetId: Int, views: RemoteViews) {
            val priceUpdate = Intent(context, PriceBroadcastReceiver::class.java)
            priceUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            priceUpdate.putExtra(EXTRA_MANUAL_REFRESH, true)
            val pendingPriceUpdate = PendingIntent.getBroadcast(context, widgetId, priceUpdate, PendingIntent.FLAG_CANCEL_CURRENT)
            views.setOnClickPendingIntent(R.id.parent, pendingPriceUpdate)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }
}
