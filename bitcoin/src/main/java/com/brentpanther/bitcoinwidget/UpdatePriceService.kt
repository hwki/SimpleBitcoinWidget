package com.brentpanther.bitcoinwidget


import android.app.PendingIntent
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.NonNull
import androidx.core.app.JobIntentService


class UpdatePriceService : JobIntentService() {

    override fun onHandleWork(@NonNull intent: Intent) {
        val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0)
        val prefs = Prefs(appWidgetId)
        val views = RemoteViews(applicationContext.packageName, prefs.themeLayout)

        when (val amount = updateValue(prefs)) {
            INVALID -> WidgetViews.putValue(applicationContext, views, applicationContext.getString(R.string.value_exchange_removed), prefs)
            else -> WidgetViews.setText(applicationContext, views, prefs, amount)
        }
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        PriceBroadcastReceiver.setOnClick(applicationContext, appWidgetId, views)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    companion object {

        const val EXTRA_MANUAL_REFRESH = "manualRefresh"
        const val INVALID = "invalid"
        private val TAG = UpdatePriceService::class.java.simpleName

        internal fun updateValue(prefs: Prefs): String? {
            return try {
                val currencyCode = prefs.exchangeCurrencyName ?: return INVALID
                val exchange = prefs.exchange ?: return INVALID
                val amount = exchange.getValue(prefs.exchangeCoinName, currencyCode) ?: return null
                prefs.setLastUpdate()
                amount
            } catch (e: IllegalArgumentException) {
                INVALID
            } catch (ignored: Exception) {
                ExchangeHelper.connectionPool.evictAll()
                null
            }
        }
    }

}
