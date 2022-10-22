package com.brentpanther.bitcoinwidget.ui.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.brentpanther.bitcoinwidget.ui.theme.HighlightRippleTheme
import java.lang.Integer.max
import java.util.*

@Composable
fun Setting(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit = {},
    title: @Composable () -> Unit,
    subtitle: @Composable (() -> Unit)? = null,
    content: @Composable ((BoxScope).() -> Unit) = {}
) {
    CompositionLocalProvider(LocalRippleTheme provides HighlightRippleTheme()) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .heightIn(min = 72.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(24.dp)
            ) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    icon()
                }
            }
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f, true)
                    .padding(start = 16.dp)
            ) {
                ProvideTextStyle(value = MaterialTheme.typography.subtitle1) {
                    title()
                }
                if (subtitle != null) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        ProvideTextStyle(value = MaterialTheme.typography.body2) {
                            subtitle()
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingsHeader(@StringRes title: Int, modifier: Modifier = Modifier, withDivider: Boolean = true) {
    Surface {
        if (withDivider) {
            Divider()
        }
        Row(
            modifier
                .fillMaxWidth()
                .height(36.dp)
                .padding(top = 16.dp, start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                ProvideTextStyle(value = MaterialTheme.typography.subtitle2.copy(color = MaterialTheme.colors.secondary)) {
                    Text(stringResource(id = title), Modifier.padding(start = 56.dp))
                }
            }
        }
    }

}

@Composable
fun SettingsSwitch(
    icon: @Composable () -> Unit = {},
    title: @Composable () -> Unit,
    subtitle: (@Composable () -> Unit)? = null,
    value: Boolean = false,
    onChange: (Boolean) -> Unit
) {
    Setting(
        modifier = Modifier.toggleable(
            value = value,
            role = Role.Switch,
            onValueChange = onChange
        ),
        icon = icon,
        title = title,
        subtitle = subtitle
    ) {
        Switch(
            checked = value,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = Color(0xffdddddd)
            )
        )
    }
}

@Composable
fun SettingsEditText(
    icon: @Composable () -> Unit = {},
    title: @Composable () -> Unit,
    subtitle: (@Composable () -> Unit)? = null,
    dialogText: (@Composable () -> Unit)? = null,
    value: String? = null,
    onChange: (String) -> Unit
) {
    var dialogVisible by remember { mutableStateOf(false) }
    Setting(
        modifier = Modifier.clickable {
            dialogVisible = true
        },
        icon = icon,
        title = title,
        subtitle = subtitle
    ) {
        var tempValue by remember { mutableStateOf(value) }
        if (dialogVisible) {
            Dialog(
                onDismissRequest = {
                    dialogVisible = false
                }
            ) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Column(
                        Modifier.padding(start = 20.dp, top = 20.dp)
                    )
                    {
                        Row(
                            Modifier.padding(bottom = 12.dp)
                        ) {
                            ProvideTextStyle(value = MaterialTheme.typography.h6) {
                                title()
                            }
                        }
                        Row(
                            Modifier.padding(bottom = 8.dp)
                        ) {
                            dialogText?.invoke()
                        }
                        OutlinedTextField(
                            value = tempValue ?: "",
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions {
                                onChange(tempValue ?: "1")
                                dialogVisible = false
                            },
                            singleLine = true,
                            onValueChange = {
                                tempValue = it
                            }
                        )
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    onChange(tempValue ?: "1")
                                    dialogVisible = false
                                }
                            ) {
                                Text(
                                    stringResource(android.R.string.ok).uppercase(Locale.getDefault())
                                )
                            }
                            TextButton(
                                onClick = {
                                    dialogVisible = false
                                }
                            ) {
                                Text(
                                    stringResource(android.R.string.cancel).uppercase(Locale.getDefault())
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsList(
    icon: @Composable () -> Unit = {},
    title: @Composable () -> Unit,
    subtitle: (@Composable (String?) -> Unit)? = null,
    items: List<String>,
    itemValues: List<String> = items,
    value: String? = null,
    onChange: (String) -> Unit
) {
    var dialogVisible by remember { mutableStateOf(false) }
    val currentIndex = itemValues.indexOf(value)
    SettingsButton(
        icon = icon,
        title = title,
        subtitle = {
            val subtitleValue = if (currentIndex > -1) items[currentIndex] else null
            subtitle?.invoke(subtitleValue)
        },
        onClick = {
            dialogVisible = true
        }
    )
    if (dialogVisible) {
        Dialog(
            onDismissRequest = {
                dialogVisible = false
            }
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Column {
                    Row(
                        Modifier.padding(start = 20.dp, top = 20.dp, bottom = 12.dp)
                    ) {
                        ProvideTextStyle(value = MaterialTheme.typography.h6) {
                            title()
                        }
                    }
                    val state = rememberLazyListState(max(0, currentIndex))
                    LazyColumn(state = state, modifier = Modifier.weight(1f, false)) {
                        itemsIndexed(items) { index, item ->
                            RadioDialogItem(
                                item = item,
                                selected = index == currentIndex,
                                onClick = {
                                    onChange(itemValues[index])
                                    dialogVisible = false
                                }
                            )
                        }
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                dialogVisible = false
                            }
                        ) {
                            Text(
                                stringResource(android.R.string.cancel).uppercase(Locale.getDefault())
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsButton(
    icon: @Composable () -> Unit = {},
    title: @Composable () -> Unit,
    subtitle: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Setting(
        modifier = Modifier.clickable(onClick = onClick),
        icon = icon,
        title = title,
        subtitle = subtitle
    )
}

@Composable
fun SettingsSlider(
    icon: @Composable () -> Unit = {},
    title: @Composable () -> Unit,
    subtitle: (@Composable () -> Unit)? = null,
    range: IntRange,
    value: Int,
    onChange: (Int) -> Unit
) {
    Column {
        Setting(
            icon = icon,
            title = title,
            subtitle = subtitle
        )
        Slider(
            value.toFloat(),
            onValueChange = {
                onChange(it.toInt())
            },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(start = 48.dp),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.secondaryVariant,
                activeTrackColor = MaterialTheme.colors.secondaryVariant
            )
        )
    }
}

@Composable
private fun RadioDialogItem(
    item: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    CompositionLocalProvider(LocalRippleTheme provides HighlightRippleTheme()) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .selectable(
                    selected = selected,
                    onClick = onClick,
                    role = Role.RadioButton
                )
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = null
            )
            Text(
                text = item,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}