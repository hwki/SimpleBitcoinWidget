package com.brentpanther.bitcoinwidget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.res.Configuration
import android.util.Pair
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import androidx.preference.PreferenceManager
import java.text.DecimalFormat
import java.text.NumberFormat

import java.util.*
import kotlin.math.min
import kotlin.math.pow

internal object WidgetViews {

    private const val TEXT_HEIGHT = .70

    fun setText(context: Context, views: RemoteViews, amount: String, prefs: Prefs) {
        val text = buildText(amount, prefs)
        prefs.lastValue = text
        putValue(context, views, text, prefs)
    }

    fun setLastText(context: Context, views: RemoteViews, prefs: Prefs) {
        prefs.lastValue?.let {
            putValue(context, views, it, prefs)
        } ?: putValue(context, views, context.getString(R.string.value_unknown), prefs)
    }

    fun putValue(context: Context, views: RemoteViews, text: String, prefs: Prefs): Float {
        val useAutoSizing = WidgetApplication.instance.useAutoSizing()
        val priceView = R.id.price
        val priceAutoSizeView = R.id.priceAutoSize
        val exchangeView = R.id.exchange
        val exchangeAutoSizeView = R.id.exchangeAutoSize
        var textSize = 0f

        show(views, if (useAutoSizing) priceAutoSizeView else priceView)
        hide(views, if (useAutoSizing) priceView else priceAutoSizeView)

        views.setViewVisibility(R.id.icon, if (prefs.showIcon()) View.VISIBLE else View.GONE)
        if (prefs.showIcon()) {
            val lightTheme = prefs.isLightTheme(context)
            val drawables = prefs.coin.drawables
            views.setImageViewResource(R.id.icon, if (lightTheme) drawables[0] else drawables[2])
        }

        if (!useAutoSizing) {
            val availableSize = getTextAvailableSize(context, prefs.widgetId)
            if (availableSize == null) {
                views.setTextViewText(priceView, text)
            } else {
                textSize = TextSizer.getTextSize(context, text, availableSize, prefs.themeLayout)
                textSize = adjustForFixedSize(context, prefs, textSize)
                views.setTextViewTextSize(priceView, TypedValue.COMPLEX_UNIT_DIP, textSize)
                views.setTextViewText(priceView, text)
            }
        } else {
            views.setTextViewText(priceAutoSizeView, text)
        }

        if (prefs.label) {
            show(views, if (useAutoSizing) exchangeAutoSizeView else exchangeView, R.id.top_space)
            hide(views, if (useAutoSizing) exchangeView else exchangeAutoSizeView)
            val shortName = try {
                prefs.exchange.shortName
            } catch (ignored: IllegalArgumentException) {
                prefs.exchangeName
            }

            if (!useAutoSizing) {
                val availableSize = getLabelAvailableSize(context, prefs.widgetId)
                if (availableSize == null) {
                    views.setTextViewText(exchangeView, shortName)
                } else {
                    val labelSize = TextSizer.getLabelSize(context, shortName!!, availableSize, prefs.themeLayout)
                    views.setTextViewTextSize(exchangeView, TypedValue.COMPLEX_UNIT_DIP, labelSize)
                    views.setTextViewText(exchangeView, shortName)
                }
            } else {
                views.setTextViewText(exchangeAutoSizeView, shortName)
            }
        } else {
            hide(views, exchangeView, exchangeAutoSizeView, R.id.top_space)
        }
        hide(views, R.id.loading)
        return textSize
    }

