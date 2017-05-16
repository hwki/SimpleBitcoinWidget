package com.brentpanther.cryptowidget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;

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
        private boolean manualRefresh;

        MyRunnable(int appWidgetId, boolean manualRefresh) {
            this.appWidgetId = appWidgetId;
            this.manualRefresh = manualRefresh;
        }

        public void run() {
            Ids ids = WidgetApplication.getInstance().getIds();
            Prefs prefs = WidgetApplication.getInstance().getPrefs(appWidgetId);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int layout = prefs.getThemeLayout();
            RemoteViews views = new RemoteViews(context.getPackageName(), layout);
            if (manualRefresh) WidgetViews.setLoading(views, ids, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, views);
            String currencyCode = prefs.getCurrency();
            Currency currency = Currency.valueOf(currencyCode);
            Exchange provider = prefs.getExchange();

            try {
                String amount = provider.getValue(currencyCode);
                WidgetViews.setText(context, views, currency, amount, appWidgetId);
                prefs.setLastUpdate();
            } catch (Exception e) {
                long lastUpdate = prefs.getLastUpdate();
                int interval = prefs.getInterval();
                //if its been "a while" since the last successful update, gray out the icon.
                boolean isOld = ((System.currentTimeMillis() - lastUpdate) > 1000 * 90 * interval);
                boolean hideIcon = prefs.getIcon();
                WidgetViews.setOld(views, isOld, ids, hideIcon);
                WidgetViews.setLastText(context, views, appWidgetId);
            }
            Intent priceUpdate = new Intent(context, PriceBroadcastReceiver.class);
            priceUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            priceUpdate.putExtra("manualRefresh", true);
            PendingIntent pendingPriceUpdate = PendingIntent.getBroadcast(context, appWidgetId, priceUpdate, FLAG_CANCEL_CURRENT);
            views.setOnClickPendingIntent(ids.parent(), pendingPriceUpdate);
            appWidgetManager.updateAppWidget(appWidgetId, views);
            stopSelf();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_STICKY;
        int appWidgetId = intent.getIntExtra("appWidgetId", 0);
        boolean manualRefresh = intent.getBooleanExtra("manualRefresh", false);
        new Thread(new MyRunnable(appWidgetId, manualRefresh)).start();
        return START_REDELIVER_INTENT;
    }
}
