package com.brentpanther.bitcoinwidget;

import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.util.Log;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.brentpanther.bitcoinwidget.BTCProvider.*;

@RunWith(AndroidJUnit4.class)
public class NewCurrenciesTest extends TestCase {

    @Test
    public void testProviders() {
        Resources resources = InstrumentationRegistry.getTargetContext().getResources();
        BTCProvider[] values = BTCProvider.values();
        List<String> currencies = new ArrayList<>();
        for (Currency c : Currency.values()) {
            currencies.add(c.name());
        }
        for (BTCProvider btc : values) {
            List<String> added = new ArrayList<>();
            List<String> removed = new ArrayList<>();
            Set<BTCProvider> skip = new HashSet<>(Arrays.asList(MTGOX, BTCXCHANGE, BUTTERCOIN, GATECOIN, CRYPTSY, VIRTEX));
            if (skip.contains(btc)) continue;
            Set<String> existingCurrencies = new HashSet<>(Arrays.asList(resources.getStringArray(btc.getCurrencies())));
            for (String currency : currencies) {
                try {
                    String value = btc.getValue(currency);
                    Double.valueOf(value);
                    if (!existingCurrencies.contains(currency)) {
                        added.add(currency);
                    }
                } catch (Exception e) {
                    if (existingCurrencies.contains(currency)) {
                        removed.add(currency);
                    }
                }
            }
            if (added.size() + existingCurrencies.size() == currencies.size()) continue;
            if (!added.isEmpty()) {
                Collections.sort(added);
                Log.e("TEST", btc.name() + " has new currencies: " + TextUtils.join(", ", added));
            }
            if (!removed.isEmpty()) {
                Collections.sort(removed);
                Log.e("TEST", btc.name() + " removed currencies: " + TextUtils.join(", ", removed));
            }
        }
    }
}
