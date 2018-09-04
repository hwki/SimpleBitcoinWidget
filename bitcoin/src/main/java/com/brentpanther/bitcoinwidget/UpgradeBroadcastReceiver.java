package com.brentpanther.bitcoinwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UpgradeBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        WidgetProvider.refreshWidgets(context, -1);
    }
}
