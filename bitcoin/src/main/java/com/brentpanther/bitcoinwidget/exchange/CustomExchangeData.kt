package com.brentpanther.bitcoinwidget.exchange

import com.brentpanther.bitcoinwidget.CoinEntry
import java.io.InputStream

class CustomExchangeData(coinEntry: CoinEntry, json: InputStream) : ExchangeData(coinEntry, json) {

    init {
        val currencies = obj?.exchanges?.first { it.name == Exchange.COINGECKO.name }?.coins?.first()?.currencies ?: listOf()
        currencyExchange = currencies
                .associateWith { mutableListOf(Exchange.COINGECKO.name) }
                .toMutableMap()
    }

    override fun getExchangeCoinName(exchange: String) = coinEntry.name

    override fun getExchangeCurrencyName(exchange: String, currency: String): String? = null
}