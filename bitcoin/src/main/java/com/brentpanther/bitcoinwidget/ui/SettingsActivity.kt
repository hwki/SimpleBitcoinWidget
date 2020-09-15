@file:Suppress("DEPRECATION")

package com.brentpanther.bitcoinwidget.ui

import android.app.Activity
import android.app.ProgressDialog
import android.appwidget.AppWidgetManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.brentpanther.bitcoinwidget.*
import com.google.gson.JsonSyntaxException
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.concurrent.atomic.AtomicReference

class SettingsActivity : AppCompatActivity() {

    private var dialog: ProgressDialog? = null
    private var widgetId: Int = 0
    private lateinit var coin: Coin
    private lateinit var currentValue: AtomicReference<String?>
    private val viewModel by viewModels<SettingsViewModel>()

    private val coinJSON: InputStream
        get() {
            try {
                return if (File(filesDir, Repository.CURRENCY_FILE_NAME).exists()) {
                    openFileInput(Repository.CURRENCY_FILE_NAME)
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
        title = getString(R.string.new_widget, coin.coinName)
        currentValue = AtomicReference(null)
        viewModel.data.observe(this, {
            if (this.isDestroyed) return@observe
            when(it) {
                false -> {
                    dialog = ProgressDialog.show(this, getString(R.string.dialog_update_title), getString(R.string.dialog_update_message), true)
                }
                true -> {
                    dialog?.dismiss()
                    populateData()
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        dialog?.dismiss()
        Prefs(widgetId).deleteIfTemporary()
    }

    private fun populateData() {
        ExchangeDataHelper.data = try {
            ExchangeData(coin, coinJSON)
        } catch (e: JsonSyntaxException) {
            // if any error parsing JSON, fall back to raw resource
            Log.e(TAG, "Error parsing JSON file, falling back to original.", e)
            deleteFile(Repository.CURRENCY_FILE_NAME)
            ExchangeData(coin, coinJSON)
        }

        findViewById<View>(R.id.previewLabel).visibility = View.VISIBLE
        findViewById<View>(R.id.previewLayout).visibility = View.VISIBLE
        if (supportFragmentManager.findFragmentByTag("settings") == null) {
            val settingsFragment = SettingsFragment.newInstance(widgetId)
            supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, settingsFragment, "settings").commit()
        }
    }

    fun updateWidget(refreshValue: Boolean) {
        val prefs = Prefs(widgetId)
        val views = LocalRemoteViews(this, prefs.themeLayout)
        if (refreshValue || currentValue.get() == null) {
            Thread {
                val amount = UpdatePriceService.updateValue(prefs)
                currentValue.set(amount)
                WidgetViews.setText(applicationContext, views, prefs, amount, true)
            }.start()
        } else {
            WidgetViews.setText(this, views, prefs, currentValue.get() as String, true)
        }
    }

    companion object {

        private val TAG = SettingsActivity::class.java.name
        const val EXTRA_COIN = "coin"
    }
}
