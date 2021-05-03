package com.brentpanther.bitcoinwidget.trend

class History {
    val coinValues: ArrayList<CoinValue> = ArrayList()

    fun addCoinValue(coinValue: CoinValue) {
        coinValues.add(coinValue)
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

}