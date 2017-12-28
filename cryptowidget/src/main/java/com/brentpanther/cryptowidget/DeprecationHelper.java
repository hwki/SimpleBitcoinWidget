package com.brentpanther.cryptowidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;


public class DeprecationHelper {

    public static void showDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle("Warning")
                .setMessage("This app is no longer being supported. You can create a new widget with the Simple Bitcoin Widget app, which now supports many different coins.")
                .setNegativeButton("Dismiss", null);
        try {
            activity.getPackageManager().getPackageInfo("com.brentpanther.bitcoinwidget", 0);
        } catch (PackageManager.NameNotFoundException e) {
            builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Uri uri = Uri.parse("market://details?id=com.brentpanther.bitcoinwidget");
                    Intent downloadIntent = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivity(downloadIntent);
                    activity.finish();
                }
            });
        }
        builder.show();
    }

}
