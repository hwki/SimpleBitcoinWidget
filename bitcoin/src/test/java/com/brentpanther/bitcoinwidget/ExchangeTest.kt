package com.brentpanther.bitcoinwidget

import com.brentpanther.bitcoinwidget.db.PriceType
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

    private val nonUSExchanges = setOf(Exchange.BYBIT, Exchange.BINANCE, Exchange.BINGX, Exchange.KUCOIN)

    private fun exchangeCanLoadValues(excluded: Collection<Exchange>) {
        val coins = EnumSet.allOf(Coin::class.java).sorted()
        val triedExchanges = mutableSetOf(*excluded.map { it.name}.toTypedArray())
        triedExchanges.addAll((Exchange.entries - Exchange.BITHUMB).map { it.name })
        for (coin in coins) {
            println("Checking $coin")
            val data = ExchangeData(coin, loadJSON())
            for (currency in data.currencies.sorted()) {
                for (exchange in data.getExchanges(currency)) {
                    if (exchange in triedExchanges) continue
                    try {
                        val e = valueOf(exchange)
                        if (e.hasSpotPriceOnly) continue
                        val ask = tryGetValue(data, exchange, currency, coin, PriceType.ASK)
                        val bid = tryGetValue(data, exchange, currency, coin, PriceType.BID)
                        if (ask == null || bid == null || ask < bid) {
                            System.err.println("Failure: $coin $exchange $currency: ask: $ask bid: $bid")
                            continue
                        }
                        val spot = tryGetValue(data, exchange, currency, coin, PriceType.SPOT)
                        if (spot == null) {
                            System.err.println("Failure: $coin $exchange $currency: spot: $spot")
                        }
                        triedExchanges.add(exchange)
                    } catch (e: Exception) {
                        System.err.println("Failure: $coin $exchange $currency: ${e.message?.substringBefore("\n")}")
                    }
                    Thread.sleep(100)
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

    private fun tryGetValue(data: ExchangeData, exchange: String, currency: String, coin: Coin, priceType: PriceType): Double? {
        var coinName = data.getExchangeCoinName(exchange)
        var currencyName = data.getExchangeCurrencyName(exchange, currency)
        if (coinName == null) coinName = coin.getSymbol()
        if (currencyName == null) currencyName = currency
        if (coinName == currencyName) return null
        return valueOf(exchange).getValue(coinName, currencyName, priceType)?.toDouble()
    }

    @Test
    fun testPair() {
        val exchange = Exchange.BITHUMB
        val coin = Coin.ALGO
        val currency = "KRW"
        val data = ExchangeData(coin, loadJSON())
        val value = tryGetValue(data, exchange.name, currency, coin, PriceType.SPOT)
        println("Got value: $value")
    }

}