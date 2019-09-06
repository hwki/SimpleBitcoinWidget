package com.brentpanther.bitcoinwidget

import org.junit.Test
import java.io.InputStream
import java.util.*


class ExchangeTest {

    private fun loadJSON(): InputStream {
        return ClassLoader.getSystemResourceAsStream("raw/cryptowidgetcoins.json")
    }

    @Test
    @Throws(Exception::class)
    fun removedCoins() {
        val coins = EnumSet.allOf(Coin::class.java)
        for (coin in coins) {
            val data = ExchangeData(coin, loadJSON())
            for (currency in data.currencies) {
                for (exchange in data.getExchanges(currency)) {
                    if (exchange == "BITCOIN_AVERAGE") continue
                    if (exchange == "BITCOIN_AVERAGE_GLOBAL") continue
                    try {
                        var coinName = data.getExchangeCoinName(exchange, coin.name)
                        var currencyName = data.getExchangeCurrencyName(exchange, currency)
                        if (coinName == null) coinName = coin.name
                        if (currencyName == null) currencyName = currency
                        Exchange.valueOf(exchange).getValue(coinName, currencyName)!!.toDouble()
                    } catch (e: Exception) {
                        println("Failure: $coin $exchange $currency: ${e.message}")
                    }

                    Thread.sleep(250)
                }
            }
        }
    }

}