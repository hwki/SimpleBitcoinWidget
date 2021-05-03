package com.brentpanther.bitcoinwidget.trend

import junit.framework.Assert.assertEquals
import org.junit.Test
import java.time.Instant.now
import java.util.concurrent.TimeUnit

class HistoryTest {

    @Test fun testGetValueAtGivenTime() {
        var history = History();
        history.addCoinValue(CoinValue(1000.0, now().toEpochMilli()))
        history.addCoinValue(CoinValue(500.0, now().toEpochMilli() - TimeUnit.MINUTES.toMillis(1)))
        history.addCoinValue(CoinValue(100.0, now().toEpochMilli() - TimeUnit.MINUTES.toMillis(2)))

        assertEquals(500.0, history.getValueWhen(now().toEpochMilli() - TimeUnit.MINUTES.toMillis(1)))
    }
}