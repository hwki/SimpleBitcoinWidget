package com.brentpanther.bitcoinwidget

import com.brentpanther.bitcoinwidget.ExchangeHelper.getJsonArray
import com.brentpanther.bitcoinwidget.ExchangeHelper.getJsonObject
import com.google.gson.JsonObject
import okhttp3.Headers

internal enum class Exchange constructor(val exchangeName: String, shortName: String? = null) {

    ABUCOINS("Abucoins") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.abucoins.com/products/$coin$currency/stats"
            return getJsonObject(url).get("last").asString
        }
    },
    BIBOX("Bibox") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.bibox.com/v1/mdata?cmd=ticker&pair=${coin}_$currency"
            return getJsonObject(url).getAsJsonObject("result").get("last").asString
        }
    },
    BINANCE("Binance") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.binance.com/api/v3/ticker/price?symbol=$coin$currency"
            return getJsonObject(url).get("price").asString
        }
    },
    BIT2C("Bit2C") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://www.bit2c.co.il/Exchanges/${coin}Nis/Ticker.json"
            return getJsonObject(url).get("ll").asString
        }
    },
    BITBAY("BitBay") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://bitbay.net/API/Public/$coin$currency/ticker.json"
            return getJsonObject(url).get("last").asString
        }
    },
    BITCOIN_AVERAGE("Bitcoin Average", "BTC avg") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://apiv2.bitcoinaverage.com/indices/local/ticker/short?crypto=$coin&fiats=$currency"
            val obj = getJsonObject(url)
            return obj.getAsJsonObject("$coin$currency").get("last").asString
        }
    },
    BITCOIN_AVERAGE_GLOBAL("Bitcoin Average (global)", "BTC avg global") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://apiv2.bitcoinaverage.com/indices/global/ticker/short?crypto=$coin&fiats=$currency"
            val obj = getJsonObject(url)
            return obj.getAsJsonObject("$coin$currency").get("last").asString
        }
    },
    BITCOINDE("Bitcoin.de") {

        override fun getValue(coin: String, currency: String): String {
            val obj = getJsonObject("https://bitcoinapi.de/widget/current-btc-price/rate.json")
            val price = obj.get("price_eur").asString
            val amount = price.split("\\u00A0".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return amount[0].replace("\\.".toRegex(), "").replace(",".toRegex(), ".")
        }
    },
    BITFINEX("Bitfinex") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.bitfinex.com/v2/ticker/t$coin$currency"
            return getJsonArray(url).get(6).asString
        }
    },
    BITFLYER("BitFlyer") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.bitflyer.jp/v1/ticker?product_code=${coin}_$currency"
            return getJsonObject(url).get("ltp").asString
        }
    },
    BITHUMB("Bithumb") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.bithumb.com/public/ticker/$coin"
            val data = getJsonObject(url).getAsJsonObject("data")
            val buy = data.get("buy_price").asString.toDouble()
            val sell = data.get("sell_price").asString.toDouble()
            return ((buy + sell) / 2).toString()
        }
    },
    BITLISH("Bitlish") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://bitlish.com/api/v1/tickers"
            val pair = "$coin$currency".toLowerCase()
            return getJsonObject(url).getAsJsonObject(pair).get("last").asString
        }
    },
    BITMARKETPL("BitMarket.pl") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://www.bitmarket.pl/json/$coin$currency/ticker.json"
            return getJsonObject(url).get("last").asString
        }
    },
    BITMEX("BitMEX") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://www.bitmex.com/api/v1/instrument?symbol=$coin$currency&columns=lastPrice"
            return getJsonArray(url).get(0).asJsonObject.get("lastPrice").asString
        }
    },
    BITPAY("BitPay") {

        override fun getValue(coin: String, currency: String): String? {
            val array = getJsonArray("https://bitpay.com/api/rates")
            for (jsonElement in array) {
                val obj = jsonElement as JsonObject
                if (currency == obj.get("code").asString) {
                    return obj.get("rate").asString
                }
            }
            return null
        }
    },
    BITSEVEN("BitSeven") {
        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.bitseven.com/q/ticker/$coin"
            return getJsonArray(url).get(2).asString
        }
    },
    BITSO("Bitso") {

        override fun getValue(coin: String, currency: String): String? {
            val payload = getJsonObject("https://api.bitso.com/v3/ticker/").getAsJsonArray("payload")
            val pair = "${coin}_$currency".toLowerCase()
            for (jsonElement in payload) {
                val obj = jsonElement as JsonObject
                if (obj.get("book").asString == pair) {
                    return obj.get("last").asString
                }
            }
            return null
        }
    },
    BITSTAMP("Bitstamp") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://www.bitstamp.net/api/v2/ticker/${coin.toLowerCase()}${currency.toLowerCase()}"
            return getJsonObject(url).get("last").asString
        }
    },
    BITTREX("Bittrex") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "$currency-$coin"
            val url = "https://bittrex.com/api/v1.1/public/getticker?market=$pair"
            return getJsonObject(url).getAsJsonObject("result").get("Last").asString
        }
    },
    BRAZILIEX("Braziliex") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "${coin}_$currency".toLowerCase()
            val url = "https://braziliex.com/api/v1/public/ticker/$pair"
            return getJsonObject(url).get("last").asString
        }
    },
    BTCBOX("BTC Box") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://www.btcbox.co.jp/api/v1/ticker/"
            return getJsonObject(url).get("last").asString
        }
    },
    BTCMARKETS("BTC Markets") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.btcmarkets.net/market/$coin/$currency/tick"
            return getJsonObject(url).get("lastPrice").asString
        }
    },
    BTCTURK("BTCTurk") {

        override fun getValue(coin: String, currency: String): String? {
            val array = getJsonArray("https://www.btcturk.com/api/ticker")
            val pair = coin + currency
            for (jsonElement in array) {
                val obj = jsonElement as JsonObject
                if (obj.get("pair").asString == pair) {
                    return obj.get("last").asString
                }
            }
            return null
        }
    },
    CEXIO("Cex.io") {
        override fun getValue(coin: String, currency: String): String {
            return getJsonObject("https://cex.io/api/last_price/$coin/$currency").get("lprice").asString
        }
    },
    CHILEBIT("ChileBit.net") {
        override fun getValue(coin: String, currency: String): String {
            return getBlinkTradeValue(coin, currency)
        }
    },
    COINBASE("Coinbase") {

        override fun getValue(coin: String, currency: String): String {
            val obj = getJsonObject("https://api.coinbase.com/v2/prices/$coin-$currency/spot")
            return obj.getAsJsonObject("data").get("amount").asString
        }
    },
    COINBASEPRO("Coinbase Pro") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.pro.coinbase.com/products/$coin-$currency/ticker"
            return getJsonObject(url).get("price").asString
        }
    },
    COINBE("Coinbe") {

        override fun getValue(coin: String, currency: String): String {
            val obj = getJsonObject("https://coinbe.net/public/graphs/ticker/ticker.json")
            val pair = "${currency}_$coin"
            return obj.getAsJsonObject(pair).get("last").asString
        }
    },
    COINBOOK("Coinbook") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://coinbook.com/api/SimpleBitcoinWidget/price"
            return getJsonObject(url).get("$coin-$currency").asString
        }
    },
    COINDELTA("Coindelta") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://coindelta.com/api/v1/public/getticker/"
            val pair = "$coin-$currency".toLowerCase()
            val array = getJsonArray(url)
            for (jsonElement in array) {
                val obj = jsonElement as JsonObject
                if (obj.get("MarketName").asString == pair) {
                    return obj.get("Last").asString
                }
            }
            return null
        }
    },
    COINDESK("Coindesk") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.coindesk.com/v1/bpi/currentprice/$currency.json"
            return getJsonObject(url).getAsJsonObject("bpi").getAsJsonObject(currency).get("rate_float").asString
        }
    },
    COINEGG("CoinEgg") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.coinegg.im/api/v1/ticker/region/${currency.toLowerCase()}?coin=${coin.toLowerCase()}"
            return getJsonObject(url).get("last").asString
        }
    },
    COINGECKO("CoinGecko") {
        override fun getValue(coin: String, currency: String): String? {
            // hardcoded map to id
            val map = mapOf("BTC" to "bitcoin",
                    "ETH" to "ethereum",
                    "XRP" to "ripple",
                    "BCH" to "bitcoin-cash",
                    "LTC" to "litecoin",
                    "NEO" to "neo",
                    "ADA" to "cardano",
                    "XLM" to "stellar",
                    "IOTA" to "iota",
                    "DASH" to "dash",
                    "XMR" to "monero",
                    "XEM" to "nem",
                    "NANO" to "nano",
                    "BTG" to "bitcoin-gold",
                    "ETC" to "ethereum-classic",
                    "ZEC" to "zcash",
                    "XVG" to "verge",
                    "DOGE" to "dogecoin",
                    "DCR" to "decred",
                    "PPC" to "peercoin",
                    "VTC" to "vertcoin",
                    "TRX" to "tron")
            val id = map[coin]
            val vs = currency.toLowerCase()
            val url = "https://api.coingecko.com/api/v3/simple/price?ids=$id&vs_currencies=$vs"
            return getJsonObject(url).getAsJsonObject(id).get(vs).asString
        }
    },
    COINJAR("CoinJar") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.coinjar.com/v3/exchange_rates"
            val pair = "$coin$currency"
            return getJsonObject(url).getAsJsonObject("exchange_rates").getAsJsonObject(pair).get("midpoint").asString
        }
    },
    COINMATE("CoinMate.io") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://coinmate.io/api/ticker?currencyPair=${coin}_$currency"
            val obj = getJsonObject(url)
            return obj.getAsJsonObject("data").get("last").asString
        }
    },
    COINNEST("Coinnest") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.coinnest.co.kr/api/pub/ticker?coin=${coin.toLowerCase()}"
            return getJsonObject(url).get("last").asString
        }
    },
    COINONE("Coinone") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.coinone.co.kr/ticker/?currency=$coin"
            return getJsonObject(url).get("last").asString
        }
    },
    COINSECURE("Coinsecure") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.coinsecure.in/v1/exchange/ticker"
            return (getJsonObject(url).getAsJsonObject("message").get("lastPrice").asLong / 100).toString()
        }
    },
    COINSQUARE("Coinsquare") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://coinsquare.io/api/v1/data/quotes"
            val array = getJsonObject(url).getAsJsonArray("quotes")
            for (jsonElement in array) {
                val obj = jsonElement as JsonObject
                if (obj.get("ticker").asString != coin) continue
                if (obj.get("base").asString != currency) continue
                return obj.get("last").asString
            }
            return null
        }
    },
    COINTREE("Cointree") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "$coin/$currency".toLowerCase()
            return getJsonObject("https://www.cointree.com.au/api/price/$pair").get("Spot").asString
        }
    },
    COINSPH("Coins.ph") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://quote.coins.ph/v1/markets/$coin-$currency"
            val obj = getJsonObject(url).getAsJsonObject("market")
            val bid = obj.get("bid").asString
            val ask = obj.get("ask").asString
            return ((bid.toDouble() + ask.toDouble()) / 2).toString()
        }
    },
    CRYPTONIT("Cryptonit") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://cryptonit.net/apiv2/rest/public/ccorder.json?bid_currency=${coin.toLowerCase()}&ask_currency=${currency.toLowerCase()}&ticker"
            return getJsonObject(url).getAsJsonObject("rate").get("last").asString
        }
    },
    CRYPTOPIA("Cryptopia") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://www.cryptopia.co.nz/api/GetMarket/${coin}_$currency"
            return getJsonObject(url).getAsJsonObject("Data").get("LastPrice").asString
        }
    },
    EXMO("Exmo") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "${coin}_$currency"
            val url = "https://api.exmo.com/v1/ticker/"
            return getJsonObject(url).getAsJsonObject(pair).get("last_trade").asString
        }
    },
    FOXBIT("FoxBit") {

        override fun getValue(coin: String, currency: String): String {
            return getBlinkTradeValue(coin, currency)
        }
    },
    GATEIO("Gate.io") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "${coin}_$currency".toLowerCase()
            val url = "https://data.gate.io/api2/1/ticker/$pair"
            return getJsonObject(url).get("last").asString
        }
    },
    GEMINI("Gemini") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "$coin$currency".toLowerCase()
            return getJsonObject("https://api.gemini.com/v1/pubticker/$pair").get("last").asString
        }
    },
    HITBTC("HitBTC") {

        override fun getValue(coin: String, currency: String): String {
            var currencyValue = currency
            if (coin == "XRP" && currencyValue == "USD") {
                currencyValue = "USDT"
            }
            return getJsonObject("https://api.hitbtc.com/api/2/public/ticker/$coin$currencyValue").get("last").asString
        }
    },
    HUOBI("Huobi") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "$coin$currency".toLowerCase()
            val url = "https://api.huobi.pro/market/detail/merged?symbol=$pair"
            val tick = getJsonObject(url).getAsJsonObject("tick")
            val ask = tick.getAsJsonArray("ask").get(0).asDouble
            val bid = tick.getAsJsonArray("bid").get(0).asDouble
            return ((ask + bid) / 2).toString()
        }
    },
    INDEPENDENT_RESERVE("Independent Reserve", "Ind. Reserve") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.independentreserve.com/Public/GetMarketSummary?primaryCurrencyCode=${coin.toLowerCase()}&secondaryCurrencyCode=${currency.toLowerCase()}"
            return getJsonObject(url).get("LastPrice").asString
        }
    },
    INDODAX("Indodax") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "${coin}_$currency".toLowerCase()
            val url = "https://indodax.com/api/$pair/ticker"
            return getJsonObject(url).getAsJsonObject("ticker").get("last").asString
        }
    },
    ITBIT("ItBit") {

        override fun getValue(coin: String, currency: String): String {
            return getJsonObject("https://api.itbit.com/v1/markets/$coin$currency/ticker").get("lastPrice").asString
        }
    },
    KOINEX("Koinex") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://koinex.in/api/ticker"
            return getJsonObject(url).getAsJsonObject("prices").getAsJsonObject(currency.toLowerCase()).get(coin).asString
        }
    },
    KORBIT("Korbit") {

        override fun getValue(coin: String, currency: String): String {
            val headers = Headers.of("User-Agent", "")
            val pair = "${coin}_$currency".toLowerCase()
            val url = "https://api.korbit.co.kr/v1/ticker?currency_pair=$pair"
            return getJsonObject(url, headers).get("last").asString
        }
    },
    KRAKEN("Kraken") {

        override fun getValue(coin: String, currency: String): String {
            val obj = getJsonObject("https://api.kraken.com/0/public/Ticker?pair=$coin$currency")
            val obj2 = obj.getAsJsonObject("result")
            val key = obj2.keySet().iterator().next()
            return obj2.getAsJsonObject(key).getAsJsonArray("c").get(0).asString
        }
    },
    KUCOIN("Kucoin") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.kucoin.com/api/v1/market/orderbook/level1?symbol=$coin-$currency"
            return getJsonObject(url).getAsJsonObject("data").get("price").asString
        }
    },
    KUNA("KunaBTC") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "$coin$currency".toLowerCase()
            val obj = getJsonObject("https://kuna.io/api/v2/tickers/$pair")
            return obj.getAsJsonObject("ticker").get("last").asString
        }
    },
    LAKEBTC("LakeBTC") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "$coin$currency".toLowerCase()
            val obj = getJsonObject("https://api.lakebtc.com/api_v2/ticker?symbol=$pair")
            return obj.getAsJsonObject(pair).get("last").asString
        }
    },
    LBANK("LBank") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "${coin}_$currency".toLowerCase()
            val url = "https://api.lbkex.com/v1/ticker.do?symbol=$pair"
            return getJsonObject(url).getAsJsonObject("ticker").get("latest").asString
        }
    },
    LIVECOIN("Livecoin") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.livecoin.net/exchange/ticker?currencyPair=$coin/$currency"
            return getJsonObject(url).get("last").asString
        }
    },
    LUNO("Luno") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.mybitx.com/api/1/ticker?pair=$coin$currency"
            return getJsonObject(url).get("last_trade").asString
        }
    },
    MERCADO("Mercado Bitcoin", "Mercado") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://www.mercadobitcoin.net/api/$coin/ticker/"
            return getJsonObject(url).getAsJsonObject("ticker").get("last").asString
        }
    },
    NDAX("NDAX") {
        override fun getValue(coin: String, currency: String): String? {
            val url = "https://ndax.io/api/returnticker"
            return getJsonObject(url).getAsJsonObject("${coin}_$currency").get("last").asString
        }
    },
    NEGOCIECOINS("NegocieCoins", "Negocie") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://broker.negociecoins.com.br/api/v3/$coin$currency/ticker"
            return getJsonObject(url).get("last").asString
        }
    },
    NEXCHANGE("Nexchange") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.nexchange.io/en/api/v1/price/$coin$currency/latest/?format=json"
            val ticker = getJsonArray(url).get(0).asJsonObject.getAsJsonObject("ticker")
            val ask = ticker.get("ask").asString
            val bid = ticker.get("bid").asString
            return ((ask.toDouble() + bid.toDouble()) / 2).toString()
        }
    },
    OKCOIN("OK Coin") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://www.okcoin.com/api/spot/v3/instruments/$coin-$currency/ticker"
            return getJsonObject(url).get("last").asString
        }
    },
    OKEX("OKEx") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "${coin}_$currency".toLowerCase()
            val url = "https://www.okex.com/api/v1/ticker.do?symbol=$pair"
            return getJsonObject(url).getAsJsonObject("ticker").get("last").asString
        }
    },
    PARIBU("Paribu") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://www.paribu.com/ticker"
            return getJsonObject(url).getAsJsonObject("${coin}_$currency").get("last").asString
        }
    },
    PAYMIUM("Paymium") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://paymium.com/api/v1/data/${currency.toLowerCase()}/ticker"
            return getJsonObject(url).get("price").asString
        }
    },
    POLONIEX("Poloniex") {

        override fun getValue(coin: String, currency: String): String {
            val obj = getJsonObject("https://poloniex.com/public?command=returnTicker")
            return obj.getAsJsonObject("${currency}_$coin").get("last").asString
        }
    },
    QUOINE("Quoine") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.quoine.com/products/code/CASH/$coin$currency"
            return getJsonObject(url).get("last_traded_price").asString
        }
    },
    SURBITCOIN("SurBitcoin") {

        override fun getValue(coin: String, currency: String): String {
            return getBlinkTradeValue(coin, currency)
        }
    },
    THEROCK("TheRock") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.therocktrading.com/v1/funds/$coin$currency/ticker"
            return getJsonObject(url).get("last").asString
        }
    },
    TRADESATOSHI("Trade Satoshi") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "${coin}_$currency"
            val url = "https://tradesatoshi.com/api/public/getticker?market=$pair"
            return getJsonObject(url).getAsJsonObject("result").get("last").asString
        }
    },
    UPHOLD("Uphold") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.uphold.com/v0/ticker/$coin$currency"
            val obj = getJsonObject(url)
            val bid = obj.get("bid").asString
            val ask = obj.get("ask").asString
            return ((bid.toDouble() + ask.toDouble()) / 2).toString()
        }
    },
    URDUBIT("UrduBit") {

        override fun getValue(coin: String, currency: String): String {
            return getBlinkTradeValue(coin, currency)
        }
    },
    VBTC("VBTC") {

        override fun getValue(coin: String, currency: String): String {
            return getBlinkTradeValue(coin, currency)
        }
    },

    WYRE("Wyre") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.sendwyre.com/v2/rates"
            val currencyName = "$currency$coin"
            return getJsonObject(url).get(currencyName).asString
        }
    },
    YOBIT("YoBit") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "${coin}_$currency".toLowerCase()
            val url = "https://yobit.net/api/3/ticker/$pair"
            return getJsonObject(url).getAsJsonObject(pair).get("last").asString
        }
    },
    ZB("ZB") {
        override fun getValue(coin: String, currency: String): String? {
            val url = "http://api.zb.cn/data/v1/ticker/?market=${coin.toLowerCase()}_${currency.toLowerCase()}"
            return getJsonObject(url).getAsJsonObject("ticker").get("last").asString
        }
    },
    ZYADO("Zyado") {

        override fun getValue(coin: String, currency: String): String {
            return getJsonObject("http://chart.zyado.com/ticker.json").get("last").asString
        }
    };

    val shortName: String = shortName ?: exchangeName

    fun getBlinkTradeValue(coin: String, currency: String): String {
        val url = "https://api.blinktrade.com/api/v1/$currency/ticker?crypto_currency=$coin"
        return getJsonObject(url).get("last").asString
    }

    abstract fun getValue(coin: String, currency: String): String?


    companion object {

        private val ALL_EXCHANGE_NAMES = Exchange.values().map { it.name}.toMutableList()

        fun getAllExchangeNames(): MutableList<String> {
            return ALL_EXCHANGE_NAMES.toMutableList()
        }
    }
}
