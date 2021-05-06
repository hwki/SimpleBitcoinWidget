package com.brentpanther.bitcoinwidget.trend

class Gain {

    fun calculatePercentageGain(price: Double, referencePrice: Double): Double {
        return ((price - referencePrice) / referencePrice) * 100.0;
    }

    fun calculateGain(holdings: Double, price: Double, referencePrice: Double): Double {
        return  (price - referencePrice) * holdings;
    }

}

