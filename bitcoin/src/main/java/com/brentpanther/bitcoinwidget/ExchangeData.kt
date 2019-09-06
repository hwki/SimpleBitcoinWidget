package com.brentpanther.bitcoinwidget

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Serializable
import java.util.Currency
import java.util.HashMap
import kotlin.Comparator


class ExchangeData(val coin: Coin, json: InputStream) {

    private val obj: JsonExchangeObject
    private var currencyExchange: MutableMap<String, ArrayList<String>> = HashMap()

    // only return currencies that we know about
    val currencies: Array<String>
        get() {
            val currencyNames = Currency.getAvailableCurrencies().map { it.currencyCode}.toMutableList()
            currencyNames.addAll(Coin.COIN_NAMES)
            currencyNames.retainAll(currencyExchange.keys)
            currencyNames.sortWith(Comparator { o1, o2 ->
                val i1 = CURRENCY_TOP_ORDER.indexOf(o1)
                val i2 = CURRENCY_TOP_ORDER.indexOf(o2)
                if (i1 >= 0 && i2 >= 0) return@Comparator i1 -i2
                if (i1 >= 0) return@Comparator -1
                if (i2 >= 0) return@Comparator 1
                o1.compareTo(o2)
            })
            return currencyNames.toTypedArray()
        }

    val defaultCurrency: String?
        get() {
            if (currencyExchange.containsKey("USD")) return "USD"
            if (currencyExchange.containsKey("EUR")) return "EUR"
            return if (currencyExchange.isEmpty()) null else currencyExchange.keys.iterator().next()
        }

    inner class JsonExchangeObject {

        lateinit var exchanges: List<JsonExchange>

        fun getExchangeCoinName(exchange: String, coin: String): String? {
            return exchanges.filter {it.name == exchange}.firstOrNull {it.coinOverrides != null}
                    ?.coinOverrides?.get(coin)
        }

        fun getExchangeCurrencyName(exchange: String, currency: String): String? {
            return exchanges.filter { it.name == exchange }.firstOrNull { it.currencyOverrides != null}
                    ?.currencyOverrides?.get(currency)
        }

    }

    inner class JsonExchange {

        lateinit var name: String
        private lateinit var coins: List<JsonCoin>
        @SerializedName("currency_overrides")
        var currencyOverrides: Map<String, String>? = null
        @SerializedName("coin_overrides")
        var coinOverrides: Map<String, String>? = null

        fun loadExchange(coin: String): List<String> {
           return coins.firstOrNull { it.name == coin}?.currencies ?: listOf()
        }
    }

    internal inner class JsonCoin : Serializable {
        lateinit var name: String
        lateinit var currencies: List<String>
    }

    init {
        this.obj = Gson().fromJson(InputStreamReader(json), JsonExchangeObject::class.java)
        loadCurrencies(coin.name)
    }

    fun getExchanges(currency: String): Array<String> {
        // only return exchanges that we know about
        val exchangeNames = Exchange.getAllExchangeNames()
        val exchanges = currencyExchange[currency] ?: return arrayOf()
        exchangeNames.retainAll(exchanges)
        return exchangeNames.toTypedArray()
    }

    fun getDefaultExchange(currency: String): String {
        val exchanges = currencyExchange[currency]
        exchanges?.let {
            if (!exchanges.contains(Exchange.COINBASE.name)) return exchanges[0]
        }
        return Exchange.COINBASE.name
    }

    fun getExchangeCoinName(exchange: String, coin: String): String? {
        return obj.getExchangeCoinName(exchange, coin)
    }

    fun getExchangeCurrencyName(exchange: String, currency: String): String? {
        return obj.getExchangeCurrencyName(exchange, currency)
    }

    private fun loadCurrencies(coin: String) {
        for (exchange in obj.exchanges) {
            for (currency in exchange.loadExchange(coin)) {
                currencyExchange.getOrPut(currency) { arrayListOf() }
                currencyExchange[currency]?.add(exchange.name)
            }
        }
    }

    companion object {

        private val CURRENCY_TOP_ORDER = listOf("USD", "EUR", "BTC")
    }

}

object ExchangeDataHelper {

    var data: ExchangeData? = null

}
