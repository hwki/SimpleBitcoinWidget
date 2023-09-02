package com.brentpanther.bitcoinwidget.ui

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.PowerManager
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.edit
import androidx.core.net.ConnectivityManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brentpanther.bitcoinwidget.WidgetApplication
import com.brentpanther.bitcoinwidget.ui.home.ManageWidgetsViewModel
import kotlinx.coroutines.launch

class BannersViewModel : ViewModel() {

    val visibleBanners = mutableStateListOf<String>()

    init {
        loadBanners()
    }

    fun loadBanners() = viewModelScope.launch {
        val application = WidgetApplication.instance
        visibleBanners.clear()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!application.packageManager.isAutoRevokeWhitelisted && !isDismissed(application, "hibernate")) {
                visibleBanners.add("hibernate")
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val restrictBackgroundStatus = connectivityManager.restrictBackgroundStatus
            if (restrictBackgroundStatus == ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED &&
                ConnectivityManagerCompat.isActiveNetworkMetered(connectivityManager) &&
                !isDismissed(application, "data")) {
                visibleBanners.add("data")
            }
        }
        val powerManager = application.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (powerManager.isPowerSaveMode && !powerManager.isIgnoringBatteryOptimizations(application.packageName) &&
            !isDismissed(application, "battery")) {
            visibleBanners.add("battery")
        }
    }

    fun setDismissed(key: String) {
        WidgetApplication.instance.getSharedPreferences("widget", Context.MODE_PRIVATE).edit {
            putLong(key, System.currentTimeMillis() + ManageWidgetsViewModel.dismissTime)
        }
        visibleBanners.remove(key)
    }

    private fun isDismissed(context: Context, key: String): Boolean {
        return context.getSharedPreferences("widget", Context.MODE_PRIVATE).getLong(key, 0) > System.currentTimeMillis()
    }

}