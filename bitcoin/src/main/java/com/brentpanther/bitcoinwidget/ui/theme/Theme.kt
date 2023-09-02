package com.brentpanther.bitcoinwidget.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val darkColorPalette = darkColors(
    primary = Color(0xff3a554e),
    secondary = Color(0xff843d19),
    secondaryVariant = Color(0xffba5624),
    primaryVariant = Color(0xff253638)
)

private val lightColorPalette = lightColors(
    primary = Color(0xff52796f),
    secondary = Color(0xffba5624),
    secondaryVariant = Color(0xffba5624),
    primaryVariant = Color(0xff354f52),

)

class HighlightRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = Highlight

    @Composable
    override fun rippleAlpha() = RippleAlpha(0.8f,0.8f,0.8f,0.8f)

}

@Composable
fun SimpleBitcoinWidgetTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        darkColorPalette
    } else {
        lightColorPalette
    }

    MaterialTheme(
        colors = colors,
        content = content
    )
}