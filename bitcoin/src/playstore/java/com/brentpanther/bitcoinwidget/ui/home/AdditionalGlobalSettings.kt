package com.brentpanther.bitcoinwidget.ui.home

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.ui.settings.SettingsButton

@Composable
fun AdditionalSettings() {
    val context = LocalContext.current
    SettingsButton(
        icon = {
            Icon(painterResource(R.drawable.ic_outline_star_outline_24), null)
        },
        title = {
            Text(stringResource(R.string.summary_rate))
        },
        onClick = {
            try {
                val uri = "http://play.google.com/store/apps/details?id=${context.packageName}".toUri()
                ContextCompat.startActivity(context, Intent(Intent.ACTION_VIEW, uri), null)
            } catch (ignored: ActivityNotFoundException) {
            }
        }
    )
}