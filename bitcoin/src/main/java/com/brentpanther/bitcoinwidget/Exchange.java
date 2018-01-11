package com.brentpanther.bitcoinwidget;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

import static com.brentpanther.bitcoinwidget.ExchangeHelper.getJSONArray;
import static com.brentpanther.bitcoinwidget.ExchangeHelper.getJSONObject;

enum Exchange {

    ABUCOINS("Abucoins") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.abucoins.com/products/%s%s/stats", coin, currency);
            return getJSONObject(url).getString("last");
        }
    },
    BINANCE("Binance") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.binance.com/api/v3/ticker/price?symbol=%s%s", coin, currency);
            return getJSONObject(url).getString("price");
        }
    },
    BIT2C("Bit2C") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://www.bit2c.co.il/Exchanges/%sNis/Ticker.json", coin);
            return getJSONObject(url).getString("av");
        }
    },
    BITBAY("BitBay") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://bitbay.net/API/Public/%s%s/ticker.json", coin, currency);
            return getJSONObject(url).getString("last");
        }
    },
    BITCOIN_AVERAGE("Bitcoin Average", "BTC avg") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://apiv2.bitcoinaverage.com/indices/local/ticker/short?crypto=%s&fiats=%s", coin, currency);
            JSONObject obj = getJSONObject(url);
            return obj.getJSONObject(String.format("%s%s", coin, currency)).getString("last");
        }
    },
    BITCOIN_AVERAGE_GLOBAL("Bitcoin Average (global)", "BTC avg global") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://apiv2.bitcoinaverage.com/indices/global/ticker/short?crypto=%s&fiats=%s", coin, currency);
            JSONObject obj = getJSONObject(url);
            return obj.getJSONObject(String.format("%s%s", coin, currency)).getString("last");
        }
    },
    BITCOINCOID("Bitcoin.co.id") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s_%s", coin, currency).toLowerCase();
            String url = String.format("https://vip.bitcoin.co.id/api/%s/ticker/", pair);
            return getJSONObject(url).getJSONObject("ticker").getString("last");
        }
    },
    BITCOINDE("Bitcoin.de") {
        @Override
        public String getValue(String code, String currency) throws Exception {
            JSONObject obj = getJSONObject("https://bitcoinapi.de/widget/current-btc-price/rate.json");
            String price = obj.getString("price_eur");
            String[] amount = price.split("\\s");
            return amount[0].replaceAll("\\.", "").replaceAll(",", ".");
        }
    },
    BITFINEX("Bitfinex") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.bitfinex.com/v1/pubticker/%s%s", coin, currency);
            JSONObject obj = getJSONObject(url);
            return obj.getString("last_price");
        }
    },
    BITFLYER("BitFlyer") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.bitflyer.jp/v1/ticker?product_code=%s_%s", coin, currency);
            return getJSONObject(url).getString("ltp");
        }
    },
    BITHUMB("Bithumb") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.bithumb.com/public/ticker/%s", coin);
            JSONObject data = getJSONObject(url).getJSONObject("data");
            Long buy = Long.valueOf(data.getString("buy_price"));
            Long sell = Long.valueOf(data.getString("sell_price"));
            return String.valueOf((buy + sell) / 2);
        }
    },
    BITMARKET24("BitMarket24") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://bitmarket24.pl/api/%s_%s/status.json", coin, currency);
            return getJSONObject(url).getString("last");
        }
    },
    BITMARKETPL("BitMarket.pl") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://www.bitmarket.pl/json/%s%s/ticker.json", coin, currency);
            return getJSONObject(url).getString("last");
        }
    },
    BITPAY("BitPay") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            JSONArray array = getJSONArray("https://bitpay.com/api/rates");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if(currency.equals(obj.getString("code"))) {
                    return obj.getString("rate");
                }
            }
            return null;
        }
    },
    BITSO("Bitso") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            JSONArray payload = getJSONObject("https://api.bitso.com/v3/ticker/").getJSONArray("payload");
            String pair = String.format("%s_%s", coin, currency).toLowerCase();
            for (int i = 0; i < payload.length(); i++) {
                JSONObject obj = payload.getJSONObject(i);
                if (obj.getString("book").equals(pair)) {
                    return obj.getString("last");
                }
            }
            return null;
        }
    },
    BITSTAMP("Bitstamp") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://www.bitstamp.net/api/v2/ticker/%s%s", coin.toLowerCase(), currency.toLowerCase());
            return getJSONObject(url).getString("last");
        }
    },
    BITTREX("Bittrex") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s-%s", currency, coin);
            String url = "https://bittrex.com/api/v1.1/public/getticker?market=" + pair;
            return getJSONObject(url).getJSONObject("result").getString("Last");
        }
    },
    BRAZILIEX("Braziliex") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s_%s", coin, currency).toLowerCase();
            String url = "https://braziliex.com/api/v1/public/ticker/" + pair;
            return getJSONObject(url).getString("last");
        }
    },
    BTCBOX("BTC Box") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = "https://www.btcbox.co.jp/api/v1/ticker/";
            return getJSONObject(url).getString("last");
        }
    },
    BTCMARKETS("BTC Markets") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.btcmarkets.net/market/%s/%s/tick", coin, currency);
            return getJSONObject(url).getString("lastPrice");
        }
    },
    BTCTURK("BTCTurk") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            JSONArray array = getJSONArray("https://www.btcturk.com/api/ticker");
            String pair = coin + currency;
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (obj.getString("pair").equals(pair)) {
                    return obj.getString("last");
                }
            }
            return null;
        }
    },
    BTCXINDIA("BTCXIndia") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = "https://api.btcxindia.com/ticker/";
            return getJSONObject(url).getString("last_traded_price");
        }
    },
    CAMPBX("Camp BX") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            JSONObject obj = getJSONObject("https://campbx.com/api/xticker.php");
            return obj.getString("Last Trade");
        }
    },
    CEXIO("Cex.io") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            return getJSONObject(String.format("https://cex.io/api/last_price/%s/%s", coin, currency)).getString("lprice");
        }
    },
    CHILEBIT("ChileBit.net") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            return getBlinkTradeValue(coin, currency);
        }
    },
    COINBASE("Coinbase") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            JSONObject obj = getJSONObject(String.format("https://api.coinbase.com/v2/prices/%s-%s/spot", coin, currency));
            return obj.getJSONObject("data").getString("amount");
        }
    },
    COINDELTA("Coindelta") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = "https://coindelta.com/api/v1/public/getticker/";
            String pair = String.format("%s-%s", coin, currency).toLowerCase();
            JSONArray array = getJSONArray(url);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (obj.getString("MarketName").equals(pair)) {
                    return obj.getString("Last");
                }
            }
            return null;
        }
    },
    COINDESK("Coindesk") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.coindesk.com/v1/bpi/currentprice/%s.json", currency);
            return getJSONObject(url).getJSONObject("bpi").getJSONObject(currency).getString("rate_float");
        }
    },
    COINMARKETCAP("CoinMarketCap") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.coinmarketcap.com/v1/ticker/%s/?convert=%s", coin, currency);
            String field = String.format("price_%s", currency).toLowerCase();
            return getJSONArray(url).getJSONObject(0).getString(field);
        }
    },
    COINMATE("CoinMate.io") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://coinmate.io/api/ticker?currencyPair=%s_%s", coin, currency);
            JSONObject obj = getJSONObject(url);
            return obj.getJSONObject("data").getString("last");
        }
    },
    COINNEST("Coinnest") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            coin = coin.toLowerCase();
            String url = "https://api.coinnest.co.kr/api/pub/ticker?coin=" + coin;
            return getJSONObject(url).getString("last");
        }
    },
    COINONE("Coinone") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = "https://api.coinone.co.kr/ticker/?currency=" + coin;
            return getJSONObject(url).getString("last");
        }
    },
    COINSECURE("Coinsecure") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = "https://api.coinsecure.in/v1/exchange/ticker";
            return String.valueOf(getJSONObject(url).getJSONObject("message").getLong("lastPrice") / 100);
        }
    },
    COINSQUARE("Coinsquare") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = "https://coinsquare.io/api/v1/data/quotes";
            JSONArray array = getJSONObject(url).getJSONArray("quotes");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (!obj.getString("ticker").equals(coin)) continue;
                if (!obj.getString("base").equals(currency)) continue;
                return obj.getString("last");
            }
            return null;
        }
    },
    COINTREE("Cointree") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s/%s", coin, currency).toLowerCase();
            return getJSONObject("https://www.cointree.com.au/api/price/" + pair).getString("Spot");
        }
    },
    COINSPH("Coins.ph") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://quote.coins.ph/v1/markets/%s-%s", coin, currency);
            JSONObject obj = getJSONObject(url).getJSONObject("market");
            String bid = obj.getString("bid");
            String ask = obj.getString("ask");
            return Double.toString((Double.valueOf(bid) + Double.valueOf(ask)) / 2);
        }
    },
    CRYPTONIT("Cryptonit") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            coin = coin.toLowerCase();
            currency = currency.toLowerCase();
            String url = String.format("https://cryptonit.net/apiv2/rest/public/ccorder.json?bid_currency=%s&ask_currency=%s&ticker", coin, currency);
            JSONObject obj = getJSONObject(url);
            return obj.getJSONObject("rate").getString("last");
        }
    },
    ETHEXINDIA("EthexIndia") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            JSONObject obj = getJSONObject("https://api.ethexindia.com/ticker");
            return obj.getString("last_traded_price");
        }
    },
    EXMO("Exmo") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s_%s", coin, currency);
            String url = "https://api.exmo.com/v1/ticker/";
            return getJSONObject(url).getJSONObject(pair).getString("last_trade");
        }
    },
    FOXBIT("FoxBit") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            return getBlinkTradeValue(coin, currency);
        }
    },
    GATECOIN("Gatecoin") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            JSONArray tickers = getJSONObject("https://api.gatecoin.com/Public/LiveTickers").getJSONArray("tickers");
            String pair = coin + currency;
            for (int i = 0; i < tickers.length(); i++) {
                JSONObject obj = tickers.getJSONObject(i);
                if (obj.getString("currencyPair").equals(pair)) {
                    return obj.getString("last");
                }
            }
            return null;
        }
    },
    GDAX("GDAX") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.gdax.com/products/%s-%s/ticker", coin, currency);
            return getJSONObject(url).getString("price");
        }
    },
    GEMINI("Gemini") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s%s", coin, currency).toLowerCase();
            return getJSONObject("https://api.gemini.com/v1/pubticker/" + pair).getString("last");
        }
    },
    HITBTC("HitBTC") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            if (coin.equals("XRP") && currency.equals("USD")) {
                currency = "USDT";
            }
            return getJSONObject(String.format("https://api.hitbtc.com/api/2/public/ticker/%s%s", coin, currency)).getString("last");
        }
    },
    INDEPENDENT_RESERVE("Independent Reserve", "Ind. Reserve") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            coin = coin.toLowerCase();
            currency = currency.toLowerCase();
            String url = String.format("https://api.independentreserve.com/Public/GetMarketSummary?primaryCurrencyCode=%s&secondaryCurrencyCode=%s", coin, currency);
            return getJSONObject(url).getString("LastPrice");
        }
    },
    ITBIT("ItBit") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            return getJSONObject(String.format("https://api.itbit.com/v1/markets/%s%s/ticker", coin, currency)).getString("lastPrice");
        }
    },
    KOINEX("Koinex") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = "https://koinex.in/api/ticker";
            return getJSONObject(url).getJSONObject("prices").getString(coin);
        }
    },
    KORBIT("Korbit") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            Headers headers = Headers.of("User-Agent", "");
            String pair = String.format("%s_%s", coin, currency).toLowerCase();
            String url = "https://api.korbit.co.kr/v1/ticker?currency_pair=" + pair;
            return getJSONObject(url, headers).getString("last");
        }
    },
    KRAKEN("Kraken") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String resultPair;
            if (coin.equals("DASH") || coin.equals("BCH")) {
                resultPair = coin + currency;
            } else {
                resultPair = "X" + coin + "Z" + currency;
            }

            JSONObject obj = getJSONObject(String.format("https://api.kraken.com/0/public/Ticker?pair=%s", resultPair));
            JSONObject obj2 = obj.getJSONObject("result").getJSONObject(resultPair);
            return (String)obj2.getJSONArray("c").get(0);
        }
    },
    KUNA("KunaBTC") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s%s", coin, currency).toLowerCase();
            JSONObject obj = getJSONObject("https://kuna.io/api/v2/tickers/" + pair);
            return obj.getJSONObject("ticker").getString("last");
        }
    },
    LAKEBTC("LakeBTC") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s%s", coin, currency).toLowerCase();
            JSONObject obj = getJSONObject("https://api.lakebtc.com/api_v2/ticker?symbol=" + pair);
            return obj.getJSONObject(pair).getString("last");
        }
    },
    LUNO("Luno") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.mybitx.com/api/1/ticker?pair=%s%s", coin, currency);
            return getJSONObject(url).getString("last_trade");
        }
    },
    MERCADO("Mercado Bitcoin", "Mercado") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://www.mercadobitcoin.net/api/%s/ticker/", coin);
            return getJSONObject(url).getJSONObject("ticker").getString("last");
        }
    },
    OKCOIN("OK Coin") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String tld = currency.equals("USD") ? "com" : "cn";
            String url = String.format("https://www.okcoin.%s/api/v1/ticker.do?symbol=%s_%s", tld,
                    coin.toLowerCase(), currency.toLowerCase());
            return getJSONObject(url).getJSONObject("ticker").getString("last");
        }
    },
    PARIBU("Paribu") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = "https://www.paribu.com/ticker";
            String pair = String.format("%s_%s", coin, currency);
            return getJSONObject(url).getJSONObject(pair).getString("last");
        }
    },
    PAYMIUM("Paymium") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://paymium.com/api/v1/data/%s/ticker", currency.toLowerCase());
            return getJSONObject(url).getString("price");
        }
    },
    POLONIEX("Poloniex") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s_%s", currency, coin);
            JSONObject obj = getJSONObject("https://poloniex.com/public?command=returnTicker");
            return obj.getJSONObject(pair).getString("last");
        }
    },
    QUADRIGA("QuadrigaCX") {
        @Override
        public String getValue(String code, String currency) throws Exception {
            String pair = String.format("%s_%s", code, currency).toLowerCase();
            String url = "https://api.quadrigacx.com/v2/ticker?book=" + pair;
            return getJSONObject(url).getString("last");
        }
    },
    QUIONE("Quione") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.quoine.com/products/code/CASH/%s%s", coin, currency);
            return getJSONObject(url).getString("last_traded_price");
        }
    },
    SIMPLECOINCZ("Simplecoin.cz") {
        @Override
		public String getValue(String coin, String currency) throws Exception {
            JSONObject obj = getJSONObject("https://www.simplecoin.cz/ticker/");
            String bid = obj.getString("offer");
            String ask = obj.getString("ask");
            return Double.toString((Double.valueOf(bid) + Double.valueOf(ask)) / 2);
        }
    },
    SURBITCOIN("SurBitcoin") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            return getBlinkTradeValue(coin, currency);
        }
    },
    THEROCK("TheRock") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.therocktrading.com/v1/funds/%s%s/ticker", coin, currency);
            return getJSONObject(url).getString("last");
        }
    },
    UPHOLD("Uphold") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.uphold.com/v0/ticker/%s%s", coin, currency);
            JSONObject obj = getJSONObject(url);
            String bid = obj.getString("bid");
            String ask = obj.getString("ask");
            return Double.toString((Double.valueOf(bid) + Double.valueOf(ask)) / 2);
        }
    },
    URDUBIT("UrduBit") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            return getBlinkTradeValue(coin, currency);
        }
    },
    VBTC("VBTC") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            return getBlinkTradeValue(coin, currency);
        }
    },
    WEX("Wex") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s_%s", coin, currency).toLowerCase();
            String url = String.format("https://wex.nz/api/3/ticker/%s", pair);
            return getJSONObject(url).getJSONObject(pair).getString("last");
        }
    },
    WYRE("Wyre") {
        @Override
        public String getValue(String coin, String currencyCode) throws Exception {
            String url = "https://api.sendwyre.com/v2/rates";
            String currency = String.format("%s%s", currencyCode, coin);
            return getJSONObject(url).getString(currency);
        }
    },
    ZEBPAY("Zebpay") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s/%s", coin, currency).toLowerCase();
            String url = "https://www.zebapi.com/api/v1/market/ticker/" + pair;
            return getJSONObject(url).getString("market");
        }
    },
    ZYADO("Zyado") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            return getJSONObject("http://chart.zyado.com/ticker.json").getString("last");
        }
    };

    String getBlinkTradeValue(String coin, String currency) throws Exception {
        String url = String.format("https://api.blinktrade.com/api/v1/%s/ticker?crypto_currency=%s", currency, coin);
        return getJSONObject(url).getString("last");
    }

    private static final List<String> ALL_EXCHANGE_NAMES;

    static {
        ALL_EXCHANGE_NAMES = new ArrayList<>();
        for (Exchange exchange : Exchange.values()) {
            ALL_EXCHANGE_NAMES.add(exchange.name());
        }
    }

    public static List<String> getAllExchangeNames() {
        return new ArrayList<>(ALL_EXCHANGE_NAMES);
    }

    private final String name;
    private final String shortName;

    Exchange(String name) {
        this(name, name);
    }

    Exchange(String name, String shortName) {
        this.name = name;
        this.shortName = shortName != null ? shortName : name;
    }

    public abstract String getValue(String coin, String currency) throws Exception;

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }
}
