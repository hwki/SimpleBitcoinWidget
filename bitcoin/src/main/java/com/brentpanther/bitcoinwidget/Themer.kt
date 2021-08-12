package com.brentpanther.bitcoinwidget

import android.content.Context
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.content.FileProvider
import com.brentpanther.bitcoinwidget.NightMode.*
import com.brentpanther.bitcoinwidget.Theme.*
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.ui.preview.WidgetPreview
import java.io.File


/**
 * Set the colors of the widget programmatically, to better handle
 * light/dark theme transitions, and simplify layouts
 */
object Themer {

    internal fun updateTheme(context: Context, views: WidgetPreview, widget: Widget, isOld: Boolean) {
        updateLayout(context, views, widget)
        updateIcon(context, views, widget, isOld)
    }

    private fun updateLayout(context: Context, views: WidgetPreview, widget: Widget) {
        val isNight = (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        var bgOuter = R.drawable.bg_outer
        var bgInner = R.drawable.bg_inner
        var textColor = Color.parseColor("#888888")

        var bgParent = R.drawable.transparent
        val nightMode = widget.nightMode
        if (widget.theme == SOLID && (nightMode == DARK || (nightMode == SYSTEM && isNight))) {
            bgOuter = R.drawable.bg_outer_dark
            bgInner = R.drawable.bg_inner_dark
            textColor = Color.parseColor("#666666")
        }
        else if (nightMode == DARK || (nightMode == SYSTEM && isNight)) {
            bgParent = R.drawable.bg_outer_dark
        }
        when (widget.theme) {
            SOLID -> {
                views.setBackground(R.id.outerParent, bgOuter)
                views.setBackground(R.id.parent, bgInner)
                for (view in arrayOf(R.id.price, R.id.priceAutoSize, R.id.exchange, R.id.exchangeAutoSize)) {
                    views.setTextColor(view, textColor)
                }
            }
            TRANSPARENT -> {
                views.setBackground(R.id.parent, bgParent)
            }
        }
    }


    private fun updateIcon(context: Context, views: WidgetPreview, widget: Widget, isOld: Boolean) {
        if (widget.showIcon) views.show(R.id.icon) else views.hide(R.id.icon)
        val customIcon = widget.customIcon
        if (customIcon != null) {
            val path = File(File(context.filesDir, "icons"), customIcon)
            val uriForFile = FileProvider.getUriForFile(context,
                "com.brentpanther.bitcoinwidget.fileprovider", path)
            try {
                val stream = context.contentResolver.openInputStream(uriForFile)
                val bitmap = BitmapFactory.decodeStream(stream)
                views.setImageViewBitmap(R.id.icon, bitmap)

            } catch (ignored: Exception) {
            }
        } else {
            val isDark = widget.nightMode.isDark(context)
            val icon = widget.coin.getIcon(widget.theme, isDark, isOld)
            views.setImageViewResource(R.id.icon, icon)
        }
    }

}