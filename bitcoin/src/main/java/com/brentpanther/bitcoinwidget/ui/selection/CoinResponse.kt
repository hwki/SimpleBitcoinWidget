package com.brentpanther.bitcoinwidget.ui.selection

import com.brentpanther.bitcoinwidget.Coin

data class CoinResponse(
    val id: String,
    val name: String,
    val symbol: String,
    val thumb: String?,
    val large: String?,
    var coin: Coin = Coin.CUSTOM
)

data class SearchResponse(
    val coins: List<CoinResponse>,
    val loading: Boolean
)