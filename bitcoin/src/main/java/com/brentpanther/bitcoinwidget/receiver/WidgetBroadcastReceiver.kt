package com.brentpanther.bitcoinwidget.receiver

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.brentpanther.bitcoinwidget.NetworkStatusHelper
import com.brentpanther.bitcoinwidget.WidgetApplication
import com.brentpanther.bitcoinwidget.WidgetUpdater
import com.brentpanther.bitcoinwidget.db.WidgetDatabase
import com.brentpanther.bitcoinwidget.strategy.presenter.RemoteWidgetPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WidgetBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_CONFIGURATION_CHANGED -> refreshAllWidgets(context)
            "message" -> postMessage(context, intent.getIntExtra("message", 0))
            "refresh" -> refreshWidget(context, intent)
        }
    }

    private fun refreshAllWidgets(context: Context) = CoroutineScope(Dispatchers.IO).launch {
        for (widgetProvider in WidgetApplication.instance.widgetProviders) {
            val widgetUpdateIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, context, widgetProvider)
            widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, WidgetApplication.instance.getWidgetIds(widgetProvider))
            context.sendBroadcast(widgetUpdateIntent)
        }
    }

    private fun refreshWidget(context: Context, intent: Intent) {
        // check if anything is restricted us from getting an updated price
        val restriction = NetworkStatusHelper.getRestriction(context)
        if (restriction != 0) {
            Toast.makeText(context, restriction, Toast.LENGTH_LONG).show()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0)
            val dao = WidgetDatabase.getInstance(context).widgetDao()
            val widget = dao.getByWidgetId(widgetId) ?: return@launch
            RemoteWidgetPresenter(context, widget).loading(context, widgetId)
            WidgetUpdater.update(context, intArrayOf(widgetId), manual = true)
        }
    }

    private fun postMessage(context: Context, message: Int) =
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}