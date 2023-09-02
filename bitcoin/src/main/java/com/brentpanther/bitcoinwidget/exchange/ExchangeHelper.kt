package com.brentpanther.bitcoinwidget.exchange

import com.brentpanther.bitcoinwidget.WidgetApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.*
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

object ExchangeHelper {

    var useCache = true
    private val cache : Cache?
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

    val JsonElement?.asString: String?
        get() = this?.jsonPrimitive?.contentOrNull

    @Throws(IOException::class)
    @JvmOverloads
    fun getJsonObject(url: String, headers: Headers? = null) = Json.decodeFromString<JsonObject>(getString(url, headers))

    @Throws(IOException::class)
    fun getJsonArray(url: String) = Json.decodeFromString<JsonArray>(getString(url))

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
