package com.brentpanther.bitcoinwidget

import android.content.Context
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.FileProvider
import com.brentpanther.bitcoinwidget.Theme.*
import com.brentpanther.bitcoinwidget.db.Widget
import java.io.File


/**
 * Set the colors of the widget programmatically, to better handle
 * light/dark theme transitions, and simplify layouts
 */
object Themer {

    internal fun updateTheme(context: Context, views: RemoteViews, settings: Widget, isOld: Boolean) {
        updateLayout(context, views, settings)
        updateIcon(context, views, settings, isOld)
    }

    private fun updateLayout(context: Context, views: RemoteViews, settings: Widget) {
        val isNight = (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        var bgOuter = R.drawable.bg_outer
        var bgInner = R.drawable.bg_inner
        var textColor = Color.parseColor("#888888")

        var bgParent = R.drawable.transparent
        val theme = settings.theme
        if (theme == DARK || (theme == DAY_NIGHT && isNight)) {
            bgOuter = R.drawable.bg_outer_dark
            bgInner = R.drawable.bg_inner_dark
            textColor = Color.parseColor("#666666")
        }
        else if (theme == TRANSPARENT_DARK || (theme == TRANSPARENT_DAY_NIGHT && isNight)) {
            bgParent = R.drawable.bg_outer_dark
        }
        when (theme) {
            LIGHT, DARK, DAY_NIGHT -> {
                views.setInt(R.id.outerParent, "setBackgroundResource", bgOuter)
                views.setInt(R.id.parent, "setBackgroundResource", bgInner)
                for (view in arrayOf(R.id.price, R.id.priceAutoSize, R.id.exchange, R.id.exchangeAutoSize)) {
                    views.setTextColor(view, textColor)
                }
            }
            TRANSPARENT, TRANSPARENT_DARK, TRANSPARENT_DAY_NIGHT -> {
                views.setInt(R.id.parent, "setBackgroundResource", bgParent)
            }
        }
    }


    private fun updateIcon(context: Context, views: RemoteViews, settings: Widget, isOld: Boolean) {
        views.setViewVisibility(R.id.icon, if (settings.showIcon) View.VISIBLE else View.GONE)
        val customIcon = settings.customIcon
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
            val icon = getIcon(context, settings.coin, settings.theme, isOld)
            views.setImageViewResource(R.id.icon, icon)
        }
    }

    /**
     * Find the icon to show based on the current theme, and whether or not the value is old.
     */
    private fun getIcon(context: Context, coin: Coin, theme: Theme, isOld: Boolean = false) : Int {
        val isNight = (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        val key = when (theme) {
            DAY_NIGHT -> if (isNight) DARK else LIGHT
            TRANSPARENT_DAY_NIGHT -> if (isNight) TRANSPARENT_DARK else TRANSPARENT
            else -> theme
        }
        var name = key.name
        if (isOld) name += "_OLD"
        val iconTheme = IconTheme.valueOf(name)
        return coin.icons.getOrElse(iconTheme, { getIcon(context, coin, iconTheme.fallback, isOld)})
    }

}