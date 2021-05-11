package com.brentpanther.bitcoinwidget

import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Test
import java.time.Instant.now

class TrendTest {

    @Test
    fun testIsNewTrendDue()
    {
        assertTrue(Trend().isNewTrendDue((now().toEpochMilli() - 1000L), 50))
        assertFalse(Trend().isNewTrendDue((now().toEpochMilli() - 1000L), 1500))
    }
}