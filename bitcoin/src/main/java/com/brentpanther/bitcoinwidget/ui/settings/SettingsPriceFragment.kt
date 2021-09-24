package com.brentpanther.bitcoinwidget.ui.settings

import android.os.Build
import androidx.preference.ListPreference
import com.brentpanther.bitcoinwidget.*
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.exchange.Exchange

class SettingsPriceFragment : SettingsFragment() {

    override fun updateWidget(widget: Widget?, rootKey: String?) {
        val defaultExchange = data.getDefaultExchange(data.defaultCurrency!!)
        viewModel.widget = viewModel.widget ?: widget ?: Widget(
            0,
            widgetId = viewModel.widgetId,
            widgetType = WidgetType.PRICE,
            exchange = Exchange.valueOf(defaultExchange),
            coin = data.coinEntry.coin,
            currency = data.defaultCurrency!!,
            coinCustomId = if (data.coinEntry.coin == Coin.CUSTOM) data.coinEntry.id else null,
            coinCustomName = if (data.coinEntry.coin == Coin.CUSTOM) data.coinEntry.name else null,
            currencyCustomName = null,
            showExchangeLabel = false,
            showCoinLabel = false,
            showIcon = true,
            showDecimals = false,
            currencySymbol = null,
            theme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Theme.MATERIAL else Theme.SOLID,
            nightMode = NightMode.SYSTEM,
            coinUnit = data.coinEntry.coin.getUnits().firstOrNull()?.text,
            currencyUnit = null,
            customIcon = data.coinEntry.iconUrl?.substringBefore("/"),
            lastUpdated = 0,
            showAmountLabel = false,
            state = WidgetState.CURRENT
        )
        viewModel.widget?.let {
            preferenceManager.preferenceDataStore = WidgetDataStore(it)
        }
        setPreferencesFromResource(R.xml.preferences_price, rootKey)
    }

    override fun loadAdditionalPreferences() {
        if (viewModel.widget?.coinUnit != null) {
            val unitNames = data.coinEntry.coin.getUnits().map { it.text }.toTypedArray()
            findPreference<ListPreference>("units_coin")?.apply {
                isVisible = true
                entries = unitNames
                entryValues = unitNames
                setSummaryProvider {
                    getString(R.string.summary_units, data.coinEntry.name, value)
                }
            }
        }
    }
}