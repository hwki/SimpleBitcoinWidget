package com.brentpanther.bitcoinwidget

import android.content.Context
import android.content.res.Configuration
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Theme(@LayoutRes val light: Int, @LayoutRes val dark: Int) : Parcelable {

    SOLID(R.layout.widget_solid_light, R.layout.widget_solid_dark),
    TRANSPARENT(R.layout.widget_transparent_light, R.layout.widget_transparent_dark),
    MATERIAL(R.layout.widget_material_light, R.layout.widget_material_dark);

    fun getLayout(isDark: Boolean) = if (isDark) dark else light
}

@Parcelize
data class IconTheme(val theme: Theme, @DrawableRes val light: Int,
                     @DrawableRes val dark: Int = light) : Parcelable

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

enum class WidgetState {

    CURRENT,STALE,ERROR

}

