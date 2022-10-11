package com.brentpanther.bitcoinwidget.strategy.data

class ValueWidgetDataStrategy(widgetId: Int)  : PriceWidgetDataStrategy(widgetId) {

    override fun setData(value: String) {
        val valueDouble = value.toDoubleOrNull() ?: 0.0
        val amountHeld = widget.amountHeld ?: 0.0
        widget.lastValue = (valueDouble * amountHeld).toString()
        widget.lastUpdated = System.currentTimeMillis()
    }

}