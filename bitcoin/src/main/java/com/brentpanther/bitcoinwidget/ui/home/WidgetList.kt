package com.brentpanther.bitcoinwidget.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.brentpanther.bitcoinwidget.R
import com.brentpanther.bitcoinwidget.db.Widget
import com.brentpanther.bitcoinwidget.ui.BannersViewModel
import com.brentpanther.bitcoinwidget.ui.WarningBanner
import com.brentpanther.bitcoinwidget.ui.WidgetPreview
import com.brentpanther.bitcoinwidget.ui.theme.HighlightRippleTheme

@Composable
fun WidgetList(
    navController: NavController,
    supportsPin: Boolean,
    viewModel: ManageWidgetsViewModel = viewModel(),
    bannersViewModel: BannersViewModel = viewModel()
) {
    val widgets = viewModel.getWidgets().collectAsState(null).value ?: return
    val settings by viewModel.globalSettings.collectAsState(null)
    val fixedSize = settings?.consistentSize ?: false
    Column {
        WarningBanner(viewModel = bannersViewModel)
        if (widgets.isEmpty()) {
            Text(
                text = stringResource(R.string.manage_widgets_empty),
                modifier = Modifier
                    .widthIn(max = 360.dp)
                    .padding(16.dp),
                lineHeight = 26.sp,
                fontSize = 18.sp
            )
            if (!supportsPin) {
                Text(
                    text = stringResource(id = R.string.manage_widgets_how_to),
                    modifier = Modifier
                        .widthIn(max = 360.dp)
                        .padding(16.dp),
                    lineHeight = 26.sp,
                    fontSize = 18.sp
                )
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(top = 8.dp, bottom = 48.dp)) {
                items(widgets, key = { it.widget.widgetId }) { item ->
                    val widget = item.widget
                    CompositionLocalProvider(LocalRippleTheme provides HighlightRippleTheme()) {
                       WidgetCard(navController, widget, fixedSize)
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun WidgetCard(
    navController: NavController,
    widget: Widget,
    fixedSize: Boolean
) {
        Card(
            onClick = {
                navController.navigate("setting/${widget.widgetId}")
            },
            modifier = Modifier
                .padding(8.dp)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = 6.dp
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                Column(
                    Modifier
                        .weight(.5f)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val coinName = widget.coinUnit ?: widget.coinCustomName ?: widget.coin.getSymbol()
                    Text(
                        stringResource(widget.widgetType.widgetName),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        stringResource(R.string.widget_list_title, coinName, widget.currency),
                        fontSize = 16.sp
                    )
                    Text(
                        widget.exchange.exchangeName,
                        fontSize = 14.sp
                    )

                }
                Box(Modifier.weight(.5f)) {
                    Image(
                        painterResource(id = R.drawable.bg), stringResource(R.string.coin_icon), contentScale = ContentScale.Crop
                    )
                    key(fixedSize) {
                        WidgetPreview(widget, fixedSize)
                    }
                }
            }
        }
}