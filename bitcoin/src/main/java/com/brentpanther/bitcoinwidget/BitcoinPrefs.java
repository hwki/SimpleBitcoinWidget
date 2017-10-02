package com.brentpanther.bitcoinwidget;

import android.content.Context;
import android.content.SharedPreferences;

import com.brentpanther.cryptowidget.Exchange;
import com.brentpanther.cryptowidget.Prefs;
import com.brentpanther.cryptowidget.Unit;

/**
 * Created by brentpanther on 5/7/17.
 */

class BitcoinPrefs extends Prefs {

    BitcoinPrefs(Context context, int widgetId) {
        super(context, widgetId);
    }

    protected SharedPreferences getPrefs() {
        return context.getSharedPreferences(context.getString(R.string.key_prefs), Context.MODE_PRIVATE);
    }

    @Override
    public String getCurrency() {
        String value = getValue(CURRENCY);
        if (value == null) return context.getString(R.string.default_currency);
        return value;
    }

    @Override
    public int getInterval() {
        String value = getValue(REFRESH);
        if (value == null) value = context.getString(R.string.default_refresh_interval);
        return Integer.valueOf(value);
    }

    @Override
    public Exchange getExchange() {
        String value = getValue(EXCHANGE);
        if (value == null) value = context.getString(R.string.default_provider);
        return BTCExchange.valueOf(value);
    }

    @Override
    public int getThemeLayout() {
        String value = getValue(THEME);
        if(value == null || "Light".equals(value)) return R.layout.widget_layout;
        if("Dark".equals(value)) return R.layout.widget_layout_dark;
        if("Transparent Dark".equals(value)) return R.layout.widget_layout_transparent_dark;
        return R.layout.widget_layout_transparent;
    }

    @Override
    public Unit getUnit() {
        String value = getValue(UNITS);
        if (value == null) return BTCUnit.BTC;
        return BTCUnit.valueOf(value);
    }
}
