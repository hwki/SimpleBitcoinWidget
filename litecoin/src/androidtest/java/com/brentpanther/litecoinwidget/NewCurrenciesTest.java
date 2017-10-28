package com.brentpanther.litecoinwidget;

import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;

import com.brentpanther.cryptowidget.Currency;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@RunWith(AndroidJUnit4.class)
public class NewCurrenciesTest extends TestCase {

    @Test
    public void testProviders() throws InterruptedException {
        Resources resources = InstrumentationRegistry.getTargetContext().getResources();
        LitecoinExchange[] values = LitecoinExchange.values();
        List<String> currencies = new ArrayList<>();
        for (Currency c : Currency.values()) {
            currencies.add(c.name());
        }
        Map<String, String> addedMap = new HashMap<>();
        Map<String, String> removedMap = new HashMap<>();
        for (LitecoinExchange ltc : values) {
            List<String> added = new ArrayList<>();
            List<String> removed = new ArrayList<>();
            Set<String> existingCurrencies = new HashSet<>(Arrays.asList(resources.getStringArray(ltc.getCurrencies())));
            for (String currency : currencies) {
                try {
                    String value = ltc.getValue(currency);
                    Double.valueOf(value);
                    if (!existingCurrencies.contains(currency)) {
                        added.add(currency);
                    }
                } catch (Exception e) {
                    if (existingCurrencies.contains(currency)) {
                        removed.add(currency);
                    }
                }
                Thread.sleep(500);
            }
            if (added.size() + existingCurrencies.size() == currencies.size()) continue;
            if (!added.isEmpty()) {
                Collections.sort(added);
                addedMap.put(ltc.getLabel(), TextUtils.join(",", added));
            }
            if (!removed.isEmpty()) {
                Collections.sort(removed);
                removedMap.put(ltc.getLabel(), TextUtils.join(",", removed));
            }
        }
        StringBuilder ad = new StringBuilder("added:\n");
        for (String key : addedMap.keySet()) {
            ad.append(key).append(": ").append(addedMap.get(key)).append("\n");
        }
        ad.append("\nremoved:\n");
        for (String key : removedMap.keySet()) {
            ad.append(key).append(": ").append(removedMap.get(key)).append("\n");
        }
        fail(ad.toString());
    }
}
