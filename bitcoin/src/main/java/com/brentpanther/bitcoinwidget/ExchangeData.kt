package com.brentpanther.bitcoinwidget

import com.google.gson.Gson
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Serializable
import java.util.ArrayList
import java.util.Arrays
import java.util.Currency
import java.util.HashMap
import kotlin.Comparator


internal class ExchangeData(val coin: Coin, json: InputStream) : Serializable {
    private val obj: JsonExchangeObject

    // only return currencies that we know about
    val currencies: Array<String>
        get() {
            val currencyNames = ArrayList<String>()
            for (currency in Currency.getAvailableCurrencies()) {
                currencyNames.add(currency.currencyCode)
            }
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

    internal inner class JsonExchangeObject {

        lateinit var exchanges: List<JsonExchange>

        fun loadCurrencies(coin: String) {
            exchanges.forEach { exchange ->
                exchange.loadExchange(coin).forEach { currency ->
                    currencyExchange.getOrPut(currency) { mutableListOf() }
                    currencyExchange[currency]?.add(exchange.name)
                }
            }
        }

        fun getExchangeCoinName(exchange: String, coin: String): String? {
            return exchanges.filter {it.name == exchange}.firstOrNull {it.coin_overrides != null}
                    ?.coin_overrides?.get(coin)
        }

        fun getExchangeCurrencyName(exchange: String, currency: String): String? {
            return exchanges.filter { it.name == exchange }.firstOrNull { it.currency_overrides != null}
                    ?.currency_overrides?.get(currency)
        }
    }

    internal inner class JsonExchange {

        lateinit var name: String
        lateinit var coins: List<JsonCoin>
        var currency_overrides: Map<String, String>? = null
        var coin_overrides: Map<String, String>? = null

        fun loadExchange(coin: String): List<String> {
           return coins.firstOrNull { it.name == coin}?.currencies ?: listOf()
        }
    }

    internal inner class JsonCoin {
        lateinit var name: String
        lateinit var currencies: List<String>
    }

    init {
        this.obj = Gson().fromJson(InputStreamReader(json), JsonExchangeObject::class.java)
        currencyExchange = HashMap()
        this.obj.loadCurrencies(coin.name)
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

    companion object {

        private lateinit var currencyExchange: MutableMap<String, MutableList<String>>
        private val CURRENCY_TOP_ORDER = Arrays.asList("USD", "EUR", "BTC")
    }

}
