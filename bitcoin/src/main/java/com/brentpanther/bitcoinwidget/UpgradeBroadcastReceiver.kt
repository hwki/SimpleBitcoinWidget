package com.brentpanther.bitcoinwidget

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class UpgradeBroadcastReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.MY_PACKAGE_REPLACED") {
            val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            for (widgetId in WidgetApplication.instance.widgetIds) {
                // remove any recurring alarms from previous versions
                val i = Intent(context, PriceBroadcastReceiver::class.java)
                alarm.cancel(PendingIntent.getBroadcast(context, widgetId + 1000, i, 0))

                // clear out last update values
                val prefs = Prefs(widgetId)
                prefs.setValue("LAST_VALUE", null)
                prefs.setValue("last_update", "0")
            }
        }
        WidgetProvider.refreshWidgets(context)
    }
}
