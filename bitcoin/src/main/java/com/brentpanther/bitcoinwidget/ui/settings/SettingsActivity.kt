@file:Suppress("DEPRECATION")

package com.brentpanther.bitcoinwidget.ui.settings

import android.app.Activity
import android.app.ProgressDialog
import android.appwidget.AppWidgetManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.brentpanther.bitcoinwidget.*
import com.brentpanther.bitcoinwidget.databinding.LayoutSettingsBinding
import com.brentpanther.bitcoinwidget.db.*
import com.brentpanther.bitcoinwidget.ui.selection.CoinEntry
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

class SettingsActivity : AppCompatActivity() {

    private var dialog: ProgressDialog? = null
    private var widgetId: Int = 0
    private lateinit var coin: CoinEntry
    private val viewModel by viewModels<SettingsViewModel>()
    private lateinit var binding: LayoutSettingsBinding

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
        binding = LayoutSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setResult(Activity.RESULT_CANCELED)
        val extras = intent.extras!!
        widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        coin = extras.getParcelable(EXTRA_COIN) ?: throw IllegalArgumentException()
        binding.labelSave.text = getString(R.string.new_widget, coin.name)
        viewModel.data.observe(this, {
            if (this.isDestroyed) return@observe
            when(it) {
                false -> {
                    dialog = ProgressDialog.show(this, getString(R.string.dialog_update_title), getString(R.string.dialog_update_message), true)
                }
                true -> {
                    dialog?.dismiss()
                    populateData()
                    binding.save.setOnClickListener {
                        // TODO: move fragment logic here
                        (supportFragmentManager.findFragmentByTag("settings") as SettingsFragment).save()
                    }
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        dialog?.dismiss()
    }

    private fun populateData() {
        ExchangeDataHelper.data = try {
            // if custom coin, make custom data
            if (coin.coin == Coin.CUSTOM) {
                CustomExchangeData(coin, coinJSON)
            } else {
                ExchangeData(coin, coinJSON)
            }
        } catch (e: JsonSyntaxException) {
            // if any error parsing JSON, fall back to raw resource
            Log.e(TAG, "Error parsing JSON file, falling back to original.", e)
            deleteFile(Repository.CURRENCY_FILE_NAME)
            ExchangeData(coin, coinJSON)
        }

        binding.previewLabel.visibility = View.VISIBLE
        binding.previewLayout.visibility = View.VISIBLE
        if (supportFragmentManager.findFragmentByTag("settings") == null) {
            val settingsFragment = SettingsFragment.newInstance(widgetId)
            supportFragmentManager.commit {
                add(R.id.fragmentContainer, settingsFragment, "settings")
            }
        }
        viewModel.widgetData.observe(this) {
            updateWidget(it)
        }
    }

    private fun updateWidget(widgetSettings: WidgetSettings) {
        val views = LocalRemoteViews(this@SettingsActivity, widgetSettings.widget.theme.layout)
        val widgetViews = WidgetViews(applicationContext, views, widgetSettings)
        lifecycleScope.launch(Dispatchers.IO) {
            if (widgetSettings.refresh) {
                val amount = UpdatePriceService.updateValue(widgetSettings.widget)
                widgetViews.setText(amount, true)
            } else {
                widgetViews.setText(widgetSettings.widget.lastValue, true)
            }
        }
    }

    companion object {

        private val TAG = SettingsActivity::class.java.name
        const val EXTRA_COIN = "coin"
    }
}
