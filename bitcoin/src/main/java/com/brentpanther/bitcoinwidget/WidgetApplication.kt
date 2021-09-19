package com.brentpanther.bitcoinwidget

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
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
    }

}