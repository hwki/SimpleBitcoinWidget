package com.brentpanther.bitcoinwidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
	
    private ListPreference refresh;
	private ListPreference currency;
	private ListPreference provider;
	private int appWidgetId;
	private int refreshValue;

	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
		if(extras != null) appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        addPreferencesFromResource(R.xml.preferences);
        refresh = (ListPreference) findPreference(getString(R.string.key_refresh_interval));
        currency = (ListPreference) findPreference(getString(R.string.key_currency));
        provider = (ListPreference) findPreference(getString(R.string.key_provider));
        
        setRefresh(Prefs.getInterval(this, appWidgetId));
        refresh.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference p, Object value) {
				setRefresh(Integer.valueOf(value.toString()));
				return true;
			}

			
		});
        currency.setSummary(getString(R.string.summary_currency, currency.getEntry()));
        currency.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference p, Object value) {
				p.setSummary(getString(R.string.summary_currency, value));
				return true;
			}
		});
        provider.setSummary(getString(R.string.summary_provider, provider.getEntry()));
        provider.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object value) {
				ListPreference p = (ListPreference)preference;
				CharSequence[] entryValues = p.getEntryValues();
				for (int i=0; i<entryValues.length; i++) {
					if(entryValues[i].equals(value)) {
						preference.setSummary(getString(R.string.summary_provider, p.getEntries()[i]));
					}
				}
				return true;
			}
		});
    }
    
    private void setRefresh(int rate) {
    	refreshValue = rate;
    	if(rate < 60) {
    		refresh.setSummary(getResources().getQuantityString(R.plurals.summary_refresh_interval_minute, rate, rate));
    	} else {
    		refresh.setSummary(getResources().getQuantityString(R.plurals.summary_refresh_interval_hour, rate/60, rate/60));
    	}
	}
    
    @Override
    public void onBackPressed() {
    	Intent broadcast = new Intent(this, WidgetProvider.class);
    	broadcast.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
    	broadcast.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{appWidgetId});
    	sendBroadcast(broadcast);
    	Prefs.setValues(this, appWidgetId, currency.getValue(), refreshValue, Integer.valueOf(provider.getValue()));
    	Intent result = new Intent();
		result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
    	setResult(RESULT_OK, result);
    	finish();
    }

}
