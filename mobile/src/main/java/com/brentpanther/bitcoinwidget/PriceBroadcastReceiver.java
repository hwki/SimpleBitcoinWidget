package com.brentpanther.bitcoinwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class PriceBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
				int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
                int layout = Prefs.getThemeLayout(context, appWidgetId);
				RemoteViews views = new RemoteViews(context.getPackageName(), layout);
				WidgetViews.setLoading(views);
				appWidgetManager.updateAppWidget(appWidgetId, views);
				
				String currencyCode = Prefs.getCurrency(context, appWidgetId);
				Currency currency = Currency.valueOf(currencyCode);
                int providerInt = Prefs.getProvider(context, appWidgetId);
                BTCProvider provider = BTCProvider.values()[providerInt];
				try {
					String amount = provider.getValue(currencyCode);
					WidgetViews.setText(context, views, currency, amount, true, provider.getLabel(), appWidgetId);
					Prefs.setLastUpdate(context, appWidgetId);
				} catch (Exception e) {
					e.printStackTrace();
					long lastUpdate = Prefs.getLastUpdate(context, appWidgetId);
                    int interval = Prefs.getInterval(context, appWidgetId);
                    //if its been "a while" since the last successful update, gray out the icon.
					boolean isOld = ((System.currentTimeMillis() - lastUpdate) > 1000 * 90 * interval);
					WidgetViews.setText(context, views, currency, null, !isOld, provider.getLabel(), appWidgetId);
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
