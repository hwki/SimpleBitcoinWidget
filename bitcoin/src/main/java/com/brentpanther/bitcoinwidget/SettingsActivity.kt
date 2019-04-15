@file:Suppress("DEPRECATION")

package com.brentpanther.bitcoinwidget

import android.app.Activity
import android.app.ProgressDialog
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.JsonSyntaxException
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.concurrent.atomic.AtomicReference

class SettingsActivity : AppCompatActivity() {

    private var dialog: ProgressDialog? = null
    private var widgetId: Int = 0
    private lateinit var coin: Coin
    private var receiver: BroadcastReceiver? = null
    private lateinit var currentValue: AtomicReference<String>

    private val coinJSON: InputStream
        get() {
            try {
                return if (File(filesDir, DownloadJSONService.CURRENCY_FILE_NAME).exists()) {
                    openFileInput(DownloadJSONService.CURRENCY_FILE_NAME)
                } else {
                    resources.openRawResource(R.raw.cryptowidgetcoins)
                }
            } catch (e: FileNotFoundException) {
                throw RuntimeException(e)
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_settings)
        setResult(Activity.RESULT_CANCELED)
        val extras = intent.extras!!
        widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        coin = Coin.valueOf(extras.getString(EXTRA_COIN, "BTC"))
        title = getString(R.string.new_widget, coin.name)
        loadData(savedInstanceState == null)
        currentValue = AtomicReference<String>(null)
    }

    private fun loadData(addFragment: Boolean) {
        if (DownloadJSONService.downloaded) {
            populateData(addFragment)
        } else {
            dialog = ProgressDialog.show(this, getString(R.string.dialog_update_title), getString(R.string.dialog_update_message), true)
            val intentFilter = IntentFilter(DownloadJSONService.JSON_DOWNLOADED_ACTION)
            receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    populateData(addFragment)
                    dialog?.dismiss()
                }
            }
            LocalBroadcastManager.getInstance(this).registerReceiver(receiver as BroadcastReceiver, intentFilter)
        }
    }

    override fun onStop() {
        super.onStop()
        Prefs(widgetId).deleteIfTemporary()
        receiver?.let { LocalBroadcastManager.getInstance(this).unregisterReceiver(it) }
    }

    private fun populateData(addFragment: Boolean) {
        val data = try {
            ExchangeData(coin, coinJSON)
        } catch (e: JsonSyntaxException) {
            // if any error parsing JSON, fall back to raw resource
            Log.e(TAG, "Error parsing JSON file, falling back to original.", e)
            deleteFile(DownloadJSONService.CURRENCY_FILE_NAME)
            ExchangeData(coin, coinJSON)
        }

        findViewById<View>(R.id.previewLabel).visibility = View.VISIBLE
        findViewById<View>(R.id.previewLayout).visibility = View.VISIBLE
        if (addFragment) {
            val settingsFragment = SettingsFragment.newInstance(data, widgetId)
            supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, settingsFragment, "settings").commit()
        }
    }

    fun updateWidget(refreshValue: Boolean) {
        val prefs = Prefs(widgetId)
        val views = LocalRemoteViews(this, prefs.themeLayout)
        if (refreshValue || currentValue.get() == null) {
            Thread { currentValue.set(UpdatePriceService.updateValue(this@SettingsActivity, views, prefs)) }.start()
        } else {
            WidgetViews.setText(this, views, currentValue.get(), prefs)
        }
    }

    companion object {

        private val TAG = SettingsActivity::class.java.name
        const val EXTRA_COIN = "coin"
    }
}
