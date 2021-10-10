package com.brentpanther.bitcoinwidget.strategy.data

import android.content.Context
import com.brentpanther.bitcoinwidget.WidgetApplication
import com.brentpanther.bitcoinwidget.WidgetType
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.db.WidgetDatabase

abstract class WidgetDataStrategy(context: Context, val widgetId: Int) {

    protected val appContext: Context = context.applicationContext
    protected val dao = WidgetDatabase.getInstance(appContext).widgetDao()

    private var _widget : Widget? = null

    var widget : Widget
        get() {
            _widget = _widget ?: dao.getByWidgetId(widgetId) ?: throw IllegalArgumentException()
            return _widget!!
        }
        set(value) {
            _widget = value
        }

    protected fun getConfig() = dao.configWithSizes()

    abstract suspend fun loadData(manual: Boolean, force: Boolean)

    suspend fun save() {
        dao.update(widget)
    }

    companion object {

        fun getStrategy(context: Context, widgetId: Int): WidgetDataStrategy {
            return when (WidgetApplication.instance.getWidgetType(widgetId)) {
                WidgetType.PRICE -> PriceWidgetDataStrategy(context, widgetId)
                WidgetType.VALUE -> ValueWidgetDataStrategy(context, widgetId)
            }
        }

    }

}