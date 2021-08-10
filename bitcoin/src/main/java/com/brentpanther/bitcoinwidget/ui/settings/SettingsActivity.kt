@file:Suppress("DEPRECATION")

package com.brentpanther.bitcoinwidget.ui.settings

import android.app.Activity
import android.app.ProgressDialog
import android.appwidget.AppWidgetManager
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.brentpanther.bitcoinwidget.*
import com.brentpanther.bitcoinwidget.databinding.LayoutSettingsBinding
import com.brentpanther.bitcoinwidget.db.*
import com.brentpanther.bitcoinwidget.ui.preview.LocalWidgetPreview
import com.brentpanther.bitcoinwidget.ui.selection.CoinEntry
import com.brentpanther.bitcoinwidget.ui.settings.SettingsViewModel.*
import com.brentpanther.bitcoinwidget.ui.settings.SettingsViewModel.DataState.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private var dialog: ProgressDialog? = null
    private lateinit var coin: CoinEntry
    private val viewModel by viewModels<SettingsViewModel>()
    private lateinit var binding: LayoutSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setResult(Activity.RESULT_CANCELED)
        val extras = intent.extras!!
        viewModel.widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        coin = extras.getParcelable(EXTRA_COIN) ?: throw IllegalArgumentException()
        if (extras.getBoolean(EXTRA_EDIT_WIDGET)) {
            title = getString(R.string.edit_widget, coin.name)
            binding.save.text = getString(R.string.settings_update)
        } else {
            title = getString(R.string.new_widget, coin.name)
            binding.save.text = getString(R.string.settings_create)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.settingsData(coin, applicationContext).collect {
                    when(it) {
                        is Downloading -> {
                            dialog = ProgressDialog.show(this@SettingsActivity, getString(R.string.dialog_update_title),
                                getString(R.string.dialog_update_message), true)
                        }
                        is Success -> {
                            binding.labelPreview.isVisible = true
                            binding.widgetPreview.previewLayout.isVisible = true
                            binding.save.setOnClickListener { viewModel.save() }
                            dialog?.dismiss()
                        }
                    }
                }
                viewModel.widgetPreviewFlow.collectLatest {
                    updateWidget(it)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        dialog?.dismiss()
    }

    private suspend fun updateWidget(widgetSettings: WidgetSettings) {
        binding.widgetPreview.widgetContainer.removeAllViews()
        View.inflate(this, widgetSettings.widget.theme.layout, binding.widgetPreview.widgetContainer)
        val views = LocalWidgetPreview(binding.widgetPreview)
        val widgetViews = WidgetViews(applicationContext, views, widgetSettings)
        if (widgetSettings.refreshPrice) {
            val amount = lifecycleScope.async(Dispatchers.IO) {
                UpdatePriceService.updateValue(widgetSettings.widget)
            }
            widgetViews.setText(amount.await(), true)
        } else {
            widgetViews.setText(widgetSettings.widget.lastValue, true)
        }
    }

    companion object {

        const val EXTRA_EDIT_WIDGET = "edit_widget"
        const val EXTRA_COIN = "coin"
    }
}
