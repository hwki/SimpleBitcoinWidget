package com.brentpanther.bitcoinwidget


import android.appwidget.AppWidgetManager
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.JobIntentService
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.db.WidgetDatabase
import com.brentpanther.bitcoinwidget.db.WidgetSettings
import com.brentpanther.bitcoinwidget.exchange.ExchangeHelper
import com.brentpanther.bitcoinwidget.receiver.PriceBroadcastReceiver
import com.brentpanther.bitcoinwidget.ui.preview.RemoteWidgetPreview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UpdatePriceService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        val widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0)
        val dao = WidgetDatabase.getInstance(applicationContext).widgetDao()
        val widget = dao.getByWidgetId(widgetId) ?: return
        val views = RemoteViews(applicationContext.packageName, widget.theme.layout)

        val widgetSettings = WidgetSettings(widget, dao.configWithSizes())
        val widgetViews = WidgetViews(applicationContext, RemoteWidgetPreview(views), widgetSettings)
        when (val amount = updateValue(widget)) {
            INVALID -> widgetViews.putValue(applicationContext.getString(R.string.value_exchange_removed))
            else -> {
                widgetViews.setText(amount, true)
                CoroutineScope(Dispatchers.IO).launch {
                    dao.update(widget)
                }
            }
        }
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        PriceBroadcastReceiver.setOnClick(applicationContext, widgetId, views)
        appWidgetManager.updateAppWidget(widgetId, views)
    }

    companion object {

        const val INVALID = "invalid"
        private val TAG = UpdatePriceService::class.java.simpleName

        internal fun updateValue(widget: Widget): String? {
            return try {
                val currencyCode = widget.currencyCustomName ?: widget.currency
                val exchange = widget.exchange
                val amount = exchange.getValue(widget.coinCustomName ?: widget.coin.name, currencyCode) ?: return null
                widget.lastUpdated = System.currentTimeMillis()
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
