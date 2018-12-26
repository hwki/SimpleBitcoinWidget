package com.brentpanther.bitcoinwidget

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.PowerManager

/**
 * Check if anything is restricted preventing the widget from downloading an update.
 */
object NetworkStatusHelper {

    fun getRestriction(context: Context): Int {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            if (powerManager.isPowerSaveMode && !powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
                return 0 //R.string.error_restricted_battery_saver;
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val restrictBackgroundStatus = connectivityManager.restrictBackgroundStatus
            if (restrictBackgroundStatus == android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED) {
                return R.string.error_restricted_data_saver
            }
        }
        return 0
    }
}
