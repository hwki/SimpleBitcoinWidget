package com.brentpanther.bitcoinwidget.trend

import java.time.Instant.now
import kotlin.collections.ArrayList

class History(var maximumAge: Long = 0L, var coinValues: ArrayList<CoinValue>) {

    constructor(maximumAge: Long = 0L) : this(maximumAge, ArrayList<CoinValue>())

    fun addCoinValue(coinValue: CoinValue) {
        purgeOutdatedValues()
        coinValues.add(coinValue)
    }

    fun getValueWhen(timestampInMillis: Long): Double {
        val iterator = coinValues.iterator()
        while(iterator.hasNext()) {
            val coinValue = iterator.next()
            if (coinValue.timestamp >= timestampInMillis) {
                return coinValue.value;
            }
        }

        return coinValues[0].value
    }

    fun getHistoryEventCount(): Int {
        return coinValues.size
    }

    fun getMaximumHistoryAge(): Long {
        return maximumAge
    }

    fun getEvents(): ArrayList<CoinValue> {
        return coinValues
    }

    private fun purgeOutdatedValues() {
        if (this.maximumAge == 0L) {
            return
        }

        val iterator = coinValues.iterator()
        while(iterator.hasNext()) {
            val coinValue = iterator.next()
            if (coinValue.timestamp < now().toEpochMilli() - maximumAge) {
                iterator.remove();
            }
        }
    }

}