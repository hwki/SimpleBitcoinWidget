package com.brentpanther.bitcoincashwidget;

import com.brentpanther.cryptowidget.Exchange;

import org.json.JSONObject;

import okhttp3.Headers;

import static com.brentpanther.cryptowidget.ExchangeHelper.getJSONObject;

enum BCHExchange implements Exchange {

    BITBAY(R.array.currencies_bitbay, "bitbay") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://bitbay.net/API/Public/BCC%s/ticker.json", currencyCode);
            return getJSONObject(url).getString("last");
        }
    },
    BITFINEX(R.array.currencies_bitfinex, "bitfinex") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://api.bitfinex.com/v1/ticker/bchusd");
            return obj.getString("last_price");
        }
    },
    BITHUMB(R.array.currencies_bithumb, "bithumb") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://api.bithumb.com/public/ticker/BCH";
            JSONObject data = getJSONObject(url).getJSONObject("data");
            Long buy = Long.valueOf(data.getString("buy_price"));
            Long sell = Long.valueOf(data.getString("sell_price"));
            return String.valueOf((buy + sell) / 2);
        }
    },
    BTER(R.array.currencies_bter, "bter") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://data.bter.com/api2/1/ticker/bcc_cny";
            return getJSONObject(url).getString("last");
        }
    },
    KORBIT(R.array.currencies_korbit, "korbit") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            Headers headers = Headers.of("User-Agent", "");
            return getJSONObject("https://api.korbit.co.kr/v1/ticker?currency_pair=bch_krw", headers).getString("last");
        }
    },
    KRAKEN(R.array.currencies_kraken, "kraken") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject(String.format("https://api.kraken.com/0/public/Ticker?pair=BCH%s", currencyCode));
            JSONObject obj2 = obj.getJSONObject("result").getJSONObject("BCH" + currencyCode);
            return (String)obj2.getJSONArray("c").get(0);
        }
    },
    OKCOIN(R.array.currencies_okcoin, "okcoin") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj;
            if("USD".equals(currencyCode)) {
                obj = getJSONObject("https://www.okcoin.com/api/v1/ticker.do?symbol=bcc_usd");
            } else if("CNY".equals(currencyCode)) {
                obj = getJSONObject("https://www.okcoin.cn/api/v1/ticker.do?symbol=bcc_cny");
            } else {
                return null;
            }
            return obj.getJSONObject("ticker").getString("last");
        }
    },
    POLONIEX(R.array.currencies_poloniex, "poloniex") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://poloniex.com/public?command=returnTicker");
            return obj.getJSONObject("USDT_BCH").getString("last");
        }
    },
    QUADRIGA(R.array.currencies_quadriga, "quadriga") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://api.quadrigacx.com/v2/ticker?book=bch_%s", currencyCode.toLowerCase());
            return getJSONObject(url).getString("last");
        }
    },
    QUIONE(R.array.currencies_quione, "quione") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://api.quoine.com/products/code/CASH/BCH%s", currencyCode);
            return getJSONObject(url).getString("last_traded_price");
        }
    },
    WEX(R.array.currencies_wex, "wex") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String pair = String.format("bch_%s", currencyCode.toLowerCase());
            String url = String.format("https://wex.nz/api/3/ticker/%s", pair);
            return getJSONObject(url).getJSONObject(pair).getString("last");
        }
    };

    private final int currencyArrayID;
    private String label;

    BCHExchange(int currencyArrayID, String label) {
        this.currencyArrayID = currencyArrayID;
        this.label = label;
    }

    public String getValue(String currencyCode) throws Exception {
        return null;
    }

    public int getCurrencies() {
        return currencyArrayID;
    }

    public String getLabel() {
        return label;
    }

}
