package com.brentpanther.bitcoinwidget.receiver

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.brentpanther.bitcoinwidget.WidgetApplication
import com.brentpanther.bitcoinwidget.WidgetProvider
import com.brentpanther.bitcoinwidget.db.WidgetDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UpgradeBroadcastReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.MY_PACKAGE_REPLACED") {
            val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            for (widgetId in WidgetApplication.instance.widgetIds) {
                // remove any recurring alarms from previous versions
                val i = Intent(context, PriceBroadcastReceiver::class.java)
                alarm.cancel(PendingIntent.getBroadcast(context, widgetId + 1000, i, 0))
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            WidgetDatabase.getInstance(context).widgetDao().apply {
                resetWidgets()
            }
            WidgetProvider.refreshWidgets(context)
        }
    }
}
