package com.brentpanther.bitcoinwidget.ui.settings

import android.app.Activity
import android.app.UiModeManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.*
import com.brentpanther.bitcoinwidget.*
import com.brentpanther.bitcoinwidget.Exchange
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.db.Configuration
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.db.WidgetDatabase
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.DecimalFormat
import java.util.*
import kotlin.math.min


class SettingsFragment : PreferenceFragmentCompat(), SettingsDialogFragment.NoticeDialogListener {

    private val viewModel: SettingsViewModel by activityViewModels()
    private lateinit var data: ExchangeData
    private var widgetId: Int = 0
    private lateinit var refresh: ListPreference
    private lateinit var currency: ListPreference
    private lateinit var symbol: ListPreference
    private lateinit var exchange: ListPreference
    private lateinit var icon: TwoStatePreference
    private lateinit var units: ListPreference
    private lateinit var decimals: TwoStatePreference
    private lateinit var label: TwoStatePreference
    private lateinit var theme: ListPreference
    private lateinit var fixedSize: TwoStatePreference
    private var checkDataSaver: Boolean = true
    private var checkBatterySaver: Boolean = true
    private var symbolValue: String? = null
    private lateinit var widget : Widget
    private lateinit var configuration: Configuration


    override fun onCreateView(@NonNull inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!requireActivity().isFinishing) {
            saveAndUpdate(true)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        setHasOptionsMenu(true)
        data = ExchangeDataHelper.data!!
        widgetId = arguments?.getInt("widgetId")!!
        loadPreferences(savedInstanceState)
    }

    override fun onSaveInstanceState(@NonNull outState: Bundle) {
        with(outState) {
            putString("refresh", refresh.value)
            putString("currency", currency.value)
            putString("exchange", exchange.value)
            putBoolean("icon", icon.isChecked)
            putBoolean("decimals", decimals.isChecked)
            putBoolean("label", label.isChecked)
            putString("theme", theme.value)
            putString("units", units.value)
            putString("symbol", symbol.value)
        }
        super.onSaveInstanceState(outState)
    }

    private fun loadPreferences(bundle: Bundle?) {
        refresh = findPreference(getString(R.string.key_refresh_interval))!!
        currency = findPreference(getString(R.string.key_currency))!!
        symbol = findPreference(getString(R.string.key_symbol))!!
        exchange = findPreference(getString(R.string.key_exchange))!!
        icon = findPreference(getString(R.string.key_icon))!!
        decimals = findPreference(getString(R.string.key_decimals))!!
        label = findPreference(getString(R.string.key_label))!!
        theme = findPreference(getString(R.string.key_theme))!!
        fixedSize = findPreference(getString(R.string.key_fixed_size))!!
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = WidgetDatabase.getInstance(requireContext()).widgetDao()
            fixedSize.isChecked = dao.config().consistentSize
        }
        units = findPreference(getString(R.string.key_units))!!
        val rate = findPreference(getString(R.string.key_rate)) as? Preference

        // refresh option
        bundle?.getString("refresh")?.let {refresh.value = it}
        setRefresh(refresh.value.toInt())
        refresh.setOnPreferenceChangeListener { _, newValue ->
            setRefresh((newValue as String).toInt())
            true
        }

        // currency option
        currency.entries = data.currencies
        currency.entryValues = data.currencies
        val defaultCurrency = data.defaultCurrency
        if (defaultCurrency == null) {
            Toast.makeText(context, R.string.error_no_currencies, Toast.LENGTH_LONG).show()
            requireActivity().finish()
            return
        }
        currency.value = bundle?.getString("currency") ?: defaultCurrency
        currency.setSummaryProvider {
            getString(R.string.summary_currency, (it as ListPreference).value)
        }

        // symbol option
        bundle?.getString("symbol")?.let { symbol.value = it}
        symbol.onUpdate {  saveAndUpdate(true) }
        symbol.setSummaryProvider {
            (it as ListPreference).entry
        }

