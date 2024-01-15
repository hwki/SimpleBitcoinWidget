package com.brentpanther.bitcoinwidget

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class WidgetUpdateWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            WidgetUpdater.update(applicationContext, WidgetApplication.instance.widgetIds, false).join()
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Exception in worker", e)
            Result.failure()
        }
    }

    companion object {
        private val TAG = WidgetUpdateWorker::class.java.simpleName
    }

}