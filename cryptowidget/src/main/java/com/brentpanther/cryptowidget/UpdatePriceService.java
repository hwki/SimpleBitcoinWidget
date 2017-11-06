package com.brentpanther.cryptowidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.widget.RemoteViews;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;

/**
 * Created by brentpanther on 11/1/17.
 */

public class UpdatePriceService extends JobIntentService {

    static final int JOB_ID = 3542;

    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, UpdatePriceService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        int appWidgetId = intent.getIntExtra("appWidgetId", 0);
        boolean manualRefresh = intent.getBooleanExtra("manualRefresh", false);
        Context context = getApplicationContext();
        Ids ids = WidgetApplication.getInstance().getIds();
        Prefs prefs = WidgetApplication.getInstance().getPrefs(appWidgetId);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int layout = prefs.getThemeLayout();
        RemoteViews views = new RemoteViews(context.getPackageName(), layout);
        String currencyCode = prefs.getCurrency();
        Currency currency = Currency.valueOf(currencyCode);
        try {
            Exchange provider = prefs.getExchange();
            String amount = provider.getValue(currencyCode);
            WidgetViews.setText(context, views, currency, amount, appWidgetId);
            prefs.setLastUpdate();
        } catch (IllegalArgumentException e) {
            WidgetViews.putValue(context, views, context.getString(R.string.value_exchange_removed), appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, views);
            return;
        } catch (Exception e) {
            setOldValue(prefs, views, ids, context, appWidgetId);
        }
        Intent priceUpdate = new Intent(context, PriceBroadcastReceiver.class);
        priceUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        priceUpdate.putExtra("manualRefresh", true);
        PendingIntent pendingPriceUpdate = PendingIntent.getBroadcast(context, appWidgetId, priceUpdate, FLAG_CANCEL_CURRENT);
        views.setOnClickPendingIntent(ids.parent(), pendingPriceUpdate);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void setOldValue(Prefs prefs, RemoteViews views, Ids ids, Context context, int appWidgetId) {
        long lastUpdate = prefs.getLastUpdate();
        int interval = prefs.getInterval();
        //if its been "a while" since the last successful update, gray out the icon.
        boolean isOld = ((System.currentTimeMillis() - lastUpdate) > 1000 * 90 * interval);
        boolean hideIcon = prefs.getIcon();
        WidgetViews.setOld(views, isOld, ids, hideIcon);
        WidgetViews.setLastText(context, views, appWidgetId);
    }

}
