package com.brentpanther.bitcoinwidget;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;

import static com.brentpanther.bitcoinwidget.ExchangeHelper.getJsonArray;
import static com.brentpanther.bitcoinwidget.ExchangeHelper.getJsonObject;

enum Exchange {

    ABUCOINS("Abucoins") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.abucoins.com/products/%s%s/stats", coin, currency);
            return getJsonObject(url).get("last").getAsString();
        }
    },
    BIBOX("Bibox") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.bibox.com/v1/mdata?cmd=ticker&pair=%s_%s", coin, currency);
            return getJsonObject(url).getAsJsonObject("result").get("last").getAsString();
        }
    },
    BINANCE("Binance") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.binance.com/api/v3/ticker/price?symbol=%s%s", coin, currency);
            return getJsonObject(url).get("price").getAsString();
        }
    },
    BIT2C("Bit2C") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://www.bit2c.co.il/Exchanges/%sNis/Ticker.json", coin);
            return getJsonObject(url).get("ll").getAsString();
        }
    },
    BITBAY("BitBay") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://bitbay.net/API/Public/%s%s/ticker.json", coin, currency);
            return getJsonObject(url).get("last").getAsString();
        }
    },
    BITCOIN_AVERAGE("Bitcoin Average", "BTC avg") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://apiv2.bitcoinaverage.com/indices/local/ticker/short?crypto=%s&fiats=%s", coin, currency);
            JsonObject obj = getJsonObject(url);
            return obj.getAsJsonObject(String.format("%s%s", coin, currency)).get("last").getAsString();
        }
    },
    BITCOIN_AVERAGE_GLOBAL("Bitcoin Average (global)", "BTC avg global") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://apiv2.bitcoinaverage.com/indices/global/ticker/short?crypto=%s&fiats=%s", coin, currency);
            JsonObject obj = getJsonObject(url);
            return obj.getAsJsonObject(String.format("%s%s", coin, currency)).get("last").getAsString();
        }
    },
    BITCOINDE("Bitcoin.de") {
        @Override
        public String getValue(String code, String currency) throws Exception {
            JsonObject obj = getJsonObject("https://bitcoinapi.de/widget/current-btc-price/rate.json");
            String price = obj.get("price_eur").getAsString();
            String[] amount = price.split("\\u00A0");
            return amount[0].replaceAll("\\.", "").replaceAll(",", ".");
        }
    },
    BITFINEX("Bitfinex") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.bitfinex.com/v2/ticker/t%s%s", coin, currency);
            return getJsonArray(url).get(6).getAsString();
        }
    },
    BITFLYER("BitFlyer") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.bitflyer.jp/v1/ticker?product_code=%s_%s", coin, currency);
            return getJsonObject(url).get("ltp").getAsString();
        }
    },
    BITHUMB("Bithumb") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.bithumb.com/public/ticker/%s", coin);
            JsonObject data = getJsonObject(url).getAsJsonObject("data");
            Long buy = Long.valueOf(data.get("buy_price").getAsString());
            Long sell = Long.valueOf(data.get("sell_price").getAsString());
            return String.valueOf((buy + sell) / 2);
        }
    },
    BITLISH("Bitlish") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = "https://bitlish.com/api/v1/tickers";
            String pair = String.format("%s%s", coin, currency).toLowerCase();
            return getJsonObject(url).getAsJsonObject(pair).get("last").getAsString();
        }
    },
    BITMARKET24("BitMarket24") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://bitmarket24.pl/api/%s_%s/status.json", coin, currency);
            return getJsonObject(url).get("last").getAsString();
        }
    },
    BITMARKETPL("BitMarket.pl") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://www.bitmarket.pl/json/%s%s/ticker.json", coin, currency);
            return getJsonObject(url).get("last").getAsString();
        }
    },
    BITMEX("BitMEX") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://www.bitmex.com/api/v1/instrument?symbol=%s%s&columns=lastPrice", coin, currency);
            return getJsonArray(url).get(0).getAsJsonObject().get("lastPrice").getAsString();
        }
    },
    BITPAY("BitPay") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            JsonArray array = getJsonArray("https://bitpay.com/api/rates");
            for (JsonElement jsonElement : array) {
                JsonObject obj = (JsonObject)jsonElement;
                if(currency.equals(obj.get("code").getAsString())) {
                    return obj.get("rate").getAsString();
                }
            }
            return null;
        }
    },
    BITSO("Bitso") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            JsonArray payload = getJsonObject("https://api.bitso.com/v3/ticker/").getAsJsonArray("payload");
            String pair = String.format("%s_%s", coin, currency).toLowerCase();
            for (JsonElement jsonElement : payload) {
                JsonObject obj = (JsonObject)jsonElement;
                if (obj.get("book").getAsString().equals(pair)) {
                    return obj.get("last").getAsString();
                }
            }
            return null;
        }
    },
    BITSTAMP("Bitstamp") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://www.bitstamp.net/api/v2/ticker/%s%s", coin.toLowerCase(), currency.toLowerCase());
            return getJsonObject(url).get("last").getAsString();
        }
    },
    BITTREX("Bittrex") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s-%s", currency, coin);
            String url = "https://bittrex.com/api/v1.1/public/getticker?market=" + pair;
            return getJsonObject(url).getAsJsonObject("result").get("Last").getAsString();
        }
    },
    BRAZILIEX("Braziliex") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s_%s", coin, currency).toLowerCase();
            String url = "https://braziliex.com/api/v1/public/ticker/" + pair;
            return getJsonObject(url).get("last").getAsString();
        }
    },
    BTCBOX("BTC Box") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = "https://www.btcbox.co.jp/api/v1/ticker/";
            return getJsonObject(url).get("last").getAsString();
        }
    },
    BTCMARKETS("BTC Markets") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.btcmarkets.net/market/%s/%s/tick", coin, currency);
            return getJsonObject(url).get("lastPrice").getAsString();
        }
    },
    BTCTURK("BTCTurk") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            JsonArray array = getJsonArray("https://www.btcturk.com/api/ticker");
            String pair = coin + currency;
            for (JsonElement jsonElement : array) {
                JsonObject obj = (JsonObject)jsonElement;
                if (obj.get("pair").getAsString().equals(pair)) {
                    return obj.get("last").getAsString();
                }
            }
            return null;
        }
    },
    CEXIO("Cex.io") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            return getJsonObject(String.format("https://cex.io/api/last_price/%s/%s", coin, currency)).get("lprice").getAsString();
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
            JsonObject obj = getJsonObject(String.format("https://api.coinbase.com/v2/prices/%s-%s/spot", coin, currency));
            return obj.getAsJsonObject("data").get("amount").getAsString();
        }
    },
    COINBASEPRO("Coinbase Pro") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.pro.coinbase.com/products/%s-%s/ticker", coin, currency);
            return getJsonObject(url).get("price").getAsString();
        }
    },
    COINBE("Coinbe") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            JsonObject obj = getJsonObject("https://coinbe.net/public/graphs/ticker/ticker.json");
            String pair = String.format("%s_%s", currency, coin);
            return obj.getAsJsonObject(pair).get("last").getAsString();
        }
    },
    COINBOOK("Coinbook") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = "https://coinbook.com/api/SimpleBitcoinWidget/price";
            return getJsonObject(url).get("Bitcoin Price").getAsString();
        }
    },
    COINDELTA("Coindelta") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = "https://coindelta.com/api/v1/public/getticker/";
            String pair = String.format("%s-%s", coin, currency).toLowerCase();
            JsonArray array = getJsonArray(url);
            for (JsonElement jsonElement : array) {
                JsonObject obj = (JsonObject)jsonElement;
                if (obj.get("MarketName").getAsString().equals(pair)) {
                    return obj.get("Last").getAsString();
                }
            }
            return null;
        }
    },
    COINDESK("Coindesk") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.coindesk.com/v1/bpi/currentprice/%s.json", currency);
            return getJsonObject(url).getAsJsonObject("bpi").getAsJsonObject(currency).get("rate_float").getAsString();
        }
    },
    COINEGG("CoinEgg") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.coinegg.im/api/v1/ticker/region/%s?coin=%s", currency.toLowerCase(), coin.toLowerCase());
            return getJsonObject(url).get("last").getAsString();
        }
    },
    COINJAR("CoinJar") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = "https://api.coinjar.com/v3/exchange_rates";
            String pair = String.format("%s%s", coin, currency);
            return getJsonObject(url).getAsJsonObject("exchange_rates").getAsJsonObject(pair).get("midpoint").getAsString();
        }
    },
    COINMARKETCAP("CoinMarketCap") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            // hard coded ids of each coin :(
            Map<String, Integer> map = new HashMap<String, Integer>() {{
                put("BTC", 1);
                put("ETH", 1027);
                put("XRP", 52);
                put("BCH", 1831);
                put("LTC", 2);
                put("NEO", 1376);
                put("ADA", 2010);
                put("XLM", 512);
                put("MIOTA", 1720);
                put("DASH", 131);
                put("XMR", 328);
                put("XEM", 873);
                put("NANO", 1567);
                put("BTG", 2083);
                put("ETC", 1321);
                put("ZEC", 1437);
            }};
            int id = map.get(coin);
            String url = String.format("https://api.coinmarketcap.com/v2/ticker/%s/?convert=%s", id, currency);
            JsonObject quotes = getJsonObject(url).getAsJsonObject("data").getAsJsonObject("quotes");
            return quotes.getAsJsonObject(currency).get("price").getAsString();
        }
    },
    COINMATE("CoinMate.io") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://coinmate.io/api/ticker?currencyPair=%s_%s", coin, currency);
            JsonObject obj = getJsonObject(url);
            return obj.getAsJsonObject("data").get("last").getAsString();
        }
    },
    COINNEST("Coinnest") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            coin = coin.toLowerCase();
            String url = "https://api.coinnest.co.kr/api/pub/ticker?coin=" + coin;
            return getJsonObject(url).get("last").getAsString();
        }
    },
    COINONE("Coinone") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = "https://api.coinone.co.kr/ticker/?currency=" + coin;
            return getJsonObject(url).get("last").getAsString();
        }
    },
    COINROOM("Coinroom") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://coinroom.com/api/ticker/%s/%s", coin, currency);
            return getJsonObject(url).get("last").getAsString();
        }
    },
    COINSECURE("Coinsecure") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = "https://api.coinsecure.in/v1/exchange/ticker";
            return String.valueOf(getJsonObject(url).getAsJsonObject("message").get("lastPrice").getAsLong() / 100);
        }
    },
    COINSQUARE("Coinsquare") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = "https://coinsquare.io/api/v1/data/quotes";
            JsonArray array = getJsonObject(url).getAsJsonArray("quotes");
            for (JsonElement jsonElement : array) {
                JsonObject obj = (JsonObject)jsonElement;
                if (!obj.get("ticker").getAsString().equals(coin)) continue;
                if (!obj.get("base").getAsString().equals(currency)) continue;
                return obj.get("last").getAsString();
            }
            return null;
        }
    },
    COINTREE("Cointree") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s/%s", coin, currency).toLowerCase();
            return getJsonObject("https://www.cointree.com.au/api/price/" + pair).get("Spot").getAsString();
        }
    },
    COINSPH("Coins.ph") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://quote.coins.ph/v1/markets/%s-%s", coin, currency);
            JsonObject obj = getJsonObject(url).getAsJsonObject("market");
            String bid = obj.get("bid").getAsString();
            String ask = obj.get("ask").getAsString();
            return Double.toString((Double.valueOf(bid) + Double.valueOf(ask)) / 2);
        }
    },
    CRYPTONIT("Cryptonit") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            coin = coin.toLowerCase();
            currency = currency.toLowerCase();
            String url = String.format("https://cryptonit.net/apiv2/rest/public/ccorder.json?bid_currency=%s&ask_currency=%s&ticker", coin, currency);
            JsonObject obj = getJsonObject(url);
            return obj.getAsJsonObject("rate").get("last").getAsString();
        }
    },
    CRYPTOPIA("Cryptopia") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://www.cryptopia.co.nz/api/GetMarket/%s_%s", coin, currency);
            return getJsonObject(url).getAsJsonObject("Data").get("LastPrice").getAsString();
        }
    },
    EXMO("Exmo") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s_%s", coin, currency);
            String url = "https://api.exmo.com/v1/ticker/";
            return getJsonObject(url).getAsJsonObject(pair).get("last_trade").getAsString();
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
            JsonArray tickers = getJsonObject("https://api.gatecoin.com/Public/LiveTickers").getAsJsonArray("tickers");
            String pair = coin + currency;
            for (JsonElement jsonElement : tickers) {
                JsonObject obj = (JsonObject)jsonElement;
                if (obj.get("currencyPair").getAsString().equals(pair)) {
                    return obj.get("last").getAsString();
                }
            }
            return null;
        }
    },
    GATEIO("Gate.io") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s_%s", coin, currency).toLowerCase();
            String url = String.format("https://data.gate.io/api2/1/ticker/%s", pair);
            return getJsonObject(url).get("last").getAsString();
        }
    },
    GEMINI("Gemini") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s%s", coin, currency).toLowerCase();
            return getJsonObject("https://api.gemini.com/v1/pubticker/" + pair).get("last").getAsString();
        }
    },
    HITBTC("HitBTC") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            if (coin.equals("XRP") && currency.equals("USD")) {
                currency = "USDT";
            }
            return getJsonObject(String.format("https://api.hitbtc.com/api/2/public/ticker/%s%s", coin, currency)).get("last").getAsString();
        }
    },
    HUOBI("Huobi") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s%s", coin, currency).toLowerCase();
            String url = String.format("https://api.huobi.pro/market/detail/merged?symbol=%s", pair);
            JsonObject tick = getJsonObject(url).getAsJsonObject("tick");
            double ask = tick.getAsJsonArray("ask").get(0).getAsDouble();
            double bid = tick.getAsJsonArray("bid").get(0).getAsDouble();
            return Double.toString((ask + bid) / 2);
        }
    },
    INDEPENDENT_RESERVE("Independent Reserve", "Ind. Reserve") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            coin = coin.toLowerCase();
            currency = currency.toLowerCase();
            String url = String.format("https://api.independentreserve.com/Public/GetMarketSummary?primaryCurrencyCode=%s&secondaryCurrencyCode=%s", coin, currency);
            return getJsonObject(url).get("LastPrice").getAsString();
        }
    },
    INDODAX("Indodax") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s_%s", coin, currency).toLowerCase();
            String url = String.format("https://indodax.com/api/%s/ticker", pair);
            return getJsonObject(url).getAsJsonObject("ticker").get("last").getAsString();
        }
    },
    ITBIT("ItBit") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            return getJsonObject(String.format("https://api.itbit.com/v1/markets/%s%s/ticker", coin, currency)).get("lastPrice").getAsString();
        }
    },
    KOINEX("Koinex") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = "https://koinex.in/api/ticker";
            return getJsonObject(url).getAsJsonObject("prices").getAsJsonObject(currency.toLowerCase()).get(coin).getAsString();
        }
    },
    KORBIT("Korbit") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            Headers headers = Headers.of("User-Agent", "");
            String pair = String.format("%s_%s", coin, currency).toLowerCase();
            String url = "https://api.korbit.co.kr/v1/ticker?currency_pair=" + pair;
            return getJsonObject(url, headers).get("last").getAsString();
        }
    },
    KRAKEN("Kraken") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            JsonObject obj = getJsonObject(String.format("https://api.kraken.com/0/public/Ticker?pair=%s%s", coin, currency));
            JsonObject obj2 = obj.getAsJsonObject("result");
            String key = obj2.keySet().iterator().next();
            return obj2.getAsJsonObject(key).getAsJsonArray("c").get(0).getAsString();
        }
    },
    KUCOIN("Kucoin") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.kucoin.com/v1/open/tick?symbol=%s-%s", coin, currency);
            return getJsonObject(url).getAsJsonObject("data").get("lastDealPrice").getAsString();
        }
    },
    KUNA("KunaBTC") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s%s", coin, currency).toLowerCase();
            JsonObject obj = getJsonObject("https://kuna.io/api/v2/tickers/" + pair);
            return obj.getAsJsonObject("ticker").get("last").getAsString();
        }
    },
    LAKEBTC("LakeBTC") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s%s", coin, currency).toLowerCase();
            JsonObject obj = getJsonObject("https://api.lakebtc.com/api_v2/ticker?symbol=" + pair);
            return obj.getAsJsonObject(pair).get("last").getAsString();
        }
    },
    LBANK("LBank") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s_%s", coin, currency).toLowerCase();
            String url = String.format("https://api.lbkex.com/v1/ticker.do?symbol=%s", pair);
            return getJsonObject(url).getAsJsonObject("ticker").get("latest").getAsString();
        }
    },
    LIVECOIN("Livecoin") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.livecoin.net/exchange/ticker?currencyPair=%s/%s", coin, currency);
            return getJsonObject(url).get("last").getAsString();
        }
    },
    LUNO("Luno") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.mybitx.com/api/1/ticker?pair=%s%s", coin, currency);
            return getJsonObject(url).get("last_trade").getAsString();
        }
    },
    MERCADO("Mercado Bitcoin", "Mercado") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://www.mercadobitcoin.net/api/%s/ticker/", coin);
            return getJsonObject(url).getAsJsonObject("ticker").get("last").getAsString();
        }
    },
    NEGOCIECOINS("NegocieCoins", "Negocie") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://broker.negociecoins.com.br/api/v3/%s%s/ticker", coin, currency);
            return getJsonObject(url).get("last").getAsString();
        }
    },
    NEXCHANGE("Nexchange") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.nexchange.io/en/api/v1/price/%s%s/latest/?format=json", coin, currency);
            JsonObject ticker = getJsonArray(url).get(0).getAsJsonObject().getAsJsonObject("ticker");
            String ask = ticker.get("ask").getAsString();
            String bid = ticker.get("bid").getAsString();
            return Double.toString((Double.valueOf(ask) + Double.valueOf(bid)) / 2);
        }
    },
    OKCOIN("OK Coin") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String tld = currency.equals("USD") ? "com" : "cn";
            String url = String.format("https://www.okcoin.%s/api/v1/ticker.do?symbol=%s_%s", tld,
                    coin.toLowerCase(), currency.toLowerCase());
            return getJsonObject(url).getAsJsonObject("ticker").get("last").getAsString();
        }
    },
    OKEX("OKEx") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s_%s", coin, currency).toLowerCase();
            String url = String.format("https://www.okex.com/api/v1/ticker.do?symbol=%s", pair);
            return getJsonObject(url).getAsJsonObject("ticker").get("last").getAsString();
        }
    },
    PARIBU("Paribu") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = "https://www.paribu.com/ticker";
            String pair = String.format("%s_%s", coin, currency);
            return getJsonObject(url).getAsJsonObject(pair).get("last").getAsString();
        }
    },
    PAYMIUM("Paymium") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://paymium.com/api/v1/data/%s/ticker", currency.toLowerCase());
            return getJsonObject(url).get("price").getAsString();
        }
    },
    POLONIEX("Poloniex") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s_%s", currency, coin);
            JsonObject obj = getJsonObject("https://poloniex.com/public?command=returnTicker");
            return obj.getAsJsonObject(pair).get("last").getAsString();
        }
    },
    QUADRIGA("QuadrigaCX") {
        @Override
        public String getValue(String code, String currency) throws Exception {
            String pair = String.format("%s_%s", code, currency).toLowerCase();
            String url = "https://api.quadrigacx.com/v2/ticker?book=" + pair;
            return getJsonObject(url).get("last").getAsString();
        }
    },
    QUOINE("Quoine") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.quoine.com/products/code/CASH/%s%s", coin, currency);
            return getJsonObject(url).get("last_traded_price").getAsString();
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
            return getJsonObject(url).get("last").getAsString();
        }
    },
    TRADESATOSHI("Trade Satoshi") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s_%s", coin, currency);
            String url = String.format("https://tradesatoshi.com/api/public/getticker?market=%s", pair);
            return getJsonObject(url).getAsJsonObject("result").get("last").getAsString();
        }
    },
    UPHOLD("Uphold") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String url = String.format("https://api.uphold.com/v0/ticker/%s%s", coin, currency);
            JsonObject obj = getJsonObject(url);
            String bid = obj.get("bid").getAsString();
            String ask = obj.get("ask").getAsString();
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

    WYRE("Wyre") {
        @Override
        public String getValue(String coin, String currencyCode) throws Exception {
            String url = "https://api.sendwyre.com/v2/rates";
            String currency = String.format("%s%s", currencyCode, coin);
            return getJsonObject(url).get(currency).getAsString();
        }
    },
    YOBIT("YoBit") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            String pair = String.format("%s_%s", coin, currency).toLowerCase();
            String url = String.format("https://yobit.net/api/3/ticker/%s", pair);
            return getJsonObject(url).getAsJsonObject(pair).get("last").getAsString();
        }
    },
    ZYADO("Zyado") {
        @Override
        public String getValue(String coin, String currency) throws Exception {
            return getJsonObject("http://chart.zyado.com/ticker.json").get("last").getAsString();
        }
    };

    String getBlinkTradeValue(String coin, String currency) throws Exception {
        String url = String.format("https://api.blinktrade.com/api/v1/%s/ticker?crypto_currency=%s", currency, coin);
        return getJsonObject(url).get("last").getAsString();
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
