package com.brentpanther.bitcoinwidget.ui.settings

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.create
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.brentpanther.bitcoinwidget.Coin
import com.brentpanther.bitcoinwidget.NightMode
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.R.string.title_preview
import com.brentpanther.bitcoinwidget.Theme
import com.brentpanther.bitcoinwidget.WidgetState
import com.brentpanther.bitcoinwidget.WidgetType
import com.brentpanther.bitcoinwidget.db.PriceType
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
    settingsViewModel: SettingsViewModel = viewModel(),
    bannersViewModel: BannersViewModel = viewModel()
) {
    settingsViewModel.loadData(widgetId)
    val widget by settingsViewModel.widgetFlow.collectAsState(null)
    val fixedSize by settingsViewModel.fixedSizeFlow.collectAsState(true)
    val navEntries by navController.visibleEntries.collectAsState()
    val activity = LocalActivity.current
    val currencies = settingsViewModel.getCurrencies()
    val exchanges = settingsViewModel.exchanges

    SettingScreenContent(
        widget = widget,
        fixedSize = fixedSize,
        currencies = currencies,
        exchanges = exchanges,
        banner = {
            WarningBanner(bannersViewModel, widget?.state)
        },
        actions = settingsViewModel,
        onSave = {
            settingsViewModel.save()
            if (navEntries.any { it.destination.route == "home" }) {
                navController.navigateUp()
            } else {
                activity?.apply {
                    val resultIntent = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreenContent(
    widget: Widget?,
    fixedSize: Boolean,
    currencies: List<String>,
    exchanges: List<Exchange>,
    actions: SettingsActions,
    onSave: () -> Unit = {},
    banner: @Composable () -> Unit = {}
) {
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
                        onSave()
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
    ) { innerPadding ->
        val modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        if (widget == null) {
            Box(contentAlignment = Alignment.Center, modifier = modifier) {
                CircularProgressIndicator(strokeWidth = 6.dp, modifier = Modifier.size(80.dp))
            }
        }
        else {
            Column(
                modifier = modifier
            ) {
                banner()
                Text(
                    text = stringResource(widget.widgetType.widgetSummary, widget.coinName()),
                    fontSize = 14.sp,
                    fontFamily = FontFamily(create("sans-serif-light", BOLD)),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .padding(horizontal = 16.dp)
                )
                SettingsHeader(
                    title = title_preview,
                    modifier = Modifier.padding(bottom = 16.dp),
                    withDivider = false
                )
                WidgetPreview(
                    widget = widget,
                    fixedSize = fixedSize,
                    modifier = Modifier.height(96.dp)
                )
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    when (widget.widgetType) {
                        WidgetType.PRICE -> PriceSettings(widget, currencies, exchanges, actions)
                        WidgetType.VALUE -> ValueSettings(widget, currencies, exchanges, actions)
                    }
                    Spacer(Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun ValueSettings(
    widget: Widget,
    currencies: List<String>,
    exchanges: List<Exchange>,
    actions: SettingsActions
) {
    DataSection(currencies, exchanges, actions, widget)
    val numberInstance = DecimalFormat.getNumberInstance().apply { maximumFractionDigits = 40 }

    SettingsHeader(title = if (widget.coin == Coin.BTC) R.string.title_wallet_bitcoin else R.string.title_wallet)

    val amountHeldValue = if (widget.amountHeld == null) "" else numberInstance.format(widget.amountHeld)
    SettingsEditText(
        icon = {
            Icon(painterResource(R.drawable.ic_outline_account_balance_wallet_24), null)
        },
        title = {
            Text(stringResource(id = R.string.title_amount_held))
        },
        subtitle = {
            Text(amountHeldValue)
        },
        dialogText = {
            Text(stringResource(R.string.dialog_amount_held, widget.coinName(), numberInstance.format(1.23)))
        },
        value = amountHeldValue,
        onChange = { value ->
            try {
                var value = value ?: "1"
                // do not assume numbers are being input in the current system locale.
                val groupingSeparator = DecimalFormatSymbols.getInstance().groupingSeparator
                val decimalSeparator = DecimalFormatSymbols.getInstance().decimalSeparator
                val parsed = if (value.contains(groupingSeparator) && value.contains(decimalSeparator)) {
                    // parse in system locale
                    numberInstance.parse(value)?.toDouble()
                } else if (value.contains(".")) {
                    // parse in english
                    DecimalFormat.getNumberInstance(Locale.ENGLISH).parse(value)?.toDouble()
                } else {
                    // parse in system locale
                    numberInstance.parse(value)?.toDouble()
                }
                parsed?.apply {
                    actions.setAmountHeld(this)
                }
            } catch (_: ParseException) {
            }

        }
    )
    if (widget.coin == Coin.BTC) {
        SettingsEditText(
            icon = {
                Icon(painterResource(R.drawable.ic_outline_account_balance_wallet_24), null)
            },
            title = {
                Text(stringResource(R.string.title_bitcoin_address))
            },
            subtitle = {
                Text(widget.address.orEmpty())
            },
            dialogText = {
                Text(stringResource(R.string.dialog_wallet_address))
            },
            value = widget.address,
            onChange = { value ->
                actions.setAddress(value)
            }
        )
    }
    FormatSection(actions, widget)
    StyleSection(actions, widget)
    DisplaySection(actions, widget)
    SettingsSwitch(
        icon = {
            Icon(painterResource(R.drawable.ic_outline_numbers_24), null)
        },
        title = {
            Text(stringResource(R.string.title_amount_label))
        },
        value = widget.showAmountLabel,
        onChange = {
            actions.setShowAmountLabel(it)
        }
    )
}

@Composable
fun PriceSettings(
    widget: Widget,
    currencies: List<String>,
    exchanges: List<Exchange>,
    actions: SettingsActions) {
    DataSection(currencies, exchanges, actions, widget)
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
            actions.setInverse(it)
        }
    )
    FormatSection(actions, widget)
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
                actions.setCoinUnit(it)
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
                actions.setCurrencyUnit(it)
            }
        )
    }
    StyleSection(actions, widget)
    DisplaySection(actions, widget)
}

@Composable
private fun DataSection(
    currencies: List<String>,
    exchanges: List<Exchange>,
    actions: SettingsActions,
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
        items = currencies,
        onChange = {
            actions.setCurrency(it)
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
        items = exchanges.map { it.exchangeName },
        itemValues = exchanges.map { it.name },
        onChange = {
            actions.setExchange(Exchange.valueOf(it))
        }
    )
    if (!widget.exchange.hasSpotPriceOnly) {
        SettingsList(
            icon = {
                Icon(painterResource(R.drawable.ic_outline_gavel_24), null)
            },
            title = {
                Text(stringResource(R.string.title_price_type))
            },
            subtitle = {
                Text(it.orEmpty())
            },
            value = widget.priceType.name,
            items = stringArrayResource(R.array.priceTypes).toList(),
            itemValues = stringArrayResource(R.array.priceTypeValues).toList(),
            onChange = {
                actions.setPriceType(PriceType.valueOf(it))
            }
        )
    }
}

@Composable
private fun FormatSection(
    actions: SettingsActions,
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
            actions.setCurrencySymbol(it)
        }
    )
}

@Composable
private fun StyleSection(
    actions: SettingsActions,
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
            actions.setTheme(Theme.valueOf(it))
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
            actions.setNightMode(NightMode.valueOf(it))
        }
    )
}

