package com.brentpanther.bitcoinwidget.trend

import junit.framework.Assert.assertEquals
import org.junit.Test
import java.lang.Thread.sleep
import java.time.Instant.now
import java.util.concurrent.TimeUnit

class HistoryTest {

    @Test fun testGetValueAtGivenTime() {
        val history = History();
        history.addCoinValue(CoinValue(1000.0, now().toEpochMilli()))
        history.addCoinValue(CoinValue(500.0, now().toEpochMilli() - TimeUnit.MINUTES.toMillis(1)))
        history.addCoinValue(CoinValue(100.0, now().toEpochMilli() - TimeUnit.MINUTES.toMillis(2)))

        assertEquals(500.0, history.getValueWhen(now().toEpochMilli() - TimeUnit.MINUTES.toMillis(1)))
    }

    @Test
    fun testPurgeOldEntries() {
        val history = History(TimeUnit.SECONDS.toMillis(1))

        var now = now().toEpochMilli()
        history.addCoinValue(CoinValue(1000.0, now))
        assertEquals(1000.0, history.getValueWhen(now))
        assertEquals(1, history.getHistoryEventCount())

        sleep(TimeUnit.MILLISECONDS.toMillis(1200))

        now = now().toEpochMilli()
        history.addCoinValue(CoinValue(2000.0, now))
        assertEquals(2000.0, history.getValueWhen(now))
        assertEquals(1, history.getHistoryEventCount())
    }
}