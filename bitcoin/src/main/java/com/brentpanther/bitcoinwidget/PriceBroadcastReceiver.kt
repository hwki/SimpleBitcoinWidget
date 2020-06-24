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
import kotlin.concurrent.thread

class PriceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val manualRefresh = intent.getBooleanExtra(UpdatePriceService.EXTRA_MANUAL_REFRESH, false)
        val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0)
        val prefs = Prefs(appWidgetId)
        if (!prefs.exists()) return
        val views = RemoteViews(context.packageName, prefs.themeLayout)

        // we don't always need to download a new price, sometimes just the layout can be updated.
        // only update the price if its a manual refresh, or if we are close to the interval
        val updatePrice = manualRefresh || (System.currentTimeMillis() - prefs.lastUpdate) > (prefs.interval * 60000 * .25)

        if (updatePrice) {
            val restriction = NetworkStatusHelper.getRestriction(context)
            if (restriction != 0) {
                if (manualRefresh) {
                    Toast.makeText(context, restriction, Toast.LENGTH_LONG).show()
                    return
                }
                setAlarm(context, appWidgetId)
                return
            }
            if (manualRefresh) {
                WidgetViews.setLoading(views)
                val throttled = NetworkStatusHelper.checkThrottled(context)
                val since = System.currentTimeMillis() - prefs.lastUpdate
                if (throttled || since < 120000) {
                    // we are either throttled by the system, or are trying to refresh too often
                    // and then we will not get scheduled
                    // so don't enqueue any work, just "finish" after a short time
                    thread(start = true) {
                        Thread.sleep(750)
                        WidgetViews.setText(context, views, prefs, prefs.lastValue, false)
                        setOnClick(context, appWidgetId, views)
                    }
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                    return
                }
            }
            val i = Intent(context, UpdatePriceService::class.java)
            i.putExtras(intent)
            JobIntentService.enqueueWork(context, UpdatePriceService::class.java, 7483, i)
            setAlarm(context, appWidgetId)
        } else {
            WidgetViews.setText(context, views, prefs, prefs.lastValue, false)
        }
        setOnClick(context, appWidgetId, views)
    }

    private fun setAlarm(context: Context, widgetId: Int) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val widgetUpdateIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, context, PriceBroadcastReceiver::class.java)
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        val pendingIntent = PendingIntent.getBroadcast(context, widgetId, widgetUpdateIntent, 0)

        // cancel any existing alarms
        alarm.cancel(pendingIntent)
        val update = Prefs(widgetId).interval * 60000
        alarm.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + update, pendingIntent);
    }

    companion object {
        private val TAG = PriceBroadcastReceiver::class.java.simpleName
        internal fun setOnClick(context: Context, appWidgetId: Int, views: RemoteViews) {
            val priceUpdate = Intent(context, PriceBroadcastReceiver::class.java)
            priceUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            priceUpdate.putExtra(UpdatePriceService.EXTRA_MANUAL_REFRESH, true)
            val pendingPriceUpdate = PendingIntent.getBroadcast(context, appWidgetId, priceUpdate, PendingIntent.FLAG_CANCEL_CURRENT)
            views.setOnClickPendingIntent(R.id.parent, pendingPriceUpdate)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
