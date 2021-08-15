package com.brentpanther.bitcoinwidget.ui.manage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.brentpanther.bitcoinwidget.db.Configuration
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.db.WidgetDatabase
import com.brentpanther.bitcoinwidget.db.WidgetSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ManageWidgetsViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = WidgetDatabase.getInstance(application).widgetDao()

    val widgets = MutableSharedFlow<List<Widget>>()

    fun getWidgets(): Flow<List<WidgetSettings>> {
        return dao.getAllAsFlow().combine(dao.configWithSizesAsFlow()) { w, c ->
            w.map { WidgetSettings(it, c, alwaysCurrent = true) }
        }
    }

    val globalSettings = dao.configAsFlow()

    fun updateGlobalSettings(config: Configuration) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.update(config)
        }
    }
}