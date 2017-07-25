package com.brentpanther.ethereumwidget;

import com.brentpanther.cryptowidget.Exchange;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import static com.brentpanther.cryptowidget.ExchangeHelper.getJSONObject;

/**
 * Created by brentpanther on 5/10/17.
 */

enum EthereumExchange implements Exchange {

    BITBAY(R.array.currencies_bitbay, "bitbay") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://bitbay.net/API/Public/ETH%s/ticker.json", currencyCode);
            return getJSONObject(url).getString("last");
        }
    },
    BITFINEX(R.array.currencies_bitfinex, "bitfinex") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://api.bitfinex.com/v1/ticker/ethusd");
            return obj.getString("last_price");
        }
    },
    BITSO(R.array.currencies_bitso, "bitso") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONArray payload = getJSONObject("https://api.bitso.com/v3/ticker/").getJSONArray("payload");
            for (int i = 0; i < payload.length(); i++) {
                JSONObject obj = payload.getJSONObject(i);
                if (obj.getString("book").equals("eth_mxn")) {
                    return obj.getString("last");
                }
            }
            return null;
        }
    },
    BTCE(R.array.currencies_btce, "btc-e") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj;
            try {
                obj = getJSONObject(String.format("https://btc-e.com/api/3/ticker/eth_%s", currencyCode.toLowerCase()));
            } catch (IOException e) {
                obj = getJSONObject(String.format("https://btc-e.nz/api/3/ticker/eth_%s", currencyCode.toLowerCase()));
            }
            obj = obj.getJSONObject(String.format("eth_%s", currencyCode.toLowerCase()));
            return obj.getString("last");
        }
    },
    BTER(R.array.currencies_bter, "bter") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "http://data.bter.com/api2/1/ticker/eth_cny";
            return getJSONObject(url).getString("last");
        }
    },
    CEXIO(R.array.currencies_cexio, "cexio") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject(String.format("https://cex.io/api/last_price/ETH/%s", currencyCode)).getString("lprice");
        }
    },
    COINBASE(R.array.currencies_coinbase, "coinbase") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject(String.format("https://api.coinbase.com/v2/prices/ETH-%s/spot", currencyCode));
            return obj.getJSONObject("data").getString("amount");
        }
    },
    ETHEXINDIA(R.array.currencies_ethexindia, "ethex") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://api.ethexindia.com/ticker");
            return obj.getString("last_traded_price");
        }
    },
    GATECOIN(R.array.currencies_gatecoin, "gate") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONArray tickers = getJSONObject("https://api.gatecoin.com/Public/LiveTickers").getJSONArray("tickers");
            String code = "ETH" + currencyCode;
            for (int i = 0; i < tickers.length(); i++) {
                JSONObject obj = tickers.getJSONObject(i);
                if (obj.getString("currencyPair").equals(code)) {
                    return obj.getString("last");
                }
            }
            return null;
        }
    },
    GEMINI(R.array.currencies_gemini, "gemini") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://api.gemini.com/v1/pubticker/ethusd").getString("last");
        }
    },
    HITBTC(R.array.currencies_hitbtc, "hitbtc") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject(String.format("https://api.hitbtc.com/api/1/public/ETH%s/ticker", currencyCode)).getString("last");
        }
    },
    INDEPENDENT_RESERVE(R.array.currencies_independentreserve, "ind. reserve") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://api.independentreserve.com/Public/GetMarketSummary?primaryCurrencyCode=eth&secondaryCurrencyCode=%s";
            return getJSONObject(String.format(url, currencyCode)).getString("LastPrice");
        }
    },
    KRAKEN(R.array.currencies_kraken, "kraken") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject(String.format("https://api.kraken.com/0/public/Ticker?pair=ETH%s", currencyCode));
            JSONObject obj2 = obj.getJSONObject("result").getJSONObject("XETHZ" + currencyCode);
            return (String)obj2.getJSONArray("c").get(0);
        }
    },
    POLONIEX(R.array.currencies_poloniex, "poloniex") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://poloniex.com/public?command=returnTicker");
            return obj.getJSONObject("USDT_ETH").getString("last");
        }
    },
    QUIONE(R.array.currencies_quione, "quione") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://api.quoine.com/products/code/CASH/ETH%s", currencyCode);
            return getJSONObject(url).getString("last_traded_price");
        }
    },
    THEROCK(R.array.currencies_therock, "therock") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://api.therocktrading.com/v1/funds/ETH%s/ticker", currencyCode);
            return getJSONObject(url).getString("last");
        }
    };

    private final int currencyArrayID;
    private String label;

    EthereumExchange(int currencyArrayID, String label) {
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
