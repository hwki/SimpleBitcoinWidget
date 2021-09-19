package com.brentpanther.bitcoinwidget.strategy.display

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.RectF
import android.util.TypedValue
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.db.ConfigurationWithSizes
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.db.WidgetDatabase
import com.brentpanther.bitcoinwidget.strategy.presenter.WidgetPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

abstract class WidgetDisplayStrategy(context: Context, val widget: Widget, val widgetPresenter: WidgetPresenter) {

    protected val appContext: Context = context.applicationContext
    protected val dao = WidgetDatabase.getInstance(appContext).widgetDao()

    protected fun getConfig(): ConfigurationWithSizes {
        return runBlocking(Dispatchers.IO) {
            dao.configWithSizes()
        }
    }

    fun Int.dpToPx() = this * Resources.getSystem().displayMetrics.density

    protected fun getWidgetSize(): RectF {
        val portrait = appContext.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        val w = if (portrait) AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH else AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH
        val h = if (portrait) AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT else AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT
        val appWidgetManager = AppWidgetManager.getInstance(appContext)
        val width = appWidgetManager.getAppWidgetOptions(widget.widgetId).getInt(w)
        val height = appWidgetManager.getAppWidgetOptions(widget.widgetId).getInt(h)
        // widgets usually have padding and there is no way to know how much. usually 8dp.
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16F, Resources.getSystem().displayMetrics)
        return RectF(0F, 0F, width.dpToPx()-px, height.dpToPx()-px)
    }

    abstract fun refresh()

    suspend fun save() {
        widgetPresenter.hide(R.id.loading)
        widgetPresenter.setOnClickRefresh(appContext, widget.widgetId)
        dao.update(widget)
    }

    companion object {
        fun getStrategy(context: Context, widget: Widget, widgetPresenter: WidgetPresenter): WidgetDisplayStrategy {
            return SolidPriceWidgetDisplayStrategy(context, widget, widgetPresenter)
        }
    }




}