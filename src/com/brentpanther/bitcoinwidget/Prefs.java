package com.brentpanther.bitcoinwidget;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
	
	private static final String SEPARATOR = ":";
	
	//used for old version apps...
	static int getOldInterval(Context context) {
		return getPrefs(context).getInt("update", -1);
	}
	//used for old versions..
	static void deleteOldInterval(Context context) {
		getPrefs(context).edit().remove("update").commit();
	}

	private static SharedPreferences getPrefs(Context context) {
		return context.getSharedPreferences(context.getString(R.string.key_prefs), Context.MODE_PRIVATE);
	}

	static long getLastUpdate(Context context) {
		return getPrefs(context).getLong(context.getString(R.string.key_last_update), 0);
	}
	
	static void setLastUpdate(Context context) {
		getPrefs(context).edit()
			.putLong(context.getString(R.string.key_last_update), System.currentTimeMillis())
			.commit();
	}

	static String getCurrency(Context context, int widgetId) {
		String string = getPrefs(context).getString("" + widgetId, context.getString(R.string.default_currency) + SEPARATOR);
		return string.split(SEPARATOR)[0];
	}
	
	static int getInterval(Context context, int widgetId) {
		String string = getPrefs(context).getString("" + widgetId, SEPARATOR + context.getString(R.string.default_refresh_interval));
		return Integer.valueOf(string.split(SEPARATOR)[1]);
	}
	
	static int getProvider(Context context, int widgetId) {
		String string = getPrefs(context).getString("" + widgetId, SEPARATOR + " " + SEPARATOR + context.getString(R.string.default_provider));
		//handle old versions
		String[] split = string.split(SEPARATOR);
		if(split.length == 2) return 0;
		return Integer.valueOf(split[2]);
	}
	
	static void setValues(Context context, int widgetId, String currency, int refreshValue, int provider) {
		getPrefs(context).edit()
			.putString("" + widgetId, currency + SEPARATOR + refreshValue + SEPARATOR + provider)
			.commit();
	}
	
	static void delete(Context context, int widgetId) {
		getPrefs(context).edit()
			.remove("" + widgetId)
			.commit();
	}


}
