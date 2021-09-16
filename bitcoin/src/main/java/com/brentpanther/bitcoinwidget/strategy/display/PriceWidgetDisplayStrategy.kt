package com.brentpanther.bitcoinwidget.strategy.display

import android.content.Context
import com.brentpanther.bitcoinwidget.Coin
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.strategy.presenter.WidgetPresenter
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.math.pow

abstract class PriceWidgetDisplayStrategy(context: Context, widget: Widget, widgetPresenter: WidgetPresenter) :
    WidgetDisplayStrategy(context, widget, widgetPresenter) {

    protected fun formatPriceString(amount: String?): String {
        if (amount.isNullOrEmpty()) return ""
        var adjustedAmount = amount.toDouble()
        widget.coinUnit?.let {
            adjustedAmount *= widget.coin.getUnitAmount(it)
        }
        widget.currencyUnit?.let {
            adjustedAmount /= Coin.valueOf(widget.currency).getUnitAmount(it)
        }

        val nf = getPriceFormat(adjustedAmount)
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

    private fun getPriceFormat(adjustedAmount: Double): NumberFormat {
        val symbol = widget.currencySymbol
        if (Coin.COIN_NAMES.contains(widget.currency)) {
            // virtual currency
            val format = Coin.getVirtualCurrencyFormat(widget.currency, symbol == "none")
            return DecimalFormat(format)
        } else {
            val nf = when (symbol) {
                null -> {
                    val nf = DecimalFormat.getCurrencyInstance()
                    nf.currency = Currency.getInstance(widget.currency)
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
            if (!widget.showDecimals && adjustedAmount > 1000) {
                nf.maximumFractionDigits = 0
            }
            return nf
        }
    }


}

