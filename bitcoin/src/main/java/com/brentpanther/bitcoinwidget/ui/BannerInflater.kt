package com.brentpanther.bitcoinwidget.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.brentpanther.bitcoinwidget.BuildConfig
import com.brentpanther.bitcoinwidget.R

class BannerInflater {

    fun inflate(layoutInflater: LayoutInflater, viewGroup: ViewGroup) {
        val context = viewGroup.context
        viewGroup.removeAllViews()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!context.packageManager.isAutoRevokeWhitelisted) {
                addBanner(layoutInflater, viewGroup, R.string.warning_hibernation,
                    R.string.button_settings) {
                    context.startActivity(Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:${BuildConfig.APPLICATION_ID}"))
                    )
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val restrictBackgroundStatus = connectivityManager.restrictBackgroundStatus
            if (restrictBackgroundStatus == ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED) {
                addBanner(layoutInflater, viewGroup, R.string.warning_data_saver, R.string.button_settings) {
                    context.startActivity(Intent(
                        Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS,
                        Uri.parse("package:${BuildConfig.APPLICATION_ID}"))
                    )
                }
            }
        }
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (powerManager.isPowerSaveMode && !powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
            addBanner(layoutInflater, viewGroup, R.string.warning_battery_saver)
        }
    }

    private fun addBanner(layoutInflater: LayoutInflater, viewGroup: ViewGroup, @StringRes text: Int,
                          @StringRes buttonText: Int? = null, onClick: ((View) -> Unit)? = null) {
        layoutInflater.inflate(R.layout.view_banner, viewGroup, false).apply {
            findViewById<TextView>(R.id.text_warning).setText(text)
            val openButton = findViewById<Button>(R.id.button_banner_open)
            openButton.isVisible = buttonText != null
            buttonText?.let {
                openButton.setText(it)
                openButton.setOnClickListener(onClick)
            }
            viewGroup.addView(this)
        }
    }

}
