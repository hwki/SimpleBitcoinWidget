package com.brentpanther.bitcoinwidget.exchange

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.*
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

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
        val client = OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .connectionSpecs(listOf(SPEC, ConnectionSpec.CLEARTEXT))
            .retryOnConnectionFailure(false)
            .connectionPool(connectionPool)
            .hostnameVerifier { _, _ -> true }.build()
        var builder: Request.Builder = Request.Builder().url(url)
        headers?.let {
            builder = builder.headers(it)
        }
        val request = builder.build()
        return client.newCall(request).execute()
    }

}
