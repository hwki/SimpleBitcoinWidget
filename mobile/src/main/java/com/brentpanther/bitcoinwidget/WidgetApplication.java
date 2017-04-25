package com.brentpanther.bitcoinwidget;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by brentpanther on 3/11/17.
 */

public class WidgetApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(new MyBroadcastReceiver(), new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED));
    }
}
