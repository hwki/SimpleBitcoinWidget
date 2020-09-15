package com.brentpanther.bitcoinwidget.ui

import android.app.Activity
import android.app.UiModeManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.preference.*
import com.brentpanther.bitcoinwidget.*
import com.brentpanther.bitcoinwidget.Exchange
import com.brentpanther.bitcoinwidget.Prefs
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.Themer.DAY_NIGHT
import com.brentpanther.bitcoinwidget.Themer.TRANSPARENT_DAY_NIGHT
import java.text.DecimalFormat
import java.util.*


class SettingsFragment : PreferenceFragmentCompat(), SettingsDialogFragment.NoticeDialogListener {

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
    private var fixedSize: Boolean = false
    private var checkDataSaver: Boolean = true
    private var checkBatterySaver: Boolean = true
    private var symbolValue: String? = null

    override fun onCreateView(@NonNull inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!requireActivity().isFinishing) {
            saveAndUpdate(true)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        fixedSize = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(getString(R.string.key_fixed_size), false)
        setHasOptionsMenu(true)
        data = ExchangeDataHelper.data!!
        widgetId = arguments?.getInt("widgetId")!!
        loadPreferences(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val menuItem = menu.add(0, 0, 0, R.string.save)
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 0) {
            save()
            return true
        }
        return super.onOptionsItemSelected(item)
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
        val fixedSize = findPreference(getString(R.string.key_fixed_size)) as? TwoStatePreference
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
        symbol.setOnPreferenceChangeListener { _, newValue ->
            saveAndUpdate(symbol, newValue,true)
        }
        symbol.setSummaryProvider {
            (it as ListPreference).entry
        }

        // exchange option
        setExchange(currency.value)
        bundle?.getString("exchange")?.let { exchange.value = it }
        currency.setOnPreferenceChangeListener { _, newValue ->
            setExchange(newValue as String)
            saveAndUpdate(currency, newValue, true)
            true
        }
        exchange.setOnPreferenceChangeListener { _, newValue -> saveAndUpdate(exchange, newValue, true) }
        exchange.setSummaryProvider {
            val exchange = Exchange.valueOf((it as ListPreference).value)
            getString(R.string.summary_exchange, exchange.exchangeName)
        }

        // icon
        icon.title = getString(R.string.title_icon, data.coin.coinName)
        bundle?.getBoolean("icon")?.let { icon.isChecked = it }
        icon.setOnPreferenceChangeListener { _, newValue -> saveAndUpdate(icon, newValue, false) }

        // decimals
        bundle?.getBoolean("decimals")?.let { decimals.isChecked = it }
        decimals.setOnPreferenceChangeListener { _, newValue -> saveAndUpdate(decimals, newValue, true) }

        // exchange label
        bundle?.getBoolean("label")?.let { label.isChecked = it }
        label.setOnPreferenceChangeListener { _, newValue -> saveAndUpdate(label, newValue, false) }

        // theme
        bundle?.getString("theme")?.let { theme.value = it }
        theme.setOnPreferenceChangeListener { _, newValue ->
            if (DAY_NIGHT == newValue || TRANSPARENT_DAY_NIGHT == newValue) {
                val service = requireActivity().getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                service.nightMode = UiModeManager.MODE_NIGHT_AUTO
            }
            saveAndUpdate(theme, newValue, false)
        }
        theme.setSummaryProvider {
            (it as ListPreference).entry
        }

        fixedSize?.setOnPreferenceChangeListener { _, newValue ->
            // if we are switching fixed text size on, clear out any pre-existing values
            if (newValue.toString().toBoolean()) {
                val widgetIds = WidgetApplication.instance.widgetIds
                for (widgetId in widgetIds) {
                    Prefs(widgetId).clearTextSize()
                }
            }
            saveAndUpdate(fixedSize, newValue, false)
        }

        // units
        val unitNames = data.coin.unitNames
        if (unitNames.isNotEmpty()) {
            findPreference<Preference>(getString(R.string.key_units))?.isVisible = true
            units.value = bundle?.getString("units") ?: unitNames[0]
            units.entries = unitNames
            units.entryValues = unitNames
            units.setOnPreferenceChangeListener { _, newValue -> saveAndUpdate(units, newValue, true) }
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

    private fun saveAndUpdate(pref: ListPreference, newValue: Any?, refreshValue: Boolean): Boolean {
        pref.value = newValue as String?
        saveAndUpdate(refreshValue)
        return true
    }

    private fun saveAndUpdate(pref: TwoStatePreference, newValue: Any, refreshValue: Boolean): Boolean {
        pref.isChecked = newValue as Boolean
        saveAndUpdate(refreshValue)
        return true
    }

    private fun saveAndUpdate(refreshValue: Boolean) {
        saveValues(true)
        (activity as SettingsActivity).updateWidget(refreshValue)
    }

    private fun saveValues(temporary: Boolean) {
        val prefs = Prefs(widgetId)
        setSymbol(symbol.value)
        prefs.setValues(data.coin.name, currency.value, (refresh.value).toInt(),
                exchange.value, label.isChecked, theme.value, icon.isChecked,
                decimals.isChecked, units.value, symbolValue)
        val exchangeCoinName = data.getExchangeCoinName(exchange.value, data.coin.name)
        val exchangeCurrencyName = data.getExchangeCurrencyName(exchange.value, currency.value)
        prefs.setExchangeValues(exchangeCoinName, exchangeCurrencyName)
        prefs.setTemporary(temporary)
    }

    private fun save() {
        if (checkDataSaver && !checkDataSaver()) return
        if (checkBatterySaver && !checkBatterySaver()) return
        saveValues(false)
        requireActivity().setResult(Activity.RESULT_OK)
        val newFixedSize = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(getString(R.string.key_fixed_size), false)
        if (fixedSize && !newFixedSize) {
            WidgetProvider.refreshWidgets(requireActivity(), widgetId)
        } else {
            WidgetProvider.refreshWidgets(requireActivity(), listOf(widgetId))
        }
        requireActivity().finish()
    }

    private fun checkBatterySaver(): Boolean {
        if (NetworkStatusHelper.checkBattery(requireContext()) > 0) {
            // user has battery saver on, warn that widget will be affected
            val dialogFragment = SettingsDialogFragment.newInstance(R.string.title_warning, R.string.warning_battery_saver, CODE_BATTERY_SAVER, false)
            dialogFragment.show(parentFragmentManager, "dialog")
            return false
        }
        return true
    }

    private fun checkDataSaver(): Boolean {
        if (NetworkStatusHelper.checkBackgroundData(requireContext()) > 0) {
            // user has data saver on, show dialog asking for permission to whitelist
            val dialogFragment = SettingsDialogFragment.newInstance(R.string.title_warning, R.string.warning_data_saver, CODE_DATA_SAVER)
            dialogFragment.show(parentFragmentManager, "dialog")
            return false
        }
        return true
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

