package com.brentpanther.bitcoinwidget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

/**
 * Created by Panther on 2/8/2017.
 */

public class BackgroundService extends Service {

    private Context context;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.context = this;
    }

    private final class MyRunnable implements Runnable {

        private final int appWidgetId;

        MyRunnable(int appWidgetId) {
            this.appWidgetId = appWidgetId;
        }

        public void run() {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int layout = Prefs.getThemeLayout(context, appWidgetId);
            RemoteViews views = new RemoteViews(context.getPackageName(), layout);

            WidgetViews.setLoading(views, context, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, views);
            String currencyCode = Prefs.getCurrency(context, appWidgetId);
            Currency currency = Currency.valueOf(currencyCode);
            int providerInt = Prefs.getProvider(context, appWidgetId);
            BTCProvider provider = BTCProvider.values()[providerInt];

            try {
                String amount = provider.getValue(currencyCode);
                WidgetViews.setText(context, views, currency, amount, appWidgetId);
                Prefs.setLastUpdate(context, appWidgetId);
            } catch (Exception e) {
                long lastUpdate = Prefs.getLastUpdate(context, appWidgetId);
                int interval = Prefs.getInterval(context, appWidgetId);
                //if its been "a while" since the last successful update, gray out the icon.
                boolean isOld = ((System.currentTimeMillis() - lastUpdate) > 1000 * 90 * interval);
                boolean hideIcon = Prefs.getIcon(context, appWidgetId);
                WidgetViews.setOld(views, isOld, hideIcon);
                WidgetViews.setLastText(context, views, appWidgetId);
            }
            Intent priceUpdate = new Intent(context, PriceBroadcastReceiver.class);
            priceUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent pendingPriceUpdate = PendingIntent.getBroadcast(context, appWidgetId, priceUpdate, 0);
            views.setOnClickPendingIntent(R.id.bitcoinParent, pendingPriceUpdate);
            appWidgetManager.updateAppWidget(appWidgetId, views);
            stopSelf();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_STICKY;
        int appWidgetId = intent.getIntExtra("appWidgetId", 0);
        new Thread(new MyRunnable(appWidgetId)).start();
        return START_REDELIVER_INTENT;
    }
}
