package com.brentpanther.bitcoinwidget

import com.brentpanther.bitcoinwidget.exchange.Exchange
import com.brentpanther.bitcoinwidget.exchange.Exchange.valueOf
import com.brentpanther.bitcoinwidget.exchange.ExchangeData
import org.junit.Test
import java.io.InputStream
import java.util.EnumSet


class ExchangeTest {

    private fun loadJSON(): InputStream {
        return ClassLoader.getSystemResourceAsStream("raw/cryptowidgetcoins_v2.json")
    }

    private val nonUSExchanges = setOf(Exchange.BYBIT, Exchange.BITGLOBAL, Exchange.BINANCE)

    private fun exchangeCanLoadValues(excluded: Collection<Exchange>) {
        val coins = EnumSet.allOf(Coin::class.java).sorted()
        for (coin in coins) {
            println("Checking $coin")
            val data = ExchangeData(coin, loadJSON())
            val triedExchanges = mutableSetOf(*excluded.map { it.name}.toTypedArray())
            for (currency in data.currencies.sorted()) {
                for (exchange in data.getExchanges(currency)) {
                    if (exchange in triedExchanges) continue
                    try {
                        tryGetValue(data, exchange, currency, coin)
                    } catch (e: Exception) {
                        System.err.println("Failure: $coin $exchange $currency: ${e.message}")
                    }
                    triedExchanges.add(exchange)
                    Thread.sleep(50)
                }
            }
        }
    }

    @Test
    fun exchangeCanLoadValuesUS() {
        exchangeCanLoadValues(nonUSExchanges)
    }

    @Test
    fun exchangeCanLoadValuesNonUS() {
        exchangeCanLoadValues(Exchange.entries - nonUSExchanges)
    }

    private fun tryGetValue(data: ExchangeData, exchange: String, currency: String, coin: Coin): Double? {
        var coinName = data.getExchangeCoinName(exchange)
        var currencyName = data.getExchangeCurrencyName(exchange, currency)
        if (coinName == null) coinName = coin.getSymbol()
        if (currencyName == null) currencyName = currency
        if (coinName == currencyName) return null
        return valueOf(exchange).getValue(coinName, currencyName)?.toDouble()
    }

    @Test
    fun testPair() {
        val exchange = Exchange.COINSBIT
        val coin = Coin.WBTC
        val currency = "USD"
        val data = ExchangeData(coin, loadJSON())
        val value = tryGetValue(data, exchange.name, currency, coin)
        println("Got value: $value")
    }

}