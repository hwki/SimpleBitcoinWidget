package com.brentpanther.bitcoinwidget.trend

import java.time.Instant.now

class History(var maximumAge: Long = 0L) {
    val coinValues: ArrayList<CoinValue> = ArrayList()

    fun addCoinValue(coinValue: CoinValue) {
        coinValues.add(coinValue)
        purgeOutdatedValues()
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

    private fun purgeOutdatedValues() {
        if (this.maximumAge == 0L) {
            return
        }

        val iterator = coinValues.iterator()
        while(iterator.hasNext()) {
            val coinValue = iterator.next()
            if (coinValue.timestamp < now().toEpochMilli() - maximumAge) {
                coinValues.remove(coinValue);
            }
        }
    }





}