@Composable
private fun DisplaySection(
    actions: SettingsActions,
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
            actions.setNumDecimals(it.roundToInt())
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
            actions.setShowIcon(it)
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
            actions.setShowCoinLabel(it)
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
            actions.setShowExchangeLabel(it)
        }
    )
}


@Preview
@Composable
fun PriceWidgetPreview() {
    val widget = Widget(
        id = 1,
        widgetId = 1,
        widgetType = WidgetType.PRICE,
        exchange = Exchange.COINGECKO,
        coin = Coin.BTC,
        currency = "USD",
        coinCustomId = null,
        coinCustomName = null,
        currencyCustomName = null,
        showExchangeLabel = false,
        showCoinLabel = false,
        showIcon = true,
        numDecimals = -1,
        currencySymbol = null,
        theme = Theme.SOLID,
        nightMode = NightMode.SYSTEM,
        coinUnit = null,
        currencyUnit = null,
        customIcon = null,
        portraitTextSize = null,
        landscapeTextSize = 0,
        lastValue = "1234.56",
        amountHeld = null,
        showAmountLabel = false,
        useInverse = false,
        priceType = PriceType.SPOT,
        lastUpdated = 0,
        state = WidgetState.DRAFT,
        address = null
    )
    MaterialTheme {
        SettingScreenContent(
            widget = widget,
            fixedSize = false,
            currencies = emptyList(),
            exchanges = emptyList(),
            actions = object: SettingsActions {}
        )
    }
}


@Preview
@Composable
fun ValueWidgetPreview() {
    val widget = Widget(
        id = 1,
        widgetId = 1,
        widgetType = WidgetType.VALUE,
        exchange = Exchange.COINGECKO,
        coin = Coin.BTC,
        currency = "USD",
        coinCustomId = null,
        coinCustomName = null,
        currencyCustomName = null,
        showExchangeLabel = false,
        showCoinLabel = false,
        showIcon = true,
        numDecimals = -1,
        currencySymbol = null,
        theme = Theme.SOLID,
        nightMode = NightMode.SYSTEM,
        coinUnit = null,
        currencyUnit = null,
        customIcon = null,
        portraitTextSize = null,
        landscapeTextSize = 0,
        lastValue = "1234.56",
        amountHeld = null,
        showAmountLabel = false,
        useInverse = false,
        priceType = PriceType.SPOT,
        lastUpdated = 0,
        state = WidgetState.DRAFT,
        address = null
    )
    MaterialTheme {
        SettingScreenContent(
            widget = widget,
            fixedSize = false,
            currencies = emptyList(),
            exchanges = emptyList(),
            actions = object: SettingsActions {}
        )
    }
}
