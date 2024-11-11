package com.brentpanther.bitcoinwidget.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.WidgetState

@Composable
fun WarningBanner(viewModel: BannersViewModel, widgetState: WidgetState? = null) {
    val context = LocalContext.current
    OnLifecycleEvent { event ->
        if (event == Lifecycle.Event.ON_RESUME) viewModel.loadBanners()
    }
    val banners = viewModel.visibleBanners
    Column(Modifier.fillMaxWidth()) {
        if ("data" in banners) {
            Banner(
                key = "data",
                text = stringResource(R.string.warning_data_saver),
                buttonText = stringResource(R.string.button_settings),
                onDismiss = {
                    viewModel.setDismissed("data")
                }
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS,
                            Uri.parse("package:${context.packageName}")
                        )
                    )
                }
            }
        }
        if ("battery" in banners) {
            Banner(
                key = "battery",
                text = stringResource(R.string.warning_battery_saver),
                onDismiss = {
                    viewModel.setDismissed("battery")
                }
            )
        }
        when(widgetState) {
            WidgetState.STALE -> R.string.state_stale
            WidgetState.RATE_LIMITED -> R.string.state_rate_limited
            WidgetState.ERROR -> R.string.state_error
            WidgetState.INVALID_ADDRESS -> R.string.state_invalid_address
            else -> null
        }?.let {
            Banner(key = "state", text = stringResource(it))
        }
    }
}

@Composable
fun Banner(
    key: String,
    text: String,
    buttonText: String? = null,
    onDismiss: ((String) -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth()) {
                Icon(painterResource(id = R.drawable.ic_outline_info_24), null, tint = MaterialTheme.colorScheme.secondary)
                Text(text, lineHeight = 22.sp, fontSize = 16.sp, modifier = Modifier.padding(start=8.dp), color = Color.Black)
            }
            onDismiss?.let {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(
                        onClick = {
                            onDismiss(key)
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onErrorContainer)
                    ) {
                        Text(stringResource(id = R.string.dismiss))
                    }
                    buttonText?.let {
                        TextButton(
                            onClick = onClick,
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onErrorContainer)
                        ) {
                            Text(it)
                        }
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
