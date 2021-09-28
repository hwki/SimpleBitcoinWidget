package com.brentpanther.bitcoinwidget.ui.manage

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.core.net.toUri
import androidx.preference.Preference

class ManageSettingsFragment : BaseManageSettingsFragment() {

    override fun loadAdditionalPreferences() {
        findPreference<Preference>("rate")?.setOnPreferenceClickListener {
            val appPackageName = requireActivity().packageName
            try {
                startActivity(Intent(Intent.ACTION_VIEW, "http://play.google.com/store/apps/details?id=$appPackageName".toUri()))
            } catch (e: ActivityNotFoundException) {
            }
            true
        }
    }
}