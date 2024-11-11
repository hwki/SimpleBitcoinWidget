package com.brentpanther.bitcoinwidget.strategy.data

import com.brentpanther.bitcoinwidget.WidgetState
import com.brentpanther.bitcoinwidget.exchange.ExchangeHelper

class ValueWidgetDataStrategy(widgetId: Int)  : PriceWidgetDataStrategy(widgetId) {

    override fun setData(value: String) {
        widget?.apply {
            val valueDouble = value.toDoubleOrNull() ?: 0.0
            val amountHeld = amountHeld
            lastValue = when {
                amountHeld != null -> (valueDouble * amountHeld).toString()
                address != null -> {
                    val url = "https://blockchain.info/q/addressbalance/$address"
                    val response = ExchangeHelper.getString(url)
                    if ("error" in response) {
                        state = WidgetState.INVALID_ADDRESS
                        lastValue
                    }
                    else {
                        val balance = response.toDoubleOrNull() ?: throw IllegalStateException()
                        (valueDouble * balance * .00000001).toString()
                    }
                }
                else -> lastValue
            }
            lastUpdated = System.currentTimeMillis()
        }
    }

}