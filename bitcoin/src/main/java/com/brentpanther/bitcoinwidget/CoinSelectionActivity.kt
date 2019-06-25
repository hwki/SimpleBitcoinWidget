package com.brentpanther.bitcoinwidget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CoinSelectionActivity : AppCompatActivity() {

    private var widgetId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin)
        title = getString(R.string.select_coin)
        val extras = intent.extras
        if (extras == null) {
            finish()
            return
        }
        widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        val coinList = findViewById<RecyclerView>(R.id.coin_list)
        coinList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        coinList.adapter = CoinSelectionAdapter{ coin: Coin -> this.selected(coin) }
        startService(Intent(this@CoinSelectionActivity, DownloadJSONService::class.java))
    }

    private fun selected(coin: Coin) {
        val intent = Intent(this, SettingsActivity::class.java)
        intent.putExtra(SettingsActivity.EXTRA_COIN, coin.name)
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
