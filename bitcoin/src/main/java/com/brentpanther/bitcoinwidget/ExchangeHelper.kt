package com.brentpanther.bitcoinwidget

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier

internal object ExchangeHelper {

    private val SPEC = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_3)
            .cipherSuites(
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_RC4_128_SHA,
                    CipherSuite.TLS_ECDHE_RSA_WITH_RC4_128_SHA,
                    CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA,
                    CipherSuite.TLS_DHE_DSS_WITH_AES_128_CBC_SHA,
                    CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA)
            .build()

    val connectionPool = ConnectionPool()

    @Suppress("unused")
    @Throws(IOException::class)
    fun getFromBitcoinCharts(symbol: String): String? {
        val array = getJsonArray("https://api.bitcoincharts.com/v1/markets.json")
        for (obj in array) {
            val o = obj.asJsonObject
            if (symbol != o.get("symbol").asString) continue
            return o.get("avg").asString
        }
        return null
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

    @Throws(IOException::class)
    private fun getString(url: String, headers: Headers? = null): String {
        val client = OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(8, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .connectionSpecs(listOf(SPEC, ConnectionSpec.CLEARTEXT))
                .retryOnConnectionFailure(true)
                .connectionPool(connectionPool)
                .hostnameVerifier(HostnameVerifier { _, _ -> true }).build()
        var builder: Request.Builder = Request.Builder()
                .url(url)
        if (headers != null) {
            builder = builder.headers(headers)
        }
        val request = builder.build()

        val response = client.newCall(request).execute()
        return response.body!!.string()
    }

}
