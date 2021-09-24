package com.brentpanther.bitcoinwidget

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import com.brentpanther.bitcoinwidget.db.WidgetDatabase
import com.brentpanther.bitcoinwidget.receiver.WidgetBroadcastReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WidgetApplication : Application() {

    val widgetIds: IntArray
        get() {
            val manager = AppWidgetManager.getInstance(this)
            val cm = ComponentName(this, WidgetProvider::class.java)
            return manager.getAppWidgetIds(cm)
        }

    fun getWidgetType(widgetId: Int): WidgetType {
        return when (AppWidgetManager.getInstance(this).getAppWidgetInfo(widgetId).provider.className) {
            WidgetProvider::class.qualifiedName -> WidgetType.PRICE
            ValueWidgetProvider::class.qualifiedName -> WidgetType.VALUE
            else -> throw IllegalArgumentException()
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        registerReceiver(WidgetBroadcastReceiver(), IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED))
        // in case of stuck entries in the database
        if (widgetIds.isEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                WidgetDatabase.getInstance(this@WidgetApplication).widgetDao().clear()
            }
        }
    }

    companion object {

        lateinit var instance: WidgetApplication
            private set

        fun Int.dpToPx() = this * Resources.getSystem().displayMetrics.density
    }

}