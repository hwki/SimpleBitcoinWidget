package com.brentpanther.bitcoinwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            saveSize(context, appWidgetManager, widgetId);
            int layout = Prefs.getThemeLayout(context, widgetId);
            RemoteViews views = new RemoteViews(context.getPackageName(), layout);
            appWidgetManager.updateAppWidget(widgetId, views);
			Intent i = new Intent(context, PriceBroadcastReceiver.class);
			i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
			PendingIntent pi = PendingIntent.getBroadcast(context, widgetId, i, 0);
			views.setOnClickPendingIntent(R.id.bitcoinParent, pi);
            setAlarm(context, widgetId);
		}
	}

	private void saveSize(Context context, AppWidgetManager appWidgetManager, int widgetId) {
        int width = appWidgetManager.getAppWidgetOptions(widgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int height = appWidgetManager.getAppWidgetOptions(widgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        Prefs.setWidgetSize(context, widgetId, width, height);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        saveSize(context, appWidgetManager, appWidgetId);
        int layout = Prefs.getThemeLayout(context, appWidgetId);
        RemoteViews views = new RemoteViews(context.getPackageName(), layout);
        WidgetViews.resize(context, views, appWidgetId);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		for (int widgetId : appWidgetIds) {
			Prefs.delete(context, widgetId);
			Intent i = new Intent(context, PriceBroadcastReceiver.class);
			PendingIntent pi = PendingIntent.getBroadcast(context, widgetId + 1000, i, 0);
			AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarm.cancel(pi);
		}
	}
	
	private void setAlarm(Context context, int widgetId) {
		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, PriceBroadcastReceiver.class);
		i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		PendingIntent pi = PendingIntent.getBroadcast(context, widgetId + 1000, i, 0);
		int update = Prefs.getInterval(context, widgetId);
		context.sendBroadcast(i);
		alarm.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis()+1000, update * 60000, pi);
	}

}
