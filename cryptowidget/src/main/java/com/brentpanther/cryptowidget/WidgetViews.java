package com.brentpanther.cryptowidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

import java.text.DecimalFormat;
import java.text.NumberFormat;

class WidgetViews {

    private static final double TEXT_HEIGHT = .70;

    static void setText(Context context, RemoteViews views, Currency currency, String amount, int widgetId) {
        Prefs prefs = WidgetApplication.getInstance().getPrefs(widgetId);
        boolean showDecimals = prefs.getShowDecimals();
        String text = buildText(currency, amount, showDecimals, prefs.getUnit());
        prefs.setLastValue(text);
        putValue(context, views, text, widgetId);
    }

    static void setLastText(Context context, RemoteViews views, int widgetId) {
        Prefs prefs = WidgetApplication.getInstance().getPrefs(widgetId);
        String lastValue = prefs.getLastValue();
        if (!TextUtils.isEmpty(lastValue)) {
            putValue(context, views, lastValue, widgetId);
        } else {
            putValue(context, views, context.getString(R.string.value_unknown), widgetId);
        }
    }

    private static void putValue(Context context, RemoteViews views, String text, int widgetId) {
        Ids ids = WidgetApplication.getInstance().getIds();
        Prefs prefs = WidgetApplication.getInstance().getPrefs(widgetId);
        int price = ids.price();
        int provider = ids.provider();
        setImageVisibility(views, ids, widgetId);
        Pair<Integer, Integer> availableSize = getTextAvailableSize(context, ids, widgetId);
        if (availableSize == null) return;
        float textSize = TextSizer.getTextSize(context, text, availableSize);
        views.setTextViewText(price, text);
        views.setTextViewTextSize(price, TypedValue.COMPLEX_UNIT_DIP, textSize);
        if (prefs.getLabel()) {
            Exchange exchange = prefs.getExchange();
            availableSize = getLabelAvailableSize(context, ids, widgetId);
            float labelSize = TextSizer.getLabelSize(context, exchange.getLabel(), availableSize);
            views.setTextViewText(provider, exchange.getLabel());
            views.setTextViewTextSize(provider, TypedValue.COMPLEX_UNIT_DIP, labelSize);
            show(views, provider, ids.topSpace());
        } else {
            hide(views, provider, ids.topSpace());
        }
        show(views, price);
        hide(views, ids.loading());
    }

    private static void setImageVisibility(RemoteViews views, Ids ids, int widgetId) {
        Prefs prefs = WidgetApplication.getInstance().getPrefs(widgetId);
        boolean hideIcon = prefs.getIcon();
        if (hideIcon) {
            hide(views, ids.imageBW());
            hide(views, ids.image());
        } else {
            hide(views, ids.imageBW());
            show(views, ids.image());
        }
    }

    private static Pair<Integer, Integer> getTextAvailableSize(Context context, Ids ids, int widgetId) {
        Pair<Integer, Integer> size = getWidgetSize(context, widgetId);
        Prefs prefs = WidgetApplication.getInstance().getPrefs(widgetId);
        if (size == null) {
            return null;
        }
        int width = size.first;
        int height = size.second;

        if (prefs.getThemeLayout() != ids.widgetLayoutTransparent()) {
            // light and dark themes have 5dp padding all around
            width -= 10;
            height -= 10;
        }

        if (!prefs.getIcon()) {
            // icon is 25% of width
            width *= .75;
        }
        if (prefs.getLabel()) {
            height *= TEXT_HEIGHT;
        }
        return Pair.create((int)(width * .9), (int)(height * .85));
    }

    private static Pair<Integer, Integer> getLabelAvailableSize(Context context, Ids ids, int widgetId) {
        Prefs prefs = WidgetApplication.getInstance().getPrefs(widgetId);
        Pair<Integer, Integer> size = getWidgetSize(context, widgetId);
        if (size == null) {
            return null;
        }
        int height = size.second;
        int width = size.first;
        if (prefs.getThemeLayout() != ids.widgetLayoutTransparent()) {
            // light and dark themes have 5dp padding all around
            height -= 10;
        }
        if (!prefs.getIcon()) {
            // icon is 25% of width
            width *= .75;
        }
        height *= ((1 - TEXT_HEIGHT) / 2);
        return Pair.create((int)(width * .9), (int)(height * .75));
    }

    private static Pair<Integer, Integer> getWidgetSize(Context context, int widgetId) {
        boolean portrait = context.getResources().getConfiguration().orientation == 1;
        String w = portrait ? AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH : AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH;
        String h = portrait ? AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT : AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int width = appWidgetManager.getAppWidgetOptions(widgetId).getInt(w);
        int height = appWidgetManager.getAppWidgetOptions(widgetId).getInt(h);
        return Pair.create(width, height);
    }

    private static String buildText(Currency currency, String amount, boolean showDecimals, Unit unit) {
        String format = currency.getFormat();
        if (!showDecimals) {
            format = format.replaceAll("\\.00", "");
        }
        NumberFormat nf = new DecimalFormat(format);
        double adjustedAmount = unit.adjust(amount);
        return nf.format(adjustedAmount);
    }

     static void setLoading(RemoteViews views, Ids ids, int widgetId) {
         Prefs prefs = WidgetApplication.getInstance().getPrefs(widgetId);
         show(views, ids.loading());
         views.setViewVisibility(ids.price(), View.INVISIBLE);
         views.setViewVisibility(ids.imageBW(), View.GONE);
         if (!prefs.getIcon()) {
             views.setViewVisibility(ids.image(), View.INVISIBLE);
         }
    }

    private static void show(RemoteViews views, int... ids) {
        for (int id : ids) views.setViewVisibility(id, View.VISIBLE);
    }

    private static void hide(RemoteViews views, int... ids) {
        for (int id : ids) views.setViewVisibility(id, View.GONE);
    }

    static void setOld(RemoteViews views, boolean isOld, Ids ids, boolean hideIcon) {
        if (!hideIcon && isOld) {
            hide(views, ids.image());
            show(views, ids.imageBW());
        } else if(!hideIcon) {
            show(views, ids.image());
        }
        show(views, ids.price());
        hide(views, ids.loading());
    }

}
