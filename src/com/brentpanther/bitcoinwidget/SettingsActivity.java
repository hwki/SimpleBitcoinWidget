package com.brentpanther.bitcoinwidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
	
    private Preference refresh;
	private Preference currency;
	private int appWidgetId;
	private int refreshValue;
	private String currencyValue;

	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
		if(extras != null) appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        addPreferencesFromResource(R.xml.preferences);
        refresh = findPreference(getString(R.string.key_refresh_interval));
        currency = findPreference(getString(R.string.key_currency));

        setRefresh(Prefs.getInterval(this, appWidgetId));
        refresh.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference p, Object value) {
				setRefresh(Integer.valueOf(value.toString()));
				return true;
			}

			
		});
        currencyValue = Prefs.getCurrency(this, appWidgetId);
        currency.setSummary(getString(R.string.summary_currency, currencyValue));
        currency.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference p, Object value) {
				currencyValue = value.toString();
				p.setSummary(getString(R.string.summary_currency, currencyValue));
				return true;
			}
		});
    }
    
    private void setRefresh(int rate) {
    	refreshValue = rate;
    	
    	if (rate == 1){
    		refresh.setSummary(getString(R.string.summary_refresh_interval_minute));
    	}else if (rate < 61){
    		refresh.setSummary(getString(R.string.summary_refresh_interval_minutes, rate));
    	}else if (rate < 1440){
    		refresh.setSummary(getString(R.string.summary_refresh_interval_hours, rate  / 60));
    	}else{
    		refresh.setSummary(getString(R.string.summary_refresh_interval_day, rate / 1440));
    	}
	}
    
    @Override
    public void onBackPressed() {
    	Intent broadcast = new Intent(this, WidgetProvider.class);
    	broadcast.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
    	broadcast.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{appWidgetId});
    	sendBroadcast(broadcast);
    	Prefs.setValues(this, appWidgetId, currencyValue, refreshValue);
    	Intent result = new Intent();
		result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
    	setResult(RESULT_OK, result);
    	finish();
    }

}
