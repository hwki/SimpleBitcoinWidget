package com.brentpanther.bitcoinwidget.ui

import android.appwidget.AppWidgetManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brentpanther.bitcoinwidget.WidgetApplication
import com.brentpanther.bitcoinwidget.db.WidgetDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    fun removeOrphanedWidgets() = viewModelScope.launch(Dispatchers.IO) {
        val widgetDao = WidgetDatabase.getInstance(WidgetApplication.instance).widgetDao()
        widgetDao.deleteOrphans(WidgetApplication.instance.widgetIds)
    }

    fun getStartDestination() = flow {
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            emit("home")
            return@flow
        }
        val widgetDao = WidgetDatabase.getInstance(WidgetApplication.instance).widgetDao()
        val widget = widgetDao.getByWidgetId(widgetId)
        if (widget == null) {
            emit("create/{widgetId}")
        } else {
            emit("setting/{widgetId}")
        }
    }.flowOn(Dispatchers.IO)


}