package com.brentpanther.bitcoinwidget.ui.home

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.ui.settings.SettingsButton
import com.brentpanther.bitcoinwidget.ui.settings.SettingsHeader
import com.brentpanther.bitcoinwidget.ui.settings.SettingsList
import com.brentpanther.bitcoinwidget.ui.settings.SettingsSwitch

@Composable
fun GlobalSettings(viewModel: ManageWidgetsViewModel = viewModel()) {
    val settings by viewModel.globalSettings.collectAsState(null)
    if (settings == null) return
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        SettingsHeader(title = R.string.nav_title_settings, withDivider = false)
        SettingsList(
            icon = {
                Icon(painterResource(R.drawable.ic_outline_timer_24), null)
            },
            title = {
                Text(stringResource(R.string.title_refresh_interval))
            },
            subtitle = { value ->
                value?.let {
                    Text(stringResource(R.string.summary_refresh_interval, it))
                }
            },
            items = stringArrayResource(id = R.array.intervals).asList(),
            itemValues = stringArrayResource(id = R.array.intervalValues).asList(),
            value = settings?.refresh?.toString() ?: "15"
        ) {
            viewModel.setRefreshInterval(it)
        }
        SettingsSwitch(
            icon = {
                Icon(painterResource(R.drawable.ic_outline_text_fields_24), null)
            },
            title = {
                Text(stringResource(R.string.title_fixed_size))
            },
            subtitle = {
                Text(stringResource(R.string.summary_fixed_size))
            },
            value = settings?.consistentSize == true,
            onChange = {
                viewModel.setFixedSize(it)
            }
        )
        SettingsHeader(title = R.string.title_other)
        val context = LocalContext.current
        val address = stringResource(R.string.btc_address)
        val title = stringResource(R.string.label_donate)
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
                    context.startActivity(Intent(Intent.ACTION_VIEW, address.toUri()))
                } catch (_: ActivityNotFoundException) {
                    val share = Intent.createChooser(Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        clipData = ClipData.newPlainText(title, address)
                        putExtra(Intent.EXTRA_TEXT, address)
                        putExtra(Intent.EXTRA_TITLE, title)

                    }, null)
                    context.startActivity(share)
                }
            }
        )
        AdditionalSettings()
        var dialogVisible by remember { mutableStateOf(false) }
        SettingsButton(
            icon = {
                Icon(painterResource(R.drawable.ic_outline_info_24), null)
            },
            title = {
                Text(stringResource(id = R.string.title_about))
            },
            subtitle = {
                val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
                Text(stringResource(R.string.version, versionName.orEmpty()))
            },
            onClick = {
                dialogVisible = true
            }
        )
        if (dialogVisible) {
            val licenseString = stringResource(R.string.licenses)
            Dialog(
                onDismissRequest = { dialogVisible = false }
            ) {
                Surface(
                    modifier = Modifier,
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = AnnotatedString.fromHtml(licenseString),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}