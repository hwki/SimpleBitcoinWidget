package com.brentpanther.bitcoinwidget.strategy

import android.content.Context
import android.content.res.Configuration
import android.graphics.RectF
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.core.net.toUri
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.WidgetState.*
import com.brentpanther.bitcoinwidget.db.ConfigurationWithSizes
import com.brentpanther.bitcoinwidget.db.Widget
import kotlin.math.min


class SolidPriceWidgetDisplayStrategy(context: Context, widget: Widget, widgetPresenter: WidgetPresenter) :
    PriceWidgetDisplayStrategy(context, widget, widgetPresenter) {

    override fun refresh() {
        updateIcon()
        val widgetSize = getWidgetSize()
        updateLabel(RectF(widgetSize))
        updatePrice(RectF(widgetSize))
        updateState()
        widgetPresenter.hide(R.id.loading)
        widgetPresenter.setOnClickRefresh(appContext, widget.widgetId)
    }

    private fun updateState() {
        with(widgetPresenter) {
            when(widget.state) {
                STALE -> {
                    show(R.id.state)
                    setImageViewResource(R.id.state, R.drawable.ic_outline_stale)
                    setOnClickMessage(appContext, R.string.state_stale)
                }
                ERROR -> {
                    show(R.id.state)
                    setImageViewResource(R.id.state, R.drawable.ic_outline_warning_amber_24)
                    setOnClickMessage(appContext, R.string.state_error)
                }
                CURRENT -> hide(R.id.state)
            }
        }
    }

    private fun updateLabel(rectF: RectF) {
        if (!widget.showLabel) {
            widgetPresenter.hide(R.id.label, R.id.space_top)
            return
        }
        widgetPresenter.show(R.id.label, R.id.space_top)
        rectF.bottom *= .15F
        val adjustSize = Build.VERSION.SDK_INT < Build.VERSION_CODES.O
        if (adjustSize) {
            getView(R.id.label).let {
                it.text = widget.exchange.shortName
                val labelSize = TextViewAutoSizeHelper.findLargestTextSizeWhichFits(it, rectF)
                widgetPresenter.setTextViewTextSize(R.id.label, TypedValue.COMPLEX_UNIT_PX, labelSize.toFloat())
            }
        }
        widgetPresenter.setTextViewText(R.id.label, widget.exchange.shortName)
    }

    private fun updateIcon() {
        if (widget.showIcon) {
            widgetPresenter.show(R.id.icon)
        } else {
            widgetPresenter.hide(R.id.icon)
            return
        }
        val customIcon = widget.customIcon
        if (customIcon != null) {
            val uri = "content://${appContext.packageName}.fileprovider/icons/$customIcon".toUri()
            widgetPresenter.setImageViewUri(R.id.icon, uri)
        } else {
            val isDark = widget.nightMode.isDark(appContext)
            val icon = widget.coin.getIcon(widget.theme, isDark)
            widgetPresenter.setImageViewResource(R.id.icon, icon)
        }
    }

    private fun updatePrice(rectF: RectF) {
        val heightPercent = if (widget.showLabel) .70F else 1F
        val widthPercent = if (widget.showIcon) .80F else 1F
        rectF.right *= widthPercent
        rectF.bottom *= heightPercent
        val price = if (widget.lastValue == null) {
            appContext.getString(R.string.placeholder_price)
        } else {
            formatPriceString(widget.lastValue)
        }
        val config = getConfig()
        val adjustSize = config.consistentSize || Build.VERSION.SDK_INT < Build.VERSION_CODES.O
        if (adjustSize) {
            widgetPresenter.hide(R.id.priceAutoSize)
            widgetPresenter.show(R.id.price)
            getView(R.id.price).let {
                it.text = price
                adjustPriceTextSize(config, it, rectF)
            }
            widgetPresenter.setTextViewText(R.id.price, price)
        } else {
            widgetPresenter.setTextViewText(R.id.priceAutoSize, price)
            widgetPresenter.show(R.id.priceAutoSize)
            widgetPresenter.hide(R.id.price)
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

    private fun getView(@IdRes layoutId: Int): TextView {
        val layout = widget.theme.getLayout(widget.nightMode.isDark(appContext))
        val vg = LayoutInflater.from(appContext).inflate(layout, null) as ViewGroup
        return vg.findViewById(layoutId)
    }

}