package com.brentpanther.bitcoinwidget;

import android.content.Context;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

import java.text.DecimalFormat;
import java.text.NumberFormat;

class WidgetViews {

    private static final double LABEL_HEIGHT = .19;

    static void setText(Context context, RemoteViews views, Currency currency, String amount, String label, int widgetId) {
        String text = buildText(currency, amount, Prefs.getShowDecimals(context, widgetId));
        Prefs.setLastValue(context, widgetId, text);
        setImageVisibility(context, views, widgetId);
        Pair<Integer, Integer> availableSize = getTextAvailableSize(context, widgetId);
        if (availableSize == null) return;
        float textSize = TextSizer.getTextSize(context, text, availableSize);
        views.setTextViewText(R.id.price, text);
        views.setTextViewTextSize(R.id.price, TypedValue.COMPLEX_UNIT_DIP, textSize);
        if (Prefs.getLabel(context, widgetId)) {
            availableSize = getLabelAvailableSize(context, widgetId);
            float labelSize = TextSizer.getLabelSize(context, label, availableSize);
            views.setTextViewText(R.id.provider, label);
            views.setTextViewTextSize(R.id.provider, TypedValue.COMPLEX_UNIT_DIP, labelSize);
            show(views, R.id.provider);
        } else {
            hide(views, R.id.provider);
        }
        show(views, R.id.price);
        hide(views, R.id.loading);
    }

    static void resize(Context context, RemoteViews views, int widgetId) {
        Pair<Integer, Integer> availableSize = getTextAvailableSize(context, widgetId);
        String lastValue = Prefs.getLastValue(context, widgetId);
        if (availableSize == null || lastValue == null) return;
        float textSize = TextSizer.getTextSize(context, lastValue, availableSize);
        views.setTextViewTextSize(R.id.price, TypedValue.COMPLEX_UNIT_DIP, textSize);
        availableSize = getLabelAvailableSize(context, widgetId);

        int providerInt = Prefs.getProvider(context, widgetId);
        BTCProvider provider = BTCProvider.values()[providerInt];
        float labelSize = TextSizer.getLabelSize(context, provider.getLabel(), availableSize);
        views.setTextViewTextSize(R.id.provider, TypedValue.COMPLEX_UNIT_DIP, labelSize);
    }

    private static void setImageVisibility(Context context, RemoteViews views, int widgetId) {
        boolean hideIcon = Prefs.getIcon(context, widgetId);
        if (hideIcon) {
            hide(views, R.id.bitcoinImageBW);
            hide(views, R.id.bitcoinImage);
        } else {
            hide(views, R.id.bitcoinImageBW);
            show(views, R.id.bitcoinImage);
        }
    }

    private static Pair<Integer, Integer> getTextAvailableSize(Context context, int widgetId) {
        Pair<Integer, Integer> size = Prefs.getWidgetSize(context, widgetId);
        if (size == null) {
            return null;
        }
        int width = size.first;
        int height = size.second;

        if (Prefs.getThemeLayout(context, widgetId) != R.layout.widget_layout_transparent) {
            // light and dark themes have 5dp padding all around
            width -= 10;
            height -= 10;
        }

        if (!Prefs.getIcon(context, widgetId)) {
            // icon is 25% of width
            width *= .75;
        }
        if (Prefs.getLabel(context, widgetId)) {
            // provider will be 18% of height
            height *= (1 - LABEL_HEIGHT);
        }
        return Pair.create(width, height);
    }

    private static Pair<Integer, Integer> getLabelAvailableSize(Context context, int widgetId) {
        Pair<Integer, Integer> size = Prefs.getWidgetSize(context, widgetId);
        if (size == null) {
            return null;
        }
        int height = size.second;
        if (Prefs.getThemeLayout(context, widgetId) != R.layout.widget_layout_transparent) {
            // light and dark themes have 5dp padding all around
            height -= 10;
        }
        return Pair.create(size.first, (int)(height * LABEL_HEIGHT));
    }

    private static String buildText(Currency currency, String amount, boolean showDecimals) {
        String format = currency.getFormat();
        if (!showDecimals) {
            format = format.replaceAll("\\.00", "");
        }
        NumberFormat nf = new DecimalFormat(format);
        return nf.format(Double.valueOf(amount));
    }

    static void setLoading(RemoteViews views) {
        show(views, R.id.loading);
        hide(views, R.id.bitcoinImage, R.id.bitcoinImageBW, R.id.price);
    }

    static void show(RemoteViews views, int... ids) {
        for (int id : ids) views.setViewVisibility(id, View.VISIBLE);
    }

    static void hide(RemoteViews views, int... ids) {
        for (int id : ids) views.setViewVisibility(id, View.GONE);
    }

    static void setOld(RemoteViews views, boolean isOld, boolean hideIcon) {
        if (!hideIcon && isOld) {
            hide(views, R.id.bitcoinImage);
            show(views, R.id.bitcoinImageBW);
        } else if(!hideIcon) {
            show(views, R.id.bitcoinImage);
        }
        show(views, R.id.price);
        hide(views, R.id.loading);
    }

}
