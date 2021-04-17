package com.brentpanther.bitcoinwidget

class Gain {

    fun calculatePercentageGain(currentPrice: Double, buyingPrice: Double): Double {
        return ((currentPrice - buyingPrice) / buyingPrice) * 100.0;
    }

    fun calculateGain(holdings: Double, currentPrice: Double, buyingPrice: Double): Double {
        return  (currentPrice - buyingPrice) * holdings;
    }

}

