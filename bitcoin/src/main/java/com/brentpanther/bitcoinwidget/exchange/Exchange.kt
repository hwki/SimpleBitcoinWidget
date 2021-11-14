package com.brentpanther.bitcoinwidget.exchange

import com.brentpanther.bitcoinwidget.exchange.ExchangeHelper.getJsonArray
import com.brentpanther.bitcoinwidget.exchange.ExchangeHelper.getJsonObject
import com.google.gson.JsonObject
import okhttp3.Headers
import java.util.*
import kotlin.math.pow

enum class Exchange(val exchangeName: String, shortName: String? = null) {

    ABUCOINS("Abucoins") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.abucoins.com/products/$coin$currency/stats"
            return getJsonObject(url).get("last").asString
        }
    },
    ASCENDEX("AscendEX") {
        override fun getValue(coin: String, currency: String): String? {
            val obj = getJsonObject("https://ascendex.com/api/pro/v1/ticker?symbol=$coin/$currency")
            return obj.getAsJsonObject("data").get("close").asString
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
            val pair = "${coin}_$currency".lowercase(Locale.ROOT)
            val url = "https://public.bitbank.cc/$pair/ticker"
            return getJsonObject(url).getAsJsonObject("data").get("last").asString
        }
    },
    BITBAY("BitBay") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.bitbay.net/rest/trading/ticker/$coin-$currency"
            return getJsonObject(url).getAsJsonObject("ticker").get("rate").asString
        }
    },
    BITCAMBIO("BitCambio") {

        override fun getValue(coin: String, currency: String): String {
            return getBlinkTradeValue(coin, currency)
        }
    },
    BITCLUDE("BitClude") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.bitclude.com/stats/ticker.json"
            val obj = getJsonObject(url)
            return obj.getAsJsonObject("${coin.lowercase(Locale.ROOT)}_${currency.lowercase(Locale.ROOT)}").get("last").asString
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
    BITHUMB_PRO("Bitglobal") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://global-openapi.bithumb.pro/openapi/v1/spot/ticker?symbol=$coin-$currency"
            return getJsonObject(url).getAsJsonArray("data").get(0).asJsonObject.get("c").asString
        }
    },
    BITMEX("BitMEX") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://www.bitmex.com/api/v1/instrument?symbol=$coin$currency&columns=lastPrice"
            return getJsonArray(url).get(0).asJsonObject.get("lastPrice").asString
        }
    },
    BITPANDA("Bitpanda") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.bitpanda.com/v1/ticker"
            return getJsonObject(url).get(coin).asJsonObject.get(currency).asString
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
    BITSO("Bitso") {

        override fun getValue(coin: String, currency: String): String? {
            val payload =
                getJsonObject("https://api.bitso.com/v3/ticker/").getAsJsonArray("payload")
            val pair = "${coin}_$currency".lowercase(Locale.ROOT)
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
            val url = "https://www.bitstamp.net/api/v2/ticker/${coin.lowercase(Locale.ROOT)}${
                currency.lowercase(Locale.ROOT)
            }"
            return getJsonObject(url).get("last").asString
        }
    },
    BITTREX("Bittrex") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.bittrex.com/v3/markets/$coin-$currency/ticker"
            return getJsonObject(url).get("lastTradeRate").asString
        }
    },
    BITRUE("Bitrue") {
        override fun getValue(coin: String, currency: String): String? {
            val url = "https://openapi.bitrue.com/api/v1/ticker/price?symbol=$coin$currency"
            return getJsonObject(url).get("price").asString
        }
    },
    BITVAVO ("Bitvavo") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://api.bitvavo.com/v2/ticker/price?market=$coin-$currency"
            return getJsonObject(url).get("price").asString
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
    BTCBOX("BTC Box") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://www.btcbox.co.jp/api/v1/ticker/?coin=${coin.lowercase(Locale.ROOT)}"
            return getJsonObject(url).get("last").asString
        }
    },
    BTCMARKETS("BTC Markets") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.btcmarkets.net/v3/markets/$coin-$currency/ticker"
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
    COINBENE("CoinBene") {
        override fun getValue(coin: String, currency: String): String? {
            val url = "https://openapi-exchange.coinbene.com/api/spot/market/summary"
            val array = getJsonArray(url)
            for (jsonElement in array) {
                val obj = jsonElement as JsonObject
                if (obj.get("trading_pairs").asString == "${coin}_$currency") {
                    return obj.get("last_price").asString
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
    COINGECKO("CoinGecko") {

        override fun getValue(coin: String, currency: String): String? {
            // hardcoded map to id
            val map = mapOf(
                "AAVE" to "aave",
                "ADA" to "cardano",
                "ALGO" to "algorand",
                "ARRR" to "pirate-chain",
                "ATOM" to "cosmos",
                "AVA" to "concierge-io",
                "AVAX" to "avalanche-2",
                "AXS" to "axie-infinity",
                "BAL" to "balancer",
                "BAND" to "band-protocol",
                "BAT" to "basic-attention-token",
                "BCD" to "bitcoin-diamond",
                "BEST" to "bitpanda-ecosystem-token",
                "BCH" to "bitcoin-cash",
                "BNB" to "binancecoin",
                "BNT" to "bancor",
                "BSV" to "bitcoin-cash-sv",
                "BTC" to "bitcoin",
                "BTG" to "bitcoin-gold",
                "BTM" to "bytom",
                "BTT" to "bittorrent-2",
                "CEL" to "celsius-degree-token",
                "CHZ" to "chiliz",
                "COMP" to "compound-coin",
                "CRO" to "crypto-com-chain",
                "CRV" to "curve-dao-token",
                "CUBE" to "somnium-space-cubes",
                "DAI" to "dai",
                "DASH" to "dash",
                "DCR" to "decred",
                "DOGE" to "dogecoin",
                "DOT" to "polkadot",
                "EGLD" to "elrond-erd-2",
                "ENJ" to "enjincoin",
                "EOS" to "eos",
                "ETC" to "ethereum-classic",
                "ETH" to "ethereum",
                "FIL" to "filecoin",
                "FIRO" to "zcoin",
                "FTT" to "ftx-token",
                "GNO" to "gnosis",
                "GNT" to "golem",
                "GRIN" to "grin",
                "GRT" to "the-graph",
                "HBAR" to "hedera-hashgraph",
                "HNS" to "handshake",
                "HT" to "huobi-token",
                "ICX" to "icon",
                "IOTA" to "iota",
                "KMD" to "komodo",
                "KNC" to "kyber-network",
                "KSM" to "kusama",
                "LEO" to "leo-token",
                "LINK" to "chainlink",
                "LRC" to "loopring",
                "LSK" to "lisk",
                "LTC" to "litecoin",
                "LTO" to "lto-network",
                "LUNA" to "terra-luna",
                "MANA" to "decentraland",
                "MATIC" to "matic-network",
                "MCO" to "monaco",
                "MKR" to "maker",
                "MLN" to "melon",
                "NANO" to "nano",
                "NEAR" to "near",
                "NEO" to "neo",
                "NRG" to "energi",
                "OKB" to "okb",
                "OMG" to "omisego",
                "ONT" to "ontology",
                "PAX" to "paxos-standard",
                "PAXG" to "pax-gold",
                "POWR" to "power-ledger",
                "PPC" to "peercoin",
                "QTUM" to "qtum",
                "RDD" to "reddcoin",
                "REN" to "republic-protocol",
                "REP" to "augur",
                "RUNE" to "thorchain",
                "RVN" to "ravencoin",
                "SHIB" to "shiba-inu",
                "SNX" to "havven",
                "SOL" to "solana",
                "SUSHI" to "sushi",
                "THETA" to "theta-token",
                "TRX" to "tron",
                "UMA" to "uma",
                "UNI" to "uniswap",
                "VET" to "vechain",
                "VTC" to "vertcoin",
                "WAVES" to "waves",
                "WBTC" to "wrapped-bitcoin",
                "XAUT" to "tether-gold",
                "XEM" to "nem",
                "XLM" to "stellar",
                "XMR" to "monero",
                "XRP" to "ripple",
                "XTZ" to "tezos",
                "XVG" to "verge",
                "XYM" to "symbol",
                "YFI" to "yearn-finance",
                "ZEC" to "zcash",
                "ZIL" to "zilliqa",
                "ZRX" to "0x"
            )
            val id = map[coin] ?: coin
            val vs = currency.lowercase(Locale.ROOT)
            val url = "https://api.coingecko.com/api/v3/simple/price?ids=$id&vs_currencies=$vs"
            return getJsonObject(url).getAsJsonObject(id).get(vs).asString
        }
    },
    COINJAR("CoinJar") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://data.exchange.coinjar.com/products/$coin$currency/ticker"
            return getJsonObject(url).get("last").asString
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
    COINSPH("Coins.ph") {

        override fun getValue(coin: String, currency: String): String {
            val region = if (currency == "THB") "TH" else "PH"
            val url = "https://quote.coins.ph/v2/markets/$coin-$currency?region=$region"
            val obj = getJsonObject(url)
            val bid = obj.get("bid").asString
            val ask = obj.get("ask").asString
            return ((bid.toDouble() + ask.toDouble()) / 2).toString()
        }
    },
    COINTREE("Cointree") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://trade.cointree.com/api/prices/AUD/change/24h?symbols=$coin"
            return getJsonArray(url).get(0).asJsonObject.get("spot").asString
        }
    },
    CRYPTO("Crypto.com") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.crypto.com/v2/public/get-ticker?instrument_name=${coin}_$currency"
            return getJsonObject(url).getAsJsonObject("result").getAsJsonObject("data").get("a").asString
        }
    },
    DEVERSIFI("DeversiFi") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.deversifi.com/bfx/v2/tickers?symbols=t${coin}$currency"
            return getJsonArray(url)[0].asJsonArray[7].asString
        }
    },
    EXMO("Exmo") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "${coin}_$currency"
            val url = "https://api.exmo.com/v1.1/ticker"
            return getJsonObject(url).getAsJsonObject(pair).get("last_trade").asString
        }
    },
    FTX("FTX") {
        override fun getValue(coin: String, currency: String): String {
            val url = "https://ftx.com/api/markets/$coin/$currency"
            return getJsonObject(url).getAsJsonObject("result").get("last").asString
        }
    },
    FTX_US("FTX US") {
        override fun getValue(coin: String, currency: String): String {
            val url = "https://ftx.us/api/markets/$coin/$currency"
            return getJsonObject(url).getAsJsonObject("result").get("last").asString
        }
    },
    FOXBIT("FoxBit") {

        override fun getValue(coin: String, currency: String): String? {
            val payload = getJsonArray("https://watcher.foxbit.com.br/api/Ticker/")
            val pair = "${currency}X$coin"
            for (jsonElement in payload) {
                val obj = jsonElement as JsonObject
                if (obj.get("currency").asString == pair) {
                    return obj.get("last").asString
                }
            }
            return null
        }
    },
    GATEIO("Gate.io") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.gateio.ws/api/v4/spot/tickers?currency_pair=${coin}_$currency"
            return getJsonArray(url)[0].asJsonObject.get("last").asString
        }
    },
    GEMINI("Gemini") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "$coin$currency".lowercase(Locale.ROOT)
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
            val pair = "${coin}$currency".lowercase(Locale.ROOT)
            val url = "https://api.huobi.pro/market/detail/merged?symbol=$pair"
            return getJsonObject(url).getAsJsonObject("tick").get("close").asString
        }
    },
    INDEPENDENT_RESERVE("Independent Reserve", "Ind. Reserve") {

        override fun getValue(coin: String, currency: String): String {
            val url =
                "https://api.independentreserve.com/Public/GetMarketSummary?primaryCurrencyCode=${
                    coin.lowercase(Locale.ROOT)
                }&secondaryCurrencyCode=${currency.lowercase(Locale.ROOT)}"
            return getJsonObject(url).get("LastPrice").asString
        }
    },
    INDODAX("Indodax") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "${coin}_$currency".lowercase(Locale.ROOT)
            val url = "https://indodax.com/api/$pair/ticker"
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
            val pair = "${coin}_$currency".lowercase(Locale.ROOT)
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
            val pair = "$coin$currency".lowercase(Locale.ROOT)
            val obj = getJsonObject("https://kuna.io/api/v2/tickers/$pair")
            return obj.getAsJsonObject("ticker").get("last").asString
        }
    },
    LBANK("LBank") {

        override fun getValue(coin: String, currency: String): String {
            val pair = "${coin}_$currency".lowercase(Locale.ROOT)
            val url = "https://api.lbkex.com/v2/ticker.do?symbol=$pair"
            return getJsonObject(url).getAsJsonArray("data")
                .get(0).asJsonObject.getAsJsonObject("ticker").get("latest").asString
        }
    },
    LIQUID("Liquid") {
        override fun getValue(coin: String, currency: String): String? {
            val array = getJsonArray("https://api.liquid.com/products")
            val pair = "$coin$currency"
            for (jsonElement in array) {
                val obj = jsonElement as JsonObject
                if (pair == obj.get("currency_pair_code").asString) {
                    return obj.get("last_traded_price").asString
                }
            }
            return null
        }
    },
    LUNO("Luno") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.luno.com/api/1/ticker?pair=$coin$currency"
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

        override fun getValue(coin: String, currency: String): String {
            val url = "https://core.ndax.io/v1/ticker"
            return getJsonObject(url).getAsJsonObject("${coin}_$currency").get("last").asString
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
    P2PB2B("P2PB2B") {
        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.p2pb2b.io/api/v2/public/ticker?market=${coin}_$currency"
            return getJsonObject(url).getAsJsonObject("result").get("last").asString
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
            val url = "https://paymium.com/api/v1/data/${currency.lowercase(Locale.ROOT)}/ticker"
            return getJsonObject(url).get("price").asString
        }
    },
    PHEMEX("Phemex") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.phemex.com/md/spot/ticker/24hr?symbol=s${coin}$currency"
            val value = getJsonObject(url).getAsJsonObject("result").get("lastEp").asString
            return (value.toDouble() / 10.0.pow(8)).toString()
        }
    },
    POCKETBITS("Pocketbits") {

        override fun getValue(coin: String, currency: String): String? {
            val url = "https://ticker.pocketbits.in/api/v1/ticker"
            val obj = getJsonArray(url).firstOrNull {
                it.asJsonObject.get("symbol").asString == "$coin$currency"
            }?.asJsonObject ?: return null
            val buy = obj.get("buy").asString.toDouble()
            val sell = obj.get("sell").asString.toDouble()
            return ((buy + sell) / 2).toString()
        }
    },
    POLONIEX("Poloniex") {

        override fun getValue(coin: String, currency: String): String {
            val obj = getJsonObject("https://poloniex.com/public?command=returnTicker")
            return obj.getAsJsonObject("${currency}_$coin").get("last").asString
        }
    },
    PROBIT("ProBit") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.probit.com/api/exchange/v1/ticker?market_ids=$coin-$currency"
            return getJsonObject(url).getAsJsonArray("data")[0].asJsonObject.get("last").asString
        }
    },
    THEROCK("TheRock") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.therocktrading.com/v1/funds/$coin$currency/ticker"
            return getJsonObject(url).get("last").asString
        }
    },
    TRADEOGRE("TradeOgre") {
        override fun getValue(coin: String, currency: String): String {
            val url = "https://tradeogre.com/api/v1/ticker/$currency-$coin"
            return getJsonObject(url).get("price").asString
        }
    },
    UPHOLD("Uphold") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.uphold.com/v0/ticker/$coin-$currency"
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
    WHITEBIT("WhiteBIT") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://whitebit.com/api/v1/public/ticker?market=${coin}_$currency"
            return getJsonObject(url).getAsJsonObject("result").get("last").asString
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
            val pair = "${coin}_$currency".lowercase(Locale.ROOT)
            val url = "https://yobit.net/api/3/ticker/$pair"
            return getJsonObject(url).getAsJsonObject(pair).get("last").asString
        }
    },
    ZB("ZB") {

        override fun getValue(coin: String, currency: String): String {
            val url = "https://api.zb.land/data/v1/ticker?market=${coin.lowercase(Locale.ROOT)}_${
                currency.lowercase(Locale.ROOT)
            }"
            return getJsonObject(url).getAsJsonObject("ticker").get("last").asString
        }
    },
    ZBG("ZBG") {
        override fun getValue(coin: String, currency: String): String {
            val pair = "${coin}_$currency".lowercase(Locale.ROOT)
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
