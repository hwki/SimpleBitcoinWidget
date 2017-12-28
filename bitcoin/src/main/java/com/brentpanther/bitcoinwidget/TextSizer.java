package com.brentpanther.bitcoinwidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

abstract class TextSizer {

    static float getTextSize(Context context, String text, Pair<Integer, Integer> availableSize) {
        @SuppressLint("InflateParams")
        ViewGroup vg = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.widget_layout, null);
        TextView textView = vg.findViewById(R.id.price);
        return getHighestInBounds(textView, text, availableSize.first, availableSize.second);
    }

    static float getLabelSize(Context context, String text, Pair<Integer, Integer> availableSize) {
        @SuppressLint("InflateParams")
        ViewGroup vg = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.widget_layout, null);
        TextView textView = vg.findViewById(R.id.exchange);
        return getHighestInBounds(textView, text, availableSize.first, availableSize.second);
    }

    private static float getHighestInBounds(TextView textView, String text, float widthPx, float heightPx) {
        Paint paint = textView.getPaint();
        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        Rect rect = new Rect();
        float dp = 6f;
        float step = 0.5f;
        while (true) {
            paint.setTextSize(dp);
            paint.getTextBounds(text, 0, text.length(), rect); // does not give accurate width
            float measuredHeight = rect.height();
            float measuredWidth = paint.measureText(text);
            if (measuredHeight > heightPx || measuredWidth >= widthPx) {
                return dp - step;
            }
            dp += step;
        }
    }

}