    private fun adjustForFixedSize(context: Context, prefs: Prefs, newTextSize: Float): Float {
        val widgetIds = WidgetApplication.instance.widgetIds
        val isPortrait = context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        val currentTextSize = prefs.getTextSize(isPortrait)
        prefs.setTextSize(newTextSize, isPortrait)

        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.key_fixed_size), false)) {
            return newTextSize
        }

        var smallestSize = Float.MAX_VALUE
        for (widgetId in widgetIds) {
            if (widgetId == prefs.widgetId) continue
            val textSize = Prefs(widgetId).getTextSize(isPortrait)
            if (smallestSize > textSize) {
                smallestSize = textSize
            }
        }

        val widgetChangedSize = currentTextSize != newTextSize
        val widgetWasSmallest = currentTextSize < smallestSize
        val widgetIsSmallest = newTextSize < smallestSize
        if (widgetChangedSize && (widgetIsSmallest || widgetWasSmallest || smallestSize == 0f)) {
            // refresh all widgets that are not the same smallestSize
            for (widgetId in widgetIds) {
                if (Prefs(widgetId).getTextSize(isPortrait) != smallestSize) {
                    WidgetProvider.refreshWidgets(context, widgetId)
                }
            }
        }
        return min(newTextSize, smallestSize)
    }

    private fun getTextAvailableSize(context: Context, widgetId: Int): Pair<Int, Int>? {
        val size = getWidgetSize(context, widgetId)
        val prefs = Prefs(widgetId)
        var width = size.first
        var height = size.second

        if (!prefs.isTransparent) {
            // light and dark themes have 5dp padding all around
            width -= 10
            height -= 10
        }

        if (prefs.showIcon()) {
            // icon is 25% of width
            width *= .75
        }
        if (prefs.label) {
            height *= TEXT_HEIGHT
        }
        return Pair.create((width * .9).toInt(), (height * .85).toInt())
    }

    private fun getLabelAvailableSize(context: Context, widgetId: Int): Pair<Int, Int>? {
        val prefs = Prefs(widgetId)
        val size = getWidgetSize(context, widgetId)
        var height = size.second
        var width = size.first
        if (!prefs.isTransparent) {
            // light and dark themes have 5dp padding all around
            height -= 10
        }
        if (prefs.showIcon()) {
            // icon is 25% of width
            width *= .75
        }
        height *= ((1 - TEXT_HEIGHT) / 2)
        return Pair.create((width * .9).toInt(), (height * .75).toInt())
    }

    private fun getWidgetSize(context: Context, widgetId: Int): Pair<Double, Double> {
        val portrait = context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        val w = if (portrait) AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH else AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH
        val h = if (portrait) AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT else AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val width = appWidgetManager.getAppWidgetOptions(widgetId).getInt(w).toDouble()
        val height = appWidgetManager.getAppWidgetOptions(widgetId).getInt(h).toDouble()
        return Pair.create(width, height)
    }

    private fun buildText(amount: String, prefs: Prefs): String {
        val currency = prefs.currency
        var adjustedAmount = amount.toDouble()
        val unit = prefs.unit
        if (unit != null) {
            adjustedAmount *= prefs.coin.getUnitAmount(unit)
        }
        val nf: NumberFormat
        if (Coin.COIN_NAMES.contains(currency)) {
            // virtual currency
            val format = Coin.getVirtualCurrencyFormat(currency!!)
            nf = DecimalFormat(format)
        } else {
            nf = DecimalFormat.getCurrencyInstance()
            nf.currency = Currency.getInstance(currency)
            if (!prefs.showDecimals && adjustedAmount > 1) {
                nf.maximumFractionDigits = 0
            }
        }
        if (adjustedAmount < 1) {
            // show at least 3 significant digits if small amount
            // e.g. show 0.00000000243 instead of 0.00 but still show 1.25 instead of 1.25000003
            var zeroes = nf.maximumFractionDigits
            while (adjustedAmount * 10.0.pow((zeroes - 2).toDouble()) < 1) {
                zeroes++
            }
            nf.maximumFractionDigits = zeroes
        }
        return nf.format(adjustedAmount)
    }

    fun setLoading(views: RemoteViews) {
        show(views, R.id.loading)
        hide(views, R.id.price, R.id.priceAutoSize, R.id.icon, R.id.exchange, R.id.exchangeAutoSize)
    }

    private fun show(views: RemoteViews, vararg ids: Int) {
        for (id in ids) views.setViewVisibility(id, View.VISIBLE)
    }

    private fun hide(views: RemoteViews, vararg ids: Int) {
        for (id in ids) views.setViewVisibility(id, View.GONE)
    }

    fun setOld(context: Context, views: RemoteViews, isOld: Boolean, prefs: Prefs) {
        if (!prefs.showIcon()) return
        val lightTheme = prefs.isLightTheme(context)
        val drawables = prefs.coin.drawables
        if (isOld) {
            views.setImageViewResource(R.id.icon, if (lightTheme) drawables[1] else drawables[3])
        } else {
            views.setImageViewResource(R.id.icon, if (lightTheme) drawables[0] else drawables[2])
        }
    }

}
