package com.brentpanther.bitcoinwidget

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.core.net.toUri
import com.brentpanther.bitcoinwidget.db.WidgetDatabase
import com.brentpanther.bitcoinwidget.receiver.WidgetBroadcastReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        registerReceiver(WidgetBroadcastReceiver(), IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED))
        grantUriAccessToWidget()
        // in case of stuck entries in the database
        if (widgetIds.isEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                WidgetDatabase.getInstance(this@WidgetApplication).widgetDao().clear()
            }
        }
    }

    private fun grantUriAccessToWidget() {
        val uri = "content://${packageName}.fileprovider/icons/".toUri()
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val launcher = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        grantUriPermission(launcher?.activityInfo?.packageName, uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
    }

    companion object {

        lateinit var instance: WidgetApplication
            private set
    }

}