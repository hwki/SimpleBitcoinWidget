package com.brentpanther.bitcoinwidget;

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
public class BTCExchangeTest {

    @Test
    public void testProviders() {
        Resources resources = InstrumentationRegistry.getTargetContext().getResources();
        List<String> list = new ArrayList<>();
        BTCExchange[] values = BTCExchange.values();
        for (BTCExchange btc : values) {
            String[] currencies = resources.getStringArray(btc.getCurrencies());
            for (String currency : currencies) {
                String tag = btc.name() + ": " + currency;
                String value = null;
                try {
                    value = btc.getValue(currency);
                    Double.valueOf(value);
                } catch (NumberFormatException e1) {
                    list.add(tag + " bad double: " + value);
                } catch (Exception e) {
                    list.add(tag + " failed with exception: " + e.getMessage());
                }
            }
        }
        if (!list.isEmpty()) {
            fail(TextUtils.join("\n", list));
        }
    }
}
