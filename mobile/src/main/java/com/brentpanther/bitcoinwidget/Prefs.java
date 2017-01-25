package com.brentpanther.bitcoinwidget;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

class Prefs {

    private static final String LAST_UPDATE = "last_update";
    private static final String CURRENCY = "currency";
    private static final String REFRESH = "refresh";
    private static final String PROVIDER = "provider";
    private static final String SHOW_LABEL = "show_label";
    private static final String THEME = "theme";
    private static final String HIDE_ICON = "icon";
    private static final String SHOW_DECIMALS = "show_decimals";
    private static final String LAST_VALUE = "last_value";

    private static SharedPreferences getPrefs(Context context) {
		return context.getSharedPreferences(context.getString(R.string.key_prefs), Context.MODE_PRIVATE);
	}

	static long getLastUpdate(Context context, int widgetId) {
		String value = getValue(context, widgetId, LAST_UPDATE);
        if(value==null) return 0;
        return Long.valueOf(value);
	}

	static void setLastUpdate(Context context, int widgetId) {
        setValue(context, widgetId, LAST_UPDATE, "" + System.currentTimeMillis());
	}

    static void setLastValue(Context context, int widgetId, String value) {
        setValue(context, widgetId, LAST_VALUE, value);
    }

    static String getLastValue(Context context, int widgetId) {
        return getValue(context, widgetId, LAST_VALUE);
    }

	static String getCurrency(Context context, int widgetId) {
        String value = getValue(context, widgetId, CURRENCY);
        if(value == null) return context.getString(R.string.default_currency);
        return value;
	}

	static int getInterval(Context context, int widgetId) {
        String value = getValue(context, widgetId, REFRESH);
        if(value == null) value = context.getString(R.string.default_refresh_interval);
		return Integer.valueOf(value);
	}

	static int getProvider(Context context, int widgetId) {
        String value = getValue(context, widgetId, PROVIDER);
        if(value == null) value = context.getString(R.string.default_provider);
        return Integer.valueOf(value);
	}

    static boolean getLabel(Context context, int widgetId) {
        return Boolean.valueOf(getValue(context, widgetId, SHOW_LABEL));
    }

    static boolean getIcon(Context context, int widgetId) {
        return Boolean.valueOf(getValue(context, widgetId, HIDE_ICON));
    }

    static int getThemeLayout(Context context, int widgetId) {
        String value = getValue(context, widgetId, THEME);
        if(value == null || "Light".equals(value)) return R.layout.widget_layout;
        if("Dark".equals(value)) return R.layout.widget_layout_dark;
        return R.layout.widget_layout_transparent;
    }

    static boolean getShowDecimals(Context context, int widgetId) {
        String value = getValue(context, widgetId, SHOW_DECIMALS);
        if (value == null) {
            return true;
        }
        return Boolean.valueOf(value);
    }

    static void setValue(Context context, int widgetId, String key, String value) {
        String string = getPrefs(context).getString("" + widgetId, null);
        JSONObject obj;
        try {
            if(string==null) {
                obj = new JSONObject();
            }  else {
                obj = new JSONObject(string);
            }
            obj.put(key, value);
            getPrefs(context).edit().putString("" + widgetId, obj.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

	static void setValues(Context context, int widgetId, String currency, int refreshValue,
                          int provider, boolean checked, String theme, boolean iconChecked,
                          boolean showDecimals) {
        JSONObject obj = new JSONObject();
        try {
            obj.put(CURRENCY, currency);
            obj.put(REFRESH, "" + refreshValue);
            obj.put(PROVIDER, "" + provider);
            obj.put(SHOW_LABEL, "" + checked);
            obj.put(THEME, theme);
            obj.put(HIDE_ICON, "" + !iconChecked);
            obj.put(SHOW_DECIMALS, "" + showDecimals);
            getPrefs(context).edit().putString("" + widgetId, obj.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}

	static void delete(Context context, int widgetId) {
		getPrefs(context).edit().remove("" + widgetId).apply();
	}

    static String getValue(Context context, int widgetId, String key) {
        String string = getPrefs(context).getString("" + widgetId, null);
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
