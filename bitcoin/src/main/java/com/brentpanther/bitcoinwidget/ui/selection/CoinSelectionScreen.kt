package com.brentpanther.bitcoinwidget.ui.selection

import android.app.Activity
import android.view.KeyEvent.KEYCODE_ENTER
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.brentpanther.bitcoinwidget.Coin
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.Theme
import com.brentpanther.bitcoinwidget.ui.theme.HighlightRippleTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CoinSelectionScreen(
    navController: NavController, widgetId: Int,
    viewModel: CoinSelectionViewModel = viewModel()
) {
    var searchText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    BackHandler {
        viewModel.removeWidget(context, widgetId)
        if (navController.previousBackStackEntry == null) {
            (context as Activity).finish()
        } else {
            navController.navigateUp()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.title_coin_select))
                }
            )
        }
    ) { paddingValues ->
        val coinResult by viewModel.coins.collectAsState(null)
        Column {
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                },
                placeholder = {
                    Text(
                        stringResource(R.string.coin_search_hint)
                    )
                },
                trailingIcon = {
                    IconButton(
                        enabled = searchText.length > 1,
                        onClick = {
                            viewModel.search(searchText)
                            keyboardController?.hide()
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_search_24),
                            contentDescription = null
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions {
                    viewModel.search(searchText)
                    keyboardController?.hide()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .onKeyEvent {
                        if (it.nativeKeyEvent.keyCode == KEYCODE_ENTER) {
                            viewModel.search(searchText)
                            keyboardController?.hide()
                            return@onKeyEvent true
                        }
                        false
                    }
            )
            Divider()
            val result = coinResult
            when {
                result == null -> {}
                result.loading -> {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(strokeWidth = 6.dp, modifier = Modifier.size(80.dp))
                    }
                }
                result.coins.isEmpty() -> {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text("No coins found.")
                    }
                }
                else -> {
                    CoinList(result.coins, paddingValues, onClick = {
                        coroutineScope.launch {
                            viewModel.createWidget(context, widgetId, it)
                            navController.navigate("setting/$widgetId")
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun CoinList(coins: List<CoinResponse>, paddingValues: PaddingValues, onClick: (CoinResponse) -> Unit) {
    val isNightMode = isSystemInDarkTheme()
    val iconModifier = Modifier.padding(start = 4.dp, end = 8.dp).size(28.dp)
    val customStartIndex = coins.indexOfFirst { it.coin == Coin.CUSTOM }
    Column {
        LazyColumn(
            Modifier
                .padding(paddingValues)
                .weight(1f, fill = true)
        ) {
            if (customStartIndex != 0) {
                item(contentType = "header") {
                    Text(
                        stringResource(R.string.coin_search_popular),
                        color = MaterialTheme.colors.secondary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                val popularCoins = if (customStartIndex == -1) coins else coins.subList(0, customStartIndex)
                items(items = popularCoins, key = { it.id }) { item ->
                    CoinRow(onClick, item, iconModifier, isNightMode)
                }
            }
            if (customStartIndex > -1) {
                item(contentType = "header") {
                    Text(
                        stringResource(R.string.coin_search_more),
                        color = MaterialTheme.colors.secondary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                items(items = coins.subList(customStartIndex, coins.size), key = { it.id }) { item ->
                    CoinRow(onClick, item, iconModifier, isNightMode)
                }
            }
        }
        Divider()
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .padding(8.dp)
        ) {
            Text(stringResource(R.string.coin_search_powered_by), fontSize = 14.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painterResource(id = R.drawable.coingecko_logo),
                null
            )
        }
    }
}

@Composable
private fun CoinRow(
    onClick: (CoinResponse) -> Unit,
    item: CoinResponse,
    modifier: Modifier,
    isNightMode: Boolean
) {
    CompositionLocalProvider(LocalRippleTheme provides HighlightRippleTheme()) {
        Row(
            Modifier
                .clickable {
                    onClick(item)
                }
                .fillMaxWidth()
                .height(50.dp)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item.coin == Coin.CUSTOM) {
                AsyncImage(
                    model = item.thumb,
                    contentDescription = null,
                    modifier = modifier,
                    placeholder = painterResource(id = R.drawable.ic_baseline_circle_24)
                )
            } else {
                Image(
                    painterResource(item.coin.getIcon(Theme.SOLID, isNightMode)),
                    contentDescription = null,
                    modifier = modifier
                )
            }
            Text(
                item.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Text(" - ${item.symbol}", fontSize = 16.sp)
        }
    }
}
