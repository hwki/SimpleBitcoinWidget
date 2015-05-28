package com.brentpanther.bitcoinwidget;

import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.security.KeyStore;

public enum BTCProvider {

    MTGOX(R.array.currencies_mtgox, "mtgx") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return null;
        }
    },
    COINBASE(R.array.currencies_coinbase, "cb") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject(String.format("https://coinbase.com/api/v1/prices/spot_rate?currency=%s", currencyCode));
            return obj.getString("amount");
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
            JSONObject obj = getJSONObject(String.format("https://api.bitcoinaverage.com/ticker/%s", currencyCode));
            return obj.getString("last");
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
            return amount[0].replaceAll(",", ".");
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
            JSONObject obj = getJSONObject(String.format("https://api.bitcoinaverage.com/ticker/global/%s", currencyCode));
            return obj.getString("last");
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
    VIRTEX(R.array.currencies_virtex, "vrtx") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            JSONObject obj = getJSONObject("https://cavirtex.com/api2/ticker.json");
            return obj.getJSONObject("ticker").getJSONObject("BTCCAD").getString("last");
        }
    },
    JUSTCOIN(R.array.currencies_justcoin, "jstcn") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = String.format("https://justcoin.com/api/2/BTC%s/money/ticker", currencyCode);
            JSONObject obj = getJSONObject(url);
            return obj.getJSONObject("data").getJSONObject("last").getString("value");
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
            JSONObject obj = getJSONObject("http://market.huobi.com/staticmarket/ticker_btc_json.js");
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
    CRYPTSY(R.array.currencies_cryptsy, "crpsy") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return getJSONObject("https://www.cryptsy.com/trades/ajaxlasttrades").getString("2");
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
    BUTTERCOIN(R.array.currencies_buttercoin, "btrcn") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            return null;
        }
    },
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
    GATECOIN(R.array.currencies_gatecoin, "gate") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://www.gatecoin.com/api/Public/LiveTicker/BTC%s";
            return getJSONObject(String.format(url, currencyCode)).getJSONObject("ticker").getString("last");
        }
    },
    MEXBT(R.array.currencies_mexbt, "mexbt") {
        @Override
        public String getValue(String currencyCode) throws Exception {
            String url = "https://data.mexbt.com/ticker/btc%s";
            return getJSONObject(String.format(url, currencyCode)).getString("last");
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
        HttpGet get = new HttpGet(url);

        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);
        MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        HttpProtocolParams.setUserAgent(params, "SimpleBitcoinWidget/1.0");

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", sf, 443));

        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
        DefaultHttpClient client = new DefaultHttpClient(ccm, params);
        client.setCookieStore(new BasicCookieStore());

        return client.execute(get, new BasicResponseHandler());
    }

    public abstract String getValue(String currencyCode) throws Exception;

    public int getCurrencies() {
        return currencyArrayID;
    }

    public String getLabel() {
        return label;
    }

}
