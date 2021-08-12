package com.brentpanther.bitcoinwidget

import android.content.Context
import android.content.res.Configuration
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Theme(@LayoutRes val layout: Int) : Parcelable {

    SOLID(R.layout.widget_layout),
    TRANSPARENT(R.layout.widget_layout_transparent);
}

@Parcelize
data class IconTheme(val theme: Theme, @DrawableRes val light: Int, @DrawableRes val lightOld: Int = light,
    @DrawableRes val dark: Int = light, @DrawableRes val darkOld: Int = lightOld) : Parcelable

enum class NightMode {
    LIGHT,
    DARK,
    SYSTEM;

    fun isDark(context: Context): Boolean {
        val isNight = (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        return this == DARK || (this == SYSTEM && isNight)
    }
}
