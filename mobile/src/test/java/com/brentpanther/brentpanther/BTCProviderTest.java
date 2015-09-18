package com.brentpanther.brentpanther;

import android.content.res.Resources;
import android.text.TextUtils;
import com.brentpanther.bitcoinwidget.BTCProvider;
import com.brentpanther.bitcoinwidget.BuildConfig;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Brent
 * Date: 3/25/15
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class BTCProviderTest extends TestCase {

    @Test
    public void testProviders() {
        Resources resources = RuntimeEnvironment.application.getResources();
        List<String> list = new ArrayList<>();
        BTCProvider[] values = BTCProvider.values();
        for (BTCProvider btc : values) {
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
