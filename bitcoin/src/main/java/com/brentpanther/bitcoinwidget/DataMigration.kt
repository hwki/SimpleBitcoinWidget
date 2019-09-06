package com.brentpanther.bitcoinwidget

import android.content.Context
import androidx.preference.PreferenceManager
import com.brentpanther.bitcoinwidget.Coin.*


/**
 * One-off migrations so that existing users' widgets do not get affected by data changes.
 */
internal object DataMigration {

    private const val EXCHANGE_OVERRIDE_MIGRATION = "exchange_override"
    private const val QUOINE_MIGRATION = "quoine"
    private const val BITTREX_TO_BCH = "bittrex_to_bch"
    private const val GDAX = "gdax_to_coinbasepro"
    private const val XRB_TO_NANO = "xrb_to_nano"
    private const val FIX_BCH_UNITS = "bch_units"
    private const val BCH_TO_ABC = "bch_abc"
    private const val INDODAX = "indodax"

    fun migrate(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val hasOverrideMigration = prefs.getBoolean(EXCHANGE_OVERRIDE_MIGRATION, false)
        if (!hasOverrideMigration) {
            migrateExchangeCoinAndCurrencyNames()
            prefs.edit().putBoolean(EXCHANGE_OVERRIDE_MIGRATION, true).apply()
        }
        val hasQuoineMigration = prefs.getBoolean(QUOINE_MIGRATION, false)
        if (!hasQuoineMigration) {
            migrateQuoine()
            prefs.edit().putBoolean(QUOINE_MIGRATION, true).apply()
        }
        val hasBittrexBCHMigration = prefs.getBoolean(BITTREX_TO_BCH, false)
        if (!hasBittrexBCHMigration) {
            migrateBittrexBCH()
            prefs.edit().putBoolean(BITTREX_TO_BCH, true).apply()
        }
        val hasGDAXMigration = prefs.getBoolean(GDAX, false)
        if (!hasGDAXMigration) {
            migrateGDAX()
            prefs.edit().putBoolean(GDAX, true).apply()
        }
        val hasXRBMigration = prefs.getBoolean(XRB_TO_NANO, false)
        if (!hasXRBMigration) {
            migrateXRB()
            prefs.edit().putBoolean(XRB_TO_NANO, true).apply()
        }
        val hasBCHUnitMigration = prefs.getBoolean(FIX_BCH_UNITS, false)
        if (!hasBCHUnitMigration) {
            migrationBCHUnits()
            prefs.edit().putBoolean(FIX_BCH_UNITS, true).apply()
        }
        val hasBCHABCMigration = prefs.getBoolean(BCH_TO_ABC, false)
        if (!hasBCHABCMigration) {
            migrationBCHABC()
            prefs.edit().putBoolean(BCH_TO_ABC, true).apply()
        }
        val hasIndoDax = prefs.getBoolean(INDODAX, false)
        if (!hasIndoDax) {
            migrationIndodax()
            prefs.edit().putBoolean(INDODAX, true).apply()
        }
    }

    private fun migrationIndodax() {
        val widgetIds = WidgetApplication.instance.widgetIds
        for (widgetId in widgetIds) {
            val prefs = Prefs(widgetId)
            if ("BITCOINCOID" == prefs.exchangeName) {
                prefs.setValue("exchange", "INDODAX")
            }
        }
    }

    private fun migrationBCHABC() {
        val widgetIds = WidgetApplication.instance.widgetIds
        for (widgetId in widgetIds) {
            val prefs = Prefs(widgetId)
            if (prefs.coin !== BCH) continue
            when (prefs.getValue("exchange")) {
                "BITFINEX" -> when (prefs.coin) {
                    BCH -> prefs.setValue("coin_custom", "BAB")
                    DASH -> prefs.setValue("coin_custom", "DSH")
                    IOTA -> prefs.setValue("coin_custom", "IOT")
                    else -> {}
                }
                "COINSQUARE" -> prefs.setValue("coin_custom", "BAB")
                "BTCMARKETS" -> prefs.setValue("coin_custom", "BCHABC")
                "BIT2C" -> prefs.setValue("coin_custom", "Bchabc")
                "KOINEX" -> prefs.setValue("coin_custom", "BCHABC")
            }
        }
    }

