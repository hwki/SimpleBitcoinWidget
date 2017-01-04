package com.brentpanther.bitcoinwidget;



import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public enum BTCProvider {

    //NO LONGER EXISTS
    MTGOX(R.array.currencies_mtgox, "mtgx") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return null;
        }
    },
    COINBASE(R.array.currencies_coinbase, "cb") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject(String.format("https://api.coinbase.com/v2/prices/BTC-%s/spot", currencyCode));
            return obj.getJSONObject("data").getString("amount");
        }
    },
    BITSTAMP(R.array.currencies_bitstamp, "btsmp") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://www.bitstamp.net/api/ticker/");
            return obj.getString("last");
        }
    },
    BITCOIN_AVERAGE(R.array.currencies_bitcoinaverage, "btavg") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://apiv2.bitcoinaverage.com/indices/local/ticker/short?crypto=BTC&fiats=%s", currencyCode);
            JSONObject obj = getJSONObject(url);
            return obj.getJSONObject(String.format("BTC%s", currencyCode)).getString("last");
        }
    },
    CAMPBX(R.array.currencies_campbx, "cmpbx") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("http://campbx.com/api/xticker.php");
            return obj.getString("Last Trade");
        }
    },
    BTCE(R.array.currencies_btce, "btce") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject(String.format("https://btc-e.com/api/2/btc_%s/ticker", currencyCode.toLowerCase()));
            return obj.getJSONObject("ticker").getString("last");
        }
    },
    MERCADO(R.array.currencies_mercado, "merc") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("http://www.mercadobitcoin.com.br/api/ticker/");
            return obj.getJSONObject("ticker").getString("last");
        }
    },
    BITCOINDE(R.array.currencies_bitcoinde, "bt.de") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://bitcoinapi.de/widget/current-btc-price/rate.json");
            String price = obj.getString("price_eur");
            String[] amount = price.split("\\s");
            return amount[0].replaceAll("\\.", "").replaceAll(",", ".");
        }
    },
    BITCUREX(R.array.currencies_bitcurex, "btcrx") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject(String.format("https://bitcurex.com/api/%s/ticker.json", currencyCode));
            StringBuilder sb = new StringBuilder(obj.getString("last_tx_price"));
            return sb.insert(sb.length()-4, ".").toString();
        }
    },
    BITFINEX(R.array.currencies_bitfinex, "btfnx") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://api.bitfinex.com/v1/ticker/btcusd");
            return obj.getString("last_price");
        }
    },
    BITCOIN_AVERAGE_GLOBAL(R.array.currencies_bitcoinaverage_global, "gbtav") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://apiv2.bitcoinaverage.com/indices/global/ticker/short?crypto=BTC&fiats=%s", currencyCode);
            JSONObject obj = getJSONObject(url);
            return obj.getJSONObject(String.format("BTC%s", currencyCode)).getString("last");
        }
    },
    BTC_CHINA(R.array.currencies_btcchina, "btchn") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://data.btcchina.com/data/ticker?market=btccny");
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
    BITPAY(R.array.currencies_bitpay, "btpay") {
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
    KRAKEN(R.array.currencies_kraken, "krkn") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject(String.format("https://api.kraken.com/0/public/Ticker?pair=XBT%s", currencyCode));
            JSONObject obj2 = obj.getJSONObject("result").getJSONObject("XXBTZ" + currencyCode);
            return (String)obj2.getJSONArray("c").get(0);
        }
    },
    BTCTURK(R.array.currencies_btcturk, "turk") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://www.btcturk.com/api/ticker");
            return obj.getString("last");
        }
    },
    //NO LONGER EXISTS
    VIRTEX(R.array.currencies_virtex, "vrtx") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return null;
        }
    },
    JUSTCOIN(R.array.currencies_justcoin, "jstcn") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return null;
        }
    },
    KUNA(R.array.currencies_kuna, "kuna") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getString("http://kuna.com.ua/index/kunabtc.index.php");
        }
    },
    LAKEBTC(R.array.currencies_lake, "lake") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://www.lakebtc.com/api_v1/ticker");
            return obj.getJSONObject(currencyCode).getString("last");
        }
    },
    CRYPTONIT(R.array.currencies_cryptonit, "crypt") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("http://cryptonit.net/apiv2/rest/public/ccorder.json?bid_currency=usd&ask_currency=btc&ticker");
            return obj.getJSONObject("rate").getString("last");
        }
    },
    COINTREE(R.array.currencies_cointree, "tree") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://www.cointree.com.au/api/price/btc/aud").getString("Spot");
        }
    },
    BTCMARKETS(R.array.currencies_btcmarkets, "bmkts") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://api.btcmarkets.net/market/BTC/AUD/tick").getString("lastPrice");
        }
    },
    HUOBI(R.array.currencies_huobi, "huobi") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("http://api.huobi.com/staticmarket/ticker_btc_json.js");
            return obj.getJSONObject("ticker").getString("last");
        }
    },
    KORBIT(R.array.currencies_korbit, "krbt") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://api.korbit.co.kr/v1/ticker/detailed").getString("last");
        }
    },
    PAYMIUM(R.array.currencies_paymium, "paym") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://paymium.com/api/v1/data/eur/ticker").getString("price");
        }
    },
    BITSO(R.array.currencies_bitso, "bitso") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://api.bitso.com/public/info").getJSONObject("btc_mxn").getString("rate");
        }
    },
    ZYADO(R.array.currencies_zyado, "zyd") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("http://chart.zyado.com/ticker.json").getString("last");
        }
    },
    //GONE
    CRYPTSY(R.array.currencies_cryptsy, "crpsy") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return null;
        }
    },
    BITBAY(R.array.currencies_bitbay, "btbay") {
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
    BTCXCHANGE(R.array.currencies_btcxchange, "btxch") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return null;
        }
    },
    OKCOIN(R.array.currencies_okcoin, "ok") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            if("USD".equals(currencyCode)) {
                return getJSONObject("https://www.okcoin.com/api/ticker.do?ok=1").getJSONObject("ticker").getString("last");
            } else if("CNY".equals(currencyCode)) {
                return getJSONObject("https://www.okcoin.cn/api/ticker.do?ok=1").getJSONObject("ticker").getString("last");
            }
            return null;
        }
    },
    HITBTC(R.array.currencies_hitbtc, "hit") {
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
    BITCOINCOID(R.array.currencies_bitcoincoid, "coid") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://vip.bitcoin.co.id/api/BTC_IDR/ticker/").getJSONObject("ticker").getString("last");
        }
    },
    FOXBIT(R.array.currencies_foxbit, "fxbt") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://api.blinktrade.com/api/v1/BRL/ticker?crypto_currency=BTC").getString("last");
        }
    },
    INDEPENDENT_RESERVER(R.array.currencies_independentreserve, "ir") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://api.independentreserve.com/Public/GetMarketSummary?primaryCurrencyCode=xbt&secondaryCurrencyCode=%s";
            return getJSONObject(String.format(url, currencyCode)).getString("LastPrice");
        }
    },
    //NO LONGER EXISTS
    BUTTERCOIN(R.array.currencies_buttercoin, "btrcn") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return null;
        }
    },
    //NO LONGER EXISTS
    CLEVERCOIN(R.array.currencies_clevercoin, "clvr") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://api.clevercoin.com/v1/ticker").getString("last");
        }
    },
    BITMARKET24(R.array.currencies_bitmarket24, "bm24") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://bitmarket24.pl/api/BTC_PLN/status.json").getString("last");
        }
    },
    QUADRIGA(R.array.currencies_quadriga, "qdrga") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://api.quadrigacx.com/v2/ticker?book=BTC_%s";
            return getJSONObject(String.format(url, currencyCode)).getString("last");
        }
    },
    //OFFLINE
    GATECOIN(R.array.currencies_gatecoin, "gate") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return null;
        }
    },
    //OFFLINE
    MEXBT(R.array.currencies_mexbt, "mexbt") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://data.mexbt.com/ticker/btc%s";
            return getJSONObject(String.format(url, currencyCode.toLowerCase(Locale.US))).getString("last");
        }
    },
    BITX(R.array.currencies_bitx, "bitx") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://coinsbank.com/api/public/ticker?pair=BTC%s";
            return getJSONObject(String.format(url, currencyCode)).getJSONObject("data").getString("last");
        }
    },
    BTCBOX(R.array.currencies_btcbox, "box") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://www.btcbox.co.jp/api/v1/ticker/";
            return getJSONObject(url).getString("last");
        }
    },
    BTCXINDIA(R.array.currencies_btcxindia, "india") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://api.btcxindia.com/ticker/";
            return getJSONObject(url).getString("last_traded_price");
        }
    },
    UPHOLD(R.array.currencies_uphold, "uphld") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://api.uphold.com/v0/ticker/BTC%s";
            JSONObject obj = getJSONObject(String.format(url, currencyCode));
            String bid = obj.getString("bid");
            String ask = obj.getString("ask");
            return Double.toString((Double.valueOf(bid) + Double.valueOf(ask)) / 2);
        }
    };


    private final int currencyArrayID;
    private String label;

    BTCProvider(int currencyArrayID, String label) {
        this.currencyArrayID = currencyArrayID;
        this.label = label;
    }

    private static String getFromBitcoinCharts(String symbol) throws Exception {
        JSONArray array = getJSONArray("http://api.bitcoincharts.com/v1/markets.json");
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            if (!symbol.equals(obj.getString("symbol"))) continue;
            return obj.getString("avg");
        }
        return null;
    }

    private static JSONObject getJSONObject(String url) throws Exception {
        return new JSONObject(getString(url));
    }

    private static JSONArray getJSONArray(String url) throws Exception {
        return new JSONArray(getString(url));
    }

    @SuppressWarnings("deprecation")
    private static String getString(String url) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder()
                .followRedirects(true)
                .readTimeout(15, TimeUnit.SECONDS)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                }).build();
        Request request = new Request.Builder()
                .addHeader("User-Agent", "curl/7.43.0")
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public abstract String getValue(String currencyCode) throws Exception;

    public int getCurrencies() {
        return currencyArrayID;
    }

    public String getLabel() {
        return label;
    }

}
