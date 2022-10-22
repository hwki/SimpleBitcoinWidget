package com.brentpanther.bitcoinwidget.ui.home

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.ValueWidgetProvider
import com.brentpanther.bitcoinwidget.WidgetProvider
import com.brentpanther.bitcoinwidget.ui.MainActivity

@Composable
fun HomeScreen(navController: NavController, viewModel: ManageWidgetsViewModel = viewModel()) {
    var index by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val supportsPin = remember {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                AppWidgetManager.getInstance(context).isRequestPinAppWidgetSupported
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) }
            )
        },
        bottomBar = {
            BottomNavigation {
                BottomNavigationItem(
                    selected = index == 0,
                    label = { Text(stringResource(R.string.nav_title_manage_widgets)) },
                    icon = { Icon(painterResource(id = R.drawable.ic_outline_widgets_24), null) },
                    onClick = { index = 0 }
                )
                BottomNavigationItem(
                    selected = index == 1,
                    label = { Text(stringResource(R.string.nav_title_settings)) },
                    icon = { Icon(painterResource(id = R.drawable.ic_outline_settings_24), null) },
                    onClick = { index = 1 }
                )
            }
        },
        floatingActionButton = {
            if (index == 0 && supportsPin) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    PinWidgetFAB()
                }
            }
        },
    ) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            when (index) {
                0 -> WidgetList(navController, supportsPin, viewModel)
                1 -> GlobalSettings(viewModel)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun <T : WidgetProvider> pinWidget(context: Context, className: Class<T>) {
    val myProvider = ComponentName(context, className)
    val intent = Intent(context.applicationContext, MainActivity::class.java)
    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }
    val pendingIntent = PendingIntent.getActivity(context.applicationContext, 123, intent, flags)
    AppWidgetManager.getInstance(context).requestPinAppWidget(myProvider, null, pendingIntent)
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PinWidgetFAB() {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(tween()),
            exit = fadeOut(tween()),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(
                    elevation = 2.dp,
                ) {
                    Text(
                        stringResource(R.string.widget_value_name),
                        Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                FloatingActionButton(
                    onClick = {
                        expanded = false
                        pinWidget(context, ValueWidgetProvider::class.java)
                    },
                    modifier = Modifier
                        .padding(4.dp)
                        .size(46.dp)
                        .animateEnterExit(enter = scaleIn(), exit = scaleOut())
                ) {
                    Icon(painterResource(R.drawable.ic_outline_attach_money_24), stringResource(R.string.add_value_widget))
                }
            }
        }
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(tween()),
            exit = fadeOut(tween())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(
                    elevation = 2.dp,
                ) {
                    Text(
                        stringResource(R.string.widget_price_name),
                        Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                FloatingActionButton(
                    onClick = {
                        expanded = false
                        pinWidget(context, WidgetProvider::class.java)
                    },
                    modifier = Modifier
                        .padding(4.dp)
                        .size(46.dp)
                        .animateEnterExit(enter = scaleIn(), exit = scaleOut())
                ) {
                    Icon(painterResource(R.drawable.ic_bitcoin), stringResource(R.string.add_price_widget))
                }
            }
        }
        val rotationAngle by animateFloatAsState(
            targetValue = if (expanded) 90f else 0f
        )
        FloatingActionButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.rotate(rotationAngle)
        ) {
            Icon(painterResource(R.drawable.ic_outline_add_24), stringResource(R.string.add_widget))
        }
    }
}
