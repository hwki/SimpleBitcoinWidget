package com.brentpanther.cryptowidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PriceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Intent i = new Intent(context, BackgroundService.class);
        i.putExtras(intent);
        context.startService(i);
    }


}
