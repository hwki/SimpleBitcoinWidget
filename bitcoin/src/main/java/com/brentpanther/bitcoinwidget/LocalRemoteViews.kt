package com.brentpanther.bitcoinwidget

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.TextView
import java.lang.ref.WeakReference


/**
 * Mock for RemoteViews that allows app to update the widget preview in the same way
 * as the actual widget.
 */
class LocalRemoteViews(activity: Activity, layoutId: Int) : RemoteViews(activity.packageName, layoutId) {

    private val activityRef: WeakReference<Activity> = WeakReference(activity)

    init {
        val container = activity.findViewById<ViewGroup>(R.id.widgetContainer)
        if (container.getChildAt(0).id != layoutId) {
            container.removeViewAt(0)
            View.inflate(activity, layoutId, container)
        }
    }

    override fun setTextViewText(viewId: Int, text: CharSequence) = invokeOn<TextView>(viewId) { it.text = text}

    override fun setViewVisibility(viewId: Int, visibility: Int) = invokeOn<View>(viewId) { it.visibility = visibility }

    override fun setImageViewResource(viewId: Int, srcId: Int) = invokeOn<ImageView>(viewId) { it.setImageResource(srcId) }

    override fun setTextViewTextSize(viewId: Int, units: Int, size: Float) = invokeOn<TextView>(viewId) { it.setTextSize(units, size) }

    override fun setTextColor(viewId: Int, color: Int) = invokeOn<TextView>(viewId) { it.setTextColor(color) }

    override fun setInt(viewId: Int, methodName: String?, value: Int) {
        when (methodName) {
            "setBackgroundResource" -> invokeOn<View>(viewId) { it.setBackgroundResource(value) }
        }
    }

    private inline fun <reified X> invokeOn(viewId: Int, crossinline func: (X) -> kotlin.Unit) {
        activityRef.get()?.runOnUiThread { (activityRef.get()?.findViewById<View>(viewId) as X).let { func.invoke(it) } }
    }

}
