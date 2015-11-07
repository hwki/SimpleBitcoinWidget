package com.brentpanther.bitcoinwidget;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONException;
import org.json.JSONObject;

public class Prefs {

    private static final String SEPARATOR = ":";
    public static final String LAST_UPDATE = "last_update";
    public static final String LAST_AMOUNT = "last_amount";
    public static final String CURRENCY = "currency";
    public static final String REFRESH = "refresh";
    public static final String PROVIDER = "provider";
    public static final String SHOW_LABEL = "show_label";
    public static final String WIDTH = "width";
    public static final String THEME = "theme";
    public static final String HIDE_ICON = "icon";

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

    static void setLastAmount(Context context, int widgetId, double amount) {
        setValue(context, widgetId, LAST_AMOUNT, "" + amount);
    }

    static void setWidth(Context context, int widgetId, int width) {
        setValue(context, widgetId, WIDTH, "" + width);
    }

    public static Double getLastAmount(Context context, int widgetId) {
        String last_amount = getValue(context, widgetId, LAST_AMOUNT);
        if(last_amount == null) return 0d;
        return Double.valueOf(last_amount);
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
        String value = getValue(context, widgetId, SHOW_LABEL);
        return Boolean.valueOf(value);
    }

    static boolean getIcon(Context context, int widgetId) {
        String value = getValue(context, widgetId, HIDE_ICON);
        return Boolean.valueOf(value);
    }

    static int getWidth(Context context, int widgetId) {
        String value = getValue(context, widgetId, WIDTH);
        if(value == null) return -1;
        return Integer.valueOf(value);
    }

    static int getThemeLayout(Context context, int widgetId) {
        String value = getValue(context, widgetId, THEME);
        if(value == null || "Light".equals(value)) return R.layout.widget_layout;
        if("Dark".equals(value)) return R.layout.widget_layout_dark;
        return R.layout.widget_layout_transparent;
    }

    static void setValue(Context context, int widgetId, String key, String value) {
        String string = getPrefs(context).getString("" + widgetId, null);
        JSONObject obj;
        try {
            if(string==null) {
                obj = new JSONObject();
            } else if(!string.startsWith("{")) {
                obj = convert(context, widgetId, string);
            } else {
                obj = new JSONObject(string);
            }
            obj.put(key, value);
            getPrefs(context).edit().putString("" + widgetId, obj.toString()).commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

	static void setValues(Context context, int widgetId, String currency, int refreshValue, int provider, boolean checked, String theme, boolean iconChecked) {
        JSONObject obj = new JSONObject();
        try {
            obj.put(CURRENCY, currency);
            obj.put(REFRESH, "" + refreshValue);
            obj.put(PROVIDER, "" + provider);
            obj.put(SHOW_LABEL, "" + checked);
            obj.put(THEME, theme);
            obj.put(HIDE_ICON, "" + !iconChecked);
            getPrefs(context).edit().putString("" + widgetId, obj.toString()).commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}

	static void delete(Context context, int widgetId) {
		getPrefs(context).edit().remove("" + widgetId).commit();
	}

    static String getValue(Context context, int widgetId, String key) {
        String string = getPrefs(context).getString("" + widgetId, null);
        if(string==null) return null;
        JSONObject obj;
        try {
            if(!string.startsWith("{")) {
                obj = convert(context, widgetId, string);
            } else {
                obj = new JSONObject(string);
            }
            return obj.getString(key);
        } catch (JSONException e) {
            return null;
        }
    }

    private static JSONObject convert(Context context, int widgetId, String string) {
        String[] strings = string.split(SEPARATOR);
        JSONObject obj = new JSONObject();
        try {
            if(strings.length > 0) obj.put(CURRENCY, strings[0]);
            if(strings.length > 1) obj.put(REFRESH, strings[1]);
            if(strings.length > 2) obj.put(PROVIDER, strings[2]);
            if(strings.length > 3) obj.put(SHOW_LABEL, strings[3]);
            if(strings.length > 4) obj.put(HIDE_ICON, strings[4]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getPrefs(context).edit().putString("" + widgetId, obj.toString()).commit();
        return obj;
    }
}
