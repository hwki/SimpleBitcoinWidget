package com.brentpanther.bitcoinwidget.ui.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.ui.settings.SettingsButton

@Composable
fun AdditionalSettings() {
    val context = LocalContext.current
    val address = stringResource(R.string.btc_address).toUri()
    val donateError = stringResource(R.string.error_donate)
    SettingsButton(
        icon = {
            Icon(painterResource(R.drawable.ic_bitcoin), null)
        },
        title = {
            Text(stringResource(id = R.string.title_donate))
        },
        subtitle = {
            Text(stringResource(id = R.string.summary_donate))
        },
        onClick = {
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, address))
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(context, donateError, Toast.LENGTH_SHORT).show()
            }
        }
    )
}