package com.brentpanther.bitcoinwidget

import android.content.Context
import com.brentpanther.bitcoinwidget.db.WidgetDatabase
import com.brentpanther.bitcoinwidget.strategy.data.WidgetDataStrategy
import com.brentpanther.bitcoinwidget.strategy.display.WidgetDisplayStrategy
import com.brentpanther.bitcoinwidget.strategy.presenter.RemoteWidgetPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

object WidgetUpdater {

     fun update(context: Context, widgetIds: IntArray, manual: Boolean) = CoroutineScope(Dispatchers.IO).launch {
        val dataStrategies = widgetIds.map { WidgetDataStrategy.getStrategy(it) }

        dataStrategies.forEachIndexed { index, strategy ->
            val widget = strategy.widget ?: return@forEachIndexed
            val widgetPresenter = RemoteWidgetPresenter(context, widget)
            val success = strategy.loadData(manual)
            strategy.save()
            if (success) {
                val displayStrategy = WidgetDisplayStrategy.getStrategy(context, widget, widgetPresenter)
                displayStrategy.refresh()
                displayStrategy.save()
                // wait before refreshing the next widget to avoid rate limiting issues
                if (index != dataStrategies.lastIndex) {
                    delay(20.seconds)
                }
            }
        }

        updateDisplays(context)
    }

    fun updateDisplays(context: Context) = CoroutineScope(Dispatchers.IO).launch {
        // change of data for different widgets may cause us to need to refresh all widgets
        val dao = WidgetDatabase.getInstance(context).widgetDao()
        val widgetIds = dao.getAll().map { it.widgetId }.toIntArray()
        val dataStrategies = widgetIds.map { WidgetDataStrategy.getStrategy(it) }
        (1..2).forEachIndexed { _, _ ->
            // refresh twice in case the size changed on the last widget
            dataStrategies.forEach { strategy ->
                val widget = strategy.widget ?: return@forEach
                val widgetPresenter = RemoteWidgetPresenter(context, widget)
                val displayStrategy = WidgetDisplayStrategy.getStrategy(context, widget, widgetPresenter)
                displayStrategy.refresh()
                displayStrategy.save()
            }
        }

    }

}