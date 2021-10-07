package com.brentpanther.bitcoinwidget.exchange

import com.brentpanther.bitcoinwidget.WidgetApplication
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

    // should not be more than throttling control period in Widget.shouldRefresh
    private const val CACHE_MAX_AGE_SECONDS = 60

    // exchanges with a price ticker for all pairs
    private val oneTickerExchanges = mapOf(
        "BITCLUDE" to "https://api.bitclude.com/stats/ticker.json",
        "BITPANDA" to "https://api.bitpanda.com/v1/ticker",
        "COINBENE" to "https://openapi-exchange.coinbene.com/api/spot/market/summary",
        "EXMO" to "https://api.exmo.com/v1.1/ticker",
        "NDAX" to "https://core.ndax.io/v1/ticker",
        "PARIBU" to "https://www.paribu.com/ticker",
        "POCKETBITS" to "https://ticker.pocketbits.in/api/v1/ticker",
        "WYRE" to "https://api.sendwyre.com/v3/rates"
    )

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .connectionSpecs(listOf(SPEC, ConnectionSpec.CLEARTEXT))
            .retryOnConnectionFailure(false)
            .connectionPool(ConnectionPool())
            .cache(Cache(WidgetApplication.instance.cacheDir, 256 * 1024L)) // 256k
            .addNetworkInterceptor { chain -> intercept(chain) }
            .hostnameVerifier { _, _ -> true }.build()
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
        var builder: Request.Builder = Request.Builder().url(url)
        headers?.let {
            builder = builder.headers(it)
        }
        val request = builder.build()
        return client.newCall(request).execute()
    }

    @Throws(IOException::class)
    private fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val builder = CacheControl.Builder()
        if (oneTickerExchanges.containsValue(request.url.toString())) {
            builder
                .maxAge(CACHE_MAX_AGE_SECONDS, TimeUnit.SECONDS)
        } else {
            builder
                .noCache()
                .noStore()
        }
        return response.newBuilder()
            .removeHeader("Pragma")
            .removeHeader("Cache-Control")
            .addHeader("Cache-Control", builder.build().toString())
            .build()
    }
}
