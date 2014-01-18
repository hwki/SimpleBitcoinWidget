package com.brentpanther.bitcoinwidget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Jelly Bean allows for changing text size of widgets with RemoteViews.setTextViewTextSize().
 * Since this widget supports earlier devices, we have to try each text size
 * out until we find one that fits.
 */
public class TextSizer {

    public static SparseArray<Integer> priceMap = new SparseArray<Integer>() {{
        put(10, R.id.price1);
        put(12, R.id.price2);
        put(14, R.id.price3);
        put(16, R.id.price4);
        put(18, R.id.price5);
        put(20, R.id.price6);
        put(22, R.id.price7);
        put(24, R.id.price8);
        put(26, R.id.price9);
        put(28, R.id.price10);
        put(30, R.id.price11);
    }};

    public static SparseArray<Integer> priceSplitMap = new SparseArray<Integer>() {{
        put(10, R.id.price1s);
        put(12, R.id.price2s);
        put(14, R.id.price3s);
        put(16, R.id.price4s);
        put(18, R.id.price5s);
        put(20, R.id.price6s);
        put(22, R.id.price7s);
        put(24, R.id.price8s);
        put(26, R.id.price9s);
        put(28, R.id.price10s);
        put(30, R.id.price11s);
    }};

    public static SparseArray<Integer> providerMap = new SparseArray<Integer>() {{
        put(9, R.id.provider1);
        put(10, R.id.provider2);
        put(11, R.id.provider3);
    }};

    public static class Group {
        int size;
        int id;
        String text;
        boolean split;
    }

    public static Group getPriceID(Context context, Currency currency, Double amount, int width) {
        Group group = new Group();
        group.split = false;
        String format = currency.getFormat(amount);
        String formatStripped = format.replaceAll("\\$\\n", "\\$");
        formatStripped = formatStripped.replaceAll("\\n", " ");
        NumberFormat nf = new DecimalFormat(formatStripped);
        String text = nf.format(amount);
        //try with no new line
        int size = getPriceSize(context, text, 30, width);
        if(size <= 18 && format.contains("\n")) {
            group.split = true;
            //try with new line
            nf = new DecimalFormat(format);
            text = nf.format(amount);
            int size1 = getPriceSize(context, text.split("\n")[0], 24, width);
            int size2 = getPriceSize(context, text.split("\n")[1], 24, width);
            size = Math.min(size1, size2);
            group.id = priceSplitMap.get(size);
        } else {
            group.id = priceMap.get(size);
        }
        group.text = text;
        group.size = size;
        return group;
    }

    private static int getPriceSize(Context context, String text, int max, int width) {
        ViewGroup vg = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.widget_layout, null);
        TextView textView = (TextView) vg.findViewById(R.id.priceJB);
        Paint paint = textView.getPaint();
        float px = getPx(context, width);
        for(int dp = max; dp >= 10; dp -= 2) {
            paint.setTextSize(getPx(context, dp));
            float fit_size = paint.measureText(text);
            if(fit_size < px - 10) {
                return dp;
            }
        }
        return 10;
    }

    public static int getProviderID(Context context, String provider) {
        ViewGroup vg = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.image, null);
        TextView textView = (TextView) vg.findViewById(R.id.provider1);
        Paint paint = textView.getPaint();
        float px = getPx(context, 28);
        int DP = 9;
        for(int dp = 11; dp >= 9; dp-=1) {
            paint.setTextSize(getPx(context, dp));
            float fit_size = paint.measureText(provider);
            if(fit_size < px - 2) {
                DP = dp;
                break;
            }
        }
        return providerMap.get(DP);
    }

    private static float getPx(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (dp * (metrics.densityDpi / 160f));
    }

}
