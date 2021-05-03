package com.brentpanther.bitcoinwidget.trend

import java.time.Instant.now

class History(var maximumAge: Long) {
    val coinValues: ArrayList<CoinValue> = ArrayList()

    fun addCoinValue(coinValue: CoinValue) {
        coinValues.add(coinValue)
    }

    fun purgeOutdatedValues() {
        val iterator = coinValues.iterator()
        while(iterator.hasNext()) {
            val coinValue = iterator.next()
            if (coinValue.timestamp < now().toEpochMilli() - maximumAge) {
                coinValues.remove(coinValue);
            }
        }
    }

    fun getValueWhen(timestampInMillis: Long): Double {
        val iterator = coinValues.iterator()
        while(iterator.hasNext()) {
            val coinValue = iterator.next()
            if (coinValue.timestamp == timestampInMillis) {
                return coinValue.value;
            }
        }

        return coinValues[0].value
    }

    fun getHistoryEventCount(): Int {
        return coinValues.size
    }



}