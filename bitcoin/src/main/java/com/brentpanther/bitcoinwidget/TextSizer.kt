package com.brentpanther.bitcoinwidget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.Pair
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

@SuppressLint("InflateParams")
internal object TextSizer {

    fun getTextSize(context: Context, text: String, availableSize: Pair<Int, Int>): Float {
        val vg = LayoutInflater.from(context).inflate(R.layout.widget_layout, null) as ViewGroup
        val textView = vg.findViewById<TextView>(R.id.price)
        return getHighestInBounds(textView, text, availableSize.first.toFloat(), availableSize.second.toFloat())
    }

    fun getLabelSize(context: Context, text: String, availableSize: Pair<Int, Int>): Float {
        val vg = LayoutInflater.from(context).inflate(R.layout.widget_layout, null) as ViewGroup
        val textView = vg.findViewById<TextView>(R.id.exchange)
        return getHighestInBounds(textView, text, availableSize.first.toFloat(), availableSize.second.toFloat())
    }

    private fun getHighestInBounds(textView: TextView, text: String, widthPx: Float, heightPx: Float): Float {
        val paint = textView.paint
        if (text.isEmpty()) {
            return 0f
        }
        val rect = Rect()
        var dp = 6f
        val step = 0.5f
        while (true) {
            paint.textSize = dp
            paint.getTextBounds(text, 0, text.length, rect) // does not give accurate width
            val measuredHeight = rect.height().toFloat()
            val measuredWidth = paint.measureText(text)
            if (measuredHeight > heightPx || measuredWidth >= widthPx) {
                return dp - step
            }
            dp += step
        }
    }

}
