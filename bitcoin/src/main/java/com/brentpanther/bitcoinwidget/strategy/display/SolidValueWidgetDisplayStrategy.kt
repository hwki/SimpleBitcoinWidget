package com.brentpanther.bitcoinwidget.strategy.display

import android.content.Context
import android.graphics.RectF
import android.os.Build
import android.util.Log
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.WidgetApplication.Companion.dpToPx
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.strategy.presenter.WidgetPresenter
import java.text.DecimalFormat
import java.util.*

class SolidValueWidgetDisplayStrategy(context: Context, widget: Widget, widgetPresenter: WidgetPresenter) :
    SolidPriceWidgetDisplayStrategy(context, widget, widgetPresenter) {

    override fun refresh() {
        updateIcon()
        val widgetSize = widgetPresenter.getWidgetSize(appContext, widget.widgetId)
        // add padding
        widgetSize.bottom -= 16.dpToPx()
        widgetSize.right -= 16.dpToPx()
        RectF(widgetSize).also {
            it.bottom *= .22F
            updateLabel(it)
            updateAmount(it)
        }

        RectF(widgetSize).also {
            it.bottom *= if (widget.showAmountLabel || widget.showIcon || widget.showCoinLabel) .56F else 1F
            updatePrice(it)
        }
        updateState()
    }

    private fun updateLabel(rectF: RectF) {
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
    }

    private fun updateAmount(rectF: RectF) {
        if (!widget.showAmountLabel && !widget.showCoinLabel) {
            widgetPresenter.gone(R.id.coinLabel)
            if (!widget.showIcon && !widget.showExchangeLabel) {
                widgetPresenter.gone(R.id.coin_layout, R.id.exchangeLabel)
            }
            return
        }
        widgetPresenter.show(R.id.coin_layout, R.id.coinLabel)
        var amount = ""
        if (widget.showAmountLabel) {
            amount = try {
                DecimalFormat.getNumberInstance(Locale.getDefault()).run {
                    minimumFractionDigits = 0
                    maximumFractionDigits = 4
                    format(widget.amountHeld)
                }
            } catch (e : Exception) {
                Log.e(TAG, "Error formatting amount: ${widget.amountHeld}", e)
                widget.amountHeld.toString()
            }
        }
        if (widget.showCoinLabel) {
            amount += " " + widget.coinName()
        }
        updateAutoTextView(R.id.coinLabel, amount, rectF)
        widgetPresenter.setTextViewText(R.id.coinLabel, amount)
    }

    companion object {
        private val TAG = SolidValueWidgetDisplayStrategy::class.java.simpleName
    }
}