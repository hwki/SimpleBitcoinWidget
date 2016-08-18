package com.brentpanther.bitcoinwidget;

import android.content.Context;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class TextSizer {

    public static Group getPriceID(Context context, Currency currency, Double amount, int width) {
        Group group = new Group();
        String format = currency.getFormat(amount);
        String formatStripped = format.replaceAll("\\$\\n", "\\$");
        formatStripped = formatStripped.replaceAll("\\n", " ");
        NumberFormat nf = new DecimalFormat(formatStripped);
        String text = nf.format(amount);
        //try with no new line
        int size = getPriceSize(context, text, 30, width);
        if(size <= 18 && format.contains("\n")) {
            //try with new line
            nf = new DecimalFormat(format);
            text = nf.format(amount);
            int size1 = getPriceSize(context, text.split("\n")[0], 24, width);
            int size2 = getPriceSize(context, text.split("\n")[1], 24, width);
            size = Math.min(size1, size2);
        }
        group.text = text;
        group.size = size;
        return group;
    }

    private static int getPriceSize(Context context, String text, int max, int width) {
        ViewGroup vg = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.widget_layout, null);
        TextView textView = (TextView) vg.findViewById(R.id.price);
        Paint paint = textView.getPaint();
        float px = getPx(context, width);
        for (int dp = max; dp >= 10; dp -= 2) {
            paint.setTextSize(getPx(context, dp));
            float fit_size = paint.measureText(text);
            if (fit_size < px - 10) {
                return dp;
            }
        }
        return 10;
    }

    public static int getProviderSize(Context context, String provider) {
        ViewGroup vg = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.image, null);
        TextView textView = (TextView) vg.findViewById(R.id.provider);
        Paint paint = textView.getPaint();
        float px = getPx(context, 28);
        int providerSize = 9;
        for (int dp = 11; dp >= 9; dp -= 1) {
            paint.setTextSize(getPx(context, dp));
            float fit_size = paint.measureText(provider);
            if (fit_size < px - 2) {
                providerSize = dp;
                break;
            }
        }
        return providerSize;
    }

    private static float getPx(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (dp * (metrics.densityDpi / 160f));
    }

    public static class Group {
        int size;
        String text;
    }

}
