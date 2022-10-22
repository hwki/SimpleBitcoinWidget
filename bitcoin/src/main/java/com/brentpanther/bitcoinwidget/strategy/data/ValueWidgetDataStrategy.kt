package com.brentpanther.bitcoinwidget.strategy.data

class ValueWidgetDataStrategy(widgetId: Int)  : PriceWidgetDataStrategy(widgetId) {

    override fun setData(value: String) {
        widget?.apply {
            val valueDouble = value.toDoubleOrNull() ?: 0.0
            val amountHeld = amountHeld ?: 0.0
            lastValue = (valueDouble * amountHeld).toString()
            lastUpdated = System.currentTimeMillis()
        }

    }

}