package com.brentpanther.bitcoinwidget.trend

import java.time.Instant.now

class CoinHistoryWriter(val history: History) {
    fun addCoinValue(value: Double) {
        this.history.addCoinValue(CoinValue(value, now().toEpochMilli()))
    }
}