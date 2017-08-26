package com.brentpanther.litecoinwidget;

import com.brentpanther.cryptowidget.Exchange;

import org.json.JSONObject;

import java.io.IOException;

import static com.brentpanther.cryptowidget.ExchangeHelper.getJSONObject;

/**
 * Created by brentpanther on 5/10/17.
 */

enum LitecoinExchange implements Exchange {

    BITBAY(R.array.currencies_bitbay, "bitbay") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://bitbay.net/API/Public/LTC%s/ticker.json", currencyCode);
            return getJSONObject(url).getString("last");
        }
    },
    BITFINEX(R.array.currencies_bitfinex, "bitfinex") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://api.bitfinex.com/v1/ticker/ltcusd");
            return obj.getString("last_price");
        }
    },
    BTCE(R.array.currencies_btce, "btc-e") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj;
            try {
                obj = getJSONObject(String.format("https://btc-e.com/api/3/ticker/ltc_%s", currencyCode.toLowerCase()));
            } catch (IOException e) {
                // try mirror
                obj = getJSONObject(String.format("https://btc-e.nz/api/3/ticker/ltc_%s", currencyCode.toLowerCase()));
            }
            obj = obj.getJSONObject(String.format("ltc_%s", currencyCode.toLowerCase()));
            return obj.getString("last");
        }
    },
    BTER(R.array.currencies_bter, "bter") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://data.bter.com/api2/1/ticker/ltc_cny";
            return getJSONObject(url).getString("last");
        }
    },
    COINBASE(R.array.currencies_coinbase, "coinbase") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject(String.format("https://api.coinbase.com/v2/prices/LTC-%s/spot", currencyCode));
            return obj.getJSONObject("data").getString("amount");
        }
    },
    HITBTC(R.array.currencies_hitbtc, "hitbtc") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject(String.format("https://api.hitbtc.com/api/1/public/LTC%s/ticker", currencyCode)).getString("last");
        }
    },
    KRAKEN(R.array.currencies_kraken, "kraken") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject(String.format("https://api.kraken.com/0/public/Ticker?pair=LTC%s", currencyCode));
            JSONObject obj2 = obj.getJSONObject("result").getJSONObject("XLTCZ" + currencyCode);
            return (String)obj2.getJSONArray("c").get(0);
        }
    },
    POLONIEX(R.array.currencies_poloniex, "poloniex") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://poloniex.com/public?command=returnTicker");
            return obj.getJSONObject("USDT_LTC").getString("last");
        }
    },
    THEROCK(R.array.currencies_therock, "therock") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://api.therocktrading.com/v1/funds/LTC%s/ticker", currencyCode);
            return getJSONObject(url).getString("last");
        }
    },
    BITMARKET24(R.array.currencies_bitmarket24, "bitmarket24") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://bitmarket24.pl/api/LTC_PLN/status.json").getString("last");
        }
    };

    private final int currencyArrayID;
    private String label;

    LitecoinExchange(int currencyArrayID, String label) {
        this.currencyArrayID = currencyArrayID;
        this.label = label;
    }

    @Override
    public String getValue(String currencyCode) throws Exception {
        return null;
    }

    @Override
    public int getCurrencies() {
        return currencyArrayID;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
