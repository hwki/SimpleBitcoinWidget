package com.brentpanther.bitcoinwidget.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.brentpanther.bitcoinwidget.*
import com.brentpanther.bitcoinwidget.exchange.Exchange

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
    var nightMode: NightMode,
    var coinUnit: String?,
    var currencyUnit: String?,
    var customIcon: String?,
    var portraitTextSize: Int? = null,
    var landscapeTextSize: Int? = null,
    var lastValue: String? = null,
    var lastUpdated: Long,
    var state: WidgetState
) {
    fun toCoinEntry() = CoinEntry(coin.name, coinCustomName ?: coin.coinName, coin.name, coin, customIcon)

    fun isOld(refresh: Int) = System.currentTimeMillis() - lastUpdated > (60000 * refresh * 1.5)

    fun shouldRefresh(refresh: Int, manual: Boolean): Boolean {
        // if this is a manual refresh, don't pull down new data if its been less than
        // 60 seconds since last time, to avoid HTTP 429 errors
        // otherwise, refresh if its close enough to the scheduled refresh time
        val since = System.currentTimeMillis() - lastUpdated
        return if (manual) {
            since > 60000
        } else {
            since > (60000 * refresh * .25)
        }
    }
}

@Entity
data class Configuration(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var refresh: Int,
    var consistentSize: Boolean,
    var dataMigrationVersion: Int
)

data class ConfigurationWithSizes(var refresh: Int, var consistentSize: Boolean, var portrait: Int, val landscape: Int)

data class WidgetSettings(val widget: Widget, val config: ConfigurationWithSizes, val refreshPrice: Boolean = true,
                          val alwaysCurrent: Boolean = false)  {
    fun shouldRefresh() = System.currentTimeMillis() - widget.lastUpdated > (60000 * config.refresh * .25)
    fun throttled() = System.currentTimeMillis() - widget.lastUpdated < 120000

}