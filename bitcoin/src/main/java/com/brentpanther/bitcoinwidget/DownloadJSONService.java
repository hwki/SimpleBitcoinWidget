package com.brentpanther.bitcoinwidget;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DownloadJSONService extends IntentService {

    private static final String LAST_MODIFIED = "last_modified";
    public static final String CURRENCY_FILE_NAME = "coins.json";
    public static final String JSON_DOWNLOADED_ACTION = "json-downloaded";
    private static final String TAG = DownloadJSONService.class.getSimpleName();
    static boolean downloaded = false;

    public DownloadJSONService() {
        super("Download JSON");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            downloadJSON();
        } catch (IOException e) {
            Log.e(TAG, "Error downloading JSON.", e);
        }
        downloaded = true;
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(JSON_DOWNLOADED_ACTION));
    }

    private void downloadJSON() throws IOException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String lastModified = prefs.getString(LAST_MODIFIED, getString(R.string.json_last_modified));
        String url = getString(R.string.json_url);
        OkHttpClient client = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .addHeader("If-Modified-Since", lastModified)
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        switch (response.code()) {
            case 304:
                Log.d(TAG, "No changes found in JSON file.");
                break;
            default:
                Log.d(TAG, "Retrieved status code: " + response.code());
                break;
            case 200:
                Log.d(TAG, "Updated JSON file found.");
                prefs.edit().putString(LAST_MODIFIED, response.header("Last-Modified")).apply();
                byte[] json = response.body().bytes();
                FileOutputStream os = openFileOutput(CURRENCY_FILE_NAME, MODE_PRIVATE);
                os.write(json);
                os.close();
        }
    }

}
