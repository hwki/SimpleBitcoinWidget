package com.brentpanther.bitcoinwidget.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.brentpanther.bitcoinwidget.BuildConfig
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.ui.theme.Highlight

@Composable
fun WarningBanner(viewModel: BannersViewModel) {
    val context = LocalContext.current
    OnLifecycleEvent { event ->
        if (event == Lifecycle.Event.ON_RESUME) viewModel.loadBanners()
    }
    val banners = viewModel.visibleBanners
    Column(Modifier.fillMaxWidth()) {
        if ("hibernate" in banners) {
            Banner(viewModel, "hibernate", R.string.warning_hibernation, R.string.button_settings) {
                context.startActivity(
                    Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:${BuildConfig.APPLICATION_ID}"))
                )
            }
        }
        if ("data" in banners) {
            Banner(viewModel, "data", R.string.warning_data_saver, R.string.button_settings) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS,
                            Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                        )
                    )
                }
            }
        }
        if ("battery" in banners) {
            Banner(viewModel, "battery", R.string.warning_battery_saver, buttonText = null)
        }
    }
}

@Composable
fun Banner(viewModel: BannersViewModel, key: String, @StringRes text: Int,
           @StringRes buttonText: Int?, onClick: () -> Unit = {}) {
    Surface(color = Highlight) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(8.dp)) {
            Row(Modifier.fillMaxWidth()) {
                Icon(painterResource(id = R.drawable.ic_outline_info_24), null, tint = MaterialTheme.colors.secondary)
                Text(stringResource(id = text), lineHeight = 22.sp, fontSize = 16.sp, modifier = Modifier.padding(start=8.dp), color = Color.Black)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = {
                    viewModel.setDismissed(key)
                }) {
                    Text(stringResource(id = R.string.dismiss))
                }
                buttonText?.let {
                    TextButton(onClick = onClick) {
                        Text(stringResource(id = it))
                    }
                }
            }
        }
    }
}

@Composable
fun OnLifecycleEvent(onEvent: (event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            eventHandler.value(event)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}
