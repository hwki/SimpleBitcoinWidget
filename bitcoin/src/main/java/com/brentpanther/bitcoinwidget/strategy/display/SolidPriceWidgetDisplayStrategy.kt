package com.brentpanther.bitcoinwidget.strategy.display

import android.content.Context
import android.content.res.Configuration
import android.graphics.RectF
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.widget.TextView
import com.brentpanther.bitcoinwidget.Coin
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.Theme
import com.brentpanther.bitcoinwidget.WidgetApplication.Companion.dpToPx
import com.brentpanther.bitcoinwidget.db.ConfigurationWithSizes
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.strategy.TextViewAutoSizeHelper
import com.brentpanther.bitcoinwidget.strategy.presenter.WidgetPresenter
import kotlin.math.min
import kotlin.math.pow


open class SolidPriceWidgetDisplayStrategy(context: Context, widget: Widget, widgetPresenter: WidgetPresenter) :
    PriceWidgetDisplayStrategy(context, widget, widgetPresenter) {

    override fun refresh() {
        val widgetSize = widgetPresenter.getWidgetSize(appContext, widget.widgetId)
        // add padding
        widgetSize.bottom -= 16.dpToPx()
        val horizontalPadding = when (widget.theme) {
            Theme.MATERIAL -> 8
            Theme.TRANSPARENT -> if (widget.nightMode.isDark(appContext)) 2 else 0
            else -> 4
        }
        widgetSize.right -= horizontalPadding.dpToPx()

        updateIcon()
        RectF(widgetSize).also {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                it.bottom *= .22F
            }
            updateLabels(it)
        }
        RectF(widgetSize).also {
            it.bottom *= if (widget.showExchangeLabel || widget.showCoinLabel) .56F else .95F
            it.right *= if (widget.showIcon) .80F else 1F
            updatePrice(it)
        }
        updateState()
    }

    private fun formatPriceString(amount: String?): String {
        if (amount.isNullOrEmpty()) return ""
        var adjustedAmount = amount.toDouble()
        widget.coinUnit?.let {
            adjustedAmount *= widget.coin.getUnitAmount(it)
        }
        widget.currencyUnit?.let {
            adjustedAmount /= Coin.valueOf(widget.currency).getUnitAmount(it)
        }
        if (widget.useInverse) {
            adjustedAmount = 1 / adjustedAmount
        }

        val nf = getPriceFormat(adjustedAmount)
        if (widget.numDecimals == -1 && adjustedAmount < 1000 && adjustedAmount > 0) {
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

    private fun updateLabels(rectF: RectF) {
        val adjustSize = Build.VERSION.SDK_INT < Build.VERSION_CODES.O

        if (widget.showExchangeLabel) {
            widgetPresenter.show(R.id.exchangeLabel)
            if (adjustSize) {
                updateAutoTextView(R.id.exchangeLabel, widget.exchange.shortName, rectF)
            }
            widgetPresenter.setTextViewText(R.id.exchangeLabel, widget.exchange.shortName)
        } else {
            widgetPresenter.hide(R.id.exchangeLabel)
        }

        if (widget.showCoinLabel) {
            widgetPresenter.show(R.id.coinLabel)
            if (adjustSize) {
                updateAutoTextView(R.id.coinLabel, widget.coinName(), rectF)
            }
            widgetPresenter.setTextViewText(R.id.coinLabel, widget.coinName())
        }
        else {
            widgetPresenter.hide(R.id.coinLabel)
        }

        if (!widget.showExchangeLabel && !widget.showCoinLabel) {
            widgetPresenter.gone(R.id.coinLabel, R.id.exchangeLabel)
        }
    }

    protected fun updatePrice(rectF: RectF) {
        val price = if (widget.lastValue == null) {
            appContext.getString(R.string.placeholder_price)
        } else {
            try {
                formatPriceString(widget.lastValue)
            } catch (e: NumberFormatException) {
                Log.e(TAG, "Error formatting price: ${widget.lastValue}")
                widget.lastValue ?: ""
            }
        }
        val config = getConfig()
        val adjustSize = config.consistentSize || Build.VERSION.SDK_INT < Build.VERSION_CODES.O
        if (adjustSize) {
            widgetPresenter.gone(R.id.priceAutoSize)
            widgetPresenter.show(R.id.price)
            getView(R.id.price).let {
                it.text = price
                adjustPriceTextSize(config, it, rectF)
            }
            widgetPresenter.setTextViewText(R.id.price, price)
        } else {
            widgetPresenter.setTextViewText(R.id.priceAutoSize, price)
            widgetPresenter.show(R.id.priceAutoSize)
            widgetPresenter.gone(R.id.price)
            widget.portraitTextSize = 0
            widget.landscapeTextSize = 0
        }
    }

    private fun adjustPriceTextSize(config: ConfigurationWithSizes, view: TextView, rectF: RectF) {
        val priceSize = TextViewAutoSizeHelper.findLargestTextSizeWhichFits(view, rectF)
        val isPortrait = appContext.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        var size = if (isPortrait) {
            widget.portraitTextSize = priceSize
            config.portrait
        } else {
            widget.landscapeTextSize = priceSize
            config.landscape
        }
        size = if (size > 0) min(size, priceSize) else priceSize
        widgetPresenter.setTextViewTextSize(R.id.price, TypedValue.COMPLEX_UNIT_PX, size.toFloat())
    }

    companion object {
        private val TAG = SolidPriceWidgetDisplayStrategy::class.java.simpleName
    }

}