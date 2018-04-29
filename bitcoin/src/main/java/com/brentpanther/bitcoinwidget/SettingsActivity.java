package com.brentpanther.bitcoinwidget;

import android.app.Activity;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class SettingsActivity extends Activity {

    private static final String TAG = SettingsActivity.class.getName();
    public static final String EXTRA_COIN = "coin";

    private ProgressDialog dialog;
    private int widgetId;
    private Coin coin;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        Bundle extras = getIntent().getExtras();
        widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        coin = Coin.valueOf(extras.getString(EXTRA_COIN));
        setTitle(getString(R.string.new_widget, coin.getName()));
        loadData();
    }

    private void loadData() {
        if (DownloadJSONService.downloaded) {
            populateData();
        } else {
            dialog = ProgressDialog.show(this, getString(R.string.dialog_update_title), getString(R.string.dialog_update_message), true);
            IntentFilter intentFilter = new IntentFilter(DownloadJSONService.JSON_DOWNLOADED_ACTION);
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    populateData();
                    dialog.dismiss();
                }
            };
            LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void populateData() {
        ExchangeData data;
        try {
            data = new ExchangeData(coin, getCoinJSON());
        } catch (JsonSyntaxException e) {
            // if any error parsing JSON, fall back to raw resource
            Log.e(TAG, "Error parsing JSON file, falling back to original.", e);
            deleteFile(DownloadJSONService.CURRENCY_FILE_NAME);
            data = new ExchangeData(coin, getCoinJSON());
        }
        if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
            getFragmentManager().beginTransaction()
                    .add(android.R.id.content, SettingsFragment.newInstance(data, widgetId)).commit();
        }
    }

    private InputStream getCoinJSON() {
        try {
            InputStream inputStream;
            if (new File(getFilesDir(), DownloadJSONService.CURRENCY_FILE_NAME).exists()) {
                return openFileInput(DownloadJSONService.CURRENCY_FILE_NAME);
            } else {
                return getResources().openRawResource(R.raw.cryptowidgetcoins);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


}
