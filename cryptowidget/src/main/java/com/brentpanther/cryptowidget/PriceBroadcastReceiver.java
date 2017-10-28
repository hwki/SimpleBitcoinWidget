package com.brentpanther.cryptowidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.RemoteViews;

import java.lang.ref.WeakReference;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;

public class PriceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final int appWidgetId = intent.getIntExtra("appWidgetId", 0);
        final boolean manualRefresh = intent.getBooleanExtra("manualRefresh", false);
        final PendingResult pendingResult = goAsync();
        new MyAsyncTask(context, appWidgetId, manualRefresh, pendingResult).execute();
    }

    private static class MyAsyncTask extends AsyncTask<String, Integer, Void> {

        private final WeakReference<Context> context;
        private final int appWidgetId;
        private final boolean manualRefresh;
        private final PendingResult pendingResult;

        MyAsyncTask(final Context context, final int appWidgetId,
                           final boolean manualRefresh, final PendingResult pendingResult) {
            this.context = new WeakReference<>(context);
            this.appWidgetId = appWidgetId;
            this.manualRefresh = manualRefresh;
            this.pendingResult = pendingResult;
        }

        @Override
        protected Void doInBackground(String... params) {
            Context context = this.context.get();
            if (context == null) return null;
            run(context, appWidgetId, manualRefresh);
            pendingResult.finish();
            return null;
        }
    }

    static void run(Context context, int appWidgetId, boolean manualRefresh) {
        Ids ids = WidgetApplication.getInstance().getIds();
        Prefs prefs = WidgetApplication.getInstance().getPrefs(appWidgetId);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int layout = prefs.getThemeLayout();
        RemoteViews views = new RemoteViews(context.getPackageName(), layout);
        if (manualRefresh) WidgetViews.setLoading(views, ids, appWidgetId);
        appWidgetManager.updateAppWidget(appWidgetId, views);
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
    }


}
