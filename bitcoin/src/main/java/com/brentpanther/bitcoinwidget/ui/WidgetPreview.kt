package com.brentpanther.bitcoinwidget.ui

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.widget.TextViewCompat
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.strategy.display.WidgetDisplayStrategy
import com.brentpanther.bitcoinwidget.strategy.presenter.ComposePreviewWidgetPresenter

//TODO replace with glance composable when ready
@Composable
fun WidgetPreview(widget: Widget, fixedSize: Boolean, modifier: Modifier = Modifier) {
    AndroidView(factory = { context ->
        LayoutInflater.from(context).inflate(R.layout.layout_widget_preview, null)
    },
        update = {
        val widgetPresenter = ComposePreviewWidgetPresenter(widget, it)
        val strategy = WidgetDisplayStrategy.getStrategy(it.context, widget, widgetPresenter)
        strategy.refresh()
        it.findViewById<View>(R.id.parent).isClickable = false
        if (!fixedSize) {
            val price = it.findViewById<TextView>(R.id.price)
            TextViewCompat.setAutoSizeTextTypeWithDefaults(
                price, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM
            )
        }
    }, modifier = modifier)
}