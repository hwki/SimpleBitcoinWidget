package com.brentpanther.bitcoinwidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;

public class CoinSelectionActivity extends Activity implements CoinSelectionView.CoinSelectedListener {

    private int widgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin);
        Bundle extras = getIntent().getExtras();
        widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        int[] coinIds = {R.id.coin1, R.id.coin2, R.id.coin3, R.id.coin4, R.id.coin5, R.id.coin6, R.id.coin7, R.id.coin8};
        Coin[] coins = Coin.values();
        for (int i = 0; i < coinIds.length; i++) {
            CoinSelectionView view = findViewById(coinIds[i]);
            view.setCoin(coins[i], this);
        }
        startService(new Intent(CoinSelectionActivity.this, DownloadJSONService.class));
    }

    @Override
    public void selected(Coin coin) {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(SettingsActivity.EXTRA_COIN, coin.name());
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        Intent broadcast = new Intent(this, WidgetProvider.class);
        broadcast.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        broadcast.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{widgetId});
        sendBroadcast(broadcast);
        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_OK, intent);
        finish();
    }
}
