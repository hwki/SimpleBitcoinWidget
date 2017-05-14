package com.brentpanther.litecoinwidget;

import com.brentpanther.cryptowidget.Ids;
import com.brentpanther.cryptowidget.Prefs;
import com.brentpanther.cryptowidget.WidgetApplication;

/**
 * Created by brentpanther on 5/10/17.
 */

public class LitecoinApplication extends WidgetApplication {

    private LitecoinIds ids;

    @Override
    public void onCreate() {
        super.onCreate();
        ids = new LitecoinIds();
    }

    @Override
    public Ids getIds() {
        return ids;
    }

    @Override
    public Prefs getPrefs(int widgetId) {
        return new LitecoinPrefs(this, widgetId);
    }
}
