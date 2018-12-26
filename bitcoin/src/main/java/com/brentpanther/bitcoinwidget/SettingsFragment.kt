package com.brentpanther.bitcoinwidget

import android.app.Activity
import android.app.UiModeManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.preference.*

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var data: ExchangeData
    private var widgetId: Int = 0
    private lateinit var refresh: ListPreference
    private lateinit var currency: ListPreference
    private lateinit var exchange: ListPreference
    private lateinit var icon: TwoStatePreference
    private lateinit var units: ListPreference
    private lateinit var decimals: TwoStatePreference
    private lateinit var label: TwoStatePreference
    private lateinit var theme: ListPreference
    private var fixedSize: Boolean = false

    override fun onCreateView(@NonNull inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        saveAndUpdate(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        fixedSize = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(getString(R.string.key_fixed_size), false)
        setHasOptionsMenu(true)
        data = arguments?.getSerializable("data") as ExchangeData
        widgetId = arguments?.getInt("widgetId")!!
        loadPreferences(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val menuItem = menu.add(0, 0, 0, R.string.save)
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
    }

    override fun onSaveInstanceState(@NonNull outState: Bundle) {
        outState.putString("refresh", refresh.value)
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
        refresh = findPreference(getString(R.string.key_refresh_interval)) as ListPreference
        currency = findPreference(getString(R.string.key_currency)) as ListPreference
        exchange = findPreference(getString(R.string.key_exchange)) as ListPreference
        icon = findPreference(getString(R.string.key_icon)) as TwoStatePreference
        decimals = findPreference(getString(R.string.key_decimals)) as TwoStatePreference
        label = findPreference(getString(R.string.key_label)) as TwoStatePreference
        theme = findPreference(getString(R.string.key_theme)) as ListPreference
        val fixedSize = findPreference(getString(R.string.key_fixed_size)) as TwoStatePreference
        units = findPreference(getString(R.string.key_units)) as ListPreference
        val rate = findPreference(getString(R.string.key_rate)) as Preference

        val refreshValue = bundle?.getString("refresh")
        if (refreshValue != null) refresh.value = refreshValue

        // refresh option
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
        currency.value = defaultCurrency

        // exchange option
        setExchange(defaultCurrency)
        currency.setOnPreferenceChangeListener { _, newValue ->
            setExchange(newValue as String)
            saveAndUpdate(currency, newValue, true)
        }
        exchange.setOnPreferenceChangeListener { _, newValue -> saveAndUpdate(exchange, newValue, true) }

        // icon
        icon.title = getString(R.string.title_icon, data.coin.name)
        icon.setOnPreferenceChangeListener { _, newValue -> saveAndUpdate(icon, newValue, false) }

        // decimals
        decimals.setOnPreferenceChangeListener { _, newValue -> saveAndUpdate(decimals, newValue, true) }

        // exchange label
        label.setOnPreferenceChangeListener { _, newValue -> saveAndUpdate(label, newValue, false) }

        // theme
        theme.setOnPreferenceChangeListener { _, newValue ->
            if ("DayNight" == newValue || "Transparent DayNight" == newValue) {
                val service = requireActivity().getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                service.nightMode = UiModeManager.MODE_NIGHT_AUTO
            }
            saveAndUpdate(theme, newValue, false)
        }

        fixedSize.setOnPreferenceChangeListener { _, newValue ->
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
            findPreference<Preference>(getString(R.string.key_units)).isVisible = true
            units.value = unitNames[0]
            units.entries = unitNames
            units.entryValues = unitNames
            units.setOnPreferenceChangeListener { _, newValue -> saveAndUpdate(units, newValue, true) }
        }

        // rate
        rate.setOnPreferenceClickListener {
            val appPackageName = requireActivity().packageName
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (anfe: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=$appPackageName")))
            }
            true
        }
    }


    private fun setRefresh(value: Int) {
        when {
            value < 60 -> refresh.summary = resources.getQuantityString(R.plurals.summary_refresh_interval_minute, value, value)
            else -> refresh.summary = resources.getQuantityString(R.plurals.summary_refresh_interval_hour, value / 60, value / 60)
        }
    }

    private fun setExchange(currency: String) {
        exchange.entryValues = data.getExchanges(currency)
        exchange.entries = exchange.entryValues.map { Exchange.valueOf(it.toString()).exchangeName }.toTypedArray()
        exchange.value = data.getDefaultExchange(currency)
    }

    private fun saveAndUpdate(pref: ListPreference, newValue: Any, refreshValue: Boolean): Boolean {
        pref.value = newValue as String
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
        prefs.setValues(data.coin.name, currency.value, (refresh.value).toInt(),
                exchange.value, label.isChecked, theme.value, icon.isChecked,
                decimals.isChecked, units.value)
        val exchangeCoinName = data.getExchangeCoinName(exchange.value, data.coin.name)
        val exchangeCurrencyName = data.getExchangeCurrencyName(exchange.value, currency.value)
        prefs.setExchangeValues(exchangeCoinName, exchangeCurrencyName)
        prefs.setTemporary(temporary)
    }

    private fun save() {
        saveValues(false)
        requireActivity().setResult(Activity.RESULT_OK)
        val newFixedSize = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(getString(R.string.key_fixed_size), false)
        if (fixedSize && !newFixedSize) {
            WidgetProvider.refreshWidgets(requireActivity(), widgetId)
        }
        requireActivity().finish()
    }

    companion object {

        internal fun newInstance(data: ExchangeData, widgetId: Int): SettingsFragment {
            val fragment = SettingsFragment()
            val bundle = Bundle()
            bundle.putInt("widgetId", widgetId)
            bundle.putSerializable("data", data)
            fragment.arguments = bundle
            return fragment
        }
    }


}

