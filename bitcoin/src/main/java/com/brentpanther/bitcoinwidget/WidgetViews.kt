package com.brentpanther.bitcoinwidget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.Pair
import android.util.TypedValue
import com.brentpanther.bitcoinwidget.db.WidgetDatabase
import com.brentpanther.bitcoinwidget.db.WidgetSettings
import com.brentpanther.bitcoinwidget.receiver.WidgetProvider
import com.brentpanther.bitcoinwidget.ui.preview.WidgetPreview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.math.min
import kotlin.math.pow

class WidgetViews(private val context: Context, private val views: WidgetPreview, private val settings: WidgetSettings) {

    fun setText(amount: String?, isUpdate: Boolean) {
        when {
            amount == null -> putValue()
            isUpdate -> {
                try {
                    settings.widget.lastValue = buildText(amount)
                } catch (ignored: NumberFormatException) {
                }
                putValue()
            }
            else -> putValue(amount)
        }
    }

    fun putValue(value: String? = null): Float {
        val text = value ?: settings.widget.lastValue ?: context.getString(R.string.value_unknown)
        val useAutoSizing = settings.useAutoSizing()
        val priceView = R.id.price
        val priceAutoSizeView = R.id.priceAutoSize
        val exchangeView = R.id.exchange
        val exchangeAutoSizeView = R.id.exchangeAutoSize
        var textSize = 0f

        views.show(if (useAutoSizing) priceAutoSizeView else priceView)
        views.hide(if (useAutoSizing) priceView else priceAutoSizeView)

        Themer.updateTheme(context, views, settings.widget, settings.isOld())

        if (!useAutoSizing) {
            val availableSize = getTextAvailableSize()
            if (availableSize == null) {
                views.setTextViewText(priceView, text)
            } else {
                textSize = TextSizer.getTextSize(context, text, availableSize, settings.widget.theme.layout)
                textSize = adjustForFixedSize(textSize)
                views.setTextViewTextSize(priceView, TypedValue.COMPLEX_UNIT_DIP, textSize)
                views.setTextViewText(priceView, text)
            }
        } else {
            views.setTextViewText(priceAutoSizeView, text)
        }

        if (settings.widget.showLabel) {
            views.show(if (useAutoSizing) exchangeAutoSizeView else exchangeView, R.id.top_space)
            views.hide(if (useAutoSizing) exchangeView else exchangeAutoSizeView)
            val shortName = settings.widget.exchange.shortName

            if (!useAutoSizing) {
                val availableSize = getLabelAvailableSize()
                if (availableSize == null) {
                    views.setTextViewText(exchangeView, shortName)
                } else {
                    val labelSize = TextSizer.getLabelSize(context, shortName, availableSize, settings.widget.theme.layout)
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

    private fun adjustForFixedSize(newTextSize: Float): Float {
        val isPortrait = context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        val currentTextSize: Float
        if (isPortrait) {
            currentTextSize = settings.widget.portraitTextSize ?: Float.MAX_VALUE
            settings.widget.portraitTextSize = newTextSize
        } else {
            currentTextSize = settings.widget.landscapeTextSize ?: Float.MAX_VALUE
            settings.widget.landscapeTextSize = newTextSize
        }

        if (!settings.config.consistentSize) {
            return newTextSize
        }

        var smallestSize = if (isPortrait) settings.config.portrait else settings.config.landscape
        smallestSize = if (smallestSize == 0f) Float.MAX_VALUE else smallestSize

        CoroutineScope(Dispatchers.IO).launch {
            val widgetChangedSize = currentTextSize != newTextSize
            val widgetWasSmallest = currentTextSize < smallestSize
            val widgetIsSmallest = newTextSize < smallestSize
            if (widgetChangedSize && (widgetIsSmallest || widgetWasSmallest || smallestSize == 0f)) {
                // refresh all widgets that are not the same smallestSize
                val widgetDao = WidgetDatabase.getInstance(context).widgetDao()
                widgetDao.getAll().filter {
                    if (isPortrait) it.portraitTextSize != smallestSize else it.landscapeTextSize != smallestSize
                }.forEach {
                    WidgetProvider.refreshWidgets(context, it.widgetId)
                }
            }
        }
        return min(newTextSize, smallestSize)
    }

    private fun getTextAvailableSize(): Pair<Int, Int>? {
        val size = getWidgetSize()
        var width = size.first
        var height = size.second

        if (!settings.widget.theme.isTransparent()) {
            // light and dark themes have 5dp padding all around
            width -= 10.toPx()
            height -= 10.toPx()
        }

        if (settings.widget.showIcon) {
            // icon is 25% of width
            width *= .75
        }
        if (settings.widget.showLabel) {
            height *= TEXT_HEIGHT
        }
        return Pair.create((width * .9).toInt(), (height * .85).toInt())
    }

    private fun Int.toPx(): Int {
        return (Resources.getSystem().displayMetrics.density * this).toInt()
    }

    private fun getLabelAvailableSize(): Pair<Int, Int>? {
        val size = getWidgetSize()
        var height = size.second
        var width = size.first
        if (!settings.widget.theme.isTransparent()) {
            // light and dark themes have 5dp padding all around
            height -= 10.toPx()
        }
        if (settings.widget.showIcon) {
            // icon is 25% of width
            width *= .75
        }
        height *= ((1 - TEXT_HEIGHT) / 2)
        return Pair.create((width * .9).toInt(), (height * .75).toInt())
    }

    private fun getWidgetSize(): Pair<Double, Double> {
        val portrait = context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        val w = if (portrait) AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH else AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH
        val h = if (portrait) AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT else AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val width = appWidgetManager.getAppWidgetOptions(settings.widget.widgetId).getInt(w).toDouble()
        val height = appWidgetManager.getAppWidgetOptions(settings.widget.widgetId).getInt(h).toDouble()
        return Pair.create(width, height)
    }

    private fun buildText(amount: String): String {
        var adjustedAmount = amount.toDouble()
        val unit = settings.widget.unit
        if (unit != null) {
            adjustedAmount *= settings.widget.coin.getUnitAmount(unit)
        }

        val nf = getFormat(adjustedAmount)
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

    private fun getFormat(adjustedAmount: Double): NumberFormat {
        val symbol = settings.widget.currencySymbol
        if (Coin.COIN_NAMES.contains(settings.widget.currency)) {
            // virtual currency
            val format = Coin.getVirtualCurrencyFormat(settings.widget.currency, symbol == "none")
            return DecimalFormat(format)
        } else {
            val nf = when (symbol) {
                null -> {
                    val nf = DecimalFormat.getCurrencyInstance()
                    nf.currency = Currency.getInstance(settings.widget.currency)
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
            if (!settings.widget.showDecimals && adjustedAmount > 1000) {
                nf.maximumFractionDigits = 0
            }
            return nf
        }
    }

    fun setLoading(views: WidgetPreview) {
        MainScope().launch {
            views.show(R.id.loading)
            views.hide(R.id.price, R.id.priceAutoSize, R.id.icon, R.id.exchange, R.id.exchangeAutoSize)
        }
    }

    companion object {
        private const val TEXT_HEIGHT = .70
    }

}
