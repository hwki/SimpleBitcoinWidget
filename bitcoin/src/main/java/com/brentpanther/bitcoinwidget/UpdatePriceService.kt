package com.brentpanther.bitcoinwidget


import android.app.PendingIntent
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.annotation.NonNull
import androidx.core.app.JobIntentService


class UpdatePriceService : JobIntentService() {

    override fun onHandleWork(@NonNull intent: Intent) {
        val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0)
        val prefs = Prefs(appWidgetId)
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val views = RemoteViews(applicationContext.packageName, prefs.themeLayout)
        try {
            updateValue(applicationContext, views, prefs)
        } catch (e: IllegalArgumentException) {
            WidgetViews.putValue(applicationContext, views, applicationContext.getString(R.string.value_exchange_removed), prefs)
            appWidgetManager.updateAppWidget(appWidgetId, views)
            return
        }

        val priceUpdate = Intent(applicationContext, PriceBroadcastReceiver::class.java)
        priceUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        priceUpdate.putExtra(EXTRA_MANUAL_REFRESH, true)
        val pendingPriceUpdate = PendingIntent.getBroadcast(applicationContext, appWidgetId, priceUpdate, FLAG_CANCEL_CURRENT)
        views.setOnClickPendingIntent(R.id.parent, pendingPriceUpdate)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    companion object {

        private const val JOB_ID = 3542
        const val EXTRA_MANUAL_REFRESH = "manualRefresh"

        internal fun enqueueWork(context: Context, work: Intent) {
            DataMigration.migrate(context)
            enqueueWork(context, UpdatePriceService::class.java, JOB_ID, work)
        }

        @Throws(IllegalArgumentException::class)
        internal fun updateValue(context: Context, views: RemoteViews, prefs: Prefs): String? {
            val currencyCode = prefs.exchangeCurrencyName ?: return null
            return try {
                val exchange = prefs.exchange
                val amount = exchange.getValue(prefs.exchangeCoinName, currencyCode) ?: throw Exception("")
                WidgetViews.setText(context, views, amount, prefs)
                prefs.setLastUpdate()
                amount
            } catch (e: IllegalArgumentException) {
                ExchangeHelper.connectionPool.evictAll()
                WidgetViews.setLastText(context, views, prefs)
                throw e
            } catch (ignored: Exception) {
                ExchangeHelper.connectionPool.evictAll()
                WidgetViews.setLastText(context, views, prefs)
                null
            }
        }
    }

}
