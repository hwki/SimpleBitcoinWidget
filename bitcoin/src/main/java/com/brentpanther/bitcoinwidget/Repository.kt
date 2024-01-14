package com.brentpanther.bitcoinwidget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.exchange.CustomExchangeData
import com.brentpanther.bitcoinwidget.exchange.ExchangeData
import com.brentpanther.bitcoinwidget.exchange.ExchangeHelper
import kotlinx.serialization.SerializationException
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.closeQuietly
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

object Repository {

    private const val LAST_MODIFIED = "last_modified"
    private const val CURRENCY_FILE_NAME = "coins.json"
    private val TAG = Repository::class.java.simpleName

    fun downloadJSON() {
        val context = WidgetApplication.instance
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
                    response.body?.byteStream()?.use {
                        val os = context.openFileOutput(CURRENCY_FILE_NAME, Context.MODE_PRIVATE)
                        it.copyTo(os)
                        os.closeQuietly()
                    }
                    prefs.edit().putString(LAST_MODIFIED, response.header("Last-Modified")).apply()
                    Log.d(TAG, "JSON downloaded.")
                }
                else -> Log.d(TAG, "Retrieved status code: " + response.code)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error downloading JSON.", e)
        }
    }

    fun downloadCustomIcon(widget: Widget) {
        val context = WidgetApplication.instance
        widget.customIcon?.let { url ->
            val dir = File(context.filesDir, "icons")
            if (!dir.exists()) {
                dir.mkdir()
            }
            val id = widget.coinCustomId ?: return@let
            val file = File(dir, id)
            if (file.exists()) {
                return
            }

            ExchangeHelper.getStream(url)?.use { stream ->
                ByteArrayOutputStream().use { os ->
                    BitmapFactory.decodeStream(stream)?.let { image ->
                        image.compress(Bitmap.CompressFormat.PNG, 100, os)
                        file.writeBytes(os.toByteArray())
                    }
                }
            }
        }
    }

    fun getExchangeData(coin: Coin, coinName: String?): ExchangeData {
        val context = WidgetApplication.instance
        return try {
            if (coin == Coin.CUSTOM) {
                CustomExchangeData(coinName ?: coin.coinName, coin, getJson(context))
            } else  {
                val data = ExchangeData(coin, getJson(context))
                if (data.numberExchanges == 0) {
                    throw SerializationException("No exchanges found.")
                }
                data
            }
        } catch(e: SerializationException) {
            Log.e(TAG, "Error parsing JSON file, falling back to original.", e)
            context.deleteFile(CURRENCY_FILE_NAME)
            PreferenceManager.getDefaultSharedPreferences(context).edit {
                remove(LAST_MODIFIED)
            }
            ExchangeData(coin,  getJson(context))
        }
    }

    private fun getJson(context: Context): InputStream {
        return if (File(context.filesDir, CURRENCY_FILE_NAME).exists()) {
            context.openFileInput(CURRENCY_FILE_NAME)
        } else {
            context.resources.openRawResource(R.raw.cryptowidgetcoins_v2)
        }
    }


}
