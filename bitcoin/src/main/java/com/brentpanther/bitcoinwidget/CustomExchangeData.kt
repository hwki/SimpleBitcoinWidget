package com.brentpanther.bitcoinwidget

import com.brentpanther.bitcoinwidget.ui.selection.CoinEntry
import java.io.InputStream

class CustomExchangeData(coin: CoinEntry, json: InputStream) : ExchangeData(coin, json) {

    init {
        val currencies = obj?.exchanges?.first { it.name == Exchange.COINGECKO.name }?.coins?.first()?.currencies ?: listOf()
        currencyExchange = currencies
                .associateWith { mutableListOf(Exchange.COINGECKO.name) }
                .toMutableMap()
    }

    override fun getExchangeCoinName(exchange: String) = coin.id

    override fun getExchangeCurrencyName(exchange: String, currency: String): String? = null
}