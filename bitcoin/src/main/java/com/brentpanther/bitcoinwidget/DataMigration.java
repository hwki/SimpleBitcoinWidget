package com.brentpanther.bitcoinwidget;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static com.brentpanther.bitcoinwidget.Coin.BCH;
import static com.brentpanther.bitcoinwidget.Coin.BTC;
import static com.brentpanther.bitcoinwidget.Coin.DASH;
import static com.brentpanther.bitcoinwidget.Coin.IOTA;
import static com.brentpanther.bitcoinwidget.Coin.LTC;


/**
 * One-off migrations so that existing users' widgets do not get affected by data changes.
 */
class DataMigration {

    private static final String EXCHANGE_OVERRIDE_MIGRATION = "exchange_override";
    private static final String QUOINE_MIGRATION = "quoine";
    private static final String COINMARKETCAP_MIGRATION = "coinmarketcap";
    private static final String BITTREX_TO_BCH = "bittrex_to_bch";
    private static final String GDAX = "gdax_to_coinbasepro";
    private static final String XRB_TO_NANO = "xrb_to_nano";
    private static final String FIX_BCH_UNITS = "bch_units";
    private static final String BCH_TO_ABC = "bch_abc";
    private static final String INDODAX = "indodax";

    static void migrate(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean hasOverrideMigration = prefs.getBoolean(EXCHANGE_OVERRIDE_MIGRATION, false);
        if (!hasOverrideMigration) {
            migrateExchangeCoinAndCurrencyNames();
            prefs.edit().putBoolean(EXCHANGE_OVERRIDE_MIGRATION, true).apply();
        }
        boolean hasQuoineMigration = prefs.getBoolean(QUOINE_MIGRATION, false);
        if (!hasQuoineMigration) {
            migrateQuoine();
            prefs.edit().putBoolean(QUOINE_MIGRATION, true).apply();
        }
        boolean hasCoinMarketCapMigration = prefs.getBoolean(COINMARKETCAP_MIGRATION, false);
        if (!hasCoinMarketCapMigration) {
            migrateCoinMarketCapV2();
            prefs.edit().putBoolean(COINMARKETCAP_MIGRATION, true).apply();
        }
        boolean hasBittrexBCHMigration = prefs.getBoolean(BITTREX_TO_BCH, false);
        if (!hasBittrexBCHMigration) {
            migrateBittrexBCH();
            prefs.edit().putBoolean(BITTREX_TO_BCH, true).apply();
        }
        boolean hasGDAXMigration = prefs.getBoolean(GDAX, false);
        if (!hasGDAXMigration) {
            migrateGDAX();
            prefs.edit().putBoolean(GDAX, true).apply();
        }
        boolean hasXRBMigration = prefs.getBoolean(XRB_TO_NANO, false);
        if (!hasXRBMigration) {
            migrateXRB();
            prefs.edit().putBoolean(XRB_TO_NANO, true).apply();
        }
        boolean hasBCHUnitMigration = prefs.getBoolean(FIX_BCH_UNITS, false);
        if (!hasBCHUnitMigration) {
            migrationBCHUnits();
            prefs.edit().putBoolean(FIX_BCH_UNITS, true).apply();
        }
        boolean hasBCHABCMigration = prefs.getBoolean(BCH_TO_ABC, false);
        if (!hasBCHABCMigration) {
            migrationBCHABC();
            prefs.edit().putBoolean(BCH_TO_ABC, true).apply();
        }
        boolean hasIndoDax = prefs.getBoolean(INDODAX, false);
        if (!hasIndoDax) {
            migrationIndodax();
            prefs.edit().putBoolean(INDODAX, true).apply();
        }
    }

    private static void migrationIndodax() {
        int[] widgetIds = WidgetApplication.getInstance().getWidgetIds();
        for (int widgetId : widgetIds) {
            Prefs prefs = new Prefs(widgetId);
            if ("BITCOINCOID".equals(prefs.getExchangeName())) {
                prefs.setValue("exchange", "INDODAX");
            }
        }
    }

    private static void migrationBCHABC() {
        int[] widgetIds = WidgetApplication.getInstance().getWidgetIds();
        for (int widgetId : widgetIds) {
            Prefs prefs = new Prefs(widgetId);
            if (prefs.getCoin() != Coin.BCH) continue;
            String exchange = prefs.getValue("exchange");
            if ("BITFINEX".equals(exchange)) {
                switch (prefs.getCoin()) {
                    case BCH:
                        prefs.setValue("coin_custom", "BAB");
                        break;
                    case DASH:
                        prefs.setValue("coin_custom", "DSH");
                        break;
                    case IOTA:
                        prefs.setValue("coin_custom", "IOT");
                }
            } else if ("COINSQUARE".equals(exchange)) {
                prefs.setValue("coin_custom", "BAB");
            } else if ("BTCMARKETS".equals(exchange)) {
                prefs.setValue("coin_custom", "BCHABC");
            } else if ("BIT2C".equals(exchange)) {
                prefs.setValue("coin_custom", "Bchabc");
            } else if ("KOINEX".equals(exchange)) {
                prefs.setValue("coin_custom", "BCHABC");
            }
        }
    }