        // exchange option
        setExchange(currency.value)
        bundle?.getString("exchange")?.let { exchange.value = it }
        currency.onUpdate {
            setExchange(it)
            saveAndUpdate(true)
        }

        exchange.onUpdate { saveAndUpdate(true) }
        exchange.setSummaryProvider {
            val exchange = Exchange.valueOf((it as ListPreference).value)
            getString(R.string.summary_exchange, exchange.exchangeName)
        }

        // icon
        icon.title = getString(R.string.title_icon, data.coin.name)
        bundle?.getBoolean("icon")?.let { icon.isChecked = it }
        icon.onUpdate { saveAndUpdate(false) }

        // decimals
        bundle?.getBoolean("decimals")?.let { decimals.isChecked = it }
        decimals.onUpdate { saveAndUpdate(true) }

        // exchange label
        bundle?.getBoolean("label")?.let { label.isChecked = it }
        label.onUpdate { saveAndUpdate(true) }

        // theme
        bundle?.getString("theme")?.let { theme.value = it }
        theme.onUpdate {
            if (Theme.DAY_NIGHT.name == it || Theme.TRANSPARENT_DAY_NIGHT.name == it) {
                val service = requireActivity().getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                service.nightMode = UiModeManager.MODE_NIGHT_AUTO
            }
            saveAndUpdate(false)
        }
        theme.setSummaryProvider {
            (it as ListPreference).entry
        }

        fixedSize.onUpdate { saveAndUpdate(true) }

        // units
        val unitNames = data.coin.coin.unitNames
        if (unitNames.isNotEmpty()) {
            findPreference<Preference>(getString(R.string.key_units))?.isVisible = true
            units.value = bundle?.getString("units") ?: unitNames[0]
            units.entries = unitNames
            units.entryValues = unitNames
            units.onUpdate { saveAndUpdate(true) }
            units.setSummaryProvider {
                getString(R.string.summary_units, (it as ListPreference).value)
            }
        }

        // rate
        rate?.setOnPreferenceClickListener {
            val appPackageName = requireActivity().packageName
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (anfe: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=$appPackageName")))
            }
            true
        }

