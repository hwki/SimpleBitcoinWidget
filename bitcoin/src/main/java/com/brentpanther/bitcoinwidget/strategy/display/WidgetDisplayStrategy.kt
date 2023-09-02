package com.brentpanther.bitcoinwidget.strategy.display

import android.content.Context
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.WidgetType
import com.brentpanther.bitcoinwidget.db.ConfigurationWithSizes
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.db.WidgetDatabase
import com.brentpanther.bitcoinwidget.strategy.presenter.WidgetPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

abstract class WidgetDisplayStrategy(context: Context, val widget: Widget, val widgetPresenter: WidgetPresenter) {

    protected val appContext: Context = context.applicationContext
    private val dao = WidgetDatabase.getInstance(appContext).widgetDao()

    protected fun getConfig(): ConfigurationWithSizes {
        return runBlocking(Dispatchers.IO) {
            dao.configWithSizes()
        }
    }

    abstract fun refresh()

    suspend fun save() {
        widgetPresenter.hide(R.id.loading)
        widgetPresenter.setOnClickRefresh(appContext, widget.widgetId)
        dao.update(widget)
    }

    companion object {
        fun getStrategy(context: Context, widget: Widget, widgetPresenter: WidgetPresenter): WidgetDisplayStrategy {
            return when (widget.widgetType) {
                WidgetType.PRICE -> SolidPriceWidgetDisplayStrategy(context, widget, widgetPresenter)
                WidgetType.VALUE -> SolidValueWidgetDisplayStrategy(context, widget, widgetPresenter)
            }
        }
    }




}