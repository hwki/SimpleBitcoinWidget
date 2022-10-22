package com.brentpanther.bitcoinwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Bundle
import androidx.work.*
import com.brentpanther.bitcoinwidget.db.WidgetDatabase
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

    override fun onEnabled(context: Context) {
        refreshWidgets(context)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, widgetIds: IntArray) {
        refreshWidgets(context)
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager,
        appWidgetId: Int, newOptions: Bundle) {
        refreshWidgets(context)
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
                refreshWidgets(context)
            }
        }
    }

    companion object {

        const val WORKNAME = "widgetRefresh"
        const val ONETIMEWORKNAME = "115575872"

        fun refreshWidgets(context: Context, restart: Boolean = false) = CoroutineScope(Dispatchers.IO).launch {
            val dao = WidgetDatabase.getInstance(context).widgetDao()
            val widgetIds = dao.getAll().map { it.widgetId }.toIntArray()
            if (widgetIds.isEmpty()) return@launch

            WidgetUpdater.update(context, widgetIds, false)
            val workManager = WorkManager.getInstance(context)
            val refresh = dao.configWithSizes().refresh

            // https://issuetracker.google.com/issues/115575872
            val immediateWork = OneTimeWorkRequestBuilder<WidgetUpdateWorker>().setInitialDelay(3650L, TimeUnit.DAYS).build()
            workManager.enqueueUniqueWork(ONETIMEWORKNAME, ExistingWorkPolicy.KEEP, immediateWork)

            val workPolicy = if (restart) ExistingPeriodicWorkPolicy.REPLACE else ExistingPeriodicWorkPolicy.KEEP
            when (refresh) {
                5 -> (0..10 step 5).forEachIndexed { i, it -> scheduleWork(workManager, 15, it, i, workPolicy) }
                10 -> (0..10 step 10).forEachIndexed { i, it -> scheduleWork(workManager, 20, it, i, workPolicy) }
                else -> scheduleWork(workManager, refresh, 0, 0, workPolicy)
            }
        }

        fun cancelWork(workManager: WorkManager) = workManager.cancelAllWorkByTag(WORKNAME)

        fun scheduleWork(workManager: WorkManager, refresh: Int, delay: Int, index: Int, policy: ExistingPeriodicWorkPolicy) {
            val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            val work = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(refresh.toLong(), TimeUnit.MINUTES)
                .setConstraints(constraints)
                .addTag(WORKNAME)
                .setInitialDelay(delay.toLong(), TimeUnit.MINUTES)
                .build()
            workManager.enqueueUniquePeriodicWork("$WORKNAME$index", policy, work)
        }

    }

}