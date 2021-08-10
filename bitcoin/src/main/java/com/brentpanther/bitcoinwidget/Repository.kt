package com.brentpanther.bitcoinwidget

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

object Repository {

    private const val LAST_MODIFIED = "last_modified"
    const val CURRENCY_FILE_NAME = "coins.json"
    private val TAG = this::class.java.simpleName

    fun downloadJSON(context: Context) {
        try {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val lastModified = prefs.getString(LAST_MODIFIED, context.getString(R.string.json_last_modified))
            val url = context.getString(R.string.json_url)
            val client = OkHttpClient.Builder()
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
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
                    val json = response.body!!.bytes()
                    val os = context.openFileOutput(CURRENCY_FILE_NAME, Context.MODE_PRIVATE)
                    os.write(json)
                    os.close()
                    prefs.edit().putString(LAST_MODIFIED, response.header("Last-Modified")).apply()
                    Log.d(TAG, "JSON downloaded.")
                }
                else -> Log.d(TAG, "Retrieved status code: " + response.code)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error downloading JSON.", e)
        }
    }


}
