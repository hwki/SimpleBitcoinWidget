package com.brentpanther.bitcoincashwidget;

import com.brentpanther.cryptowidget.Ids;
import com.brentpanther.cryptowidget.Prefs;
import com.brentpanther.cryptowidget.WidgetApplication;
import com.brentpanther.cryptowidget.WidgetProvider;

/**
 * Created by brentpanther on 5/7/17.
 */

public class BitcoinWidgetApplication extends WidgetApplication {

    private BitcoinIds ids;

    @Override
    public void onCreate() {
        super.onCreate();
        ids = new BitcoinIds();
    }

    @Override
    public Ids getIds() {
        return ids;
    }

    @Override
    public Prefs getPrefs(int widgetId) {
        return new BitcoinPrefs(this, widgetId);
    }

    @Override
    public Class getWidgetProvider() {
        return WidgetProvider.class;
    }
}
