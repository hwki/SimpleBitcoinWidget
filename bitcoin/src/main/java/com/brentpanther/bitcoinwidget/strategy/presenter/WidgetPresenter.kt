package com.brentpanther.bitcoinwidget.strategy.presenter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.net.Uri


interface WidgetPresenter {

    fun setTextViewText(viewId: Int, text: CharSequence)

    fun setImageViewResource(viewId: Int, srcId: Int)

    fun setImageViewUri(viewId: Int, uri: Uri)

    fun setImageViewBitmap(viewId: Int, bitmap: Bitmap)

    fun setTextViewTextSize(viewId: Int, units: Int, size: Float)

    fun setTextColor(viewId: Int, color: Int)

    fun setBackground(viewId: Int, value: Int)

    fun show(vararg viewIds: Int)

    fun hide(vararg viewIds: Int)

    fun gone(vararg viewIds: Int)

    fun setOnClickRefresh(context: Context, widgetId: Int)

    fun setOnClickError(context: Context, widgetId: Int)

    fun getWidgetSize(context: Context, widgetId: Int): RectF


}