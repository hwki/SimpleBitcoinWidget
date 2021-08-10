package com.brentpanther.bitcoinwidget.ui.preview

import android.graphics.Bitmap

interface WidgetPreview {

    fun setTextViewText(viewId: Int, text: CharSequence)

    fun setImageViewResource(viewId: Int, srcId: Int)

    fun setImageViewBitmap(viewId: Int, bitmap: Bitmap)

    fun setTextViewTextSize(viewId: Int, units: Int, size: Float)

    fun setTextColor(viewId: Int, color: Int)

    fun setBackground(viewId: Int, value: Int)

    fun show(vararg viewIds: Int)

    fun hide(vararg viewIds: Int)

}