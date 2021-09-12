package com.brentpanther.bitcoinwidget.strategy

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.StringRes
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.receiver.WidgetBroadcastReceiver

class RemoteWidgetPresenter(context: Context, widget: Widget) : WidgetPresenter {

    private var remoteViews: RemoteViews

    init {
        val isDark = widget.nightMode.isDark(context)
        val layout = widget.theme.getLayout(isDark)
        remoteViews = RemoteViews(context.packageName, layout)
    }

    override fun setTextViewText(viewId: Int, text: CharSequence) = remoteViews.setTextViewText(viewId, text)

    override fun setImageViewResource(viewId: Int, srcId: Int) = remoteViews.setImageViewResource(viewId, srcId)

    override fun setImageViewUri(viewId: Int, uri: Uri) = remoteViews.setImageViewUri(viewId, uri)

    override fun setImageViewBitmap(viewId: Int, bitmap: Bitmap) = remoteViews.setImageViewBitmap(viewId, bitmap)

    override fun setTextViewTextSize(viewId: Int, units: Int, size: Float) = remoteViews.setTextViewTextSize(viewId, units, size)

    override fun setTextColor(viewId: Int, color: Int) = remoteViews.setTextColor(viewId, color)

    override fun setBackground(viewId: Int, value: Int) = remoteViews.setInt(viewId, "setBackgroundResource", value)

    override fun show(vararg viewIds: Int) = viewIds.forEach { remoteViews.setViewVisibility(it, View.VISIBLE) }

    override fun hide(vararg viewIds: Int) = viewIds.forEach { remoteViews.setViewVisibility(it, View.GONE) }

    override fun setOnClickRefresh(context: Context, widgetId: Int) {
        val priceUpdate = Intent(context, WidgetBroadcastReceiver::class.java)
        priceUpdate.action = "refresh"
        priceUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        val pendingPriceUpdate = PendingIntent.getBroadcast(context, widgetId, priceUpdate,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        remoteViews.setOnClickPendingIntent(R.id.parent, pendingPriceUpdate)
        AppWidgetManager.getInstance(context).updateAppWidget(widgetId, remoteViews)
    }

    override fun setOnClickMessage(context: Context, @StringRes message: Int) {
        val messageIntent = Intent(context, WidgetBroadcastReceiver::class.java)
        messageIntent.action = "message"
        messageIntent.putExtra("message", message)
        val alertPendingIntent = PendingIntent.getBroadcast(context, 96854, messageIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        remoteViews.setOnClickPendingIntent(R.id.state, alertPendingIntent)
    }

    fun loading(context: Context, widgetId: Int) {
        show(R.id.loading)
        hide(R.id.state)
        AppWidgetManager.getInstance(context).updateAppWidget(widgetId, remoteViews)
    }

}