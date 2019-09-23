package com.brentpanther.bitcoinwidget

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.TextView
import java.lang.ref.WeakReference


class LocalRemoteViews(activity: Activity, layoutId: Int) : RemoteViews(activity.packageName, layoutId) {

    private val activityRef: WeakReference<Activity> = WeakReference(activity)

    init {
        val container = activity.findViewById<ViewGroup>(R.id.widgetContainer)
        if (container.getChildAt(0).id != layoutId) {
            container.removeViewAt(0)
            View.inflate(activity, layoutId, container)
        }
    }

    override fun setTextViewText(viewId: Int, text: CharSequence) {
        getActivity()?.runOnUiThread { (getActivity()?.findViewById<View>(viewId) as TextView).text = text }
    }

    override fun setViewVisibility(viewId: Int, visibility: Int) {
        getActivity()?.runOnUiThread { getActivity()?.findViewById<View>(viewId)?.visibility = visibility }
    }

    override fun setImageViewResource(viewId: Int, srcId: Int) {
        getActivity()?.runOnUiThread { (activityRef.get()?.findViewById<View>(viewId) as ImageView).setImageResource(srcId) }
    }

    override fun setTextViewTextSize(viewId: Int, units: Int, size: Float) {
        getActivity()?.runOnUiThread { (getActivity()?.findViewById<View>(viewId) as TextView).setTextSize(units, size) }
    }

    private fun getActivity() = activityRef.get()
}
