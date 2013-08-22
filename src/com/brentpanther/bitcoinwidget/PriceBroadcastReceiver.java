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

	private static final String MTGOX = "https://data.mtgox.com/api/2/BTC%s/money/ticker_fast";
	private static final String COINBASE = "https://coinbase.com/api/v1/prices/spot_rate?currency=%s";

	@Override
	public void onReceive(final Context context, final Intent intent) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
				int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
				RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
				WidgetViews.setLoading(views);
				appWidgetManager.updateAppWidget(appWidgetId, views);
				
				String currencyCode = Prefs.getCurrency(context, appWidgetId);
				Currency currency = Currency.valueOf(currencyCode);
				
				try {
					String amount = getValue(context, appWidgetId, currencyCode);
					WidgetViews.setText(views, currency, amount, true);
					Prefs.setLastUpdate(context);
				} catch (Exception e) {
					e.printStackTrace();
					long lastUpdate = Prefs.getLastUpdate(context);
					//if its been "a while" since the last successful update, gray out the icon.
					boolean isOld = ((System.currentTimeMillis() - lastUpdate) > 1000 * 60 * 5);
					WidgetViews.setText(views, currency, null, !isOld);
				}
				Intent priceUpdate = new Intent(context, PriceBroadcastReceiver.class);
				priceUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
				PendingIntent pendingPriceUpdate = PendingIntent.getBroadcast(context, appWidgetId, priceUpdate, 0);
				views.setOnClickPendingIntent(R.id.bitcoinParent, pendingPriceUpdate);
				appWidgetManager.updateAppWidget(appWidgetId, views);
			}
		}).start();
	}
	
	private String getValue(Context context, int widgetId, String currencyCode) throws Exception {
		int provider = Prefs.getProvider(context, widgetId);
		if(provider == 0) { 
			JSONObject obj = getAmount(String.format(MTGOX, currencyCode));
			return obj.getJSONObject("data").getJSONObject("last").getString("value");
		} else if (provider == 1) {
			JSONObject obj = getAmount(String.format(COINBASE, currencyCode));
			return obj.getString("amount");
		}
		return null;
	}

	private JSONObject getAmount(String url) throws Exception {
		HttpGet get = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();
		String result = client.execute(get, new BasicResponseHandler());
		return new JSONObject(result);
	}

}
