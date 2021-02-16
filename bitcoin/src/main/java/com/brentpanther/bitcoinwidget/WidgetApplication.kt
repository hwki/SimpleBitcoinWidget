package com.brentpanther.bitcoinwidget

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.FileProvider
import androidx.preference.PreferenceManager
import java.io.File


class WidgetApplication : Application() {

    val widgetIds: IntArray
        get() {
            val manager = AppWidgetManager.getInstance(this)
            val cm = ComponentName(this, WidgetProvider::class.java)
            return manager.getAppWidgetIds(cm)
        }

    override fun onCreate() {
        super.onCreate()
        instance = this
        registerReceiver(MyBroadcastReceiver(), IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED))
        grantUriAccessToWidget()
    }

    private fun grantUriAccessToWidget() {
        val path = File(File(filesDir, "icons"), "1")
        val uri = FileProvider.getUriForFile(this, "com.brentpanther.bitcoinwidget.fileprovider", path)
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val launcher = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        grantUriPermission(launcher?.activityInfo?.packageName, uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
    }

    fun useAutoSizing(): Boolean {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val fixedSize = sharedPrefs.getBoolean(getString(R.string.key_fixed_size), false)
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !fixedSize
    }

    companion object {

        lateinit var instance: WidgetApplication
            private set
    }

}