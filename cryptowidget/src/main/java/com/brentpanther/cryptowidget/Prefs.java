package com.brentpanther.cryptowidget;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Prefs {

    protected static final String LAST_UPDATE = "last_update";
    protected static final String CURRENCY = "currency";
    protected static final String REFRESH = "refresh";
    protected static final String PROVIDER = "provider";
    protected static final String EXCHANGE = "exchange";
    protected static final String SHOW_LABEL = "show_label";
    protected static final String THEME = "theme";
    protected static final String HIDE_ICON = "icon";
    protected static final String SHOW_DECIMALS = "show_decimals";
    protected static final String LAST_VALUE = "last_value";
    protected static final String UNITS = "units";
    protected int widgetId;
    protected Context context;

    protected abstract SharedPreferences getPrefs();

    protected Prefs(Context context, int widgetId) {
        this.widgetId = widgetId;
        this.context = context;
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

	public abstract String getCurrency();

	public abstract int getInterval();

    public abstract Exchange getExchange();

    boolean getLabel() {
        return Boolean.valueOf(getValue(SHOW_LABEL));
    }

    boolean getIcon() {
        return Boolean.valueOf(getValue(HIDE_ICON));
    }

    public abstract int getThemeLayout();

    boolean getShowDecimals() {
        String value = getValue(SHOW_DECIMALS);
        if (value == null) {
            return true;
        }
        return Boolean.valueOf(value);
    }

    public abstract Unit getUnit();

    protected void setValue(String key, String value) {
        String string = getPrefs().getString("" + widgetId, null);
        JSONObject obj;
        try {
            if(string==null) {
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

	public void setValues(String currency, int refreshValue,
                          String exchange, boolean checked, String theme, boolean iconChecked,
                          boolean showDecimals, String unit) {
        JSONObject obj = new JSONObject();
        try {
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

    protected String getValue(String key) {
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

}
