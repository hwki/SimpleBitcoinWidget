package com.brentpanther.bitcoinwidget.ui.settings

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Typeface
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.brentpanther.bitcoinwidget.Coin
import com.brentpanther.bitcoinwidget.NightMode
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.Theme
import com.brentpanther.bitcoinwidget.WidgetApplication
import com.brentpanther.bitcoinwidget.WidgetState
import com.brentpanther.bitcoinwidget.WidgetType
import com.brentpanther.bitcoinwidget.db.ConfigurationWithSizes
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.exchange.Exchange
import com.brentpanther.bitcoinwidget.ui.BannersViewModel
import com.brentpanther.bitcoinwidget.ui.WarningBanner
import com.brentpanther.bitcoinwidget.ui.WidgetPreview
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    navController: NavController, widgetId: Int,
    viewModel: SettingsViewModel = viewModel(),
    bannersViewModel: BannersViewModel = viewModel()
) {
    BaseSettingsScreen(navController, viewModel, bannersViewModel, widgetId) {
        when (WidgetApplication.instance.getWidgetType(widgetId)) {
            WidgetType.PRICE -> PriceSettings(it, viewModel)
            WidgetType.VALUE -> ValueSettings(it, viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseSettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel,
    bannersViewModel: BannersViewModel,
    widgetId: Int, content: @Composable (Widget) -> Unit
) {
    viewModel.loadData(widgetId)
    val widgetState by viewModel.widgetFlow.collectAsState(null)
    val widget = widgetState
    val config by viewModel.configFlow.collectAsState(
        ConfigurationWithSizes(15, false, 0, 0)
    )
    val context = LocalContext.current
    val navEntries by navController.visibleEntries.collectAsState()
    val fromHome = remember(Unit) {
        navEntries.any { it.destination.route == "home" }
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    when (widget?.state) {
                        null -> {}
                        WidgetState.DRAFT -> Text(stringResource(R.string.new_widget, widget.coinName()))
                        else -> Text(stringResource(R.string.edit_widget, widget.coinName()))
                    }
                }
            )
        },
        floatingActionButton = {
            widget?.let {
                ExtendedFloatingActionButton(
                    onClick = {
                        viewModel.save()
                        if (fromHome) {
                            navController.navigateUp()
                        } else {
                            (context as Activity).apply {
                                val resultIntent = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                                setResult(Activity.RESULT_OK, resultIntent)
                                finish()
                            }
                        }
                    },
                    icon = {
                        Icon(painterResource(R.drawable.ic_outline_check_24), null)
                    },
                    text = {
                        when (it.state) {
                            WidgetState.DRAFT -> Text(
                                stringResource(R.string.settings_create).uppercase()
                            )
                            else -> Text(
                                stringResource(R.string.settings_update).uppercase()
                            )
                        }
                    }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        when (widget) {
            null -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(strokeWidth = 6.dp, modifier = Modifier.size(80.dp))
                }
            }
            else -> {
                Column(
                    Modifier
                        .padding(paddingValues)
                        .fillMaxWidth()
                ) {
                    if (widget.state != WidgetState.DRAFT) {
                        WarningBanner(bannersViewModel)
                    }
                    Text(
                        text = stringResource(widget.widgetType.widgetSummary, widget.coinName()),
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Typeface.create("sans-serif-light", Typeface.BOLD)),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .padding(horizontal = 16.dp)
                    )
                    SettingsHeader(
                        title = R.string.title_preview,
                        modifier = Modifier.padding(bottom = 16.dp),
                        withDivider = false
                    )
                    WidgetPreview(
                        widget = widget,
                        fixedSize = config.consistentSize,
                        modifier = Modifier.height(96.dp)
                    )
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        content(widget)
                        Spacer(Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun ValueSettings(widget: Widget, settingsPriceViewModel: SettingsViewModel) {
    DataSection(settingsPriceViewModel, widget)
    val numberInstance = DecimalFormat.getNumberInstance()
    numberInstance.maximumFractionDigits = 40
    SettingsEditText(
        icon = {
            Icon(painterResource(R.drawable.ic_outline_account_balance_wallet_24), null)
        },
        title = {
            Text(stringResource(id = R.string.title_amount_held))
        },
        subtitle = {
            Text(numberInstance.format(widget.amountHeld))
        },
        dialogText = {
            Text(stringResource(R.string.dialog_amount_held, widget.coinName(), numberInstance.format(1.23)))
        },
        value = numberInstance.format(widget.amountHeld),
        onChange = {
            try {
                // do not assume numbers are being input in the current system locale.
                val groupingSeparator = DecimalFormatSymbols.getInstance().groupingSeparator
                val decimalSeparator = DecimalFormatSymbols.getInstance().decimalSeparator
                val parsed = if (it.contains(groupingSeparator) && it.contains(decimalSeparator)) {
                    // parse in system locale
                    numberInstance.parse(it)?.toDouble()
                } else if (it.contains(".")) {
                    // parse in english
                    DecimalFormat.getNumberInstance(Locale.ENGLISH).parse(it)?.toDouble()
                } else {
                    // parse in system locale
                    numberInstance.parse(it)?.toDouble()
                }
                parsed?.apply {
                    settingsPriceViewModel.setAmountHeld(this)
                }
            } catch (_: ParseException) {
            }

        }
    )
    FormatSection(settingsPriceViewModel, widget)
    StyleSection(settingsPriceViewModel, widget)
    DisplaySection(settingsPriceViewModel, widget)
    SettingsSwitch(
        icon = {
            Icon(painterResource(R.drawable.ic_outline_numbers_24), null)
        },
        title = {
            Text(stringResource(R.string.title_amount_label))
        },
        value = widget.showAmountLabel,
        onChange = {
            settingsPriceViewModel.setShowAmountLabel(it)
        }
    )
}

@Composable
fun PriceSettings(widget: Widget, settingsPriceViewModel: SettingsViewModel) {
    DataSection(settingsPriceViewModel, widget)
    SettingsSwitch(
        icon = {
            Icon(painterResource(R.drawable.ic_outline_change_24), null)
        },
        title = {
            Text(stringResource(R.string.title_inverse))
        },
        subtitle = {
            if (widget.useInverse) {
                Text(stringResource(R.string.summary_inverse, widget.currency, widget.coinName()))
            } else {
                Text(stringResource(R.string.summary_inverse, widget.coinName(), widget.currency))
            }
        },
        value = widget.useInverse,
        onChange = {
            settingsPriceViewModel.setInverse(it)
        }
    )
    FormatSection(settingsPriceViewModel, widget)
    if (widget.coinUnit != null) {
        SettingsList(
            icon = {
                Icon(painterResource(R.drawable.ic_decimal_comma), null)
            },
            title = {
                Text(stringResource(R.string.title_units))
            },
            subtitle = { value ->
                Text(stringResource(R.string.summary_units, widget.coinName(), value ?: widget.coinName()))
            },
            value = widget.coinUnit,
            items = widget.coin.getUnits().map { it.text },
            onChange = {
                settingsPriceViewModel.setCoinUnit(it)
            }
        )
    }
    if (widget.currency in Coin.COIN_NAMES) {
        val currencyUnits = Coin.valueOf(widget.currency).getUnits()
        SettingsList(
            icon = {
                Icon(painterResource(R.drawable.ic_decimal_comma), null)
            },
            title = {
                Text(stringResource(R.string.title_units))
            },
            subtitle = { value ->
                Text(stringResource(R.string.summary_units, widget.currency, value ?: widget.currency))
            },
            value = widget.currencyUnit ?: widget.currency,
            items = currencyUnits.map { it.text },
            onChange = {
                settingsPriceViewModel.setCurrencyUnit(it)
            }
        )
    }
    StyleSection(settingsPriceViewModel, widget)
    DisplaySection(settingsPriceViewModel, widget)
}

@Composable
private fun DataSection(
    settingsPriceViewModel: SettingsViewModel,
    widget: Widget
) {
    SettingsHeader(title = R.string.title_data)
    SettingsList(
        icon = {
            Icon(painterResource(R.drawable.ic_outline_local_atm_24), null)
        },
        title = {
            Text(stringResource(R.string.title_currency))
        },
        subtitle = {
            Text(stringResource(R.string.summary_currency, widget.currency))
        },
        value = widget.currency,
        items = settingsPriceViewModel.getCurrencies(),
        onChange = {
            settingsPriceViewModel.setCurrency(it)
        }
    )
    SettingsList(
        icon = {
            Icon(painterResource(R.drawable.ic_outline_account_balance_24), null)
        },
        title = {
            Text(stringResource(R.string.title_exchange))
        },
        subtitle = {
            Text(stringResource(R.string.summary_exchange, widget.exchange.exchangeName))
        },
        value = widget.exchange.toString(),
        items = settingsPriceViewModel.exchanges.map { it.exchangeName },
        itemValues = settingsPriceViewModel.exchanges.map { it.name },
        onChange = {
            settingsPriceViewModel.setExchange(Exchange.valueOf(it))
        }
    )
}

@Composable
private fun FormatSection(
    settingsPriceViewModel: SettingsViewModel,
    widget: Widget
) {
    SettingsHeader(
        title = R.string.title_format
    )
    val value = when (widget.currencySymbol) {
        null -> "ISO"
        "none" -> "NONE"
        else -> "LOCAL"
    }
    SettingsList(
        icon = {
            Icon(painterResource(R.drawable.ic_outline_attach_money_24), null)
        },
        title = {
            Text(stringResource(R.string.title_currency_symbol))
        },
        subtitle = {
            Text(it ?: "")
        },
        value = value,
        items = stringArrayResource(R.array.symbols).toList(),
        itemValues = stringArrayResource(R.array.symbolValues).toList(),
        onChange = {
            settingsPriceViewModel.setCurrencySymbol(it)
        }
    )
}

@Composable
private fun StyleSection(
    settingsPriceViewModel: SettingsViewModel,
    widget: Widget
) {
    SettingsHeader(title = R.string.title_style)
    SettingsList(
        icon = {
            Icon(painterResource(R.drawable.ic_outline_color_lens_24), null)
        },
        title = {
            Text(stringResource(R.string.title_theme))
        },
        subtitle = {
            Text(it ?: "")
        },
        value = widget.theme.name,
        items = stringArrayResource(R.array.themes).toList(),
        itemValues = stringArrayResource(R.array.themeValues).toList(),
        onChange = {
            settingsPriceViewModel.setTheme(Theme.valueOf(it))
        }
    )
    SettingsList(
        icon = {
            Icon(painterResource(R.drawable.ic_outline_nightlight_24), null)
        },
        title = {
            Text(stringResource(R.string.title_night_mode))
        },
        subtitle = {
            Text(it ?: "")
        },
        value = widget.nightMode.name,
        items = stringArrayResource(R.array.nightModes).toList(),
        itemValues = stringArrayResource(R.array.nightModeValues).toList(),
        onChange = {
            settingsPriceViewModel.setNightMode(NightMode.valueOf(it))
        }
    )
}

@Composable
private fun DisplaySection(
    settingsViewModel: SettingsViewModel,
    widget: Widget
) {
    SettingsHeader(title = R.string.title_display)
    var numDecimals by remember { mutableFloatStateOf(widget.numDecimals.toFloat()) }
    SettingsSlider(
        icon = {
            Icon(painterResource(R.drawable.ic_decimal), null)
        },
        title = {
            Text(stringResource(R.string.title_decimals))
        },
        subtitle = {
            val value = if (widget.numDecimals == -1) {
                stringResource(R.string.summary_decimals_auto)
            } else {
                widget.numDecimals.toString()
            }
            Text(value)
        },
        value = numDecimals,
        range = -1..10,
        onChange = {
            numDecimals = it
            settingsViewModel.setNumDecimals(it.roundToInt())
        }
    )
    SettingsSwitch(
        icon = {
            Icon(painterResource(R.drawable.ic_bitcoin), null)
        },
        title = {
            Text(stringResource(R.string.title_icon))
        },
        value = widget.showIcon,
        onChange = {
            settingsViewModel.setShowIcon(it)
        }
    )
    SettingsSwitch(
        icon = {
            Icon(painterResource(R.drawable.ic_outline_label_24), null)
        },
        title = {
            Text(stringResource(R.string.title_coin_label))
        },
        value = widget.showCoinLabel,
        onChange = {
            settingsViewModel.setShowCoinLabel(it)
        }
    )
    SettingsSwitch(
        icon = {
            Icon(painterResource(R.drawable.ic_outline_label_24), null)
        },
        title = {
            Text(stringResource(R.string.title_exchange_label))
        },
        value = widget.showExchangeLabel,
        onChange = {
            settingsViewModel.setShowExchangeLabel(it)
        }
    )
}

