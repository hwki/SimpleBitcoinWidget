package com.brentpanther.bitcoinwidget.strategy.data

import com.brentpanther.bitcoinwidget.WidgetApplication
import com.brentpanther.bitcoinwidget.WidgetState
import com.brentpanther.bitcoinwidget.WidgetType
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.db.WidgetDatabase

abstract class WidgetDataStrategy(val widgetId: Int) {

    private val dao = WidgetDatabase.getInstance(WidgetApplication.instance).widgetDao()

    private var _widget : Widget? = null

    var widget : Widget?
        get() {
            _widget = _widget ?: dao.getByWidgetId(widgetId)
            return _widget
        }
        set(value) {
            _widget = value
        }

    protected fun getConfig() = dao.configWithSizes()

    abstract suspend fun loadData(manual: Boolean)

    suspend fun save() {
        widget?.let { dao.update(it) }
    }

    fun shouldRefresh(): Boolean {
        return widget?.let {
            it.state == WidgetState.DRAFT || System.currentTimeMillis() - it.lastUpdated > 30000
        } ?: false
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