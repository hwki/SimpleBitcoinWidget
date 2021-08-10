package com.brentpanther.bitcoinwidget.ui.preview

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.brentpanther.bitcoinwidget.databinding.LayoutWidgetPreviewBinding

class LocalWidgetPreview(private val binding: LayoutWidgetPreviewBinding) : WidgetPreview {

    override fun setTextViewText(viewId: Int, text: CharSequence) {
        binding.root.findViewById<TextView>(viewId).text = text
    }

    override fun setImageViewResource(viewId: Int, srcId: Int) {
        binding.root.findViewById<ImageView>(viewId).setImageResource(srcId)
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
        viewIds.forEach { binding.root.findViewById<View>(it).isVisible = false }
    }
}