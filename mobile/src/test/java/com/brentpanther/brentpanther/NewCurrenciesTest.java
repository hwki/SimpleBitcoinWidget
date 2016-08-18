package com.brentpanther.brentpanther;

import android.content.res.Resources;
import android.text.TextUtils;

import com.brentpanther.bitcoinwidget.BTCProvider;
import com.brentpanther.bitcoinwidget.BuildConfig;
import com.brentpanther.bitcoinwidget.Currency;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.brentpanther.bitcoinwidget.BTCProvider.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class NewCurrenciesTest extends TestCase {

    @Test
    public void testProviders() {
        Resources resources = RuntimeEnvironment.application.getResources();
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
                System.out.println(btc.name() + " has new currencies: " + TextUtils.join(", ", added));
            }
            if (!removed.isEmpty()) {
                Collections.sort(removed);
                System.err.println(btc.name() + " removed currencies: " + TextUtils.join(", ", removed));
            }
        }
    }
}
