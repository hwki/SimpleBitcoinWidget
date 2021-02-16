package com.brentpanther.bitcoinwidget.ui

import android.app.Application
import androidx.lifecycle.*
import com.brentpanther.bitcoinwidget.Coin
import com.brentpanther.bitcoinwidget.R
import com.google.gson.Gson
import com.google.gson.JsonArray
import kotlinx.coroutines.Dispatchers
import java.io.InputStreamReader
import java.util.*

class CoinSelectionViewModel(application: Application) : AndroidViewModel(application) {

    private var allCoins = Coin.values().filterNot { it == Coin.CUSTOM }.associateBy { it.name }
    private var fullCoins: List<CoinEntry> = allCoins.map {
        CoinEntry(it.key, it.value.coinName, it.key, it.value)
    }.sortedWith { o1, o2 ->
        String.CASE_INSENSITIVE_ORDER.compare(o1.coin.coinName, o2.coin.coinName)
    }
    private var coinList: List<CoinEntry> = mutableListOf()
    private val resource = application.resources.openRawResource(R.raw.othercoins)

    val coins = liveData(Dispatchers.IO) {
        emit(fullCoins)
        loadOtherCoins()
        emit(coinList)
    }

    private fun loadOtherCoins() {
        val jsonArray = Gson().fromJson(InputStreamReader(resource), JsonArray::class.java)
        val otherCoins = jsonArray.map {
            val obj = it.asJsonObject
            CoinEntry(
                obj.get("id").asString,
                obj.get("name").asString,
                obj.get("symbol").asString.toUpperCase(Locale.ROOT),
                Coin.CUSTOM,
                obj.get("icon").asString
            )
        }.filterNot {
            allCoins.containsKey(it.symbol)
        }
        coinList = fullCoins.plus(otherCoins)
    }

}