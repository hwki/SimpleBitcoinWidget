package com.brentpanther.bitcoinwidget;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

public class WidgetViews {

    public static void setText(Context context, RemoteViews views, Currency currency, String text, boolean color, String label, int widgetId) {
        TextSizer.Group group;
        int width = Prefs.getWidth(context, widgetId);
        if (width <= 0) width = 78;
        if (text != null) {
            Double amount = Double.valueOf(text);
            Prefs.setLastAmount(context, widgetId, amount);
            group = TextSizer.getPriceID(context, currency, amount, width);
            views.setTextViewText(R.id.price, group.text);
            views.setTextViewTextSize(R.id.price, TypedValue.COMPLEX_UNIT_DIP, group.size);
        }
        int providerTextSize = TextSizer.getProviderSize(context, label);
        hide(views, R.id.provider);

        boolean showLabel = Prefs.getLabel(context, widgetId);
        if (showLabel) {
            show(views, R.id.provider);
            hide(views, R.id.space);
            views.setTextViewText(R.id.provider, label);
            views.setTextViewTextSize(R.id.provider, TypedValue.COMPLEX_UNIT_DIP, providerTextSize);
        } else {
            show(views, R.id.space);
            hide(views, R.id.provider);
        }
        show(views, R.id.price, R.id.imageLayout);
        boolean hideIcon = Prefs.getIcon(context, widgetId);
        if (hideIcon) {
            hide(views, R.id.bitcoinImageBW);
            hide(views, R.id.bitcoinImage);
        } else if(color) {
            hide(views, R.id.bitcoinImageBW);
            show(views, R.id.bitcoinImage);
        } else {
            hide(views, R.id.bitcoinImage);
            show(views, R.id.bitcoinImageBW);
        }
        if (hideIcon && !showLabel) {
            hide(views, R.id.imageLayout);
        }
        hide(views, R.id.loading);
	}

	public static void setLoading(RemoteViews views) {
        show(views, R.id.loading);
        hide(views, R.id.imageLayout, R.id.price);
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
