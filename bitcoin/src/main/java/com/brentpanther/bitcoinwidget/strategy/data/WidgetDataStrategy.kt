package com.brentpanther.bitcoinwidget.strategy.data

import android.content.Context
import com.brentpanther.bitcoinwidget.WidgetApplication
import com.brentpanther.bitcoinwidget.WidgetType
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.db.WidgetDatabase
import kotlinx.coroutines.delay

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

    protected suspend fun loading(refresh: Int, manual: Boolean, force: Boolean): Boolean {
        val shouldRefresh = widget.shouldRefresh(refresh, manual)
        return if (!force && !shouldRefresh) {
            // give time for the loading indicator to be noticeable if manual
            if (manual) {
                delay(750)
            }
            true
        } else {
            false
        }
    }

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