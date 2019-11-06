package com.brentpanther.bitcoinwidget

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.view.View
import android.widget.RemoteViews

/**
 * Set the colors of the widget programmatically, to better handle
 * light/dark theme transitions, and simplify layouts
 */
object Themer {

    internal fun updateTheme(context: Context, views: RemoteViews, prefs: Prefs, isOld: Boolean) {
        updateLayout(context, views, prefs)
        updateIcon(context, views, prefs, isOld)
    }

    private fun updateLayout(context: Context, views: RemoteViews, prefs: Prefs) {
        val isNight = (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        var bgOuter = R.drawable.bg_outer
        var bgInner = R.drawable.bg_inner
        var textColor = Color.parseColor("#888888")

        var bgParent = R.drawable.transparent
        val theme = prefs.theme
        if (theme == "Dark" || (theme == "DayNight" && isNight)) {
            bgOuter = R.drawable.bg_outer_dark
            bgInner = R.drawable.bg_inner_dark
            textColor = Color.parseColor("#666666")
        }
        else if (theme == "Transparent Dark" || (theme == "Transparent DayNight" && isNight)) {
            bgParent = R.drawable.bg_outer_dark
        }
        when (theme) {
            "Light", "Dark", "DayNight" -> {
                views.setInt(R.id.outerParent, "setBackgroundResource", bgOuter)
                views.setInt(R.id.parent, "setBackgroundResource", bgInner)
                for (view in arrayOf(R.id.price, R.id.priceAutoSize, R.id.exchange, R.id.exchangeAutoSize)) {
                    views.setTextColor(view, textColor)
                }
            }
            "Transparent", "Transparent Dark", "Transparent DayNight" -> {
                views.setInt(R.id.parent, "setBackgroundResource", bgParent)
            }
        }
    }

    private fun updateIcon(context: Context, views: RemoteViews, prefs: Prefs, isOld: Boolean) {
        views.setViewVisibility(R.id.icon, if (prefs.showIcon()) View.VISIBLE else View.GONE)
        val icon = getIcon(context, prefs.coin, prefs.theme, isOld)
        views.setImageViewResource(R.id.icon, icon)
    }

    /**
     * Find the icon to show based on the current theme, and whether or not the value is old.
     */
    private fun getIcon(context: Context, coin: Coin, theme: String, isOld: Boolean = false) : Int {
        val isNight = (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        var key = if (theme == "DayNight") {
            if (isNight) "Dark" else "Light"
        } else if (theme == "Transparent DayNight") {
            if (isNight) "Transparent Dark" else "Transparent"
        } else {
            theme
        }
        if (isOld) key += " Old"
        val fallback = fallbacks[key] ?: "Light"
        return coin.icons.getOrElse(key, { getIcon(context, coin, fallback) })
    }

    // if an icon is not defined for a theme, fallback to another icon
    private val fallbacks = mapOf("Light Old" to "Light", "Dark Old" to "Light Old",
            "Transparent Dark Old" to "Dark Old", "Transparent Old" to "Light Old",
            "Transparent Dark" to "Dark", "Transparent" to "Light", "Dark" to "Light")

}