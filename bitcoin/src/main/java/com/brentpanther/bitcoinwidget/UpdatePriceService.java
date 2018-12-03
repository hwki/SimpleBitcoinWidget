package com.brentpanther.bitcoinwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;


import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;


public class UpdatePriceService extends JobIntentService {

    private static final int JOB_ID = 3542;
    public static final String EXTRA_MANUAL_REFRESH = "manualRefresh";

    static void enqueueWork(Context context, Intent work) {
        DataMigration.migrate(context);
        enqueueWork(context, UpdatePriceService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
        boolean manualRefresh = intent.getBooleanExtra(EXTRA_MANUAL_REFRESH, false);
        Context context = getApplicationContext();
        Prefs prefs = new Prefs(appWidgetId);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int layout = prefs.getThemeLayout();
        RemoteViews views = new RemoteViews(context.getPackageName(), layout);
        String currencyCode = prefs.getExchangeCurrencyName();
        if (currencyCode == null) return;
        try {
            Exchange exchange = prefs.getExchange();
            String amount = exchange.getValue(prefs.getExchangeCoinName(), currencyCode);
            WidgetViews.setText(context, views, amount, prefs);
            prefs.setLastUpdate();
            setOldValue(prefs, views, context);
        } catch (IllegalArgumentException e) {
            float textSize = WidgetViews.putValue(context, views, context.getString(R.string.value_exchange_removed), prefs);
            appWidgetManager.updateAppWidget(appWidgetId, views);
            return;
        } catch (Exception e) {
            setOldValue(prefs, views, context);
        }
        Intent priceUpdate = new Intent(context, PriceBroadcastReceiver.class);
        priceUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        priceUpdate.putExtra(EXTRA_MANUAL_REFRESH, true);
        PendingIntent pendingPriceUpdate = PendingIntent.getBroadcast(context, appWidgetId, priceUpdate, FLAG_CANCEL_CURRENT);
        views.setOnClickPendingIntent(R.id.parent, pendingPriceUpdate);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void setOldValue(Prefs prefs, RemoteViews views, Context context) {
        long lastUpdate = prefs.getLastUpdate();
        int interval = prefs.getInterval();
        //if its been "a while" since the last successful update, gray out the icon.
        boolean isOld = ((System.currentTimeMillis() - lastUpdate) > 1000 * 90 * interval);
        WidgetViews.setLastText(context, views, prefs);
        WidgetViews.setOld(context, views, isOld, prefs);
    }

}
