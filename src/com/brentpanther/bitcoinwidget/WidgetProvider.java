package com.brentpanther.bitcoinwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		//used to upgrade old apps to new way of saving data...
		doWidgetUpdate(context, appWidgetIds);
		for (int widgetId : appWidgetIds) {
			setAlarm(context, widgetId);
			Intent i = new Intent(context, PriceBroadcastReceiver.class);
			i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
			PendingIntent pi = PendingIntent.getBroadcast(context, widgetId, i, 0);
			views.setOnClickPendingIntent(R.id.bitcoinParent, pi);
			appWidgetManager.updateAppWidget(widgetId, views);
		}
	}

	private void doWidgetUpdate(Context context, int[] appWidgetIds) {
		int oldInterval = Prefs.getOldInterval(context);
		if(oldInterval != -1) {
			for (int widgetId : appWidgetIds) {
				Prefs.setValues(context, widgetId, "USD", oldInterval);
			}
			Prefs.deleteOldInterval(context);
			onDeleted(context, new int[]{-1000});
		}
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
