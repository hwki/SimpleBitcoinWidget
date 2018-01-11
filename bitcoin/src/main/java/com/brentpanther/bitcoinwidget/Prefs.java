package com.brentpanther.bitcoinwidget;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

class Prefs {

    private static final String LAST_UPDATE = "last_update";
    private static final String CURRENCY = "currency";
    private static final String CURRENCY_CUSTOM = "currency_custom";
    private static final String REFRESH = "refresh";
    private static final String PROVIDER = "provider";
    private static final String EXCHANGE = "exchange";
    private static final String SHOW_LABEL = "show_label";
    private static final String THEME = "theme";
    private static final String HIDE_ICON = "icon";
    private static final String SHOW_DECIMALS = "show_decimals";
    private static final String LAST_VALUE = "last_value";
    private static final String UNITS = "units";
    private static final String COIN = "coin";
    private static final String COIN_CUSTOM = "coin_custom";
    private static final String PORTRAIT_TEXT_SIZE = "portrait_text_size";
    private static final String LANDSCAPE_TEXT_SIZE = "landscape_text_size";
    private final int widgetId;
    private final Context context;

    Prefs(int widgetId) {
        this.context = WidgetApplication.getInstance();
        this.widgetId = widgetId;
    }

    int getWidgetId() {
        return widgetId;
    }

    private SharedPreferences getPrefs() {
        return context.getSharedPreferences(context.getString(R.string.key_prefs), Context.MODE_PRIVATE);
    }

    Coin getCoin() {
        String coin = getValue(COIN);
        return coin != null ? Coin.valueOf(coin) : Coin.BTC;
    }

    String getExchangeCoinName() {
        String coin = getValue(COIN_CUSTOM);
        return coin != null ? coin : getValue(COIN);
    }

    Currency getCurrency() {
        return Currency.valueOf(getValue(CURRENCY));
    }

    String getExchangeCurrencyName() {
        String code = getValue(CURRENCY_CUSTOM);
        return code != null ? code : getValue(CURRENCY);
    }

    int getInterval() {
        String value = getValue(REFRESH);
        if (value == null) return 30;
        return Integer.valueOf(value);
    }

    Exchange getExchange() {
        String value = getValue(EXCHANGE);
        if (value == null) {
            return Exchange.values()[0];
        }
        return Exchange.valueOf(value);
    }

    int getThemeLayout() {
        String value = getValue(THEME);
        if(value == null || "Light".equals(value)) return R.layout.widget_layout;
        if("Dark".equals(value)) return R.layout.widget_layout_dark;
        if("Transparent Dark".equals(value)) return R.layout.widget_layout_transparent_dark;
        return R.layout.widget_layout_transparent;
    }

    boolean isLightTheme() {
        int themeLayout = getThemeLayout();
        return themeLayout == R.layout.widget_layout || themeLayout == R.layout.widget_layout_transparent;
    }

    String getUnit() {
        return getValue(UNITS);
    }

    long getLastUpdate() {
		String value = getValue(LAST_UPDATE);
        if(value==null) return 0;
        return Long.valueOf(value);
	}

	void setLastUpdate() {
        setValue(LAST_UPDATE, "" + System.currentTimeMillis());
	}

    void setLastValue(String value) {
        setValue(LAST_VALUE, value);
    }

    String getLastValue() {
        return getValue(LAST_VALUE);
    }

    boolean getLabel() {
        return Boolean.valueOf(getValue(SHOW_LABEL));
    }

    boolean showIcon() {
        return !Boolean.valueOf(getValue(HIDE_ICON));
    }

    boolean getShowDecimals() {
        String value = getValue(SHOW_DECIMALS);
        if (value == null) {
            return true;
        }
        return Boolean.valueOf(value);
    }

    void setValue(String key, String value) {
        String string = getPrefs().getString("" + widgetId, null);
        JSONObject obj;
        try {
            if (string == null) {
                obj = new JSONObject();
            }  else {
                obj = new JSONObject(string);
            }
            obj.put(key, value);
            getPrefs().edit().putString("" + widgetId, obj.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void setValues(String coin, String currency, int refreshValue, String exchange, boolean checked,
                   String theme, boolean iconChecked, boolean showDecimals, String unit) {
        JSONObject obj = new JSONObject();
        try {
            obj.put(COIN, coin);
            obj.put(CURRENCY, currency);
            obj.put(REFRESH, "" + refreshValue);
            obj.put(EXCHANGE, exchange);
            obj.put(SHOW_LABEL, "" + checked);
            obj.put(THEME, theme);
            obj.put(HIDE_ICON, "" + !iconChecked);
            obj.put(SHOW_DECIMALS, "" + showDecimals);
            obj.put(UNITS, unit);
            getPrefs().edit().putString("" + widgetId, obj.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}

    void delete() {
		getPrefs().edit().remove("" + widgetId).apply();
	}

    private String getValue(String key) {
        String string = getPrefs().getString("" + widgetId, null);
        if(string==null) return null;
        JSONObject obj;
        try {
            obj = new JSONObject(string);
            return obj.getString(key);
        } catch (JSONException e) {
            return null;
        }
    }

    void setTextSize(float size, boolean portrait) {
        setValue(portrait ? PORTRAIT_TEXT_SIZE : LANDSCAPE_TEXT_SIZE, Float.toString(size));
    }

    float getTextSize(boolean portrait) {
        String size = getValue(portrait ? PORTRAIT_TEXT_SIZE : LANDSCAPE_TEXT_SIZE);
        if (size == null) {
            return Float.MAX_VALUE;
        }
        float textSize = Float.valueOf(size);
        return textSize > 0 ? textSize : Float.MAX_VALUE;
    }

    void clearTextSize() {
        setTextSize(0, true);
        setTextSize(0, false);
    }

    void setExchangeValues(String exchangeCoinName, String exchangeCurrencyName) {
        if (exchangeCoinName != null) setValue(COIN_CUSTOM, exchangeCoinName);
        if (exchangeCurrencyName != null) setValue(CURRENCY_CUSTOM, exchangeCurrencyName);
    }
}
