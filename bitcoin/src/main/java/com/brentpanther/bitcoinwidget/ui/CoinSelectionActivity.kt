package com.brentpanther.bitcoinwidget.ui

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.Repository
import com.brentpanther.bitcoinwidget.WidgetProvider

class CoinSelectionActivity : AppCompatActivity() {

    private lateinit var adapter: CoinSelectionAdapter
    private val viewModel: CoinSelectionViewModel by viewModels()

    private var widgetId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin)
        Repository.data(this)
        val extras = intent.extras
        if (extras == null) {
            finish()
            return
        }
        widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        val coinList = findViewById<RecyclerView>(R.id.coin_list)
        coinList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        adapter = CoinSelectionAdapter { this.selected(it) }
        coinList.adapter = adapter
        viewModel.coins.observe(this, {
            adapter.coins = it
            adapter.notifyDataSetChanged()
        })
        val search = findViewById<EditText>(R.id.search)
        search.doAfterTextChanged {
            adapter.filter.filter(it)
        }
    }

    private fun selected(coin: CoinEntry) {
        val intent = Intent(this, SettingsActivity::class.java)
        intent.putExtra(SettingsActivity.EXTRA_COIN, coin)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data)
        } else {
            val broadcast = Intent(this, WidgetProvider::class.java)
            broadcast.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            broadcast.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(widgetId))
            sendBroadcast(broadcast)
            val intent = Intent()
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}
