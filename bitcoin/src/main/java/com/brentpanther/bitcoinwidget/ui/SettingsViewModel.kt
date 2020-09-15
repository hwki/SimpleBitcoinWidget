package com.brentpanther.bitcoinwidget.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.brentpanther.bitcoinwidget.Repository

class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    val data = Repository.data(app)

}