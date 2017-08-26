package com.brentpanther.cryptowidget;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;

/**
 * Created by brentpanther on 5/10/17.
 */

public class ExchangeHelper {

    private static ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
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

    public static String getFromBitcoinCharts(String symbol) throws Exception {
        JSONArray array = getJSONArray("http://api.bitcoincharts.com/v1/markets.json");
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            if (!symbol.equals(obj.getString("symbol"))) continue;
            return obj.getString("avg");
        }
        return null;
    }

    public static JSONObject getJSONObject(String url) throws Exception {
        return getJSONObject(url, null);
    }

    public static JSONObject getJSONObject(String url, Headers headers) throws Exception {
        return new JSONObject(getString(url, headers));
    }

    public static JSONArray getJSONArray(String url) throws Exception {
        return new JSONArray(getString(url));
    }

    public static String getString(String url) throws Exception {
        return getString(url, null);
    }

    public static String getString(String url, Headers headers) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder()
                .followRedirects(true)
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .connectionSpecs(Arrays.asList(spec, ConnectionSpec.CLEARTEXT))
                .hostnameVerifier(new HostnameVerifier() {
                    @SuppressLint("BadHostnameVerifier")
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                }).build();
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