        widget = Widget(
            0, widgetId = widgetId, exchange = Exchange.valueOf(exchange.value),
            coin = data.coin.coin, currency = currency.value, coinCustomName = null,
            currencyCustomName = null, showLabel = label.isChecked,
            showIcon = icon.isChecked, showDecimals = decimals.isChecked,
            currencySymbol = symbolValue, theme = Theme.valueOf(theme.value),
            unit = units.value, customIcon = data.coin.iconUrl?.substringBefore("/"),
            lastUpdated = 0
        )
    }

    private fun setSymbol(value: String) {
        symbolValue = when (value) {
            "ISO" -> null
            "LOCAL" -> getLocalSymbol(currency.value)
            "NONE" -> "none"
            else -> null
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

    private fun setRefresh(value: Int) {
        when {
            value < 60 -> refresh.summary = resources.getQuantityString(R.plurals.summary_refresh_interval_minute, value, value)
            else -> refresh.summary = resources.getQuantityString(R.plurals.summary_refresh_interval_hour, value / 60, value / 60)
        }
    }

    private fun setExchange(currency: String) {
        with (exchange) {
            entryValues = data.getExchanges(currency)
            entries = entryValues.map { Exchange.valueOf(it.toString()).exchangeName }.toTypedArray()
            value = data.getDefaultExchange(currency)
        }
    }

    private fun saveAndUpdate(refresh: Boolean) {
        updateWidget()
        downloadCustomIcon(widget.customIcon)
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = WidgetDatabase.getInstance(requireContext()).widgetDao()
            val globalSettings = dao.config()
            val smallestWidgetSizes = dao.getSmallestSizes()
            viewModel.widgetData.postValue(com.brentpanther.bitcoinwidget.db.WidgetSettings(widget, globalSettings, smallestWidgetSizes, refresh))
        }
    }

    private fun updateWidget() {
        val exchangeCoinName = data.getExchangeCoinName(exchange.value)
        val exchangeCurrencyName = data.getExchangeCurrencyName(exchange.value, currency.value)
        val symbolValue = when (symbol.value) {
            "LOCAL" -> getLocalSymbol(currency.value)
            "NONE" -> "none"
            else -> null
        }
        widget = widget.copy(exchange = Exchange.valueOf(exchange.value),
            coin = data.coin.coin, currency = currency.value, coinCustomName = exchangeCoinName,
            currencyCustomName = exchangeCurrencyName, showLabel = label.isChecked,
            showIcon = icon.isChecked, showDecimals = decimals.isChecked,
            currencySymbol = symbolValue, theme = Theme.valueOf(theme.value),
            unit = units.value, customIcon = data.coin.iconUrl?.substringBefore("/"))
    }

    private fun downloadCustomIcon(customIcon: String?) = CoroutineScope(Dispatchers.IO).launch {
        if (customIcon == null) return@launch
        data.coin.getFullIconUrl()?.let {
            val dir = File(requireContext().filesDir, "icons")
            if (!dir.exists()) {
                dir.mkdir()
            }
            val file = File(dir, customIcon)
            if (file.exists()) {
                return@launch
            }

            val os = ByteArrayOutputStream()
            val stream = ExchangeHelper.getStream(it)
            val image = BitmapFactory.decodeStream(stream)
            image.compress(Bitmap.CompressFormat.PNG, 100, os)
            file.writeBytes(os.toByteArray())
        }
    }

    fun save() {
        if (checkDataSaver && !checkDataSaver()) return
        if (checkBatterySaver && !checkBatterySaver()) return
        updateWidget()
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = WidgetDatabase.getInstance(requireContext()).widgetDao()
            widget.lastUpdated = 0
            dao.insert(widget)
            val globalSettings = dao.config()
            globalSettings.refresh = min(globalSettings.refresh, refresh.value.toInt())
            globalSettings.consistentSize = fixedSize.isChecked
            dao.update(globalSettings)
            WidgetProvider.refreshWidgets(requireActivity(), listOf(widgetId))
        }
        requireActivity().setResult(Activity.RESULT_OK)
        requireActivity().finish()
    }

    private fun checkBatterySaver(): Boolean {
        return if (NetworkStatusHelper.checkBattery(requireContext()) > 0) {
            // user has battery saver on, warn that widget will be affected
            val dialogFragment = SettingsDialogFragment.newInstance(
                R.string.title_warning,
                R.string.warning_battery_saver,
                CODE_BATTERY_SAVER,
                false
            )
            dialogFragment.show(parentFragmentManager, "dialog")
            false
        } else true
    }

    private fun checkDataSaver(): Boolean {
        return if (NetworkStatusHelper.checkBackgroundData(requireContext()) > 0) {
            // user has data saver on, show dialog asking for permission to whitelist
            val dialogFragment =
                SettingsDialogFragment.newInstance(R.string.title_warning, R.string.warning_data_saver, CODE_DATA_SAVER)
            dialogFragment.show(parentFragmentManager, "dialog")
            false
        } else true
    }

    override fun onDialogPositiveClick(code: Int) {
        (parentFragmentManager.findFragmentByTag("dialog") as DialogFragment).dismissAllowingStateLoss()
        when (code) {
            CODE_BATTERY_SAVER -> checkBatterySaver = false
            CODE_DATA_SAVER -> checkDataSaver = false
        }
        save()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDialogNegativeClick() {
        startActivity(Intent(Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS,
                Uri.parse("package:" + requireActivity().packageName)))
    }

    companion object {

        private const val CODE_DATA_SAVER = 1
        private const val CODE_BATTERY_SAVER = 2

        internal fun newInstance(widgetId: Int): SettingsFragment {
            val fragment = SettingsFragment()
            val bundle = Bundle()
            bundle.putInt("widgetId", widgetId)
            fragment.arguments = bundle
            return fragment
        }
    }

}

