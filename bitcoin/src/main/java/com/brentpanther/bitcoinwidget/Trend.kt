package com.brentpanther.bitcoinwidget

import java.time.Instant.now

class Trend {
    fun isNewTrendDue(trendReferenceSavedAtLast: Long, thresholdInMillis: Long): Boolean {
        if ( (trendReferenceSavedAtLast + thresholdInMillis).compareTo(now().toEpochMilli()) == -1) {
            return true
        }
        return false
    }
}