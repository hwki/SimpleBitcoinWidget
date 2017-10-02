package com.brentpanther.bitcoinwidget;

import com.brentpanther.cryptowidget.Exchange;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Headers;

import static com.brentpanther.cryptowidget.ExchangeHelper.getJSONArray;
import static com.brentpanther.cryptowidget.ExchangeHelper.getJSONObject;
import static com.brentpanther.cryptowidget.ExchangeHelper.getString;

enum BTCExchange implements Exchange {

    BIT2C(R.array.currencies_bit2c, "bit2c") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://www.bit2c.co.il/Exchanges/BtcNis/Ticker.json");
            return obj.getString("av");
        }
    },
    BITBAY(R.array.currencies_bitbay, "bitbay") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://bitbay.net/API/Public/BTC%s/ticker.json", currencyCode);
            return getJSONObject(url).getString("last");
        }
    },
    BITCOIN_AVERAGE(R.array.currencies_bitcoinaverage, "btc avg") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://apiv2.bitcoinaverage.com/indices/local/ticker/short?crypto=BTC&fiats=%s", currencyCode);
            JSONObject obj = getJSONObject(url);
            return obj.getJSONObject(String.format("BTC%s", currencyCode)).getString("last");
        }
    },
    BITCOIN_AVERAGE_GLOBAL(R.array.currencies_bitcoinaverage_global, "btc avg glb") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://apiv2.bitcoinaverage.com/indices/global/ticker/short?crypto=BTC&fiats=%s", currencyCode);
            JSONObject obj = getJSONObject(url);
            return obj.getJSONObject(String.format("BTC%s", currencyCode)).getString("last");
        }
    },
    BITCOINCOID(R.array.currencies_bitcoincoid, "bitcoin.co.id") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://vip.bitcoin.co.id/api/btc_idr/ticker/").getJSONObject("ticker").getString("last");
        }
    },
    BITCOINDE(R.array.currencies_bitcoinde, "bitcoinde") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://bitcoinapi.de/widget/current-btc-price/rate.json");
            String price = obj.getString("price_eur");
            String[] amount = price.split("\\s");
            return amount[0].replaceAll("\\.", "").replaceAll(",", ".");
        }
    },
    BITFINEX(R.array.currencies_bitfinex, "bitfinex") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://api.bitfinex.com/v1/ticker/btcusd");
            return obj.getString("last_price");
        }
    },
    BITFLYER(R.array.currencies_bitflyer, "bitflyer") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://api.bitflyer.jp/v1/ticker?product_code=BTC_%s", currencyCode);
            return getJSONObject(url).getString("ltp");
        }
    },
    BITHUMB(R.array.currencies_bithumb, "bithumb") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://api.bithumb.com/public/ticker/BTC";
            JSONObject data = getJSONObject(url).getJSONObject("data");
            Long buy = Long.valueOf(data.getString("buy_price"));
            Long sell = Long.valueOf(data.getString("sell_price"));
            return String.valueOf((buy + sell) / 2);
        }
    },
    BITMARKET24(R.array.currencies_bitmarket24, "bitmarket24") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://bitmarket24.pl/api/BTC_PLN/status.json").getString("last");
        }
    },
    BITMARKETPL(R.array.currencies_bitmarketpl, "bitmarket.pl") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://www.bitmarket.pl/json/BTC%s/ticker.json", currencyCode);
            return getJSONObject(url).getString("last");
        }
    },
    BITPAY(R.array.currencies_bitpay, "bitpay") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONArray array = getJSONArray("https://bitpay.com/api/rates");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if(currencyCode.equals(obj.getString("code"))) {
                    return obj.getString("rate");
                }
            }
            return null;
        }
    },
    BITSO(R.array.currencies_bitso, "bitso") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONArray payload = getJSONObject("https://api.bitso.com/v3/ticker/").getJSONArray("payload");
            for (int i = 0; i < payload.length(); i++) {
                JSONObject obj = payload.getJSONObject(i);
                if (obj.getString("book").equals("btc_mxn")) {
                    return obj.getString("last");
                }
            }
            return null;
        }
    },
    BITSTAMP(R.array.currencies_bitstamp, "bitstamp") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://www.bitstamp.net/api/v2/ticker/btc%s", currencyCode.toLowerCase());
            return getJSONObject(url).getString("last");
        }
    },
    BITTREX(R.array.currencies_bittrex, "bittrex") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://bittrex.com/api/v1.1/public/getticker?market=USDT-BTC";
            return getJSONObject(url).getJSONObject("result").getString("Last");
        }
    },
    BTCBOX(R.array.currencies_btcbox, "btcbox") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://www.btcbox.co.jp/api/v1/ticker/";
            return getJSONObject(url).getString("last");
        }
    },
    BTC_CHINA(R.array.currencies_btcchina, "btc china") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://data.btcchina.com/data/ticker");
            return obj.getJSONObject("ticker").getString("last");
        }
    },
    BTCMARKETS(R.array.currencies_btcmarkets, "btcmarkets") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://api.btcmarkets.net/market/BTC/AUD/tick").getString("lastPrice");
        }
    },
    BTCTURK(R.array.currencies_btcturk, "btcturk") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://www.btcturk.com/api/ticker");
            return obj.getString("last");
        }
    },
    BTCXINDIA(R.array.currencies_btcxindia, "btcxindia") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://api.btcxindia.com/ticker/";
            return getJSONObject(url).getString("last_traded_price");
        }
    },
    BTER(R.array.currencies_bter, "bter") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://data.bter.com/api2/1/ticker/btc_cny";
            return getJSONObject(url).getString("last");
        }
    },
    CAMPBX(R.array.currencies_campbx, "campbx") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://campbx.com/api/xticker.php");
            return obj.getString("Last Trade");
        }
    },
    CEXIO(R.array.currencies_cexio, "cexio") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject(String.format("https://cex.io/api/last_price/BTC/%s", currencyCode)).getString("lprice");
        }
    },
    CHILEBIT(R.array.currencies_chilebit, "chilebit") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://api.blinktrade.com/api/v1/CLP/ticker?crypto_currency=BTC").getString("last");
        }
    },
    COINBASE(R.array.currencies_coinbase, "coinbase") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject(String.format("https://api.coinbase.com/v2/prices/BTC-%s/spot", currencyCode));
            return obj.getJSONObject("data").getString("amount");
        }
    },
    COINDESK(R.array.currencies_coindesk, "coindesk") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://api.coindesk.com/v1/bpi/currentprice/%s.json", currencyCode);
            return getJSONObject(url).getJSONObject("bpi").getJSONObject(currencyCode).getString("rate_float");
        }
    },
    COINMATE(R.array.currencies_coinmate, "coinmate") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://coinmate.io/api/ticker?currencyPair=BTC_%s", currencyCode);
            JSONObject obj = getJSONObject(url);
            return obj.getJSONObject("data").getString("last");
        }
    },
    COINSECURE(R.array.currencies_coinsecure, "coinsecure") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://api.coinsecure.in/v0/noauth/newticker";
            return String.valueOf(getJSONObject(url).getLong("lastprice") / 100);
        }
    },
    COINTREE(R.array.currencies_cointree, "cointree") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://www.cointree.com.au/api/price/btc/aud").getString("Spot");
        }
    },
    COINSPH(R.array.currencies_coinsph, "coins.ph") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://quote.coins.ph/v1/markets/BTC-%s", currencyCode);
            JSONObject obj = getJSONObject(url).getJSONObject("market");
            String bid = obj.getString("bid");
            String ask = obj.getString("ask");
            return Double.toString((Double.valueOf(bid) + Double.valueOf(ask)) / 2);
        }
    },
    CRYPTONIT(R.array.currencies_cryptonit, "crypt") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://cryptonit.net/apiv2/rest/public/ccorder.json?bid_currency=usd&ask_currency=btc&ticker");
            return obj.getJSONObject("rate").getString("last");
        }
    },
    FOXBIT(R.array.currencies_foxbit, "foxbit") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://api.blinktrade.com/api/v1/BRL/ticker?crypto_currency=BTC").getString("last");
        }
    },
    GATECOIN(R.array.currencies_gatecoin, "gate") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONArray tickers = getJSONObject("https://api.gatecoin.com/Public/LiveTickers").getJSONArray("tickers");
            String code = "BTC" + currencyCode;
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
            return getJSONObject("https://api.gemini.com/v1/pubticker/btcusd").getString("last");
        }
    },
    HITBTC(R.array.currencies_hitbtc, "hitbtc") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject(String.format("https://api.hitbtc.com/api/1/public/BTC%s/ticker", currencyCode)).getString("last");
        }
    },
    HUOBI(R.array.currencies_huobi, "huobi") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://api.huobi.com/usdmarket/ticker_btc_json.js");
            return obj.getJSONObject("ticker").getString("last");
        }
    },
    INDEPENDENT_RESERVE(R.array.currencies_independentreserve, "ind. reserve") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://api.independentreserve.com/Public/GetMarketSummary?primaryCurrencyCode=xbt&secondaryCurrencyCode=%s";
            return getJSONObject(String.format(url, currencyCode)).getString("LastPrice");
        }
    },
    ITBIT(R.array.currencies_itbit, "itbit") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject(String.format("https://api.itbit.com/v1/markets/XBT%s/ticker", currencyCode)).getString("lastPrice");
        }
    },
    KORBIT(R.array.currencies_korbit, "korbit") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            Headers headers = Headers.of("User-Agent", "");
            return getJSONObject("https://api.korbit.co.kr/v1/ticker?currency_pair=btc_krw", headers).getString("last");
        }
    },
    KRAKEN(R.array.currencies_kraken, "kraken") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject(String.format("https://api.kraken.com/0/public/Ticker?pair=XBT%s", currencyCode));
            JSONObject obj2 = obj.getJSONObject("result").getJSONObject("XXBTZ" + currencyCode);
            return (String)obj2.getJSONArray("c").get(0);
        }
    },
    KUNA(R.array.currencies_kuna, "kuna") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://kuna.io/api/v2/tickers/btcuah");
            return obj.getJSONObject("ticker").getString("last");
        }
    },
    LAKEBTC(R.array.currencies_lake, "lakebtc") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://api.lakebtc.com/api_v2/ticker");
            String code = "btc" + currencyCode.toLowerCase();
            return obj.getJSONObject(code).getString("last");
        }
    },
    LUNO(R.array.currencies_luno, "luno") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://api.mybitx.com/api/1/ticker?pair=XBT%s";
            return getJSONObject(String.format(url, currencyCode)).getString("last_trade");
        }
    },
    MERCADO(R.array.currencies_mercado, "mercado") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://www.mercadobitcoin.net/api/ticker/");
            return obj.getJSONObject("ticker").getString("last");
        }
    },
    OKCOIN(R.array.currencies_okcoin, "okcoin") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj;
            if("USD".equals(currencyCode)) {
                obj = getJSONObject("https://www.okcoin.com/api/v1/ticker.do?symbol=btc_usd");
            } else if("CNY".equals(currencyCode)) {
                obj = getJSONObject("https://www.okcoin.cn/api/v1/ticker.do?symbol=btc_cny");
            } else {
                return null;
            }
            return obj.getJSONObject("ticker").getString("last");
        }
    },
    PAYMIUM(R.array.currencies_paymium, "paymium") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://paymium.com/api/v1/data/eur/ticker").getString("price");
        }
    },
    POLONIEX(R.array.currencies_poloniex, "poloniex") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://poloniex.com/public?command=returnTicker");
            return obj.getJSONObject("USDT_BTC").getString("last");
        }
    },
    QUADRIGA(R.array.currencies_quadriga, "quadriga") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://api.quadrigacx.com/v2/ticker?book=btc_%s", currencyCode.toLowerCase());
            return getJSONObject(url).getString("last");
        }
    },
    QUIONE(R.array.currencies_quione, "quione") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://api.quoine.com/products/code/CASH/BTC%s", currencyCode);
            return getJSONObject(url).getString("last_traded_price");
        }
    },
    SURBITCOIN(R.array.currencies_surbitcoin, "surbitcoin") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://api.blinktrade.com/api/v1/VEF/ticker?crypto_currency=BTC").getString("last");
        }
    },
    THEROCK(R.array.currencies_therock, "therock") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://api.therocktrading.com/v1/funds/BTC%s/ticker", currencyCode);
            return getJSONObject(url).getString("last");
        }
    },
    UNOCOIN(R.array.currencies_unocoin, "unocoin") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getString("https://www.unocoin.com/trade?avg");
        }
    },
    UPHOLD(R.array.currencies_uphold, "uphold") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://api.uphold.com/v0/ticker/BTC%s";
            JSONObject obj = getJSONObject(String.format(url, currencyCode));
            String bid = obj.getString("bid");
            String ask = obj.getString("ask");
            return Double.toString((Double.valueOf(bid) + Double.valueOf(ask)) / 2);
        }
    },
    URDUBIT(R.array.currencies_urdubit, "urdubit") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://api.blinktrade.com/api/v1/PKR/ticker?crypto_currency=BTC").getString("last");
        }
    },
    VBTC(R.array.currencies_vbtc, "vbtc") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://api.blinktrade.com/api/v1/VND/ticker?crypto_currency=BTC").getString("last");
        }
    },
    WEX(R.array.currencies_wex, "wex") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String pair = String.format("btc_%s", currencyCode.toLowerCase());
            String url = String.format("https://wex.nz/api/3/ticker/%s", pair);
            return getJSONObject(url).getJSONObject(pair).getString("last");
        }
    },
    ZEBPAY(R.array.currencies_zebpay, "zebpay") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://www.zebapi.com/api/v1/market/ticker/btc/inr";
            return getJSONObject(url).getString("market");
        }
    },
    ZYADO(R.array.currencies_zyado, "zyado") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("http://chart.zyado.com/ticker.json").getString("last");
        }
    };

    private final int currencyArrayID;
    private String label;

    BTCExchange(int currencyArrayID, String label) {
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
