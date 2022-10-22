package com.brentpanther.bitcoinwidget

import android.content.Context
import com.brentpanther.bitcoinwidget.strategy.data.WidgetDataStrategy
import com.brentpanther.bitcoinwidget.strategy.display.WidgetDisplayStrategy
import com.brentpanther.bitcoinwidget.strategy.presenter.RemoteWidgetPresenter
import kotlinx.coroutines.coroutineScope

object WidgetUpdater {

    suspend fun update(context: Context, widgetIds: IntArray, manual: Boolean) = coroutineScope {

        val dataStrategies = widgetIds.map { WidgetDataStrategy.getStrategy(it) }

        // update display immediately to avoid looking bad
        for (strategy in dataStrategies) {
            val widget = strategy.widget ?: continue
            val widgetPresenter = RemoteWidgetPresenter(context, widget)
            val displayStrategy = WidgetDisplayStrategy.getStrategy(context, widget, widgetPresenter)
            displayStrategy.refresh()
        }

        // update data
        for (strategy in dataStrategies) {
            strategy.loadData(manual)
            strategy.save()
        }

        // data may cause display to need refreshed
        for (strategy in dataStrategies) {
            val widget = strategy.widget ?: continue
            val widgetPresenter = RemoteWidgetPresenter(context, widget)
            val displayStrategy = WidgetDisplayStrategy.getStrategy(context, widget, widgetPresenter)
            displayStrategy.refresh()
            displayStrategy.save()
        }
    }

}