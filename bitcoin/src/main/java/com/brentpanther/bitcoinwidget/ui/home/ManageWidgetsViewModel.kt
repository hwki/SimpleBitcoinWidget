package com.brentpanther.bitcoinwidget.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brentpanther.bitcoinwidget.WidgetApplication
import com.brentpanther.bitcoinwidget.WidgetProvider
import com.brentpanther.bitcoinwidget.db.Configuration
import com.brentpanther.bitcoinwidget.db.WidgetDatabase
import com.brentpanther.bitcoinwidget.db.WidgetSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ManageWidgetsViewModel : ViewModel() {

    private val dao = WidgetDatabase.getInstance(WidgetApplication.instance).widgetDao()

    fun getWidgets(): Flow<List<WidgetSettings>> {
        return dao.getAllAsFlow().combine(dao.configWithSizesAsFlow()) { w, c ->
            w.map { WidgetSettings(it, c, alwaysCurrent = true) }
        }
    }

    val globalSettings = dao.configAsFlow()

    private fun setValue(action: ((Configuration) -> Unit)) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.config().apply {
                action(this)
                dao.update(this)
            }
        }
    }

    fun setFixedSize(value: Boolean) {
        setValue {
            it.consistentSize = value
        }
        WidgetProvider.refreshWidgets(WidgetApplication.instance)
    }
    fun setRefreshInterval(value: String) {
        setValue {
            it.refresh = value.toInt()
        }
        WidgetProvider.refreshWidgets(WidgetApplication.instance, restart = true)
    }

    companion object {
        const val dismissTime: Long = 172800000L
    }
}