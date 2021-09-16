package com.brentpanther.bitcoinwidget

import android.content.Context
import com.brentpanther.bitcoinwidget.strategy.data.PriceWidgetDataStrategy
import com.brentpanther.bitcoinwidget.strategy.display.WidgetDisplayStrategy
import com.brentpanther.bitcoinwidget.strategy.presenter.RemoteWidgetPresenter
import kotlinx.coroutines.coroutineScope

object WidgetUpdater {

    suspend fun update(context: Context, widgetIds: IntArray, manual: Boolean) = coroutineScope {

        val dataStrategies = widgetIds.map { PriceWidgetDataStrategy(context, it) }
        for (strategy in dataStrategies) {
            strategy.loadData(manual, false)
            strategy.save()
        }

        for (strategy in dataStrategies) {
            val widgetPresenter = RemoteWidgetPresenter(context, strategy.widget)
            val displayStrategy = WidgetDisplayStrategy.getStrategy(context, strategy.widget, widgetPresenter)
            displayStrategy.refresh()
            displayStrategy.save()
        }
    }

}