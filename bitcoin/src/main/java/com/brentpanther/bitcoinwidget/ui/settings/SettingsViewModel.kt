package com.brentpanther.bitcoinwidget.ui.settings

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.brentpanther.bitcoinwidget.Coin
import com.brentpanther.bitcoinwidget.CoinEntry
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.Repository
import com.brentpanther.bitcoinwidget.db.ConfigurationWithSizes
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.db.WidgetDatabase
import com.brentpanther.bitcoinwidget.db.WidgetSettings
import com.brentpanther.bitcoinwidget.exchange.CustomExchangeData
import com.brentpanther.bitcoinwidget.exchange.ExchangeData
import com.brentpanther.bitcoinwidget.ui.settings.SettingsViewModel.DataState.Downloading
import com.brentpanther.bitcoinwidget.ui.settings.SettingsViewModel.DataState.Success
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.InputStream

class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    var widgetId: Int = 0
    val saveWidgetFlow = MutableLiveData<Boolean>()
    private lateinit var configWithSizes: ConfigurationWithSizes
    private var success: Success? = null
    var exchangeDataFlow = MutableSharedFlow<Success>(1)

    val widgetPreviewFlow = MutableSharedFlow<WidgetSettings>()

    var widget: Widget? = null

    @OptIn(FlowPreview::class)
    fun settingsData(coin: CoinEntry, context: Context) = flow {
        if (success == null) {
            emit(Downloading(true))
            val dao = WidgetDatabase.getInstance(getApplication()).widgetDao()
            val exchangeData = viewModelScope.async(Dispatchers.IO) { getExchangeData(coin, context) }
            val widgetData = viewModelScope.async(Dispatchers.IO) { dao.getByWidgetId(widgetId) }
            val configData = viewModelScope.async(Dispatchers.IO) { dao.configWithSizes() }
            configWithSizes = configData.await()
            success = Success(widgetData.await(), exchangeData.await())
        }
        success?.let {
            exchangeDataFlow.emit(it)
            emit(it)
        }
    }.debounce(350)

    fun updateWidget(refreshPrice: Boolean) {
        viewModelScope.launch {
            widget?.let {
                widgetPreviewFlow.emit(WidgetSettings(it, configWithSizes, refreshPrice, alwaysCurrent = true))
            }
        }
    }

    private fun getExchangeData(coin: CoinEntry, context: Context): ExchangeData {
        Log.i("SettingsViewModel", "Downloading JSON...")
        Repository.downloadJSON(context.applicationContext)
        return try {
            if (coin.coin == Coin.CUSTOM) {
                CustomExchangeData(coin, getJson(context))
            } else  {
                ExchangeData(coin, getJson(context))
            }
        } catch(e: JsonSyntaxException) {
            Log.e("SettingsViewModel", "Error parsing JSON file, falling back to original.", e)
            context.deleteFile(Repository.CURRENCY_FILE_NAME)
            ExchangeData(coin, getJson(context))
        } catch (e: FileNotFoundException) {
            throw RuntimeException(e)
        }
    }

    private fun getJson(context: Context): InputStream {
       return if (java.io.File(context.filesDir, Repository.CURRENCY_FILE_NAME).exists()) {
            context.openFileInput(Repository.CURRENCY_FILE_NAME)
        } else {
            context.resources.openRawResource(R.raw.cryptowidgetcoins)
        }
    }

    fun save() {
        saveWidgetFlow.postValue(true)
    }

    sealed class DataState {
        data class Downloading(val downloading: Boolean) : DataState()
        data class Success(val widget: Widget?, val exchangeData: ExchangeData) : DataState()
    }

}