package com.brentpanther.bitcoinwidget

import com.brentpanther.bitcoinwidget.trend.Gain
import org.junit.Assert.assertEquals
import org.junit.Test


class HoldingsTest {

   @Test fun testPercentageGain() {
        assertEquals(
            46.07, Gain.calculatePercentageGain(81.80, 56.0),
            0.01
        )

       assertEquals(
           750.0, Gain.calculatePercentageGain(51000.0, 6000.0),
           0.01
       )

       assertEquals(
           186.38, Gain.calculatePercentageGain(51000.0, 17808.0),
           0.01
       )
    }


    @Test fun testGain() {
        assertEquals(
            24562.08,
            Gain().calculateGain(.74, 51000.0, 17808.0),
            0.01)

        assertEquals(
            516.00,
            Gain().calculateGain(20.0, 81.80, 56.0),
            0.01)
    }
}