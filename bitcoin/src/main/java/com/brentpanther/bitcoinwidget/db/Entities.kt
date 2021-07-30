package com.brentpanther.bitcoinwidget.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.brentpanther.bitcoinwidget.Coin
import com.brentpanther.bitcoinwidget.Exchange
import com.brentpanther.bitcoinwidget.Theme

@Entity(indices = [Index(value=["widgetId"], unique=true)])
data class Widget(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var widgetId: Int,
    var exchange: Exchange,
    var coin: Coin,
    var currency: String,
    var coinCustomName: String?,
    var currencyCustomName: String?,
    var showLabel: Boolean,
    var showIcon: Boolean,
    var showDecimals: Boolean,
    var currencySymbol: String?,
    var theme: Theme,
    var unit: String?,
    var customIcon: String?,
    var portraitTextSize: Float? = null,
    var landscapeTextSize: Float? = null,
    var lastValue: String? = null,
    var lastUpdated: Long
)

@Entity
data class Configuration(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var refresh: Int,
    var consistentSize: Boolean,
    var dataMigrationVersion: Int
)

data class SmallestWidgetSizes(var portrait: Float, val landscape: Float)

data class WidgetSettings(val widget: Widget, val config: Configuration, val sizes: SmallestWidgetSizes,
                          val refresh: Boolean = true)  {
    fun isOld() = System.currentTimeMillis() - widget.lastUpdated > (60000 * config.refresh * 1.5)
    fun shouldRefresh() = System.currentTimeMillis() - widget.lastUpdated > (60000 * config.refresh * .25)
    fun throttled() = System.currentTimeMillis() - widget.lastUpdated < 120000
}