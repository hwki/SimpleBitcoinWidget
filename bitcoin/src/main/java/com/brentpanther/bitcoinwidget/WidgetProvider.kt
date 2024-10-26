package com.brentpanther.bitcoinwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.brentpanther.bitcoinwidget.db.WidgetDatabase
import com.brentpanther.bitcoinwidget.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

open class WidgetProvider : AppWidgetProvider() {

    override fun onRestored(context: Context, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        val widgetDao = WidgetDatabase.getInstance(context).widgetDao()
        CoroutineScope(Dispatchers.IO).launch {
            oldWidgetIds?.zip(newWidgetIds ?: IntArray(0))?.forEach { (old, new) ->
                widgetDao.getByWidgetId(old)?.apply {
                    widgetId = new
                    widgetDao.update(this)
                }
            }
        }
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager,
                                           appWidgetId: Int, newOptions: Bundle) {
        WidgetUpdater.updateDisplays(context)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // check for any unconfigured widgets, setting them to "Tap to Configure" state
        CoroutineScope(Dispatchers.IO).launch {
            val allWidgetIds = WidgetDatabase.getInstance(context).widgetDao().getAll().map { it.widgetId }
            appWidgetIds.subtract(allWidgetIds).forEach { widgetId ->
                setConfigure(context, widgetId)
            }
        }
        WidgetUpdater.updateDisplays(context)
    }

    private fun setConfigure(context: Context, appWidgetId: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        RemoteViews(context.packageName, R.layout.widget_empty).apply {
            val pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT)
            setOnClickPendingIntent(R.id.parent, pendingIntent)
            AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, this)
        }
    }

    override fun onDeleted(context: Context, widgetIds: IntArray) {
        val widgetDao = WidgetDatabase.getInstance(context).widgetDao()
        CoroutineScope(Dispatchers.IO).launch {
            widgetDao.delete(widgetIds)
            if (widgetDao.getAll().isEmpty()) {
                val workManager = WorkManager.getInstance(context)
                workManager.cancelUniqueWork(ONETIMEWORKNAME)
                cancelWork(workManager)
            } else if (widgetDao.configWithSizes().consistentSize) {
                WidgetUpdater.updateDisplays(context)
            }
        }
    }

    companion object {

        private const val WORKNAME = "widgetRefresh"
        const val ONETIMEWORKNAME = "115575872"

        fun refreshWidgets(context: Context, widgetId: Int) = refreshWidgets(context, intArrayOf(widgetId))

        fun refreshWidgets(context: Context, widgetIds: IntArray? = null, restart: Boolean = false) = CoroutineScope(Dispatchers.IO).launch {
            val dao = WidgetDatabase.getInstance(context).widgetDao()
            val widgetIdsToRefresh = widgetIds ?: dao.getAll().map { it.widgetId }.toIntArray()
            if (widgetIdsToRefresh.isEmpty()) return@launch

            WidgetUpdater.update(context, widgetIdsToRefresh, false)
            val workManager = WorkManager.getInstance(context)
            val refresh = dao.configWithSizes().refresh

            // https://issuetracker.google.com/issues/115575872
            val immediateWork = OneTimeWorkRequestBuilder<WidgetUpdateWorker>()
                .setInitialDelay(3650L, TimeUnit.DAYS).build()
            workManager.enqueueUniqueWork(ONETIMEWORKNAME, ExistingWorkPolicy.KEEP, immediateWork)

            if (restart) {
                workManager.cancelAllWorkByTag(WORKNAME)
            }
            when (refresh) {
                0 -> workManager.cancelAllWorkByTag(WORKNAME)
                5 -> (5..15 step 5).forEachIndexed { i, it -> scheduleWork(workManager, 15, it, i) }
                10 -> (10..20 step 10).forEachIndexed { i, it -> scheduleWork(workManager, 20, it, i) }
                else -> scheduleWork(workManager, refresh, refresh, 0)
            }
        }

        fun cancelWork(workManager: WorkManager) = workManager.cancelAllWorkByTag(WORKNAME)

        private fun scheduleWork(workManager: WorkManager, refresh: Int, delay: Int, index: Int) {
            val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            val work = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(refresh.toLong(), TimeUnit.MINUTES)
                .setConstraints(constraints)
                .addTag(WORKNAME)
                .setInitialDelay(delay.toLong(), TimeUnit.MINUTES)
                .build()
            workManager.enqueueUniquePeriodicWork("$WORKNAME$index", ExistingPeriodicWorkPolicy.KEEP, work)
        }

    }

}