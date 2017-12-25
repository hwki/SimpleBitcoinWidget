package com.brentpanther.bitcoinwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

import java.text.DecimalFormat;
import java.text.NumberFormat;

class WidgetViews {

    private static final double TEXT_HEIGHT = .70;

    static void setText(Context context, RemoteViews views, String amount, Prefs prefs) {
        String text = buildText(amount, prefs);
        prefs.setLastValue(text);
        putValue(context, views, text, prefs);
    }

    static void setLastText(Context context, RemoteViews views, Prefs prefs) {
        String lastValue = prefs.getLastValue();
        if (!TextUtils.isEmpty(lastValue)) {
            putValue(context, views, lastValue, prefs);
        } else {
            putValue(context, views, context.getString(R.string.value_unknown), prefs);
        }
    }

    static float putValue(Context context, RemoteViews views, String text, Prefs prefs) {
        boolean useAutoSizing = WidgetApplication.getInstance().useAutoSizing();
        int priceView = R.id.price;
        int priceAutoSizeView = R.id.priceAutoSize;
        int exchangeView = R.id.exchange;
        int exchangeAutoSizeView = R.id.exchangeAutoSize;
        float textSize = 0;

        show(views, useAutoSizing ? priceAutoSizeView : priceView);
        hide(views, useAutoSizing ? priceView : priceAutoSizeView);

        views.setViewVisibility(R.id.icon, prefs.showIcon() ? View.VISIBLE : View.GONE);
        if (prefs.showIcon()) {
            boolean lightTheme = prefs.isLightTheme();
            int[] drawables = Coin.valueOf(prefs.getCoin()).getDrawables();
            views.setImageViewResource(R.id.icon, lightTheme ? drawables[0] : drawables[2]);
        }

        if (!useAutoSizing) {
            Pair<Integer, Integer> availableSize = getTextAvailableSize(context, prefs.getWidgetId());
            if (availableSize == null) return textSize;
            textSize = TextSizer.getTextSize(context, text, availableSize);
            textSize = adjustForFixedSize(context, views, prefs, textSize);
            views.setTextViewTextSize(priceView, TypedValue.COMPLEX_UNIT_DIP, textSize);
            views.setTextViewText(priceView, text);
        } else {
            views.setTextViewText(priceAutoSizeView, text);
        }

        if (prefs.getLabel()) {
            show(views, useAutoSizing ? exchangeAutoSizeView : exchangeView, R.id.top_space);
            hide(views, useAutoSizing ? exchangeView : exchangeAutoSizeView);
            Exchange exchange;
            try {
                exchange = prefs.getExchange();
            } catch (IllegalArgumentException e) {
                WidgetViews.putValue(context, views, context.getString(R.string.value_exchange_removed), prefs);
                return textSize;
            }
            if (!useAutoSizing) {
                Pair<Integer, Integer> availableSize = getLabelAvailableSize(context, prefs.getWidgetId());
                float labelSize = TextSizer.getLabelSize(context, exchange.getShortName(), availableSize);
                views.setTextViewTextSize(exchangeView, TypedValue.COMPLEX_UNIT_DIP, labelSize);
                views.setTextViewText(exchangeView, exchange.getShortName());
            } else {
                views.setTextViewText(exchangeAutoSizeView, exchange.getShortName());
            }
        } else {
            hide(views, exchangeView, exchangeAutoSizeView, R.id.top_space);
        }
        hide(views, R.id.loading);
        return textSize;
    }

