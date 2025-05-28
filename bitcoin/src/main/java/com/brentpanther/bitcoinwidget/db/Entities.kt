package com.brentpanther.bitcoinwidget.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.brentpanther.bitcoinwidget.Coin
import com.brentpanther.bitcoinwidget.NightMode
import com.brentpanther.bitcoinwidget.Theme
import com.brentpanther.bitcoinwidget.WidgetState
import com.brentpanther.bitcoinwidget.WidgetType
import com.brentpanther.bitcoinwidget.exchange.Exchange

@Entity(indices = [Index(value=["widgetId"], unique=true)])
data class Widget(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var widgetId: Int,
    var widgetType: WidgetType,
    var exchange: Exchange,
    var coin: Coin,
    var currency: String,
    var coinCustomId: String?,
    var coinCustomName: String?,
    var currencyCustomName: String?,
    var showExchangeLabel: Boolean,
    var showCoinLabel: Boolean,
    var showIcon: Boolean,
    var numDecimals: Int,
    var currencySymbol: String?,
    var theme: Theme,
    var nightMode: NightMode,
    var coinUnit: String?,
    var currencyUnit: String?,
    var customIcon: String?,
    var portraitTextSize: Int? = null,
    var landscapeTextSize: Int? = null,
    var lastValue: String? = null,
    var amountHeld: Double? = null,
    var address: String? = null,
    var showAmountLabel: Boolean,
    var useInverse: Boolean,
    var priceType: PriceType,
    var lastUpdated: Long,
    var state: WidgetState
) {

    fun coinName() = if (coinCustomId != null) coinCustomName ?: coin.coinName else coin.coinName

    fun isOld(refresh: Int) = System.currentTimeMillis() - lastUpdated > (60000 * refresh * 1.5)
}

enum class PriceType {
    SPOT, BID, ASK
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
                          val alwaysCurrent: Boolean = false)