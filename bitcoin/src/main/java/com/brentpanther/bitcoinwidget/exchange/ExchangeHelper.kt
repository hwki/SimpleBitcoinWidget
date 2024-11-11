package com.brentpanther.bitcoinwidget.exchange

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.*
import java.io.IOException
import java.io.InputStream

object ExchangeHelper {

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    val JsonElement?.asString: String?
        get() = this?.jsonPrimitive?.contentOrNull

    @Throws(IOException::class)
    @JvmOverloads
    fun getJsonObject(url: String, headers: Headers? = null) = Json.decodeFromString<JsonObject>(getString(url, headers))

    @Throws(IOException::class)
    fun getJsonArray(url: String) = Json.decodeFromString<JsonArray>(getString(url))

    fun getStream(url: String): InputStream? = get(url)?.body?.byteStream()

    fun getString(url: String, headers: Headers? = null) = get(url, headers)?.body?.string() ?: ""

    private fun get(url: String, headers: Headers? = null): Response? {
        return try {
            var builder = Request.Builder().url(url)
            headers?.let {
                builder = builder.headers(it)
            }
            builder = builder.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:130.0) Gecko/20100101 Firefox/130.0")
            val request = builder.build()
            val response = client.newCall(request).execute()
            if (response.code == 429) {
                throw RateLimitedException()
            }
            return response
        } catch (_: IllegalArgumentException) {
            null
        }
    }

}