    private static float adjustForFixedSize(Context context, RemoteViews views,
                                            Prefs prefs, float newTextSize) {
        int[] widgetIds = WidgetApplication.getInstance().getWidgetIds();
        boolean isPortrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        float currentTextSize = prefs.getTextSize(isPortrait);
        prefs.setTextSize(newTextSize, isPortrait);

        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.key_fixed_size), false)) {
            return newTextSize;
        }

        float smallestSize = Float.MAX_VALUE;
        for (int widgetId : widgetIds) {
            if (widgetId == prefs.getWidgetId()) continue;
            float textSize = new Prefs(widgetId).getTextSize(isPortrait);
            if (smallestSize > textSize) {
                smallestSize = textSize;
            }
        }

        boolean widgetChangedSize = currentTextSize != newTextSize;
        boolean widgetWasSmallest = currentTextSize < smallestSize;
        boolean widgetIsSmallest = newTextSize < smallestSize;
        if (widgetChangedSize && (widgetIsSmallest || widgetWasSmallest || smallestSize == 0)) {
            WidgetProvider.refreshWidgets(context, prefs.getWidgetId());
        }
        return Math.min(newTextSize, smallestSize);
    }

    private static Pair<Integer, Integer> getTextAvailableSize(Context context, int widgetId) {
        Pair<Integer, Integer> size = getWidgetSize(context, widgetId);
        Prefs prefs = new Prefs(widgetId);
        if (size == null) {
            return null;
        }
        int width = size.first;
        int height = size.second;

        if (prefs.getThemeLayout() != R.layout.widget_layout_transparent) {
            // light and dark themes have 5dp padding all around
            width -= 10;
            height -= 10;
        }

        if (prefs.showIcon()) {
            // icon is 25% of width
            width *= .75;
        }
        if (prefs.getLabel()) {
            height *= TEXT_HEIGHT;
        }
        return Pair.create((int)(width * .9), (int)(height * .85));
    }

    private static Pair<Integer, Integer> getLabelAvailableSize(Context context, int widgetId) {
        Prefs prefs = new Prefs(widgetId);
        Pair<Integer, Integer> size = getWidgetSize(context, widgetId);
        if (size == null) {
            return null;
        }
        int height = size.second;
        int width = size.first;
        if (prefs.getThemeLayout() != R.layout.widget_layout_transparent) {
            // light and dark themes have 5dp padding all around
            height -= 10;
        }
        if (prefs.showIcon()) {
            // icon is 25% of width
            width *= .75;
        }
        height *= ((1 - TEXT_HEIGHT) / 2);
        return Pair.create((int)(width * .9), (int)(height * .75));
    }

    private static Pair<Integer, Integer> getWidgetSize(Context context, int widgetId) {
        boolean portrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        String w = portrait ? AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH : AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH;
        String h = portrait ? AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT : AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int width = appWidgetManager.getAppWidgetOptions(widgetId).getInt(w);
        int height = appWidgetManager.getAppWidgetOptions(widgetId).getInt(h);
        return Pair.create(width, height);
    }

    private static String buildText(String amount, Prefs prefs) {
        Currency currency = Currency.valueOf(prefs.getCurrency());
        String format = currency.getFormat();
        if (!prefs.getShowDecimals()) {
            format = format.replaceAll("\\.00", "");
        }
        NumberFormat nf = new DecimalFormat(format);
        Double adjustedAmount = Double.valueOf(amount);
        String unit = prefs.getUnit();
        if (unit != null) {
            double unitAmount = Coin.valueOf(prefs.getCoin()).getUnitAmount(unit);
            adjustedAmount *= unitAmount;
        }
        return nf.format(adjustedAmount);
    }

    static void setLoading(RemoteViews views) {
        show(views, R.id.loading);
        hide(views, R.id.price, R.id.priceAutoSize, R.id.icon, R.id.exchange, R.id.exchangeAutoSize);
    }

    private static void show(RemoteViews views, int... ids) {
        for (int id : ids) views.setViewVisibility(id, View.VISIBLE);
    }

    private static void hide(RemoteViews views, int... ids) {
        for (int id : ids) views.setViewVisibility(id, View.GONE);
    }

    static void setOld(RemoteViews views, boolean isOld, Prefs prefs) {
        if (!prefs.showIcon()) return;
        boolean lightTheme = prefs.isLightTheme();
        int[] drawables = Coin.valueOf(prefs.getCoin()).getDrawables();
        if (isOld) {
            views.setImageViewResource(R.id.icon, lightTheme ? drawables[1] : drawables[3]);
        } else {
            views.setImageViewResource(R.id.icon, lightTheme ? drawables[0] : drawables[2]);
        }
    }

}
