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
import com.brentpanther.bitcoinwidget.db.PriceType
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.db.WidgetDao
import com.brentpanther.bitcoinwidget.db.WidgetDatabase
import com.brentpanther.bitcoinwidget.exchange.Exchange
import com.brentpanther.bitcoinwidget.exchange.ExchangeData
import com.brentpanther.bitcoinwidget.strategy.data.WidgetDataStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.Currency
import java.util.Locale

class SettingsViewModel : ViewModel(), SettingsActions {

    private var dao: WidgetDao = WidgetDatabase.getInstance(WidgetApplication.instance).widgetDao()

    val fixedSizeFlow = dao.configWithSizesAsFlow().map { it.consistentSize }
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
            exchangeData = getExchangeData(widget.coin, widget.coinName())
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
        widget.lastUpdated = 0
        strategy.widget = widget
        strategy.loadData(manual = false)
        widgetFlow.tryEmit(widget.copy(lastUpdated = System.currentTimeMillis()))
    }

    override fun setCurrency(currency: String) {
        widget.currency = currency
        updateData()
        loadExchanges()
    }

    override fun setExchange(exchange: Exchange) {
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

    override fun setInverse(isInverse: Boolean) = emit {
        widget.useInverse = isInverse
    }

    override fun setCurrencySymbol(symbol: String) = emit {
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
            } catch (_: Exception) {
                false
            }
        }.minByOrNull {
            // pick the best currency symbol, which is probably the one that does not match the ISO symbol
            val symbols = (DecimalFormat.getCurrencyInstance(it) as DecimalFormat).decimalFormatSymbols
            if (symbols.currencySymbol == symbols.internationalCurrencySymbol) 1 else 0
        } ?: return null
        return (DecimalFormat.getCurrencyInstance(locale) as DecimalFormat).decimalFormatSymbols.currencySymbol
    }

    override fun setCoinUnit(unit: String) = emit {
        widget.coinUnit = unit
    }

    override fun setCurrencyUnit(unit: String?) = emit {
        widget.currencyUnit = unit
    }

    override fun setTheme(theme: Theme) = emit {
        widget.theme = theme
    }

    override fun setNightMode(mode: NightMode) = emit {
        widget.nightMode = mode
    }

    override fun setNumDecimals(amount: Int) = emit {
        widget.numDecimals = amount
    }

    override fun setShowIcon(isShown: Boolean) = emit {
        widget.showIcon = isShown
    }

    override fun setShowCoinLabel(isShown: Boolean) = emit {
        widget.showCoinLabel = isShown
    }

    override fun setShowExchangeLabel(isShown: Boolean) = emit {
        widget.showExchangeLabel = isShown
    }

    override fun setAmountHeld(amount: Double?) = emit {
        widget.amountHeld = amount
        widget.address = null
        updateData()
    }

    override fun setAddress(address: String?) = emit {
        widget.address = address
        widget.amountHeld = null
        widget.showAmountLabel = false
        updateData()
    }

    override fun setShowAmountLabel(isShown: Boolean) = emit {
        widget.showAmountLabel = isShown
    }

    override fun setPriceType(type: PriceType) = emit {
        widget.priceType = type
        updateData()
    }

    fun save() = viewModelScope.launch(Dispatchers.IO) {
        if (widget.state == WidgetState.DRAFT) {
            widget.state = WidgetState.CURRENT
        }
        dao.update(widget)
        WidgetProvider.refreshWidgets(WidgetApplication.instance, widget.widgetId)
    }


}

interface SettingsActions {
    fun setCurrency(currency: String) {}
    fun setExchange(exchange: Exchange) {}
    fun setCurrencySymbol(symbol: String) {}
    fun setInverse(isInverse: Boolean) {}
    fun setAddress(address: String?) {}
    fun setAmountHeld(amount: Double?) {}
    fun setNightMode(mode: NightMode) {}
    fun setShowAmountLabel(isShown: Boolean) {}
    fun setNumDecimals(amount: Int) {}
    fun setCoinUnit(unit: String) {}
    fun setCurrencyUnit(unit: String?) {}
    fun setTheme(theme: Theme) {}
    fun setShowIcon(isShown: Boolean) {}
    fun setShowCoinLabel(isShown: Boolean) {}
    fun setShowExchangeLabel(isShown: Boolean) {}
    fun setPriceType(type: PriceType) {}
}
