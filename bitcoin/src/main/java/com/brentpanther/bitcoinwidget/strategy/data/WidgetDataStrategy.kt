package com.brentpanther.bitcoinwidget.strategy.data

import com.brentpanther.bitcoinwidget.WidgetApplication
import com.brentpanther.bitcoinwidget.WidgetType
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.db.WidgetDatabase

abstract class WidgetDataStrategy(val widgetId: Int) {

    private val dao = WidgetDatabase.getInstance(WidgetApplication.instance).widgetDao()

    private var _widget : Widget? = null

    var widget : Widget?
        get() {
            return _widget ?: dao.getByWidgetId(widgetId)
        }
        set(value) {
            _widget = value
        }

    protected fun getConfig() = dao.configWithSizes()

    abstract suspend fun loadData(manual: Boolean)

    suspend fun save() {
        widget?.let { dao.update(it) }
    }

    companion object {

        fun getStrategy(widgetId: Int): WidgetDataStrategy {
            return when (WidgetApplication.instance.getWidgetType(widgetId)) {
                WidgetType.PRICE -> PriceWidgetDataStrategy(widgetId)
                WidgetType.VALUE -> ValueWidgetDataStrategy(widgetId)
            }
        }

    }

}