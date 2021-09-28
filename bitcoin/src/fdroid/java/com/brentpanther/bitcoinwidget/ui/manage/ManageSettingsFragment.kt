package com.brentpanther.bitcoinwidget.ui.manage

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.preference.Preference
import android.widget.Toast
import androidx.core.net.toUri
import com.brentpanther.bitcoinwidget.R


class ManageSettingsFragment : BaseManageSettingsFragment() {

    override fun loadAdditionalPreferences() {
        findPreference<Preference>("donate")?.setOnPreferenceClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, getString(R.string.btc_address).toUri()))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(activity, getString(R.string.error_donate), Toast.LENGTH_SHORT).show()
            }
            true
        }
    }
}