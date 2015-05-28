package com.brentpanther.brentpanther;

import android.test.InstrumentationTestCase;
import com.brentpanther.bitcoinwidget.BTCProvider;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends InstrumentationTestCase {


    public void testProviders() throws Exception {
        BTCProvider[] providers = BTCProvider.values();
        for (BTCProvider provider : providers) {
            int id = provider.getCurrencies();
            String[] strings = getInstrumentation().getContext().getResources().getStringArray(id);
            for (String string : strings) {
                String value = provider.getValue(string);
                if (value == null || value.isEmpty()) {
                    fail("Provider " + provider.name() + " failed with currency " + string);
                }
            }
            break;
        }
    }

}