package com.brentpanther.bitcoinwidget

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.preference.PreferenceManager


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
        registerReceiver(MyBroadcastReceiver(), IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED))
    }

    fun useAutoSizing(): Boolean {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val fixedSize = sharedPrefs.getBoolean(getString(R.string.key_fixed_size), false)
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !fixedSize
    }

    companion object {

        lateinit var instance: WidgetApplication
            private set
    }

}