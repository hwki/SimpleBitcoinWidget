package com.brentpanther.bitcoinwidget.ui.preview

import android.graphics.Bitmap
import android.view.View
import android.widget.RemoteViews

class RemoteWidgetPreview(private val remoteViews: RemoteViews) : WidgetPreview {

    override fun setTextViewText(viewId: Int, text: CharSequence) = remoteViews.setTextViewText(viewId, text)

    override fun setImageViewResource(viewId: Int, srcId: Int) = remoteViews.setImageViewResource(viewId, srcId)

    override fun setImageViewBitmap(viewId: Int, bitmap: Bitmap) = remoteViews.setImageViewBitmap(viewId, bitmap)

    override fun setTextViewTextSize(viewId: Int, units: Int, size: Float) = remoteViews.setTextViewTextSize(viewId, units, size)

    override fun setTextColor(viewId: Int, color: Int) = remoteViews.setTextColor(viewId, color)

    override fun setBackground(viewId: Int, value: Int) = remoteViews.setInt(viewId, "setBackgroundResource", value)

    override fun show(vararg viewIds: Int) = viewIds.forEach { remoteViews.setViewVisibility(it, View.VISIBLE) }

    override fun hide(vararg viewIds: Int) = viewIds.forEach { remoteViews.setViewVisibility(it, View.GONE) }

}