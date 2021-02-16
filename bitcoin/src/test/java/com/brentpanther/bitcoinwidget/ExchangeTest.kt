package com.brentpanther.bitcoinwidget

import com.brentpanther.bitcoinwidget.Exchange.valueOf
import com.brentpanther.bitcoinwidget.ui.CoinEntry
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
        val coins = EnumSet.allOf(Coin::class.java).sorted()
        for (coin in coins) {
            val entry = CoinEntry(coin.name, coin.coinName, coin.name, coin)
            val data = ExchangeData(entry, loadJSON())
            for (currency in data.currencies.sorted()) {
                for (exchange in data.getExchanges(currency).toList().parallelStream()) {
                    // ignore exchanges that get grumpy for too many requests
                    if (exchange == "BITCOIN_AVERAGE") continue
                    if (exchange == "BITCOIN_AVERAGE_GLOBAL") continue
                    try {
                        var coinName = data.getExchangeCoinName(exchange)
                        var currencyName = data.getExchangeCurrencyName(exchange, currency)
                        if (coinName == null) coinName = coin.name
                        if (currencyName == null) currencyName = currency
                        valueOf(exchange).getValue(coinName, currencyName)!!.toDouble()
                    } catch (e: Exception) {
                        println("Failure: $coin $exchange $currency: ${e.message}")
                    }
                    Thread.sleep(1500)
                }
            }
        }
    }

}