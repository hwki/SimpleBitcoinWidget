package com.brentpanther.bitcoinwidget.exchange

import com.brentpanther.bitcoinwidget.Coin
import com.brentpanther.bitcoinwidget.exchange.ExchangeHelper.asString
import com.brentpanther.bitcoinwidget.exchange.ExchangeHelper.getJsonArray
import com.brentpanther.bitcoinwidget.exchange.ExchangeHelper.getJsonObject
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Headers
import kotlin.math.pow

enum class Exchange(val exchangeName: String, shortName: String? = null) {

    ASCENDEX("AscendEX") {
        override fun getValue(coin: String, currency: String): String? {
            val obj = getJsonObject("https://ascendex.com/api/pro/v1/ticker?symbol=$coin/$currency")
            return obj["data"]?.jsonObject?.get("close").asString
        }
    },
    BIBOX("Bibox") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.bibox.com/v3/mdata/market?pair=${coin}_$currency"
            return getJsonObject(url)["result"]?.jsonObject?.get("last").asString
        }
    },
    BIGONE("BigONE") {
        override fun getValue(coin: String, currency: String): String? {
            val url = "https://big.one/api/v3/asset_pairs/$coin-$currency/ticker"
            val obj = getJsonObject(url)["data"]?.jsonObject ?: return null
            val bid = obj["bid"]?.jsonObject?.get("price").asString?.toDoubleOrNull() ?: return null
            val ask = obj["ask"]?.jsonObject?.get("price").asString?.toDoubleOrNull() ?: return null
            return ((bid + ask) / 2).toString()
        }
    },
    BINANCE("Binance.com") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.binance.com/api/v3/ticker/price?symbol=$coin$currency"
            return getJsonObject(url)["price"].asString
        }
    },
    BINANCE_US("Binance.us") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.binance.us/api/v3/ticker/price?symbol=$coin$currency"
            return getJsonObject(url)["price"].asString
        }
    },
    BINGX("BingX") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://open-api.bingx.com/openApi/swap/v2/quote/ticker?symbol=$coin-${currency}"
            return getJsonObject(url)["data"]?.jsonObject?.get("lastPrice").asString
        }
    },
    BIT2C("Bit2C") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://www.bit2c.co.il/Exchanges/${coin}Nis/Ticker.json"
            return getJsonObject(url)["ll"].asString
        }
    },
    BITBANK("Bitbank") {
        override fun getValue(coin: String, currency: String): String? {
            val pair = "${coin}_$currency".lowercase()
            val url = "https://public.bitbank.cc/$pair/ticker"
            return getJsonObject(url)["data"]?.jsonObject?.get("last").asString
        }
    },
    BITCLUDE("BitClude") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.bitclude.com/stats/ticker.json"
            val obj = getJsonObject(url)
            return obj["${coin.lowercase()}_${currency.lowercase()}"]?.jsonObject?.get("last").asString
        }
    },
    BITCOINDE("Bitcoin.de") {

        override fun getValue(coin: String, currency: String): String? {
            val obj = getJsonObject("https://bitcoinapi.de/widget/current-btc-price/rate.json")
            val price = obj["price_eur"].asString
            val amount = price?.split("\\u00A0".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray() ?: return null
            return amount[0].replace("\\.".toRegex(), "").replace(",".toRegex(), ".")
        }
    },
    BITFINEX("Bitfinex") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api-pub.bitfinex.com/v2/ticker/t$coin$currency"
            return getJsonArray(url)[6].asString
        }
    },
    BITFLYER("bitFlyer") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.bitflyer.com/v1/ticker?product_code=${coin}_$currency"
            return getJsonObject(url)["ltp"].asString
        }
    },
    BITGLOBAL("BitGlobal") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://global-openapi.bithumb.pro/openapi/v1/spot/ticker?symbol=$coin-$currency"
            return getJsonObject(url)["data"]?.jsonArray?.get(0)?.jsonObject?.get("c")?.asString
        }
    },
    BITHUMB("Bithumb") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.bithumb.com/public/ticker/${coin}_$currency"
            val data = getJsonObject(url)["data"]?.jsonObject ?: return null
            val buy = data["opening_price"].asString?.toDoubleOrNull() ?: return null
            val sell = data["closing_price"].asString?.toDoubleOrNull() ?: return null
            return ((buy + sell) / 2).toString()
        }
    },
    BITMART("BitMart") {
        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api-cloud.bitmart.com/spot/v1/ticker?symbol=${coin}_$currency"
            val tickers = getJsonObject(url)["data"]?.jsonObject?.get("tickers")?.jsonArray ?: return null
            return tickers[0].jsonObject["last_price"].asString
        }
    },
    BITPANDA("Bitpanda") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.bitpanda.com/v1/ticker"
            return getJsonObject(url)[coin]?.jsonObject?.get(currency).asString
        }
    },
    BITPAY("BitPay") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://bitpay.com/api/rates/$coin/$currency"
            return getJsonObject(url)["rate"].asString
        }
    },
    BITSO("Bitso") {

        override fun getValue(coin: String, currency: String): String? {
            val payload = getJsonObject("https://api.bitso.com/v3/ticker/")["payload"]
                ?.jsonArray ?: return null
            val pair = "${coin}_$currency".lowercase()
            for (jsonElement in payload) {
                val obj = jsonElement as JsonObject
                if (obj["book"].asString == pair) {
                    return obj["last"].asString
                }
            }
            return null
        }
    },
    BITSTAMP("Bitstamp") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://www.bitstamp.net/api/v2/ticker/${coin.lowercase()}${
                currency.lowercase()
            }"
            return getJsonObject(url)["last"].asString
        }
    },
    BITTREX("Bittrex") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.bittrex.com/v3/markets/$coin-$currency/ticker"
            return getJsonObject(url)["lastTradeRate"].asString
        }
    },
    BITRUE("Bitrue") {
        override fun getValue(coin: String, currency: String): String? {
            val url = "https://openapi.bitrue.com/api/v1/ticker/price?symbol=$coin$currency"
            return getJsonObject(url)["price"].asString
        }
    },
    BITVAVO ("Bitvavo") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.bitvavo.com/v2/ticker/price?market=$coin-$currency"
            return getJsonObject(url)["price"].asString
        }

    },
    BTCBOX("BTC Box") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://www.btcbox.co.jp/api/v1/ticker/?coin=${coin.lowercase()}"
            return getJsonObject(url)["last"].asString
        }
    },
    BTCMARKETS("BTC Markets") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.btcmarkets.net/v3/markets/$coin-$currency/ticker"
            return getJsonObject(url)["lastPrice"].asString
        }
    },
    BTCTURK("BTCTurk") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.btcturk.com/api/v2/ticker?pairSymbol=${coin}_$currency"
            val array = getJsonObject(url)["data"]?.jsonArray ?: return null
            return array[0].jsonObject["last"].asString
        }
    },
    BYBIT("Bybit") {
        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.bybit.com/v5/market/tickers?category=spot&symbol=$coin$currency"
            return getJsonObject(url)["result"]?.jsonObject?.get("list")?.jsonArray?.get(0)?.jsonObject?.get("lastPrice").asString
        }
    },
    CEXIO("Cex.io") {

        override fun getValue(coin: String, currency: String): String? {
            return getJsonObject("https://cex.io/api/last_price/$coin/$currency")["lprice"].asString
        }
    },
    CHILEBIT("ChileBit.net") {

        override fun getValue(coin: String, currency: String): String? {
            return getBlinkTradeValue(coin, currency)
        }
    },
    COINBASE("Coinbase") {

        override fun getValue(coin: String, currency: String): String? {
            val obj = getJsonObject("https://api.coinbase.com/v2/prices/$coin-$currency/spot")
            return obj["data"]?.jsonObject?.get("amount").asString
        }
    },
    COINBASEPRO("Coinbase Pro") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.pro.coinbase.com/products/$coin-$currency/ticker"
            return getJsonObject(url)["price"].asString
        }
    },
    COINDESK("Coindesk") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.coindesk.com/v1/bpi/currentprice/$currency.json"
            return getJsonObject(url)["bpi"]?.jsonObject?.get(currency)?.jsonObject?.get("rate_float").asString
        }
    },
    COINGECKO("CoinGecko") {

        override fun getValue(coin: String, currency: String): String? {
            val id = Coin.getByName(coin)?.coinGeckoId ?: coin
            val vs = currency.lowercase()
            val url = "https://api.coingecko.com/api/v3/simple/price?ids=$id&vs_currencies=$vs"
            return getJsonObject(url)[id]?.jsonObject?.get(vs).asString
        }
    },
    COINJAR("CoinJar") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://data.exchange.coinjar.com/products/$coin$currency/ticker"
            return getJsonObject(url)["last"].asString
        }
    },
    COINMATE("CoinMate.io") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://coinmate.io/api/ticker?currencyPair=${coin}_$currency"
            val obj = getJsonObject(url)
            return obj["data"]?.jsonObject?.get("last").asString
        }
    },
    COINONE("Coinone") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.coinone.co.kr/public/v2/ticker_new/$currency/$coin"
            return getJsonObject(url)["tickers"]?.jsonArray?.get(0)?.jsonObject?.get("last").asString
        }
    },
    COINSBIT("Coinsbit") {
        override fun getValue(coin: String, currency: String): String? {
            val coinName = if (coin == "WBTC") "wBTC" else coin
            val url = "https://coinsbit.io/api/v1/public/ticker?market=${coinName}_$currency"
            return getJsonObject(url)["result"]?.jsonObject?.get("last").asString
        }
    },
    COINSPH("Coins.ph") {

        override fun getValue(coin: String, currency: String): String? {
            val region = if (currency == "THB") "TH" else "PH"
            val url = "https://quote.coins.ph/v2/markets/$coin-$currency?region=$region"
            val obj = getJsonObject(url)
            val bid = obj["bid"].asString?.toDoubleOrNull() ?: return null
            val ask = obj["ask"].asString?.toDoubleOrNull() ?: return null
            return ((bid + ask) / 2).toString()
        }
    },
    COINTREE("Cointree") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://trade.cointree.com/api/prices/AUD/change/24h?symbols=$coin"
            return getJsonArray(url)[0].jsonObject["spot"].asString
        }
    },
    CRYPTO("Crypto.com") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.crypto.com/v2/public/get-ticker?instrument_name=${coin}_$currency"
            return getJsonObject(url)["result"]?.jsonObject?.get("data")?.jsonArray?.get(0)?.jsonObject?.get("a").asString
        }
    },
    DEVERSIFI("DeversiFi") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.deversifi.com/bfx/v2/tickers?symbols=t${coin}$currency"
            return getJsonArray(url)[0].jsonArray[7].asString
        }
    },
    DIGIFINEX("Digifinex") {
        override fun getValue(coin: String, currency: String): String? {
            val url = "https://openapi.digifinex.com/v3/ticker?symbol=${coin}_$currency"
            return getJsonObject(url)["ticker"]?.jsonArray?.get(0)?.jsonObject?.get("last").asString
        }
    },
    EXMO("Exmo") {

        override fun getValue(coin: String, currency: String): String? {
            val pair = "${coin}_$currency"
            val url = "https://api.exmo.com/v1.1/ticker"
            return getJsonObject(url)[pair]?.jsonObject?.get("last_trade").asString
        }
    },
    FOXBIT("FoxBit") {

        override fun getValue(coin: String, currency: String): String? {
            val payload = getJsonArray("https://watcher.foxbit.com.br/api/Ticker/")
            val pair = "${currency}X$coin"
            for (jsonElement in payload) {
                val obj = jsonElement as JsonObject
                if (obj["currency"].asString == pair) {
                    return obj["last"].asString
                }
            }
            return null
        }
    },
    GATEIO("Gate.io") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.gateio.ws/api/v4/spot/tickers?currency_pair=${coin}_$currency"
            return getJsonArray(url)[0].jsonObject["last"].asString
        }
    },
    GEMINI("Gemini") {

        override fun getValue(coin: String, currency: String): String? {
            val pair = "$coin$currency".lowercase()
            return getJsonObject("https://api.gemini.com/v1/pubticker/$pair")["last"].asString
        }
    },
    HASHKEY("Hashkey") {
        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api-pro.hashkey.com/quote/v1/ticker/price?symbol=$coin$currency"
            return getJsonArray(url)[0].jsonObject["p"].asString
        }
    },
    HITBTC("HitBTC") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.hitbtc.com/api/3/public/ticker?symbols=$coin$currency"
            return getJsonObject(url)["$coin$currency"]?.jsonObject?.get("last").asString
        }
    },
    HUOBI("Huobi") {
        override fun getValue(coin: String, currency: String): String? {
            val pair = "${coin}$currency".lowercase()
            val url = "https://api.huobi.pro/market/detail/merged?symbol=$pair"
            return getJsonObject(url)["tick"]?.jsonObject?.get("close").asString
        }
    },
    INDEPENDENT_RESERVE("Independent Reserve", "Ind. Reserve") {

        override fun getValue(coin: String, currency: String): String? {
            val url =
                "https://api.independentreserve.com/Public/GetMarketSummary?primaryCurrencyCode=${
                    coin.lowercase()
                }&secondaryCurrencyCode=${currency.lowercase()}"
            return getJsonObject(url)["LastPrice"].asString
        }
    },
    INDODAX("Indodax") {

        override fun getValue(coin: String, currency: String): String? {
            val pair = "${coin}_$currency".lowercase()
            val url = "https://indodax.com/api/$pair/ticker"
            return getJsonObject(url)["ticker"]?.jsonObject?.get("last").asString
        }
    },
    ITBIT("ItBit") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.paxos.com/v2/markets/$coin$currency/ticker"
            return getJsonObject(url)["last_execution"]?.jsonObject?.get("price").asString
        }
    },
    KORBIT("Korbit") {

        override fun getValue(coin: String, currency: String): String? {
            val headers = Headers.headersOf("User-Agent", "")
            val pair = "${coin}_$currency".lowercase()
            val url = "https://api.korbit.co.kr/v1/ticker?currency_pair=$pair"
            return getJsonObject(url, headers)["last"].asString
        }
    },
    KRAKEN("Kraken") {

        override fun getValue(coin: String, currency: String): String? {
            val obj = getJsonObject("https://api.kraken.com/0/public/Ticker?pair=$coin$currency")
            val obj2 = obj["result"]?.jsonObject
            val key = obj2?.keys?.first() ?: return null
            return obj2[key]?.jsonObject?.get("c")?.jsonArray?.get(0).asString
        }
    },
    KUCOIN("Kucoin") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.kucoin.com/api/v1/market/orderbook/level1?symbol=$coin-$currency"
            return getJsonObject(url)["data"]?.jsonObject?.get("price").asString
        }
    },
    KUNA("KunaBTC") {

        override fun getValue(coin: String, currency: String): String? {
            val pair = "$coin$currency".lowercase()
            val url = "https://api.kuna.io/v3/tickers?symbols=$pair"
            return getJsonArray(url)[0].jsonArray[1].asString
        }
    },
    LBANK("LBank") {

        override fun getValue(coin: String, currency: String): String? {
            val pair = "${coin}_$currency".lowercase()
            val url = "https://api.lbkex.com/v2/ticker.do?symbol=$pair"
            return getJsonObject(url)["data"]?.jsonArray?.get(0)?.jsonObject?.get("ticker")
                ?.jsonObject?.get("latest").asString
        }
    },
    LIQUID("Liquid") {
        override fun getValue(coin: String, currency: String): String? {
            val array = getJsonArray("https://api.liquid.com/products")
            val pair = "$coin$currency"
            for (jsonElement in array) {
                val obj = jsonElement as JsonObject
                if (pair == obj["currency_pair_code"].asString) {
                    return obj["last_traded_price"].asString
                }
            }
            return null
        }
    },
    LUNO("Luno") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.luno.com/api/1/ticker?pair=$coin$currency"
            return getJsonObject(url)["last_trade"].asString
        }
    },
    MERCADO("Mercado Bitcoin", "Mercado") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.mercadobitcoin.net/api/v4/tickers?symbols=$coin-$currency"
            return getJsonArray(url)[0].jsonObject["last"].asString
        }
    },
    MEXC("MEXC") {
        override fun getValue(coin: String, currency: String): String? {
            val url = "https://www.mexc.com/open/api/v2/market/ticker?symbol=${coin}_$currency"
            return getJsonObject(url)["data"]?.jsonArray?.get(0)?.jsonObject?.get("last").asString
        }
    },
    NDAX("NDAX") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://core.ndax.io/v1/ticker"
            return getJsonObject(url)["${coin}_$currency"]!!.jsonObject["last"].asString
        }
    },
    NEXCHANGE("Nexchange") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.n.exchange/en/api/v1/price/$coin$currency/latest/?format=json"
            val ticker = getJsonArray(url)[0].jsonObject["ticker"]!!.jsonObject
            val ask = ticker["ask"]?.jsonPrimitive.asString?.toDouble() ?: return null
            val bid = ticker["bid"]?.jsonPrimitive.asString?.toDouble() ?: return null
            return ((ask + bid) / 2).toString()
        }
    },
    OKCOIN("OK Coin") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://www.okcoin.com/api/spot/v3/instruments/$coin-$currency/ticker"
            return getJsonObject(url)["last"].asString
        }
    },
    OKX("OKX") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://www.okx.com/api/v5/market/ticker?instId=$coin-$currency"
            return getJsonObject(url)["data"]?.jsonArray?.get(0)?.jsonObject?.get("last").asString
        }
    },
    P2PB2B("P2PB2B") {
        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.p2pb2b.io/api/v2/public/ticker?market=${coin}_$currency"
            return getJsonObject(url)["result"]?.jsonObject?.get("last").asString
        }
    },
    PARIBU("Paribu") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://www.paribu.com/ticker"
            return getJsonObject(url)["${coin}_$currency"]?.jsonObject?.get("last").asString
        }
    },
    PAYMIUM("Paymium") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://paymium.com/api/v1/data/${currency.lowercase()}/ticker"
            return getJsonObject(url)["price"].asString
        }
    },
    PHEMEX("Phemex") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.phemex.com/md/spot/ticker/24hr?symbol=s${coin}$currency"
            val value = getJsonObject(url)["result"]?.jsonObject?.get("lastEp").asString ?: return null
            return (value.toDouble() / 10.0.pow(8)).toString()
        }
    },
    POCKETBITS("Pocketbits") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://ticker.pocketbits.in/api/v1/ticker"
            val obj = getJsonArray(url).firstOrNull {
                it.jsonObject["symbol"].asString == "$coin$currency"
            }?.jsonObject ?: return null
            val buy = obj["buy"].asString?.toDoubleOrNull() ?: return null
            val sell = obj["sell"].asString?.toDoubleOrNull() ?: return null
            return ((buy + sell) / 2).toString()
        }
    },
    POLONIEX("Poloniex") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.poloniex.com/markets/${coin}_$currency/price"
            return getJsonObject(url)["price"].asString
        }
    },
    PROBIT("ProBit") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.probit.com/api/exchange/v1/ticker?market_ids=$coin-$currency"
            return getJsonObject(url)["data"]?.jsonArray?.get(0)?.jsonObject?.get("last").asString
        }
    },
    TRADEOGRE("TradeOgre") {
        override fun getValue(coin: String, currency: String): String? {
            val url = "https://tradeogre.com/api/v1/ticker/$currency-$coin"
            return getJsonObject(url)["price"].asString
        }
    },
    UPHOLD("Uphold") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.uphold.com/v0/ticker/$coin-$currency"
            val obj = getJsonObject(url)
            val bid = obj["bid"].asString?.toDoubleOrNull() ?: return null
            val ask = obj["ask"].asString?.toDoubleOrNull() ?: return null
            return ((bid + ask) / 2).toString()
        }
    },
    VBTC("VBTC") {

        override fun getValue(coin: String, currency: String): String? {
            return getBlinkTradeValue(coin, currency)
        }
    },
    WHITEBIT("WhiteBIT") {
        // v3 and v4 api does not allow for ticker on a single market
        override fun getValue(coin: String, currency: String): String? {
            val url = "https://whitebit.com/api/v1/public/ticker?market=${coin}_$currency"
            return getJsonObject(url)["result"]?.jsonObject?.get("last").asString
        }
    },
    XT("XT.com") {
        override fun getValue(coin: String, currency: String): String? {
            val url = "https://sapi.xt.com/v4/public/ticker/price?symbol=${coin.lowercase()}_${currency.lowercase()}"
            return getJsonObject(url)["result"]?.jsonArray?.get(0)?.jsonObject?.get("p")?.asString
        }

    },
    YADIO("Yadio") {
        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.yadio.io/exrates/$currency"
            return getJsonObject(url)["BTC"].asString
        }
    },
    YOBIT("YoBit") {
        override fun getValue(coin: String, currency: String): String? {
            val pair = "${coin}_$currency".lowercase()
            val url = "https://yobit.net/api/3/ticker/$pair"
            return getJsonObject(url)[pair]?.jsonObject?.get("last").asString
        }
    },
    ZONDA("Zonda") {
        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.zonda.exchange/rest/trading/ticker/$coin-$currency"
            return getJsonObject(url)["ticker"]?.jsonObject?.get("rate").asString
        }
    };


    val shortName: String = shortName ?: exchangeName

    fun getBlinkTradeValue(coin: String, currency: String): String? {
        val url = "https://api.blinktrade.com/api/v1/$currency/ticker?crypto_currency=$coin"
        return getJsonObject(url).jsonObject["last"].asString
    }

    abstract fun getValue(coin: String, currency: String): String?


    companion object {

        private val ALL_EXCHANGE_NAMES = entries.map { it.name}.toMutableList()

        fun getAllExchangeNames(): MutableList<String> {
            return ALL_EXCHANGE_NAMES.toMutableList()
        }
    }
}
