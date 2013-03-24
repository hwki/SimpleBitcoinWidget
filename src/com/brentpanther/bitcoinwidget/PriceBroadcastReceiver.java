package com.brentpanther.bitcoinwidget;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class PriceBroadcastReceiver extends BroadcastReceiver {

	private static final String MTGOX = "https://data.mtgox.com/api/2/BTC%s/money/ticker";

	@Override
	public void onReceive(final Context context, final Intent intent) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
				int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
				RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
				String currency = Prefs.getCurrency(context, appWidgetId);
				
				CurrencySizeMapper.setLoading(views);
				appWidgetManager.updateAppWidget(appWidgetId, views);
				
				String url = String.format(MTGOX, currency);
				HttpGet get = new HttpGet(url);
				HttpClient client = new DefaultHttpClient();
				try {
					String result = client.execute(get, new BasicResponseHandler());
					JSONObject obj = new JSONObject(result);
					String amount = obj.getJSONObject("data").getJSONObject("last").getString("display_short");
					CurrencySizeMapper.setText(views, currency, amount);
					Prefs.setLastUpdate(context);
				} catch (Exception e) {
					long lastUpdate = Prefs.getLastUpdate(context);
					//if its been "a while" since the last successful update, show N/A instead of last value.
					boolean showNA = (System.currentTimeMillis() - lastUpdate > 1000 * 60 * 3);
					CurrencySizeMapper.setText(views, currency, showNA ? "N/A" : null);
				}
				Intent priceUpdate = new Intent(context, PriceBroadcastReceiver.class);
				priceUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
				PendingIntent pendingPriceUpdate = PendingIntent.getBroadcast(context, appWidgetId, priceUpdate, 0);
				views.setOnClickPendingIntent(R.id.bitcoinParent, pendingPriceUpdate);
				appWidgetManager.updateAppWidget(appWidgetId, views);
			}
		}).start();
	}

}
