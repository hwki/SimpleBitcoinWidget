package com.brentpanther.bitcoinwidget.ui.home

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.text.HtmlCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.ui.settings.SettingsButton
import com.brentpanther.bitcoinwidget.ui.settings.SettingsHeader
import com.brentpanther.bitcoinwidget.ui.settings.SettingsList
import com.brentpanther.bitcoinwidget.ui.settings.SettingsSwitch

@Composable
fun GlobalSettings(viewModel: ManageWidgetsViewModel = viewModel()) {
    val settings by viewModel.globalSettings.collectAsState(null)
    val context = LocalContext.current
    if (settings == null) return
    Column {
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
            value = settings?.consistentSize ?: false,
            onChange = {
                viewModel.setFixedSize(it)
            }
        )
        SettingsHeader(title = R.string.title_other)
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
            val textColor = MaterialTheme.colorScheme.onSurface
            Dialog(
                onDismissRequest = { dialogVisible = false }
            ) {
                Surface(
                    modifier = Modifier,
                    shape = MaterialTheme.shapes.medium,
                ) {
                    AndroidView(
                        modifier = Modifier.padding(8.dp),
                        factory = { context ->
                            LayoutInflater.from(context).inflate(R.layout.layout_license, null) as TextView
                        },
                        update = {
                            it.setText(
                                HtmlCompat.fromHtml(licenseString, HtmlCompat.FROM_HTML_MODE_COMPACT),
                                TextView.BufferType.SPANNABLE
                            )
                            it.movementMethod = LinkMovementMethod.getInstance()
                            it.setTextColor(textColor.toArgb())
                        }
                    )
                }
            }
        }
    }
}