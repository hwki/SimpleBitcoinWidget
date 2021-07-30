package com.brentpanther.bitcoinwidget.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.brentpanther.bitcoinwidget.Repository
import com.brentpanther.bitcoinwidget.db.WidgetSettings

class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    val data = Repository.data(app)

    val widgetData = MutableLiveData<WidgetSettings>()

}