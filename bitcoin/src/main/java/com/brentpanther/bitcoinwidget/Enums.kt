package com.brentpanther.bitcoinwidget

import android.content.Context
import android.content.res.Configuration
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.brentpanther.bitcoinwidget.ui.settings.SettingsFragment
import com.brentpanther.bitcoinwidget.ui.settings.SettingsPriceFragment
import com.brentpanther.bitcoinwidget.ui.settings.SettingsValueFragment
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Theme(@LayoutRes val lightPrice: Int, @LayoutRes val darkPrice: Int,
                 @LayoutRes val lightValue: Int, @LayoutRes val darkValue: Int) : Parcelable {

    SOLID(R.layout.widget_price_solid_light, R.layout.widget_price_solid_dark,
        R.layout.widget_value_solid_light, R.layout.widget_value_solid_dark),
    TRANSPARENT(R.layout.widget_price_transparent_light, R.layout.widget_price_transparent_dark,
        R.layout.widget_value_transparent_light, R.layout.widget_value_transparent_dark),
    MATERIAL(R.layout.widget_price_material_light, R.layout.widget_price_material_dark,
        R.layout.widget_value_material_light, R.layout.widget_value_material_dark);

    fun getLayout(isDark: Boolean, type: WidgetType): Int {
        return when(type) {
            WidgetType.PRICE -> if (isDark) darkPrice else lightPrice
            WidgetType.VALUE -> if (isDark) darkValue else lightValue
        }
    }
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
    CURRENT, STALE, ERROR
}

enum class WidgetType(@StringRes val widgetName: Int, @StringRes val widgetSummary: Int) {
    PRICE(R.string.widget_price_name, R.string.widget_price_summary),
    VALUE(R.string.widget_value_name, R.string.widget_value_summary);

    fun getSettingsFragment(): SettingsFragment = when(this) {
        PRICE -> SettingsPriceFragment()
        VALUE -> SettingsValueFragment()
    }
}

