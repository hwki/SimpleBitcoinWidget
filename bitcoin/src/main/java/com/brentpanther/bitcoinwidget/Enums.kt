package com.brentpanther.bitcoinwidget

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes

enum class Theme(@LayoutRes val lightPrice: Int, @LayoutRes val darkPrice: Int,
                 @LayoutRes val lightValue: Int, @LayoutRes val darkValue: Int) {

    SOLID(R.layout.widget_price_solid_light, R.layout.widget_price_solid_dark,
        R.layout.widget_value_solid_light, R.layout.widget_value_solid_dark),
    TRANSPARENT(R.layout.widget_price_transparent_light, R.layout.widget_price_transparent_dark,
        R.layout.widget_value_transparent_light, R.layout.widget_value_transparent_dark),
    MATERIAL(R.layout.widget_price_material_light, R.layout.widget_price_material_dark,
        R.layout.widget_value_material_light, R.layout.widget_value_material_dark),
    TRANSPARENT_MATERIAL(R.layout.widget_price_transparent_light_material, R.layout.widget_price_transparent_dark_material,
        R.layout.widget_value_transparent_light_material, R.layout.widget_value_transparent_dark_material);

    fun getLayout(isDark: Boolean, type: WidgetType): Int {
        return when(type) {
            WidgetType.PRICE -> if (isDark) darkPrice else lightPrice
            WidgetType.VALUE -> if (isDark) darkValue else lightValue
        }
    }
}

data class IconTheme(val theme: Theme, @DrawableRes val light: Int,
                     @DrawableRes val dark: Int = light)

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
    DRAFT, CURRENT, STALE, RATE_LIMITED, ERROR
}

enum class WidgetType(@StringRes val widgetName: Int, @StringRes val widgetSummary: Int) {
    PRICE(R.string.widget_price_name, R.string.widget_price_summary),
    VALUE(R.string.widget_value_name, R.string.widget_value_summary);
}

