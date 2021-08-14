package com.brentpanther.bitcoinwidget.ui.selection

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brentpanther.bitcoinwidget.CoinEntry
import com.brentpanther.bitcoinwidget.databinding.ActivityCoinBinding
import com.brentpanther.bitcoinwidget.ui.settings.SettingsActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CoinSelectionActivity : AppCompatActivity() {

    private var job: Job? = null
    private lateinit var adapter: CoinSelectionAdapter
    private val viewModel: CoinSelectionViewModel by viewModels()
    private lateinit var binding: ActivityCoinBinding

    private var widgetId: Int = 0

    private val activityLaunch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                setResult(Activity.RESULT_OK, this)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinBinding.inflate(layoutInflater)
        if (intent.extras == null) {
            finish()
        }
        widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        job = lifecycleScope.launch {
            viewModel.getWidget(widgetId).collect {
                if (it != null) {
                    finish()
                    coinSelected(it.toCoinEntry(), true)
                } else {
                    setContentView(binding.root)
                    showAdapter()
                }
                job?.cancel()
            }
            setResult(Activity.RESULT_CANCELED)
        }
    }

    private fun showAdapter() {
        binding.coinList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        adapter = CoinSelectionAdapter {
            coinSelected(it, false)
        }
        binding.coinList.adapter = adapter
        viewModel.coins.observe(this, {
            adapter.coins = it
            adapter.notifyItemRangeInserted(adapter.coins.count(), it.count())
        })
        binding.search.doAfterTextChanged(adapter.filter::filter)
    }

    private fun coinSelected(it: CoinEntry, edit: Boolean) {
        val intent = Intent(this, SettingsActivity::class.java)
        intent.putExtra(SettingsActivity.EXTRA_COIN, it)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        if (edit) {
            intent.putExtra(SettingsActivity.EXTRA_EDIT_WIDGET, true)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY
        }
        this.activityLaunch.launch(intent)
    }

}
