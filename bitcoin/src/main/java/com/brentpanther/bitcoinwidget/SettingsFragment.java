package com.brentpanther.bitcoinwidget;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.TwoStatePreference;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment {

    private ExchangeData data;
    private int widgetId;
    private ListPreference refresh;
    private ListPreference currency;
    private ListPreference exchange;
    private TwoStatePreference icon;
    private ListPreference units;
    private TwoStatePreference decimals;
    private TwoStatePreference label;
    private ListPreference theme;
    private Integer refreshValue;
    private boolean fixedSize;

    public static Fragment newInstance(ExchangeData data, int widgetId) {
        SettingsFragment fragment = new SettingsFragment();
        fragment.data = data;
        fragment.widgetId = widgetId;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        loadPreferences();
        fixedSize = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(getString(R.string.key_fixed_size), false);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem menuItem = menu.add(0, 0, 0, "Save");
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            save();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadPreferences() {
        addPreferencesFromResource(R.xml.preferences);
        refresh = (ListPreference) findPreference(getString(R.string.key_refresh_interval));
        currency = (ListPreference) findPreference(getString(R.string.key_currency));
        exchange = (ListPreference) findPreference(getString(R.string.key_exchange));
        icon = (TwoStatePreference) findPreference(getString(R.string.key_icon));
        decimals = (TwoStatePreference) findPreference(getString(R.string.key_decimals));
        label = (TwoStatePreference) findPreference(getString(R.string.key_label));
        theme = (ListPreference) findPreference(getString(R.string.key_theme));
        TwoStatePreference fixedSize = (TwoStatePreference) findPreference(getString(R.string.key_fixed_size));
        units = (ListPreference) findPreference(getString(R.string.key_units));
        Preference rate = findPreference(getString(R.string.key_rate));
        Preference donate = findPreference(getString(R.string.key_donate));

        // refresh option
        setRefresh(Integer.valueOf(refresh.getValue()));
        refresh.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                setRefresh(Integer.valueOf((String)newValue));
                return true;
            }
        });

        // currency option
        currency.setEntries(data.getCurrencies());
        currency.setEntryValues(data.getCurrencies());
        String defaultCurrency = data.getDefaultCurrency();
        if (defaultCurrency == null) {
            Toast.makeText(getActivity(), R.string.error_no_currencies, Toast.LENGTH_LONG).show();
            getActivity().finish();
            return;
        }
        currency.setValue(defaultCurrency);
        currency.setSummary(getString(R.string.summary_currency, defaultCurrency));

        // exchange option
        setExchange(defaultCurrency);
        currency.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                currency.setSummary(getString(R.string.summary_currency, (String)newValue));
                setExchange((String)newValue);
                return true;
            }
        });
        exchange.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String exchangeCode = (String) newValue;
                String exchangeName = Exchange.valueOf(exchangeCode).getName();
                exchange.setSummary(getString(R.string.summary_exchange, exchangeName));
                return true;
            }
        });

        // icon
        icon.setTitle(getString(R.string.title_icon, data.getCoin().getName()));

        // theme
        theme.setValueIndex(0);
        theme.setSummary(theme.getValue());
        theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                theme.setSummary((String)newValue);
                return true;
            }
        });

        fixedSize.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // if we are switching fixed text size on, clear out any pre-existing values
                if (Boolean.valueOf(newValue.toString())) {
                    int[] widgetIds = WidgetApplication.getInstance().getWidgetIds();
                    for (int widgetId : widgetIds) {
                        new Prefs(widgetId).clearTextSize();
                    }
                }
                return true;
            }
        });

        // units
        String[] unitNames = data.getCoin().getUnitNames();
        if (unitNames.length == 0) {
            ((PreferenceCategory) findPreference(getString(R.string.key_style))).removePreference(this.units);
        } else {
            units.setValue(unitNames[0]);
            units.setSummary(getString(R.string.summary_units, unitNames[0]));
            units.setEntries(unitNames);
            units.setEntryValues(unitNames);
        }
        units.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                units.setSummary(getString(R.string.summary_units, newValue.toString()));
                return true;
            }
        });

        // rate
        rate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final String appPackageName = getActivity().getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                return true;
            }
        });

        // donation
        donate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
             @Override
             public boolean onPreferenceClick(Preference preference) {
                 Intent btc = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.btc_address)));
                 try {
                     startActivity(btc);
                 } catch (ActivityNotFoundException e) {
                     Toast.makeText(getActivity(), getString(R.string.error_donate), Toast.LENGTH_SHORT).show();
                 }
                 return true;
             }
        });
    }

    private void setRefresh(Integer value) {
        refreshValue = value;
        if (value < 60) {
            refresh.setSummary(getResources().getQuantityString(R.plurals.summary_refresh_interval_minute, value, value));
        } else {
            refresh.setSummary(getResources().getQuantityString(R.plurals.summary_refresh_interval_hour, value / 60, value / 60));
        }
    }

    private void setExchange(String currency) {
        String[] exchangeCodes = data.getExchanges(currency);
        String[] exchangeNames = new String[exchangeCodes.length];

        for (int i = 0; i < exchangeCodes.length; i++) {
            exchangeNames[i] = Exchange.valueOf(exchangeCodes[i]).getName();
        }
        String defaultExchange = data.getDefaultExchange(currency);

        exchange.setEntries(exchangeNames);
        exchange.setEntryValues(exchangeCodes);
        exchange.setValue(defaultExchange);
        exchange.setSummary(getString(R.string.summary_exchange, Exchange.valueOf(defaultExchange).getName()));
    }

    private void save() {
        Prefs prefs = new Prefs(widgetId);
        prefs.setValues(data.getCoin().name(), currency.getValue(), refreshValue,
                        exchange.getValue(), label.isChecked(), theme.getValue(), icon.isChecked(),
                        decimals.isChecked(), units.getValue());
        getActivity().setResult(Activity.RESULT_OK);
        boolean newFixedSize = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(getString(R.string.key_fixed_size), false);
        if (fixedSize && !newFixedSize) {
            WidgetProvider.refreshWidgets(getActivity(), widgetId);
        }
        getActivity().finish();
    }


}

