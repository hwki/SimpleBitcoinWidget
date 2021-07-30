package com.brentpanther.bitcoinwidget.ui.selection

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brentpanther.bitcoinwidget.Repository
import com.brentpanther.bitcoinwidget.WidgetProvider
import com.brentpanther.bitcoinwidget.databinding.ActivityCoinBinding
import com.brentpanther.bitcoinwidget.ui.settings.SettingsActivity

class CoinSelectionActivity : AppCompatActivity() {

    private lateinit var adapter: CoinSelectionAdapter
    private val viewModel: CoinSelectionViewModel by viewModels()
    private lateinit var binding: ActivityCoinBinding

    private var widgetId: Int = 0

    private val activityLaunch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            coinSelected()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Repository.data(this)
        val extras = intent.extras
        if (extras == null) {
            finish()
            return
        }
        widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        binding.coinList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        adapter = CoinSelectionAdapter {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra(SettingsActivity.EXTRA_COIN, it)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            this.activityLaunch.launch(intent)
        }
        binding.coinList.adapter = adapter
        viewModel.coins.observe(this, {
            val size = adapter.coins.count()
            adapter.coins = it
            adapter.notifyItemRangeInserted(size, it.count())
        })
        binding.search.doAfterTextChanged(adapter.filter::filter)
    }

    private fun coinSelected() {
        val resultIntent = Intent()
        resultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

}
