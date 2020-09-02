package com.brentpanther.bitcoinwidget

import com.brentpanther.bitcoinwidget.ExchangeHelper.getJsonArray
import com.brentpanther.bitcoinwidget.ExchangeHelper.getJsonObject
import com.google.gson.JsonObject
import okhttp3.Headers
import java.util.*

internal enum class Exchange(val exchangeName: String, shortName: String? = null) {

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
    BIGONE("BigONE") {
        override fun getValue(coin: String, currency: String): String {
            val url = "https://big.one/api/v3/asset_pairs/$coin-$currency/ticker"
            val obj = getJsonObject(url).getAsJsonObject("data")
            val bid = obj.getAsJsonObject("bid").get("price").asString
            val ask = obj.getAsJsonObject("ask").get("price").asString
            return ((bid.toDouble() + ask.toDouble()) / 2).toString()
        }
    },
    BINANCE("Binance.com") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.binance.com/api/v3/ticker/price?symbol=$coin$currency"
            return getJsonObject(url).get("price").asString
        }
    },
    BINANCE_US("Binance.us") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.binance.us/api/v3/ticker/price?symbol=$coin$currency"
            return getJsonObject(url).get("price").asString
        }
    },
    BIT2C("Bit2C") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://www.bit2c.co.il/Exchanges/${coin}Nis/Ticker.json"
            return getJsonObject(url).get("ll").asString
        }
    },
    BITBANK("Bitbank") {
        override fun getValue(coin: String, currency: String): String {
            val pair = "${coin}_$currency".toLowerCase(Locale.ROOT)
            val url = "https://public.bitbank.cc/$pair/ticker"
            return getJsonObject(url).getAsJsonObject("data").get("last").asString
        }
    },
    BITBAY("BitBay") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://bitbay.net/API/Public/$coin$currency/ticker.json"
            return getJsonObject(url).get("last").asString
        }
    },
    BITCLUDE("BitClude") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.bitclude.com/stats/ticker.json"
            val obj = getJsonObject(url)
            return obj.getAsJsonObject("${coin.toLowerCase(Locale.ROOT)}_${currency.toLowerCase(Locale.ROOT)}").get("last").asString
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
            val url = "https://api-pub.bitfinex.com/v2/ticker/t$coin$currency"
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
            val url = "https://api.bithumb.com/public/ticker/${coin}_$currency"
            val data = getJsonObject(url).getAsJsonObject("data")
            val buy = data.get("opening_price").asString.toDouble()
            val sell = data.get("closing_price").asString.toDouble()
            return ((buy + sell) / 2).toString()
        }
    },
    BITMAX("BitMax") {
        override fun getValue(coin: String, currency: String): String? {
            val obj = getJsonObject("https://bitmax.io/api/v1/quote?symbol=$coin-$currency")
            val bid = obj.get("bidPrice").asString
            val ask = obj.get("askPrice").asString
            return ((bid.toDouble() + ask.toDouble()) / 2).toString()
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
            val pair = "${coin}_$currency".toLowerCase(Locale.ROOT)
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
            val url = "https://www.bitstamp.net/api/v2/ticker/${coin.toLowerCase(Locale.ROOT)}${currency.toLowerCase(Locale.ROOT)}"
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
    BLEUTRADE("Bleutrade") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "${coin}_$currency"
            val url = "https://bleutrade.com/api/v3/public/getticker?market=$pair"
            val result = getJsonObject(url).getAsJsonArray("result").get(0).asJsonObject
            return result.get("Last").asString
        }
    },
    BRAZILIEX("Braziliex") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "${coin}_$currency".toLowerCase(Locale.ROOT)
            val url = "https://braziliex.com/api/v1/public/ticker/$pair"
            return getJsonObject(url).get("last").asString
        }
    },
    BTCBOX("BTC Box") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://www.btcbox.co.jp/api/v1/ticker/$coin"
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

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.btcturk.com/api/v2/ticker?pairSymbol=${coin}_$currency"
            val array = getJsonObject(url).getAsJsonArray("data")
            return array.get(0).asJsonObject.get("last").asString
        }
    },
    BYBIT("Bybit") {
        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.bybit.com/v2/public/tickers?symbol=$coin$currency"
            return getJsonObject(url).getAsJsonArray("result")[0].asJsonObject.get("last_price").asString
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
    COINDESK("Coindesk") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.coindesk.com/v1/bpi/currentprice/$currency.json"
            return getJsonObject(url).getAsJsonObject("bpi").getAsJsonObject(currency).get("rate_float").asString
        }
    },
    COINEGG("CoinEgg") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.coinegg.vip/api/v1/ticker/region/${currency.toLowerCase(Locale.ROOT)}?coin=${coin.toLowerCase(Locale.ROOT)}"
            return getJsonObject(url).get("last").asString
        }
    },
    COINGECKO("CoinGecko") {

        override fun getValue(coin: String, currency: String): String? {
            // hardcoded map to id
            val map = mapOf(
                    "ADA" to "cardano",
                    "ALGO" to "algorand",
                    "ARRR" to "pirate-chain",
                    "ATOM" to "cosmos",
                    "BAT" to "basic-attention-token",
                    "BCD" to "bitcoin-diamond",
                    "BCH" to "bitcoin-cash",
                    "BNB" to "binancecoin",
                    "BSV" to "bitcoin-cash-sv",
                    "BTC" to "bitcoin",
                    "BTG" to "bitcoin-gold",
                    "BTM" to "bytom",
                    "CRO" to "crypto-com-chain",
                    "DASH" to "dash",
                    "DCR" to "decred",
                    "DOGE" to "dogecoin",
                    "DOT" to "polkadot",
                    "ENJ" to "enjincoin",
                    "EOS" to "eos",
                    "ETC" to "ethereum-classic",
                    "ETH" to "ethereum",
                    "FTT" to "ftx-token",
                    "HBAR" to "hedera-hashgraph",
                    "HNS" to "handshake",
                    "HT" to "huobi-token",
                    "ICX" to "icon",
                    "IOTA" to "iota",
                    "KMD" to "komodo",
                    "KSM" to "kusama",
                    "LEO" to "leo-token",
                    "LINK" to "chainlink",
                    "LSK" to "lisk",
                    "LTC" to "litecoin",
                    "MKR" to "maker",
                    "NANO" to "nano",
                    "NEO" to "neo",
                    "OKB" to "okb",
                    "OMG" to "omisego",
                    "ONT" to "ontology",
                    "PPC" to "peercoin",
                    "QTUM" to "qtum",
                    "RDD" to "reddcoin",
                    "REP" to "augur",
                    "RVN" to "ravencoin",
                    "TRX" to "tron",
                    "VET" to "vechain",
                    "VTC" to "vertcoin",
                    "WAVES" to "waves",
                    "XEM" to "nem",
                    "XLM" to "stellar",
                    "XMR" to "monero",
                    "XRP" to "ripple",
                    "XTZ" to "tezos",
                    "XVG" to "verge",
                    "XZC" to "zcoin",
                    "ZEC" to "zcash")
            val id = map[coin]
            val vs = currency.toLowerCase(Locale.ROOT)
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
    COINONE("Coinone") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.coinone.co.kr/ticker/?currency=$coin"
            return getJsonObject(url).get("last").asString
        }
    },
    COINSBIT("Coinsbit") {
        override fun getValue(coin: String, currency: String): String {
            val url = "https://coinsbit.io/api/v1/public/ticker?market=${coin}_$currency"
            return getJsonObject(url).getAsJsonObject("result").get("last").asString
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
            val url = "https://trade.cointree.com/api/prices/AUD/change/24h?symbols=$coin"
            return getJsonArray(url).get(0).asJsonObject.get("spot").asString
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
    CRYPTO("Crypto.com") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.crypto.com/v1/ticker?symbol=$coin$currency"
            return getJsonObject(url).getAsJsonObject("data").get("last").asString
        }
    },
    EXMO("Exmo") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "${coin}_$currency"
            val url = "https://api.exmo.com/v1/ticker/"
            return getJsonObject(url).getAsJsonObject(pair).get("last_trade").asString
        }
    },
    FTX("FTX") {
        override fun getValue(coin: String, currency: String): String {
            val url = "https://ftx.com/api/markets/$coin/$currency"
            return getJsonObject(url).getAsJsonObject("result").get("last").asString
        }
    },
    FOXBIT("FoxBit") {

        override fun getValue(coin: String, currency: String): String {
            return getBlinkTradeValue(coin, currency)
        }
    },
    GATEIO("Gate.io") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "${coin}_$currency".toLowerCase(Locale.ROOT)
            val url = "https://data.gate.io/api2/1/ticker/$pair"
            return getJsonObject(url).get("last").asString
        }
    },
    GEMINI("Gemini") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "$coin$currency".toLowerCase(Locale.ROOT)
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
    INDEPENDENT_RESERVE("Independent Reserve", "Ind. Reserve") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.independentreserve.com/Public/GetMarketSummary?primaryCurrencyCode=${coin.toLowerCase(Locale.ROOT)}&secondaryCurrencyCode=${currency.toLowerCase(Locale.ROOT)}"
            return getJsonObject(url).get("LastPrice").asString
        }
    },
    INDODAX("Indodax") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "${coin}_$currency".toLowerCase(Locale.ROOT)
            val url = "https://indodax.com/api/ticker/$pair"
            return getJsonObject(url).getAsJsonObject("ticker").get("last").asString
        }
    },
    ITBIT("ItBit") {

        override fun getValue(coin: String, currency: String): String {
            return getJsonObject("https://api.itbit.com/v1/markets/$coin$currency/ticker").get("lastPrice").asString
        }
    },
    KORBIT("Korbit") {

        override fun getValue(coin: String, currency: String): String {
            val headers = Headers.headersOf("User-Agent", "")
            val pair = "${coin}_$currency".toLowerCase(Locale.ROOT)
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
            val pair = "$coin$currency".toLowerCase(Locale.ROOT)
            val obj = getJsonObject("https://kuna.io/api/v2/tickers/$pair")
            return obj.getAsJsonObject("ticker").get("last").asString
        }
    },
    LAKEBTC("LakeBTC") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "$coin$currency".toLowerCase(Locale.ROOT)
            val obj = getJsonObject("https://api.lakebtc.com/api_v2/ticker?symbol=$pair")
            return obj.getAsJsonObject(pair).get("last").asString
        }
    },
    LBANK("LBank") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "${coin}_$currency".toLowerCase(Locale.ROOT)
            val url = "https://api.lbkex.com/v2/ticker.do?symbol=$pair"
            return getJsonObject(url).getAsJsonArray("data").get(0).asJsonObject.getAsJsonObject("ticker").get("latest").asString
        }
    },
    LIQUID("Liquid") {
        override fun getValue(coin: String, currency: String): String {
            // hardcoded map to currency pair
            val map = mapOf(
                    "BCHBTC" to 114,
                    "BCHJPY" to 41,
                    "BCHSGD" to 40,
                    "BCHUSD" to 39,
                    "BTCAUD" to 13,
                    "BTCEUR" to 3,
                    "BTCHKD" to 9,
                    "BTCJPY" to 5,
                    "BTCSGD" to 7,
                    "BTCUSD" to 1,
                    "DASHBTC" to 116,
                    "ETCBTC" to 110,
                    "ETHAUD" to 33,
                    "ETHBTC" to 37,
                    "ETHCNY" to 35,
                    "ETHEUR" to 28,
                    "ETHHKD" to 31,
                    "ETHINR" to 36,
                    "ETHJPY" to 29,
                    "ETHPHP" to 34,
                    "ETHSGD" to 30,
                    "ETHUSD" to 27,
                    "IOTABTC" to 614,
                    "IOTAUSD" to 613,
                    "KMDBTC" to 550,
                    "LTCBTC" to 112,
                    "NEOBTC" to 119,
                    "NEOEUR" to 56,
                    "NEOSGD" to 55,
                    "NEOUSD" to 53,
                    "OMGBTC" to 125,
                    "TRXBTC" to 117,
                    "TRXETH" to 120,
                    "XEMBTC" to 113,
                    "XLMBTC" to 115,
                    "XLMETH" to 141,
                    "XMRBTC" to 109,
                    "XRPBTC" to 111,
                    "XRPEUR" to 85,
                    "XRPIDR" to 87,
                    "XRPJPY" to 83,
                    "XRPSGD" to 86,
                    "XRPUSD" to 84,
                    "ZECBTC" to 107)
            val id = map["$coin$currency"]
            val url = "https://api.liquid.com/products/$id"
            return getJsonObject(url).get("last_traded_price").asString
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
    NEXCHANGE("Nexchange") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.n.exchange/en/api/v1/price/$coin$currency/latest/?format=json"
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
            val url = "https://www.okex.com/api/spot/v3/instruments/$coin-$currency/ticker"
            return getJsonObject(url).get("last").asString
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
            val url = "https://paymium.com/api/v1/data/${currency.toLowerCase(Locale.ROOT)}/ticker"
            return getJsonObject(url).get("price").asString
        }
    },
    POLONIEX("Poloniex") {

        override fun getValue(coin: String, currency: String): String {
            val obj = getJsonObject("https://poloniex.com/public?command=returnTicker")
            return obj.getAsJsonObject("${currency}_$coin").get("last").asString
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
            val url = "https://api.sendwyre.com/v3/rates"
            val currencyName = "$currency$coin"
            return getJsonObject(url).get(currencyName).asString
        }
    },
    YOBIT("YoBit") {
        override fun getValue(coin: String, currency: String): String {
            val pair = "${coin}_$currency".toLowerCase(Locale.ROOT)
            val url = "https://yobit.net/api/3/ticker/$pair"
            return getJsonObject(url).getAsJsonObject(pair).get("last").asString
        }
    },
    ZB("ZB") {

        override fun getValue(coin: String, currency: String): String {
            val url = "http://api.zb.live/data/v1/ticker?market=${coin.toLowerCase(Locale.ROOT)}_${currency.toLowerCase(Locale.ROOT)}"
            return getJsonObject(url).getAsJsonObject("ticker").get("last").asString
        }
    },
    ZBG("ZBG") {
        override fun getValue(coin: String, currency: String): String {
            val pair = "${coin}_$currency".toLowerCase(Locale.ROOT)
            val url = "https://kline.zbg.com/api/data/v1/ticker?marketName=$pair"
            return getJsonObject(url).getAsJsonArray("datas")[1].asString
        }
    };

    val shortName: String = shortName ?: exchangeName

    fun getBlinkTradeValue(coin: String, currency: String): String {
        val url = "https://api.blinktrade.com/api/v1/$currency/ticker?crypto_currency=$coin"
        return getJsonObject(url).get("last").asString
    }

    abstract fun getValue(coin: String, currency: String): String?


    companion object {

        private val ALL_EXCHANGE_NAMES = values().map { it.name}.toMutableList()

        fun getAllExchangeNames(): MutableList<String> {
            return ALL_EXCHANGE_NAMES.toMutableList()
        }
    }
}
