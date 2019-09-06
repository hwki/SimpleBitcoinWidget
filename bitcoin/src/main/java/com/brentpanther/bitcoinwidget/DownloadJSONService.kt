package com.brentpanther.bitcoinwidget

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.Nullable
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit


class DownloadJSONService : IntentService("Download JSON") {

    override fun onHandleIntent(@Nullable intent: Intent?) {
        try {
            downloadJSON()
        } catch (e: IOException) {
            Log.e(TAG, "Error downloading JSON.", e)
        }

        downloaded = true
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(JSON_DOWNLOADED_ACTION))
    }

    @Throws(IOException::class)
    private fun downloadJSON() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val lastModified = prefs.getString(LAST_MODIFIED, getString(R.string.json_last_modified))
        val url = getString(R.string.json_url)
        val client = OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
                .build()
        val request = Request.Builder()
                .addHeader("If-Modified-Since", lastModified!!)
                .url(url)
                .build()

        val response = client.newCall(request).execute()
        when (response.code) {
            304 -> Log.d(TAG, "No changes found in JSON file.")
            200 -> {
                Log.d(TAG, "Updated JSON file found.")
                prefs.edit().putString(LAST_MODIFIED, response.header("Last-Modified")).apply()
                val json = response.body!!.bytes()
                val os = openFileOutput(CURRENCY_FILE_NAME, Context.MODE_PRIVATE)
                os.write(json)
                os.close()
            }
            else -> Log.d(TAG, "Retrieved status code: " + response.code)
        }
    }

    companion object {

        private const val LAST_MODIFIED = "last_modified"
        const val CURRENCY_FILE_NAME = "coins.json"
        const val JSON_DOWNLOADED_ACTION = "json-downloaded"
        private val TAG = DownloadJSONService::class.java.simpleName
        internal var downloaded = false
    }

}
