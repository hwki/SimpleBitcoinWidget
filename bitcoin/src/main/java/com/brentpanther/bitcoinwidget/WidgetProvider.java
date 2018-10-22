package com.brentpanther.bitcoinwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

public class WidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
        	Prefs prefs = new Prefs(widgetId);
            int layout = prefs.getThemeLayout();
            RemoteViews views = new RemoteViews(context.getPackageName(), layout);
            appWidgetManager.updateAppWidget(widgetId, views);
			Intent i = new Intent(context, PriceBroadcastReceiver.class);
			i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
			PendingIntent pi = PendingIntent.getBroadcast(context, widgetId, i, 0);
			views.setOnClickPendingIntent(R.id.parent, pi);
            setAlarm(context, widgetId);
		}
	}

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        Prefs prefs = new Prefs(appWidgetId);
        int layout = prefs.getThemeLayout();
        RemoteViews views = new RemoteViews(context.getPackageName(), layout);
        WidgetViews.setLastText(context, views, prefs);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		for (int widgetId : appWidgetIds) {
        	Prefs prefs = new Prefs(widgetId);
			prefs.delete();
			Intent i = new Intent(context, PriceBroadcastReceiver.class);
			PendingIntent pi = PendingIntent.getBroadcast(context, widgetId + 1000, i, 0);
			AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarm.cancel(pi);
		}
        refreshIfFixedSize(context);
	}

    private void refreshIfFixedSize(Context context) {
        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.key_fixed_size), false)) return;
        refreshWidgets(context, -1);
    }

    public static void refreshWidgets(Context context, int widgetId) {
        int[] widgetIds = WidgetApplication.getInstance().getWidgetIds();
        List<Integer> refreshWidgetIds = new ArrayList<>();
        for (int appWidgetId : widgetIds) {
            if (appWidgetId == widgetId) continue;
            refreshWidgetIds.add(appWidgetId);
        }
        refreshWidgets(context, refreshWidgetIds);
    }

    public static void refreshWidgets(Context context, List<Integer> widgetIds) {
        for (Integer widgetId : widgetIds) {
            Intent widgetUpdateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, context, PriceBroadcastReceiver.class);
            widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            context.sendBroadcast(widgetUpdateIntent);
        }
    }

    private void setAlarm(Context context, int widgetId) {
        Prefs prefs = new Prefs(widgetId);
		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, PriceBroadcastReceiver.class);
		i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		PendingIntent pi = PendingIntent.getBroadcast(context, widgetId + 1000, i, 0);
		int update = prefs.getInterval();
		context.sendBroadcast(i);
		alarm.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis()+1000, update * 60000, pi);
	}


}
