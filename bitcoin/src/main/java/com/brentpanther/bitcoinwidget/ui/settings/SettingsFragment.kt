package com.brentpanther.bitcoinwidget.ui.settings

import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.*
import com.brentpanther.bitcoinwidget.*
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.db.WidgetDatabase
import com.brentpanther.bitcoinwidget.exchange.Exchange
import com.brentpanther.bitcoinwidget.exchange.ExchangeData
import com.brentpanther.bitcoinwidget.exchange.ExchangeHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.DecimalFormat
import java.util.*

abstract class SettingsFragment : PreferenceFragmentCompat() {

    protected val viewModel: SettingsViewModel by activityViewModels()
    protected lateinit var data: ExchangeData
    private var checkDataSaver: Boolean = true
    private var checkBatterySaver: Boolean = true

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.exchangeDataFlow.collect { (widget, data) ->
                    updateWidget(data, widget, rootKey)
                }
            }
        }
    }

    abstract fun updateWidget(widget: Widget?, rootKey: String?)

    private fun updateWidget(data: ExchangeData, widget: Widget?, rootKey: String?) {
        this@SettingsFragment.data = data
        val defaultCurrency = data.defaultCurrency
        if (defaultCurrency == null) {
            Toast.makeText(context, R.string.error_no_currencies, Toast.LENGTH_LONG).show()
            requireActivity().finish()
            return
        }
        updateWidget(widget, rootKey)
        loadPreferences()
    }

    open fun loadAdditionalPreferences() {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.saveWidgetFlow.observe(viewLifecycleOwner) {
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.widget?.let {
                    it.lastUpdated = 0
                    WidgetDatabase.getInstance(requireContext()).widgetDao().insert(it)
                }
                WidgetProvider.refreshWidgets(requireContext())
            }
            requireActivity().setResult(Activity.RESULT_OK)
            requireActivity().finish()
        }
    }

    protected open fun loadPreferences() {
        findPreference<ListPreference>(getString(R.string.key_currency))?.apply {
            entries = data.currencies
            entryValues = data.currencies
        }
        updateExchangeValues()
        updateCurrencyUnits()
        loadAdditionalPreferences()
        CoroutineScope(Dispatchers.IO).launch {
            downloadCustomIcon()
            viewModel.updateWidget(true)
        }
    }

    private fun updateExchangeValues() {
        findPreference<ListPreference>(getString(R.string.key_exchange))?.apply {
            viewModel.widget?.let { widget ->
                entryValues = data.getExchanges(widget.currency)
                entries = entryValues.map { Exchange.valueOf(it.toString()).exchangeName }.toTypedArray()
                if (!entryValues.contains(value)) {
                    value = data.getDefaultExchange(widget.currency)
                }
            }
        }
    }

    private fun updateCurrencyUnits() {
        findPreference<ListPreference>("units_currency")?.apply {
            viewModel.widget?.let {
                val units = if (it.currency in Coin.COIN_NAMES) {
                    Coin.valueOf(it.currency).getUnits()
                } else {
                    emptyList()
                }
                isVisible = if (units.isNotEmpty()) {
                    val unitNames = units.map { u -> u.text }.toTypedArray()
                    entries = unitNames
                    entryValues = unitNames
                    value = unitNames[0]
                    setSummaryProvider { _ ->
                        getString(R.string.summary_units, it.currency, value)
                    }
                    true
                } else {
                    value = null
                    false
                }
            }
        }
    }


    private fun getLocalSymbol(currencyCode: String): String? {
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

    private fun downloadCustomIcon() {
        data.coinEntry.getFullIconUrl()?.let {
            val dir = File(requireContext().filesDir, "icons")
            if (!dir.exists()) {
                dir.mkdir()
            }
            val file = File(dir, data.coinEntry.iconUrl!!.substringBefore("/"))
            if (file.exists()) {
                return
            }

            val os = ByteArrayOutputStream()
            val stream = ExchangeHelper.getStream(it)
            val image = BitmapFactory.decodeStream(stream)
            image.compress(Bitmap.CompressFormat.PNG, 100, os)
            file.writeBytes(os.toByteArray())
            viewModel.updateWidget(false)
        }
    }

    inner class WidgetDataStore(val widget: Widget) : PreferenceDataStore() {
        override fun getBoolean(key: String?, defValue: Boolean): Boolean {
            return when(key) {
                "icon" -> widget.showIcon
                "decimals" -> widget.showDecimals
                "coinLabel" -> widget.showCoinLabel
                "exchangeLabel" -> widget.showExchangeLabel
                "amountLabel" -> widget.showAmountLabel
                "useInverse" -> widget.useInverse
                else -> throw IllegalArgumentException()
            }
        }

        override fun putBoolean(key: String?, value: Boolean) {
            val updatePrice = when(key) {
                "icon" -> {
                    widget.showIcon = value
                    false
                }
                "decimals" -> {
                    widget.showDecimals = value
                    true
                }
                "exchangeLabel" -> {
                    widget.showExchangeLabel = value
                    false
                }
                "coinLabel" -> {
                    widget.showCoinLabel = value
                    false
                }
                "amountLabel" -> {
                    widget.showAmountLabel = value
                    false
                }
                "useInverse" -> {
                    widget.useInverse = value
                    false
                }
                else -> false
            }
            viewModel.updateWidget(updatePrice)
        }

        override fun getString(key: String?, defValue: String?): String? {
            return when(key) {
                "currency" -> widget.currency
                "symbol" -> {
                    when(widget.currencySymbol) {
                        null -> "ISO"
                        "none" -> "NONE"
                        else -> "LOCAL"
                    }
                }
                "exchange" -> widget.exchange.name
                "units_coin" -> widget.coinUnit
                "units_currency" -> widget.currencyUnit
                "theme" -> widget.theme.name
                "nightMode" -> widget.nightMode.name
                "amountHeld" -> widget.amountHeld.toString()
                else -> throw IllegalArgumentException()
            }
        }

        override fun putString(key: String?, value: String?) {
            val updatePrice = when(key) {
                "currency" -> {
                    widget.currency = value ?: widget.currency
                    widget.currencyCustomName = data.getExchangeCurrencyName(widget.exchange.name, widget.currency)
                    updateExchangeValues()
                    updateCurrencyUnits()
                    true
                }
                "symbol" -> {
                    widget.currencySymbol = when(value) {
                        "ISO" -> null
                        "NONE" -> "none"
                        else -> getLocalSymbol(widget.currency)
                    }
                    false
                }
                "exchange" -> {
                    widget.exchange = Exchange.valueOf(value ?: Exchange.COINGECKO.name)
                    widget.coinCustomName = data.getExchangeCoinName(widget.exchange.name)
                    widget.currencyCustomName = data.getExchangeCurrencyName(widget.exchange.name, widget.currency)
                    true
                }
                "units_coin" -> {
                    widget.coinUnit = value
                    true
                }
                "units_currency" -> {
                    widget.currencyUnit = value
                    true
                }
                "theme" -> {
                    widget.theme = Theme.valueOf(value ?: Theme.SOLID.name)
                    false
                }
                "nightMode" -> {
                    widget.nightMode = NightMode.valueOf(value ?: NightMode.SYSTEM.name)
                    if (widget.nightMode == NightMode.SYSTEM) {
                        val service = requireActivity().getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                        service.nightMode = UiModeManager.MODE_NIGHT_AUTO
                    }
                    false
                }
                "amountHeld" -> {
                    widget.amountHeld = value?.toDoubleOrNull() ?: widget.amountHeld
                    true
                }
                else -> false
            }
            viewModel.updateWidget(updatePrice)
        }
    }

}