    // wrong bch units, need to fix
    private fun migrationBCHUnits() {
        val widgetIds = WidgetApplication.instance.widgetIds
        for (widgetId in widgetIds) {
            val prefs = Prefs(widgetId)
            if (prefs.coin !== BCH) continue
            when (prefs.unit) {
                "BTC" -> prefs.setValue("units", "BCH")
                "mBTC" -> prefs.setValue("units", "mBCH")
                "μBTC" -> prefs.setValue("units", "μBCH")
            }

        }
    }

    // exchanges changed from using code xrb to nano
    private fun migrateXRB() {
        val widgetIds = WidgetApplication.instance.widgetIds
        for (widgetId in widgetIds) {
            val prefs = Prefs(widgetId)
            if ("XRB" == prefs.exchangeCoinName) {
                prefs.setValue("coin_custom", "NANO")
            }
        }
    }

    private fun migrateGDAX() {
        val widgetIds = WidgetApplication.instance.widgetIds
        for (widgetId in widgetIds) {
            val prefs = Prefs(widgetId)
            val exchange = prefs.getValue("exchange")
            if ("GDAX" == exchange) {
                prefs.setValue("exchange", "COINBASEPRO")
            }
        }
    }

    // bittrex changed from using BCC to BCH
    private fun migrateBittrexBCH() {
        val widgetIds = WidgetApplication.instance.widgetIds
        for (widgetId in widgetIds) {
            val prefs = Prefs(widgetId)
            val exchange = prefs.getValue("exchange")
            if ("BITTREX" == exchange) {
                val exchangeCoinName = prefs.exchangeCoinName
                if ("BCC" == exchangeCoinName) {
                    prefs.setValue("coin_custom", "BCH")
                }
            }
        }
    }

    // spelled quoine wrong, so migrate users who have the prior bad spelling
    private fun migrateQuoine() {
        val widgetIds = WidgetApplication.instance.widgetIds
        for (widgetId in widgetIds) {
            val prefs = Prefs(widgetId)
            val exchange = prefs.getValue("exchange")
            if ("QUIONE" == exchange) {
                prefs.setValue("exchange", Exchange.QUOINE.name)
            }
        }
    }

    // Certain exchanges have different names for coins and currencies than the rest.
    // Instead of hard coding these, we will now have these custom values populated from the JSON file,
    // and do a migration for existing users who already have widgets with these values.
    private fun migrateExchangeCoinAndCurrencyNames() {
        val widgetIds = WidgetApplication.instance.widgetIds
        for (widgetId in widgetIds) {
            val prefs = Prefs(widgetId)
            try {
                val exchange = prefs.exchange
                val coin = prefs.coin
                val currency = prefs.currency
                var coinName: String? = null
                var currencyName: String? = null
                when (exchange) {
                    Exchange.BIT2C -> {
                        if (BTC === coin) coinName = "Btc"
                        if (BCH === coin) coinName = "Bch"
                        if (LTC === coin) coinName = "Ltc"
                    }
                    Exchange.BITBAY -> if (BCH === coin) coinName = "BCC"
                    Exchange.BITFINEX -> {
                        if (DASH === coin) coinName = "dsh"
                        if (IOTA === coin) coinName = "iot"
                    }
                    Exchange.BITTREX -> {
                        if (BCH === coin) coinName = "BCC"
                        if ("USD" == currency) currencyName = "USDT"
                    }
                    Exchange.INDEPENDENT_RESERVE -> if (BTC === coin) coinName = "xbt"
                    Exchange.ITBIT -> if (BTC === coin) coinName = "XBT"
                    Exchange.KOINEX -> if (IOTA === coin) coinName = "MIOTA"
                    Exchange.KRAKEN -> if (BTC === coin) coinName = "XBT"
                    Exchange.LUNO -> if (BTC === coin) coinName = "XBT"
                    Exchange.PARIBU -> if ("TRY" == currency) currencyName = "TL"
                    Exchange.POLONIEX -> if ("USD" == currency) currencyName = "USDT"
                    else -> {}
                }
                prefs.setExchangeValues(coinName, currencyName)
            } catch (e: Exception) {
                // user's exchange no longer exists
            }

        }
    }

}
