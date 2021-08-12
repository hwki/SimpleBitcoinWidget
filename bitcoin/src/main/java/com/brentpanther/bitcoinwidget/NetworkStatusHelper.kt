package com.brentpanther.bitcoinwidget

import android.app.usage.UsageStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.PowerManager

/**
 * Check if anything is restricted preventing the widget from downloading an update.
 */
object NetworkStatusHelper {

    fun checkBattery(context: Context): Int {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (powerManager.isPowerSaveMode && !powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
            return R.string.error_restricted_battery_saver
        }
        return 0
    }

    fun checkBackgroundData(context: Context): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val restrictBackgroundStatus = connectivityManager.restrictBackgroundStatus
            if (restrictBackgroundStatus == ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED) {
                return R.string.error_restricted_data_saver
            }
        }
        return 0
    }

    fun getRestriction(context: Context): Int {
        val checkBattery = checkBattery(context)
        if (checkBattery > 0) return checkBattery
        return checkBackgroundData(context)
    }

    fun checkThrottled(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val usageManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            if (usageManager.appStandbyBucket > UsageStatsManager.STANDBY_BUCKET_ACTIVE) {
                return true
            }
        }
        return false
    }
}
