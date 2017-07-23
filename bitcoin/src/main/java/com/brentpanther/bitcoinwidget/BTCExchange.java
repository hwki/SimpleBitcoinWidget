package com.brentpanther.bitcoinwidget;

import com.brentpanther.cryptowidget.Exchange;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.SocketException;

import static com.brentpanther.cryptowidget.ExchangeHelper.getJSONArray;
import static com.brentpanther.cryptowidget.ExchangeHelper.getJSONObject;
import static com.brentpanther.cryptowidget.ExchangeHelper.getString;

enum BTCExchange implements Exchange {

    //NO LONGER EXISTS
    MTGOX(R.array.currencies_mtgox, "mtgx"),
    COINBASE(R.array.currencies_coinbase, "coinbase") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject(String.format("https://api.coinbase.com/v2/prices/BTC-%s/spot", currencyCode));
            return obj.getJSONObject("data").getString("amount");
        }
    },
    BITSTAMP(R.array.currencies_bitstamp, "bitstamp") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://www.bitstamp.net/api/v2/ticker/btc%s", currencyCode.toLowerCase());
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
    CAMPBX(R.array.currencies_campbx, "campbx") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://campbx.com/api/xticker.php");
            return obj.getString("Last Trade");
        }
    },
    BTCE(R.array.currencies_btce, "btc-e") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj;
            try {
                obj = getJSONObject(String.format("https://btc-e.com/api/3/ticker/btc_%s", currencyCode.toLowerCase()));
            } catch (SocketException e) {
                // try mirror
                obj = getJSONObject(String.format("https://btc-e.nz/api/3/ticker/btc_%s", currencyCode.toLowerCase()));
            }
            obj = obj.getJSONObject(String.format("btc_%s", currencyCode.toLowerCase()));
            return obj.getString("last");
        }
    },
    MERCADO(R.array.currencies_mercado, "mercado") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://www.mercadobitcoin.net/api/ticker/");
            return obj.getJSONObject("ticker").getString("last");
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
    //GONE
    BITCUREX(R.array.currencies_bitcurex, "bitcurex"),
    BITFINEX(R.array.currencies_bitfinex, "bitfinex") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://api.bitfinex.com/v1/ticker/btcusd");
            return obj.getString("last_price");
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
    BTC_CHINA(R.array.currencies_btcchina, "btc china") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://data.btcchina.com/data/ticker");
            return obj.getJSONObject("ticker").getString("last");
        }
    },
    BIT2C(R.array.currencies_bit2c, "bit2c") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://www.bit2c.co.il/Exchanges/BtcNis/Ticker.json");
            return obj.getString("av");
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
    KRAKEN(R.array.currencies_kraken, "kraken") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject(String.format("https://api.kraken.com/0/public/Ticker?pair=XBT%s", currencyCode));
            JSONObject obj2 = obj.getJSONObject("result").getJSONObject("XXBTZ" + currencyCode);
            return (String)obj2.getJSONArray("c").get(0);
        }
    },
    BTCTURK(R.array.currencies_btcturk, "btcturk") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://www.btcturk.com/api/ticker");
            return obj.getString("last");
        }
    },
    //NO LONGER EXISTS
    VIRTEX(R.array.currencies_virtex, "vrtx"),
    //NO LONGER EXISTS
    JUSTCOIN(R.array.currencies_justcoin, "justcoin"),
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
    CRYPTONIT(R.array.currencies_cryptonit, "crypt") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://cryptonit.net/apiv2/rest/public/ccorder.json?bid_currency=usd&ask_currency=btc&ticker");
            return obj.getJSONObject("rate").getString("last");
        }
    },
    COINTREE(R.array.currencies_cointree, "cointree") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://www.cointree.com.au/api/price/btc/aud").getString("Spot");
        }
    },
    BTCMARKETS(R.array.currencies_btcmarkets, "btcmarkets") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://api.btcmarkets.net/market/BTC/AUD/tick").getString("lastPrice");
        }
    },
    HUOBI(R.array.currencies_huobi, "huobi") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://api.huobi.com/usdmarket/ticker_btc_json.js");
            return obj.getJSONObject("ticker").getString("last");
        }
    },
    KORBIT(R.array.currencies_korbit, "korbit") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://api.korbit.co.kr/v1/ticker").getString("last");
        }
    },
    PAYMIUM(R.array.currencies_paymium, "paymium") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://paymium.com/api/v1/data/eur/ticker").getString("price");
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
    ZYADO(R.array.currencies_zyado, "zyado") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("http://chart.zyado.com/ticker.json").getString("last");
        }
    },
    //GONE
    CRYPTSY(R.array.currencies_cryptsy, "crpsy"),
    BITBAY(R.array.currencies_bitbay, "bitbay") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://bitbay.net/API/Public/BTC%s/ticker.json", currencyCode);
            return getJSONObject(url).getString("last");
        }
    },
    CEXIO(R.array.currencies_cexio, "cexio") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject(String.format("https://cex.io/api/last_price/BTC/%s", currencyCode)).getString("lprice");
        }
    },
    //NO LONGER EXISTS
    BTCXCHANGE(R.array.currencies_btcxchange, "btxch"),
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
    HITBTC(R.array.currencies_hitbtc, "hitbtc") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject(String.format("https://api.hitbtc.com/api/1/public/BTC%s/ticker", currencyCode)).getString("last");
        }
    },
    ITBIT(R.array.currencies_itbit, "itbit") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject(String.format("https://api.itbit.com/v1/markets/XBT%s/ticker", currencyCode)).getString("lastPrice");
        }
    },
    BITCOINCOID(R.array.currencies_bitcoincoid, "bitcoin.co.id") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://vip.bitcoin.co.id/api/BTC_IDR/ticker/").getJSONObject("ticker").getString("last");
        }
    },
    FOXBIT(R.array.currencies_foxbit, "foxbit") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://api.blinktrade.com/api/v1/BRL/ticker?crypto_currency=BTC").getString("last");
        }
    },
    INDEPENDENT_RESERVE(R.array.currencies_independentreserve, "ind. reserve") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://api.independentreserve.com/Public/GetMarketSummary?primaryCurrencyCode=xbt&secondaryCurrencyCode=%s";
            return getJSONObject(String.format(url, currencyCode)).getString("LastPrice");
        }
    },
    //NO LONGER EXISTS
    BUTTERCOIN(R.array.currencies_buttercoin, "btrcn"),
    //NO LONGER EXISTS
    CLEVERCOIN(R.array.currencies_clevercoin, "clvr"),
    BITMARKET24(R.array.currencies_bitmarket24, "bitmarket24") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://bitmarket24.pl/api/BTC_PLN/status.json").getString("last");
        }
    },
    QUADRIGA(R.array.currencies_quadriga, "quadriga") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://api.quadrigacx.com/v2/ticker?book=btc_%s", currencyCode.toLowerCase());
            return getJSONObject(url).getString("last");
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
    //OFFLINE
    MEXBT(R.array.currencies_mexbt, "mexbt"),
    LUNO(R.array.currencies_luno, "luno") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://api.mybitx.com/api/1/ticker?pair=XBT%s";
            return getJSONObject(String.format(url, currencyCode)).getString("last_trade");
        }
    },
    BTCBOX(R.array.currencies_btcbox, "btcbox") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://www.btcbox.co.jp/api/v1/ticker/";
            return getJSONObject(url).getString("last");
        }
    },
    BTCXINDIA(R.array.currencies_btcxindia, "btcxindia") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://api.btcxindia.com/ticker/";
            return getJSONObject(url).getString("last_traded_price");
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
    SURBITCOIN(R.array.currencies_surbitcoin, "surbitcoin") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://api.blinktrade.com/api/v1/VEF/ticker?crypto_currency=BTC").getString("last");
        }
    },
    VBTC(R.array.currencies_vbtc, "vbtc") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://api.blinktrade.com/api/v1/VND/ticker?crypto_currency=BTC").getString("last");
        }
    },
    URDUBIT(R.array.currencies_urdubit, "urdubit") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://api.blinktrade.com/api/v1/PKR/ticker?crypto_currency=BTC").getString("last");
        }
    },
    CHILEBIT(R.array.currencies_chilebit, "chilebit") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://api.blinktrade.com/api/v1/CLP/ticker?crypto_currency=BTC").getString("last");
        }
    },
    GEMINI(R.array.currencies_gemini, "gemini") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://api.gemini.com/v1/pubticker/btcusd").getString("last");
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
    BITMARKETPL(R.array.currencies_bitmarketpl, "bitmarket.pl") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://www.bitmarket.pl/json/BTC%s/ticker.json", currencyCode);
            return getJSONObject(url).getString("last");
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
    POLONIEX(R.array.currencies_poloniex, "poloniex") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://poloniex.com/public?command=returnTicker");
            return obj.getJSONObject("USDT_BTC").getString("last");
        }
    },
    UNOCOIN(R.array.currencies_unocoin, "unocoin") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getString("https://www.unocoin.com/trade?avg");
        }
    },
    THEROCK(R.array.currencies_therock, "therock") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://api.therocktrading.com/v1/funds/BTC%s/ticker", currencyCode);
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
    BTER(R.array.currencies_bter, "bter") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://data.bter.com/api2/1/ticker/btc_cny";
            return getJSONObject(url).getString("last");
        }
    },
    COINSECURE(R.array.currencies_coinsecure, "coinsecure") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://api.coinsecure.in/v0/noauth/newticker";
            return String.valueOf(getJSONObject(url).getLong("lastprice") / 100);
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
