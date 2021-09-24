package com.brentpanther.bitcoinwidget.strategy.presenter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.databinding.LayoutWidgetPreviewBinding
import com.brentpanther.bitcoinwidget.db.Widget

class PreviewWidgetPresenter(widget: Widget, val binding: LayoutWidgetPreviewBinding) : WidgetPresenter {

    init {
        val context = binding.root.context
        val isDark = widget.nightMode.isDark(context)
        val layout = widget.theme.getLayout(isDark, widget.widgetType)
        binding.widgetContainer.removeAllViews()
        View.inflate(context, layout, binding.widgetContainer)
    }

    override fun setTextViewText(viewId: Int, text: CharSequence) {
        binding.root.findViewById<TextView>(viewId).text = text
    }

    override fun setImageViewResource(viewId: Int, srcId: Int) {
        binding.root.findViewById<ImageView>(viewId).setImageResource(srcId)
    }

    override fun setImageViewUri(viewId: Int, uri: Uri) {
        binding.root.findViewById<ImageView>(viewId).setImageURI(uri)
    }

    override fun setImageViewBitmap(viewId: Int, bitmap: Bitmap) {
        binding.root.findViewById<ImageView>(viewId).setImageBitmap(bitmap)
    }

    override fun setTextViewTextSize(viewId: Int, units: Int, size: Float) {
        binding.root.findViewById<TextView>(viewId).setTextSize(units, size)
    }

    override fun setTextColor(viewId: Int, color: Int) {
        binding.root.findViewById<TextView>(viewId).setTextColor(color)
    }

    override fun setBackground(viewId: Int, value: Int) {
        binding.root.findViewById<View>(viewId).setBackgroundResource(value)
    }

    override fun show(vararg viewIds: Int) {
        viewIds.forEach { binding.root.findViewById<View>(it).isVisible = true }
    }

    override fun hide(vararg viewIds: Int) {
        viewIds.forEach { binding.root.findViewById<View>(it).isInvisible = true }
    }

    override fun gone(vararg viewIds: Int) {
        viewIds.forEach { binding.root.findViewById<View>(it).isVisible = false }
    }

    override fun setOnClickRefresh(context: Context, widgetId: Int) {
    }

    override fun setOnClickMessage(context: Context, message: Int) {
    }

    override fun getWidgetSize(context: Context, widgetId: Int): RectF {
        return RectF(0F, 0F, context.resources.getDimensionPixelSize(R.dimen.widget_preview_width).toFloat(),
            context.resources.getDimensionPixelSize(R.dimen.widget_preview_height).toFloat())
    }

}