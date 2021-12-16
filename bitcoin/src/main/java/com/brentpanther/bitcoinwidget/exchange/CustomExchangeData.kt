package com.brentpanther.bitcoinwidget.exchange

import com.brentpanther.bitcoinwidget.CoinEntry
import java.io.InputStream

class CustomExchangeData(coinEntry: CoinEntry, json: InputStream) : ExchangeData(coinEntry, json) {

    init {
        val exchanges = obj?.exchanges?.first { it.name == Exchange.COINGECKO.name }
        val currencies = exchanges?.coins?.first()?.currencies?.plus(exchanges.all ?: listOf()) ?: listOf()
        currencyExchange = currencies
                .associateWith { mutableListOf(Exchange.COINGECKO.name) }
                .toMutableMap()
    }

    override fun getExchangeCoinName(exchange: String) = coinEntry.name

    override fun getExchangeCurrencyName(exchange: String, currency: String): String? = null
}