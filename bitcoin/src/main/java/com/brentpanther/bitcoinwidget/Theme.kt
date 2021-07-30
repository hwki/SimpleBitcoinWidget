package com.brentpanther.bitcoinwidget

import androidx.annotation.LayoutRes

enum class Theme(@LayoutRes val layout: Int) {

    LIGHT(R.layout.widget_layout),
    DARK(R.layout.widget_layout),
    DAY_NIGHT(R.layout.widget_layout),
    TRANSPARENT(R.layout.widget_layout_transparent),
    TRANSPARENT_DARK(R.layout.widget_layout_transparent),
    TRANSPARENT_DAY_NIGHT(R.layout.widget_layout_transparent);

    fun isTransparent(): Boolean = this in arrayOf(TRANSPARENT, TRANSPARENT_DARK, TRANSPARENT_DAY_NIGHT)
}

enum class IconTheme(val fallback: Theme) {

    LIGHT(Theme.LIGHT),
    DARK(Theme.LIGHT),
    TRANSPARENT(Theme.LIGHT),
    TRANSPARENT_DARK(Theme.DARK),
    LIGHT_OLD(Theme.LIGHT),
    DARK_OLD(Theme.LIGHT),
    TRANSPARENT_OLD(Theme.LIGHT),
    TRANSPARENT_DARK_OLD(Theme.DARK)

}