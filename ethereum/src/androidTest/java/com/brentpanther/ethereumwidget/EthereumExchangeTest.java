package com.brentpanther.ethereumwidget;

import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;

import com.brentpanther.cryptowidget.Exchange;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Brent
 * Date: 3/25/15
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class EthereumExchangeTest {

    @Test
    public void testProviders() {
        Resources resources = InstrumentationRegistry.getTargetContext().getResources();
        List<String> list = new ArrayList<>();
        Exchange[] values = EthereumExchange.values();
        for (Exchange exchange : values) {
            String[] currencies = resources.getStringArray(exchange.getCurrencies());
            for (String currency : currencies) {
                String tag = exchange.getLabel() + ": " + currency;
                String value = null;
                try {
                    value = exchange.getValue(currency);
                    Double.valueOf(value);
                } catch (NumberFormatException e1) {
                    list.add(tag + " bad double: " + value);
                } catch (Exception e) {
                    list.add(tag + " failed with exception: " + e.getMessage());
                }
            }
        }
        if (!list.isEmpty()) {
            Assert.fail(TextUtils.join("\n", list));
        }
    }
}
