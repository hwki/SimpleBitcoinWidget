package com.brentpanther.ethereumwidget;

import com.brentpanther.cryptowidget.Ids;
import com.brentpanther.cryptowidget.Prefs;
import com.brentpanther.cryptowidget.WidgetApplication;

/**
 * Created by brentpanther on 5/10/17.
 */

public class EthereumApplication extends WidgetApplication {

    private EthereumIds ids;

    @Override
    public void onCreate() {
        super.onCreate();
        ids = new EthereumIds();
    }

    @Override
    public Ids getIds() {
        return ids;
    }

    @Override
    public Prefs getPrefs(int widgetId) {
        return new EthereumPrefs(this, widgetId);
    }
}