    // wrong bch units, need to fix
    private static void migrationBCHUnits() {
        int[] widgetIds = WidgetApplication.getInstance().getWidgetIds();
        for (int widgetId : widgetIds) {
            Prefs prefs = new Prefs(widgetId);
            if (prefs.getCoin() != Coin.BCH) continue;
            switch (prefs.getUnit()) {
                case "BTC":
                    prefs.setValue("units", "BCH");
                    break;
                case "mBTC":
                    prefs.setValue("units", "mBCH");
                    break;
                case "μBTC":
                    prefs.setValue("units", "μBCH");
                    break;
            }

        }
    }

    // exchanges changed from using code xrb to nano
    private static void migrateXRB() {
        int[] widgetIds = WidgetApplication.getInstance().getWidgetIds();
        for (int widgetId : widgetIds) {
            Prefs prefs = new Prefs(widgetId);
            if ("XRB".equals(prefs.getExchangeCoinName())) {
                prefs.setValue("coin_custom", "NANO");
            }
        }
    }

    private static void migrateGDAX() {
        int[] widgetIds = WidgetApplication.getInstance().getWidgetIds();
        for (int widgetId : widgetIds) {
            Prefs prefs = new Prefs(widgetId);
            String exchange = prefs.getValue("exchange");
            if ("GDAX".equals(exchange)) {
                prefs.setValue("exchange", "COINBASEPRO");
            }
        }
    }

    // bittrex changed from using BCC to BCH
    private static void migrateBittrexBCH() {
        int[] widgetIds = WidgetApplication.getInstance().getWidgetIds();
        for (int widgetId : widgetIds) {
            Prefs prefs = new Prefs(widgetId);
            String exchange = prefs.getValue("exchange");
            if ("BITTREX".equals(exchange)) {
                Coin coin = prefs.getCoin();
                String exchangeCoinName = prefs.getExchangeCoinName();
                if ("BCC".equals(exchangeCoinName)) {
                    prefs.setValue("coin_custom", "BCH");
                }
            }
        }
    }

    // coinmarketcap changed their API a lot
    private static void migrateCoinMarketCapV2() {
        int[] widgetIds = WidgetApplication.getInstance().getWidgetIds();
        for (int widgetId : widgetIds) {
            Prefs prefs = new Prefs(widgetId);
            String exchange = prefs.getValue("exchange");
            if ("COINMARKETCAP".equals(exchange)) {
                String exchangeCoinName = prefs.getExchangeCoinName();
                if ("iota".equals(exchangeCoinName)) {
                    prefs.setValue("coin_custom", "MIOTA");
                } else {
                    prefs.setValue("coin_custom", prefs.getCoin().name());
                }
            }
        }
    }


    // spelled quoine wrong, so migrate users who have the prior bad spelling
    private static void migrateQuoine() {
        int[] widgetIds = WidgetApplication.getInstance().getWidgetIds();
        for (int widgetId : widgetIds) {
            Prefs prefs = new Prefs(widgetId);
            String exchange = prefs.getValue("exchange");
            if ("QUIONE".equals(exchange)) {
                prefs.setValue("exchange", Exchange.QUOINE.name());
            }
        }
    }

    // Certain exchanges have different names for coins and currencies than the rest.
    // Instead of hard coding these, we will now have these custom values populated from the JSON file,
    // and do a migration for existing users who already have widgets with these values.
    private static void migrateExchangeCoinAndCurrencyNames() {
        int[] widgetIds = WidgetApplication.getInstance().getWidgetIds();
        for (int widgetId : widgetIds) {
            Prefs prefs = new Prefs(widgetId);
            try {
                Exchange exchange = prefs.getExchange();
                Coin coin = prefs.getCoin();
                String currency = prefs.getCurrency();
                String coinName = null;
                String currencyName = null;
                switch (exchange) {
                    case BIT2C:
                        if (BTC == coin) coinName = "Btc";
                        if (BCH == coin) coinName = "Bch";
                        if (LTC == coin) coinName = "Ltc";
                        break;
                    case BITBAY:
                        if (BCH == coin) coinName = "BCC";
                        break;
                    case BITFINEX:
                        if (DASH == coin) coinName = "dsh";
                        if (IOTA == coin) coinName = "iot";
                        break;
                    case BITMARKET24:
                        if (BCH == coin) coinName = "BCC";
                        break;
                    case BITMARKETPL:
                        if (BCH == coin) coinName = "BCC";
                        break;
                    case BITTREX:
                        if (BCH == coin) coinName = "BCC";
                        if ("USD".equals(currency))currencyName = "USDT";
                        break;
                    case INDEPENDENT_RESERVE:
                        if (BTC == coin) coinName = "xbt";
                        break;
                    case ITBIT:
                        if (BTC == coin) coinName = "XBT";
                        break;
                    case KOINEX:
                        if (IOTA == coin) coinName = "MIOTA";
                        break;
                    case KRAKEN:
                        if (BTC == coin) coinName = "XBT";
                        break;
                    case LUNO:
                        if (BTC == coin) coinName = "XBT";
                        break;
                    case PARIBU:
                        if ("TRY".equals(currency)) currencyName = "TL";
                        break;
                    case POLONIEX:
                        if ("USD".equals(currency)) currencyName = "USDT";
                        break;
                }
                prefs.setExchangeValues(coinName, currencyName);
            } catch (Exception e) {
                // user's exchange no longer exists
            }
        }
    }

}
