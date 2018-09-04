package com.brentpanther.bitcoinwidget;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;

class ExchangeHelper {

    private static final ConnectionSpec SPEC = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_0, TlsVersion.TLS_1_1, TlsVersion.TLS_1_2, TlsVersion.TLS_1_3)
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
            .build();

    public static String getFromBitcoinCharts(String symbol) throws IOException {
        JsonArray array = getJsonArray("https://api.bitcoincharts.com/v1/markets.json");
        for (JsonElement obj : array) {
            JsonObject o = obj.getAsJsonObject();
            if (!symbol.equals(o.get("symbol").getAsString())) continue;
            return o.get("avg").getAsString();
        }
        return null;
    }

    static JsonObject getJsonObject(String url) throws IOException {
        return getJsonObject(url, null);
    }

    static JsonObject getJsonObject(String url, Headers headers) throws IOException {
        return new Gson().fromJson(getString(url, headers), JsonObject.class);
    }

    static JsonArray getJsonArray(String url) throws IOException {
        return new Gson().fromJson(getString(url), JsonArray.class);
    }

    private static String getString(String url) throws IOException {
        return getString(url, null);
    }

    private static String getString(String url, Headers headers) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(8, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .connectionSpecs(Arrays.asList(SPEC, ConnectionSpec.CLEARTEXT))
                .hostnameVerifier((hostname, session) -> true).build();
        Request.Builder builder = new Request.Builder()
                .url(url);
        if (headers != null) {
            builder = builder.headers(headers);
        }
        Request request = builder.build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
