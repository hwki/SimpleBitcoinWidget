package com.brentpanther.bitcoinwidget;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.PowerManager;

/**
 * Check if anything is restricted preventing the widget from downloading an update.
 */
public class NetworkStatusHelper {

    public static int getRestriction(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (powerManager.isPowerSaveMode() && !powerManager.isIgnoringBatteryOptimizations(context.getPackageName())) {
                return 0; //R.string.error_restricted_battery_saver;
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            int restrictBackgroundStatus = connectivityManager.getRestrictBackgroundStatus();
            if (restrictBackgroundStatus == android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED) {
                return R.string.error_restricted_data_saver;
            }
        }
        return 0;
    }
}
