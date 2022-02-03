package com.brentpanther.bitcoinwidget.ui.settings

import android.os.Build
import android.text.InputType
import androidx.preference.EditTextPreference
import com.brentpanther.bitcoinwidget.*
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.exchange.Exchange
import java.text.DecimalFormat
import java.util.*

class SettingsValueFragment : SettingsFragment() {

    override fun updateWidget(widget: Widget?, rootKey: String?) {
        val defaultExchange = data.getDefaultExchange(data.defaultCurrency!!)
        viewModel.widget = viewModel.widget ?: widget ?: Widget(
            0,
            widgetId = viewModel.widgetId,
            widgetType = WidgetType.VALUE,
            exchange = Exchange.valueOf(defaultExchange),
            coin = data.coinEntry.coin,
            currency = data.defaultCurrency!!,
            coinCustomId = if (data.coinEntry.coin == Coin.CUSTOM) data.coinEntry.id else null,
            coinCustomName = if (data.coinEntry.coin == Coin.CUSTOM) data.coinEntry.name else null,
            currencyCustomName = data.getExchangeCurrencyName(defaultExchange, data.defaultCurrency!!),
            showExchangeLabel = false,
            showCoinLabel = false,
            showIcon = true,
            numDecimals = -1,
            currencySymbol = null,
            theme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Theme.MATERIAL else Theme.SOLID,
            nightMode = NightMode.SYSTEM,
            coinUnit = null,
            currencyUnit = null,
            customIcon = data.coinEntry.iconUrl?.substringBefore("/"),
            lastUpdated = 0,
            amountHeld = 1.0,
            showAmountLabel = true,
            useInverse = false,
            state = WidgetState.CURRENT,
        )
        viewModel.widget?.let {
            preferenceManager.preferenceDataStore = WidgetDataStore(it)
        }
        setPreferencesFromResource(R.xml.preferences_value, rootKey)
    }

    override fun loadAdditionalPreferences() {
        findPreference<EditTextPreference>("amountHeld")?.apply {
            dialogMessage = getString(R.string.dialog_amount_held, viewModel.widget?.coinName())
            setOnBindEditTextListener {
                it.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
                it.setSelection(it.length())
            }
            setSummaryProvider {
                DecimalFormat.getNumberInstance(Locale.getDefault()).run {
                    minimumFractionDigits = 0
                    maximumFractionDigits = 10
                    format(viewModel.widget?.amountHeld)
                }
            }
        }
    }
}