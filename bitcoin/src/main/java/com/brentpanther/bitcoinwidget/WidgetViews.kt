package com.brentpanther.bitcoinwidget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.Pair
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import androidx.preference.PreferenceManager
import java.lang.NumberFormatException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.math.min
import kotlin.math.pow

internal object WidgetViews {

    private const val TEXT_HEIGHT = .70

    fun setText(context: Context, views: RemoteViews, prefs: Prefs, amount: String?, isUpdate: Boolean) {
        if (amount == null) {
            setUnknownAmount(context, views, prefs)
        } else {
            if (isUpdate) {
                // store the formatted value
                try {
                    val text = buildText(amount, prefs)
                    prefs.lastValue = text
                    putValue(context, views, text, prefs)
                } catch (e: NumberFormatException) {
                    setUnknownAmount(context, views, prefs)
                }
            } else {
                putValue(context, views, amount, prefs)
            }
        }
    }

    private fun setUnknownAmount(context: Context, views: RemoteViews, prefs: Prefs) {
        val lastUpdate = prefs.lastUpdate
        // gray out older values
        val isOld = System.currentTimeMillis() - lastUpdate > 60000 * prefs.interval * 1.5
        val value = prefs.lastValue ?: context.getString(R.string.value_unknown)
        putValue(context, views, value, prefs, isOld)
    }

    fun putValue(context: Context, views: RemoteViews, text: String, prefs: Prefs, isOld: Boolean = false): Float {
        val useAutoSizing = WidgetApplication.instance.useAutoSizing()
        val priceView = R.id.price
        val priceAutoSizeView = R.id.priceAutoSize
        val exchangeView = R.id.exchange
        val exchangeAutoSizeView = R.id.exchangeAutoSize
        var textSize = 0f

        views.show(if (useAutoSizing) priceAutoSizeView else priceView)
        views.hide(if (useAutoSizing) priceView else priceAutoSizeView)

        Themer.updateTheme(context, views, prefs, isOld)

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
            views.show(if (useAutoSizing) exchangeAutoSizeView else exchangeView, R.id.top_space)
            views.hide(if (useAutoSizing) exchangeView else exchangeAutoSizeView)
            val shortName = prefs.exchange?.shortName ?: prefs.exchangeName

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
            views.hide(exchangeView, exchangeAutoSizeView, R.id.top_space)
        }
        views.hide(R.id.loading)
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
            width -= 10.toPx()
            height -= 10.toPx()
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

    private fun Int.toPx(): Int {
        return (Resources.getSystem().displayMetrics.density * this).toInt()
    }

    private fun getLabelAvailableSize(context: Context, widgetId: Int): Pair<Int, Int>? {
        val prefs = Prefs(widgetId)
        val size = getWidgetSize(context, widgetId)
        var height = size.second
        var width = size.first
        if (!prefs.isTransparent) {
            // light and dark themes have 5dp padding all around
            height -= 10.toPx()
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
        var adjustedAmount = amount.toDouble()
        val unit = prefs.unit
        if (unit != null) {
            adjustedAmount *= prefs.coin.getUnitAmount(unit)
        }

        val nf = getFormat(prefs, adjustedAmount)
        if (adjustedAmount < 1000) {
            // show at least 3 significant digits if small amount
            // e.g. show 0.00000000243 instead of 0.00 but still show 1.250 instead of 1.25000003
            var zeroes = nf.maximumFractionDigits
            while (adjustedAmount * 10.0.pow((zeroes - 2).toDouble()) < 1) {
                zeroes++
            }
            nf.maximumFractionDigits = zeroes
            nf.minimumFractionDigits = zeroes
        }
        return nf.format(adjustedAmount)
    }

    private fun getFormat(prefs: Prefs, adjustedAmount: Double): NumberFormat {
        val currency = prefs.currency ?: Currency.getInstance(Locale.getDefault()).currencyCode
        val symbol = prefs.currencySymbol
        if (Coin.COIN_NAMES.contains(currency)) {
            // virtual currency
            val format = Coin.getVirtualCurrencyFormat(currency, symbol == "none")
            return DecimalFormat(format)
        } else {
            val nf = when (symbol) {
                null -> {
                    val nf = DecimalFormat.getCurrencyInstance()
                    nf.currency = Currency.getInstance(currency)
                    nf
                }
                "none" -> NumberFormat.getNumberInstance()
                else -> {
                    val nf = DecimalFormat.getCurrencyInstance() as DecimalFormat
                    val decimalFormatSymbols = nf.decimalFormatSymbols
                    decimalFormatSymbols.currencySymbol = symbol
                    nf.decimalFormatSymbols = decimalFormatSymbols
                    nf
                }
            }
            if (!prefs.showDecimals && adjustedAmount > 1000) {
                nf.maximumFractionDigits = 0
            }
            return nf
        }
    }

    fun setLoading(views: RemoteViews) {
        views.show(R.id.loading)
        views.hide(R.id.price, R.id.priceAutoSize, R.id.icon, R.id.exchange, R.id.exchangeAutoSize)
    }

    private fun RemoteViews.hide(vararg ids: Int) {
        for (id in ids) setViewVisibility(id, View.GONE)
    }

    private fun RemoteViews.show(vararg ids: Int) {
        for (id in ids) setViewVisibility(id, View.VISIBLE)
    }

}
