package com.brentpanther.bitcoinwidget

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import com.brentpanther.bitcoinwidget.receiver.WidgetBroadcastReceiver

class WidgetApplication : Application() {

    fun <T : WidgetProvider> getWidgetIds(className: Class<T>): IntArray {
        val name = ComponentName(this, className)
        return AppWidgetManager.getInstance(this).getAppWidgetIds(name)
    }
    val widgetIds: IntArray
        get() {
            return widgetProviders.map { getWidgetIds(it).toList() }.flatten().toIntArray()
        }

    val widgetProviders = listOf(WidgetProvider::class.java, ValueWidgetProvider::class.java)

    fun getWidgetType(widgetId: Int): WidgetType {
        val widgetInfo = AppWidgetManager.getInstance(this).getAppWidgetInfo(widgetId)
            ?: return WidgetType.PRICE
        return when (widgetInfo.provider.className) {
            WidgetProvider::class.qualifiedName -> WidgetType.PRICE
            ValueWidgetProvider::class.qualifiedName -> WidgetType.VALUE
            else -> throw IllegalArgumentException()
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        registerReceiver(WidgetBroadcastReceiver(), IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED))
        WidgetUpdater.updateDisplays(this)
    }

    companion object {

        lateinit var instance: WidgetApplication
            private set

        fun Int.dpToPx() = this * Resources.getSystem().displayMetrics.density
    }

}