package com.brentpanther.bitcoinwidget.ui.settings

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brentpanther.bitcoinwidget.NightMode
import com.brentpanther.bitcoinwidget.Repository.downloadCustomIcon
import com.brentpanther.bitcoinwidget.Repository.downloadJSON
import com.brentpanther.bitcoinwidget.Repository.getExchangeData
import com.brentpanther.bitcoinwidget.Theme
import com.brentpanther.bitcoinwidget.WidgetApplication
import com.brentpanther.bitcoinwidget.WidgetProvider
import com.brentpanther.bitcoinwidget.WidgetState
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.db.WidgetDao
import com.brentpanther.bitcoinwidget.db.WidgetDatabase
import com.brentpanther.bitcoinwidget.exchange.Exchange
import com.brentpanther.bitcoinwidget.exchange.ExchangeData
import com.brentpanther.bitcoinwidget.strategy.data.WidgetDataStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.Currency
import java.util.Locale

class SettingsViewModel : ViewModel() {

    private var dao: WidgetDao = WidgetDatabase.getInstance(WidgetApplication.instance).widgetDao()

    val configFlow = dao.configWithSizesAsFlow()
    private var exchangeData: ExchangeData? = null

    val exchanges = mutableStateListOf<Exchange>()
    val widgetFlow = MutableStateFlow<Widget?>(null)
    private var widgetCopy: Widget? = null
    private val widget by lazy { widgetCopy ?: throw IllegalStateException() }

    fun loadData(widgetId: Int) {
        if (widgetCopy != null) return
        viewModelScope.launch(Dispatchers.IO) {
            val dbWidget = dao.getByWidgetId(widgetId) ?: return@launch
            widgetCopy = dbWidget.copy()
            downloadJSON()
            exchangeData = getExchangeData(widget)
            loadExchanges()
            updateData()
            downloadCustomIcon(widget)
            widgetFlow.tryEmit(widget.copy(lastUpdated = System.currentTimeMillis()))
        }
    }

    fun getCurrencies() = exchangeData?.currencies?.toList() ?: emptyList()

    private fun updateData() = viewModelScope.launch(Dispatchers.IO) {
        val strategy = WidgetDataStrategy.getStrategy(widget.widgetId)
        widget.currencyCustomName = exchangeData?.getExchangeCurrencyName(widget.exchange.name, widget.currency)
        widget.coinCustomName = exchangeData?.getExchangeCoinName(widget.exchange.name)
        strategy.widget = widget
        strategy.loadData(manual = false)
        widgetFlow.tryEmit(widget.copy(lastUpdated = System.currentTimeMillis()))
    }

    fun setCurrency(currency: String) {
        widget.currency = currency
        updateData()
        loadExchanges()
    }

    fun setExchange(exchange: Exchange) {
        widget.exchange = exchange
        updateData()
    }

    private fun loadExchanges() {
        val exchangeList = exchangeData!!.getExchanges(widget.currency).map {
            Exchange.valueOf(it)
        }
        exchanges.clear()
        exchanges.addAll(exchangeList)

        if (widget.exchange !in exchangeList) {
            val defaultExchange = exchangeData!!.getDefaultExchange(widget.currency)
            widget.exchange = Exchange.valueOf(defaultExchange)
        }
    }

    private fun emit(action: (Widget) -> Unit) {
        action(widget)
        widgetFlow.tryEmit(widget.copy(lastUpdated = System.currentTimeMillis()))
    }

    fun setInverse(inverse: Boolean) = emit {
        widget.useInverse = inverse
    }

    fun setCurrencySymbol(symbol: String) = emit {
        val currencySymbol = when(symbol) {
            "ISO" -> null
            "NONE" -> "none"
            else -> getLocalSymbol(widget.currency)
        }
        widget.currencySymbol = currencySymbol
    }

    private fun getLocalSymbol(currencyCode: String?): String? {
        val locale = Locale.getAvailableLocales().filter {
            // find all locales that match this currency code
            try {
                Currency.getInstance(it).currencyCode == currencyCode
            } catch (ignored: Exception) {
                false
            }
        }.minByOrNull {
            // pick the best currency symbol, which is probably the one that does not match the ISO symbol
            val symbols = (DecimalFormat.getCurrencyInstance(it) as DecimalFormat).decimalFormatSymbols
            if (symbols.currencySymbol == symbols.internationalCurrencySymbol) 1 else 0
        } ?: return null
        return (DecimalFormat.getCurrencyInstance(locale) as DecimalFormat).decimalFormatSymbols.currencySymbol
    }

    fun setCoinUnit(unit: String) = emit {
        widget.coinUnit = unit
    }

    fun setCurrencyUnit(unit: String?) = emit {
        widget.currencyUnit = unit
    }

    fun setTheme(theme: Theme) = emit {
        widget.theme = theme
    }

    fun setNightMode(nightMode: NightMode) = emit {
        widget.nightMode = nightMode
    }

    fun setNumDecimals(numDecimals: Int) = emit {
        widget.numDecimals = numDecimals
    }

    fun setShowIcon(showIcon: Boolean) = emit {
        widget.showIcon = showIcon
    }

    fun setShowCoinLabel(showCoinLabel: Boolean) = emit {
        widget.showCoinLabel = showCoinLabel
    }

    fun setShowExchangeLabel(showExchangeLabel: Boolean) = emit {
        widget.showExchangeLabel = showExchangeLabel
    }

    fun setAmountHeld(amount: Double) = emit {
        widget.amountHeld = amount
        updateData()
    }

    fun setShowAmountLabel(showAmountLabel: Boolean) = emit {
        widget.showAmountLabel = showAmountLabel
    }

    fun save() = viewModelScope.launch(Dispatchers.IO) {
        widget.state = WidgetState.CURRENT
        dao.update(widget)
        WidgetProvider.refreshWidgets(WidgetApplication.instance)
    }


}
