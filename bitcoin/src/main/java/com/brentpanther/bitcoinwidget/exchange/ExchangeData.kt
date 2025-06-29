@file:OptIn(ExperimentalSerializationApi::class)

package com.brentpanther.bitcoinwidget.exchange

import com.brentpanther.bitcoinwidget.Coin
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream
import java.util.Currency


open class ExchangeData(val coin: Coin, json: InputStream) {

    protected var obj: JsonExchangeObject? = null
    protected var currencyExchange: MutableMap<String, MutableList<String>> = HashMap()

    init {
        this.obj = Json.decodeFromStream(json)
        loadCurrencies(coin.getSymbol())
    }

    // only return currencies that we know about
    open val currencies: Array<String>
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


    @kotlinx.serialization.Serializable
    class JsonExchangeObject {

        lateinit var exchanges: MutableList<JsonExchange>

        fun getExchangeCoinName(exchange: String, coin: String): String? {
            return exchanges.filter {it.name == exchange}.firstOrNull {it.coinOverrides != null}
                    ?.coinOverrides?.get(coin)
        }

        fun getExchangeCurrencyName(exchange: String, currency: String): String? {
            return exchanges.filter { it.name == exchange }.firstOrNull { it.currencyOverrides != null}
                    ?.currencyOverrides?.get(currency)
        }

    }

    @kotlinx.serialization.Serializable
    class JsonExchange {

        var name: String = ""
        var coins: List<JsonCoin> = listOf()
        @SerialName("ccy_ovr")
        var currencyOverrides: Map<String, String>? = null
        @SerialName("c_ovr")
        var coinOverrides: Map<String, String>? = null
        var all: List<String> = listOf()

        fun loadExchange(coin: String): List<String> {
            return coins.firstOrNull { it.name == coin }?.currencies?.plus(all) ?: listOf()
        }
    }

    @kotlinx.serialization.Serializable
    data class JsonCoin(
        var name: String,
        @SerialName("ccy")
        var currencies: List<String>
    )

    fun getExchanges(currency: String): Array<String> {
        // only return exchanges that we know about
        val exchangeNames = Exchange.getAllExchangeNames()
        val exchanges = currencyExchange[currency] ?: return arrayOf()
        exchangeNames.retainAll(exchanges.toSet())
        return exchangeNames.toTypedArray()
    }

    var numberExchanges = currencyExchange.count()

    //TODO: change to use exchange
    fun getDefaultExchange(currency: String): String {
        val exchanges = currencyExchange[currency]
        return exchanges?.get(0) ?: Exchange.COINGECKO.name
    }

    open fun getExchangeCoinName(exchange: String): String? {
        return obj?.getExchangeCoinName(exchange, coin.getSymbol())
    }

    open fun getExchangeCurrencyName(exchange: String, currency: String): String? {
        return obj?.getExchangeCurrencyName(exchange, currency)
    }

    private fun loadCurrencies(coin: String) {
        obj?.exchanges?.forEach { exchange ->
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