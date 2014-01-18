package com.brentpanther.bitcoinwidget;

import android.content.Context;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

public class WidgetViews {

	public static void setText(Context context, RemoteViews views, Currency currency, String text, boolean color, String label, int widgetId) {
        TextSizer.Group group = null;
        int width = Prefs.getWidth(context, widgetId);
        if(width <= 0) width = 78;
		if(text!=null) {
            Double amount = Double.valueOf(text);
            Prefs.setLastAmount(context, widgetId, amount);
            group = TextSizer.getPriceID(context, currency, amount, width);
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN || group.split) {
                views.setTextViewText(group.id, group.text);
            } else {
                views.setTextViewText(R.id.priceJB, group.text);
                views.setTextViewTextSize(R.id.priceJB, TypedValue.COMPLEX_UNIT_DIP, group.size);
            }
		}
        int providerID = TextSizer.getProviderID(context, label);
        for(int i=0; i<TextSizer.providerMap.size(); i++) {
            hide(views, TextSizer.providerMap.valueAt(i));
        }
        if(color) {
            hide(views, R.id.bitcoinImageBW);
            show(views, R.id.bitcoinImage);
		} else {
            hide(views, R.id.bitcoinImage);
            show(views, R.id.bitcoinImageBW);
		}
        boolean showLabel = Prefs.getLabel(context, widgetId);
        if(showLabel) {
            show(views, providerID);
            hide(views, R.id.space);
            views.setTextViewText(providerID, label);
        } else {
            show(views, R.id.space);
            hide(views, providerID);
        }
        if(group == null) {
            Double amount = Prefs.getLastAmount(context, widgetId);
            group = TextSizer.getPriceID(context, currency, amount, width);
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN || group.split) {
            show(views, group.id, R.id.imageLayout);
        } else {
            show(views, R.id.priceJB, R.id.imageLayout);
        }
        hide(views, R.id.loading);
	}
	
	public static void setLoading(RemoteViews views) {
        show(views, R.id.loading);
        hide(views, R.id.imageLayout, R.id.priceJB);
        for(int i=0; i<TextSizer.priceMap.size(); i++) {
            hide(views, TextSizer.priceMap.valueAt(i));
        }
        for(int i=0; i<TextSizer.priceSplitMap.size(); i++) {
            hide(views, TextSizer.priceSplitMap.valueAt(i));
        }
	}

    static void show(RemoteViews views, int... ids) {
        for (int id : ids) {
            views.setViewVisibility(id, View.VISIBLE);
        }
    }

    static void hide(RemoteViews views, int... ids) {
        for (int id : ids) {
            views.setViewVisibility(id, View.GONE);
        }
    }

}
