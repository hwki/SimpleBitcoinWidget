package com.brentpanther.ethereumwidget;

import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

/**
 * User: Brent
 * Date: 3/25/15
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class ETHExchangeTest {

    @Test
    public void testProviders() throws InterruptedException {
        Resources resources = InstrumentationRegistry.getTargetContext().getResources();
        List<String> list = new ArrayList<>();
        EthereumExchange[] values = EthereumExchange.values();
        for (EthereumExchange eth : values) {
            String[] currencies = resources.getStringArray(eth.getCurrencies());
            for (String currency : currencies) {
                String tag = eth.name() + ": " + currency;
                String value = null;
                try {
                    value = eth.getValue(currency);
                    Double.valueOf(value);
                } catch (NumberFormatException e1) {
                    list.add(tag + " bad double: " + value);
                } catch (Exception e) {
                    list.add(tag + " failed with exception: " + e.getMessage());
                }
                Thread.sleep(500);
            }
        }
        if (!list.isEmpty()) {
            fail(TextUtils.join("\n", list));
        }
    }
}
