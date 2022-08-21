package com.brentpanther.bitcoinwidget.exchange

import com.brentpanther.bitcoinwidget.WidgetApplication
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.*
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

object ExchangeHelper {

    var useCache = true
    val cache : Cache?
        get() = if (!useCache) null else Cache(WidgetApplication.instance.cacheDir, 256 * 1024L) // 256k

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .connectionPool(ConnectionPool())
            .cache(cache)
            .addNetworkInterceptor { chain -> intercept(chain) }
                .build()
    }

    @Throws(IOException::class)
    @JvmOverloads
    fun getJsonObject(url: String, headers: Headers? = null): JsonObject {
        return Gson().fromJson(getString(url, headers), JsonObject::class.java)
    }

    @Throws(IOException::class)
    fun getJsonArray(url: String): JsonArray {
        return Gson().fromJson(getString(url), JsonArray::class.java)
    }

    fun getStream(url: String): InputStream = get(url).body!!.byteStream()

    private fun getString(url: String, headers: Headers? = null) = get(url, headers).body!!.string()

    private fun get(url: String, headers: Headers? = null): Response {
        var builder = Request.Builder().url(url)
        headers?.let {
            builder = builder.headers(it)
        }
        val request = builder.build()
        return client.newCall(request).execute()
    }

    @Throws(IOException::class)
    private fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("Cache-Control", "public, max-age=60")
            .build()
        val response = chain.proceed(request)
        return response.newBuilder()
            .removeHeader("Pragma")
            .header("Cache-Control", "max-age=60")
            .build()
    }
}
