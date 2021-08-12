package com.brentpanther.bitcoinwidget.ui.manage

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceDataStore
import androidx.preference.PreferenceFragmentCompat
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.db.Configuration
import com.brentpanther.bitcoinwidget.WidgetProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class ManageSettingsFragment : PreferenceFragmentCompat() {

    private val viewModel : ManageWidgetsViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.globalSettings.distinctUntilChanged().collect { config ->
                lifecycleScope.launch(Dispatchers.Main) {
                    loadPreferences(config, rootKey)
                }
            }
        }
    }

    private fun loadPreferences(config: Configuration, rootKey: String?) {
        preferenceManager.preferenceDataStore = ConfigDataStore(config)
        setPreferencesFromResource(R.xml.global_preferences, rootKey)
        findPreference<Preference>(getString(R.string.key_rate))?.setOnPreferenceClickListener {
            val appPackageName = requireActivity().packageName
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=$appPackageName")))
            }
            true
        }
        findPreference<ListPreference>("refresh_interval")?.setSummaryProvider {
            val pref = (it as ListPreference)
            getString(R.string.summary_refresh_interval, pref.entries[pref.findIndexOfValue(pref.value)])
        }
    }

    inner class ConfigDataStore(private val config: Configuration) : PreferenceDataStore() {
        override fun getBoolean(key: String?, defValue: Boolean): Boolean {
            return when(key) {
                "fixed_size" -> config.consistentSize
                else -> throw IllegalArgumentException()
            }
        }

        override fun putBoolean(key: String?, value: Boolean) {
            when(key) {
                "fixed_size" -> config.consistentSize = value
            }
            viewModel.updateGlobalSettings(config)
            WidgetProvider.refreshWidgets(requireContext())
        }

        override fun getString(key: String?, defValue: String?): String {
            return when(key) {
                "refresh_interval" -> config.refresh.toString()
                else -> throw IllegalArgumentException()
            }
        }

        override fun putString(key: String?, value: String?) {
            when(key) {
                "refresh_interval" -> config.refresh = value?.toInt() ?: 30
            }
            viewModel.updateGlobalSettings(config)
            WidgetProvider.refreshWidgets(requireContext())
        }
    }

}