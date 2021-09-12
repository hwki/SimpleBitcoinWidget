package com.brentpanther.bitcoinwidget.strategy

import android.content.Context
import android.util.Log
import com.brentpanther.bitcoinwidget.WidgetState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException

class PriceWidgetDataStrategy(context: Context, widgetId: Int) : WidgetDataStrategy(context, widgetId) {

    override suspend fun loadData(manual: Boolean, force: Boolean): Unit = withContext(Dispatchers.IO) {
        val config = getConfig()

        val shouldRefresh = widget.shouldRefresh(config.refresh, manual)
        if (!force && !shouldRefresh) {
            // give time for the loading indicator to be noticeable if manual
            if (manual) {
                delay(750)
            }
            return@withContext
        }
        try {
            val currency = widget.currencyCustomName ?: widget.currency
            val coin = widget.coinCustomName ?: widget.coin.name
            val value = widget.exchange.getValue(coin, currency)
            widget.state = WidgetState.CURRENT
            if (value == null) {
                throw IllegalArgumentException()
            } else {
                widget.lastValue = value
                widget.lastUpdated = System.currentTimeMillis()
            }
            Log.i(TAG, "$widgetId: downloaded value $value")
        } catch (e: IOException) {
            // unable to reach exchange. potentially mark data as stale
            if (widget.isOld(config.refresh)) {
                widget.state = WidgetState.STALE
            }
            Log.w(TAG, "Error getting value from exchange: ${widget.exchange}.", e)
        } catch (e: Exception) {
            // error with data from exchange. potentially mark as error
            if (widget.isOld(config.refresh) || widget.lastValue == null) {
                widget.state = WidgetState.ERROR
            }
            Log.e(TAG, "Error parsing value from exchange: ${widget.exchange}.", e)
        }
    }

    companion object {
        private val TAG = this::class.java.simpleName
    }
}
