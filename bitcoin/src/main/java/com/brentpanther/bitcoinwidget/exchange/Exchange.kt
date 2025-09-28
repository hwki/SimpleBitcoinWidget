package com.brentpanther.bitcoinwidget.exchange

import com.brentpanther.bitcoinwidget.Coin
import com.brentpanther.bitcoinwidget.db.PriceType
import com.brentpanther.bitcoinwidget.db.PriceType.ASK
import com.brentpanther.bitcoinwidget.db.PriceType.BID
import com.brentpanther.bitcoinwidget.db.PriceType.SPOT
import com.brentpanther.bitcoinwidget.exchange.ExchangeHelper.asString
import com.brentpanther.bitcoinwidget.exchange.ExchangeHelper.getJsonArray
import com.brentpanther.bitcoinwidget.exchange.ExchangeHelper.getJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlin.math.pow

enum class Exchange(val exchangeName: String, shortName: String? = null) {

    ASCENDEX("AscendEX") {
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val obj = getJsonObject("https://ascendex.com/api/pro/v1/ticker?symbol=$coin/$currency")
            val data = obj["data"]?.jsonObject
            return when (priceType) {
                SPOT -> data?.get("close")
                BID -> data?.get("bid")?.jsonArray[0]
                ASK -> data?.get("ask")?.jsonArray[0]
            }.asString
        }
    },
    BIBOX("Bibox") {
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.bibox.com/api/v4/marketdata/ticker?symbol=${coin}_$currency"
            val data = getJsonArray(url)[0].jsonObject
            return when (priceType) {
                SPOT -> data["p"]
                BID -> data["bp"]
                ASK -> data["ap"]
            }.asString
        }
    },
    BIGONE("BigONE") {
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://big.one/api/v3/asset_pairs/$coin-$currency/ticker"
            val obj = getJsonObject(url)["data"]?.jsonObject ?: return null
            val bid = obj["bid"]?.jsonObject?.get("price").asString ?: return null
            val ask = obj["ask"]?.jsonObject?.get("price").asString ?: return null
            return when (priceType) {
                SPOT -> ((bid.toDouble() + ask.toDouble()) / 2).toString()
                BID -> bid
                ASK -> ask
            }
        }
    },
    BINANCE("Binance.com") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val type = if (priceType == SPOT) "price" else "bookTicker"
            val url = "https://api.binance.com/api/v3/ticker/$type?symbol=$coin$currency"
            val data = getJsonObject(url)
            return when (priceType) {
                SPOT -> data["price"]
                BID -> data["bidPrice"]
                ASK -> data["askPrice"]
            }.asString
        }
    },
    BINANCE_P2P("Binancep2p") {
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            return getCriptoYaValue("binancep2p", coin, currency, priceType)
        }
    },
    BINANCE_US("Binance.us") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val type = if (priceType == SPOT) "price" else "bookTicker"
            val url = "https://api.binance.us/api/v3/ticker/$type?symbol=$coin$currency"
            val data = getJsonObject(url)
            return when (priceType) {
                SPOT -> data["price"]
                BID -> data["bidPrice"]
                ASK -> data["askPrice"]
            }.asString
        }
    },
    BINGX("BingX") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://open-api.bingx.com/openApi/swap/v2/quote/ticker?symbol=$coin-${currency}"
            val data = getJsonObject(url)["data"]?.jsonObject ?: return null
            return when (priceType) {
                SPOT -> data["lastPrice"]
                BID -> data["bidPrice"]
                ASK -> data["askPrice"]
            }.asString
        }
    },
    BIT2C("Bit2C") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://www.bit2c.co.il/Exchanges/${coin}Nis/Ticker.json"
            val data = getJsonObject(url)
            return when (priceType) {
                SPOT -> data["ll"]
                BID -> data["h"]
                ASK -> data["l"]
            }.asString
        }
    },
    BITBANK("Bitbank") {
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val pair = "${coin}_$currency".lowercase()
            val url = "https://public.bitbank.cc/$pair/ticker"
            val data = getJsonObject(url)["data"]?.jsonObject ?: return null
            return when (priceType) {
                SPOT -> data["last"]
                BID -> data["buy"]
                ASK -> data["sell"]
            }.asString
        }
    },
    BITCOINDE("Bitcoin.de") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val obj = getJsonObject("https://bitcoinapi.de/widget/current-btc-price/rate.json")
            val price = obj["price_eur"].asString
            val amount = price?.split("\\u00A0".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray() ?: return null
            return amount[0].replace("\\.".toRegex(), "").replace(",".toRegex(), ".")
        }
        override val hasSpotPriceOnly = true
    },
    BITFINEX("Bitfinex") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api-pub.bitfinex.com/v2/ticker/t$coin$currency"
            val data = getJsonArray(url)
            return when (priceType) {
                SPOT -> data[6]
                BID -> data[0]
                ASK -> data[2]
            }.asString
        }
    },
    BITFLYER("bitFlyer") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.bitflyer.com/v1/ticker?product_code=${coin}_$currency"
            val data = getJsonObject(url)
            return when (priceType) {
                SPOT -> data["ltp"]
                BID -> data["best_bid"]
                ASK -> data["best_ask"]
            }.asString
        }
    },
    BITHUMB("Bithumb") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.bithumb.com/v1/ticker?markets=$currency-$coin"
            val data = getJsonArray(url)[0].jsonObject
            return when (priceType) {
                SPOT -> data["trade_price"]
                BID -> data["low_price"]
                ASK -> data["high_price"]
            }.asString
        }
    },
    BITMART("BitMart") {
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api-cloud.bitmart.com/spot/quotation/v3/ticker?symbol=${coin}_$currency"
            val data = getJsonObject(url)["data"]?.jsonObject ?: return null
            return when (priceType) {
                SPOT -> data["last"]
                BID -> data["bid_px"]
                ASK -> data["ask_px"]
            }.asString
        }
    },
    BITPANDA("Bitpanda") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.bitpanda.com/v1/ticker"
            return getJsonObject(url)[coin]?.jsonObject?.get(currency).asString
        }

        override val hasSpotPriceOnly = true
    },
    BITPAY("BitPay") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://bitpay.com/api/rates/$coin/$currency"
            return getJsonObject(url)["rate"].asString
        }

        override val hasSpotPriceOnly = true
    },
    BITSO("Bitso") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.bitso.com/v3/ticker?book=${coin.lowercase()}_${currency.lowercase()}"
            val data = getJsonObject(url)["payload"]?.jsonObject ?: return null
            return when (priceType) {
                SPOT -> data["last"]
                BID -> data["bid"]
                ASK -> data["ask"]
            }.asString
        }
    },
    BITSO_ALPHA("Bitso Alpha") {
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            return getCriptoYaValue("bitsoalpha", coin, currency, priceType)
        }
    },
    BITSTAMP("Bitstamp") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://www.bitstamp.net/api/v2/ticker/${coin.lowercase()}${currency.lowercase()}"
            val data = getJsonObject(url)
            return when (priceType) {
                SPOT -> data["last"]
                BID -> data["bid"]
                ASK -> data["ask"]
            }.asString
        }
    },
    BITRUE("Bitrue") {
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val type = if (priceType == SPOT) "price" else "bookTicker"
            val url = "https://openapi.bitrue.com/api/v1/ticker/$type?symbol=$coin$currency"
            val data = getJsonObject(url)
            return when (priceType) {
                SPOT -> data["price"]
                BID -> data["bidPrice"]
                ASK -> data["askPrice"]
            }.asString
        }
    },
    BITVAVO ("Bitvavo") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val type = if (priceType == SPOT) "price" else "book"
            val url = "https://api.bitvavo.com/v2/ticker/$type?market=$coin-$currency"
            val data = getJsonObject(url)
            return when (priceType) {
                SPOT -> data["price"]
                BID -> data["bid"]
                ASK -> data["ask"]
            }.asString
        }

    },
    BTCBOX("BTC Box") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://www.btcbox.co.jp/api/v1/ticker/?coin=${coin.lowercase()}"
            val data = getJsonObject(url)
            return when (priceType) {
                SPOT -> data["last"]
                BID -> data["buy"]
                ASK -> data["sell"]
            }.asString
        }
    },
    BTCMARKETS("BTC Markets") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.btcmarkets.net/v3/markets/$coin-$currency/ticker"
            val data = getJsonObject(url)
            return when (priceType) {
                SPOT -> data["lastPrice"]
                BID -> data["bestBid"]
                ASK -> data["bestAsk"]
            }.asString
        }
    },
    BTCTURK("BTCTurk") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.btcturk.com/api/v2/ticker?pairSymbol=${coin}_$currency"
            val data = getJsonObject(url)["data"]?.jsonArray?.get(0)?.jsonObject ?: return null
            return when (priceType) {
                SPOT -> data["last"]
                BID -> data["bid"]
                ASK -> data["ask"]
            }.asString
        }
    },
    BYBIT("Bybit") {
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.bybit.com/v5/market/tickers?category=spot&symbol=$coin$currency"
            val data = getJsonObject(url)["result"]?.jsonObject?.get("list")?.jsonArray?.get(0)?.jsonObject ?: return null
            return when (priceType) {
                SPOT -> data["lastPrice"]
                BID -> data["bid1Price"]
                ASK -> data["ask1Price"]
            }.asString
        }
    },
    CEXIO("Cex.io") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val data = getJsonObject("https://cex.io/api/ticker/$coin/$currency")
            return when (priceType) {
                SPOT -> data["last"]
                BID -> data["bid"]
                ASK -> data["ask"]
            }.asString
        }
    },
    CHILEBIT("ChileBit.net") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            return getBlinkTradeValue(coin, currency, priceType)
        }
    },
    COINBASE("Coinbase") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val type = when(priceType) {
                SPOT -> "spot"
                BID -> "buy"
                ASK -> "sell"
            }
            val obj = getJsonObject("https://api.coinbase.com/v2/prices/$coin-$currency/$type")
            return obj["data"]?.jsonObject?.get("amount").asString
        }
    },
    COINGECKO("CoinGecko") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val id = Coin.getByName(coin)?.coinGeckoId ?: coin
            val vs = currency.lowercase()
            val url = "https://api.coingecko.com/api/v3/simple/price?ids=$id&vs_currencies=$vs"
            return getJsonObject(url)[id]?.jsonObject?.get(vs).asString
        }

        override val hasSpotPriceOnly = true
    },
    COINJAR("CoinJar") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://data.exchange.coinjar.com/products/$coin$currency/ticker"
            val data = getJsonObject(url)
            return when (priceType) {
                SPOT -> data["last"]
                BID -> data["bid"]
                ASK -> data["ask"]
            }.asString
        }
    },
    COINMATE("CoinMate.io") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://coinmate.io/api/ticker?currencyPair=${coin}_$currency"
            val data = getJsonObject(url)["data"]?.jsonObject ?: return null
            return when (priceType) {
                SPOT -> data["last"]
                BID -> data["bid"]
                ASK -> data["ask"]
            }.asString
        }
    },
    COINONE("Coinone") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.coinone.co.kr/public/v2/ticker_new/$currency/$coin"
            val data = getJsonObject(url)["tickers"]?.jsonArray?.get(0)?.jsonObject ?: return null
            return when (priceType) {
                SPOT -> data["last"]
                BID -> data["best_bids"]?.jsonArray[0]?.jsonObject["price"]
                ASK -> data["best_asks"]?.jsonArray[0]?.jsonObject["price"]
            }.asString
        }
    },
    COINPAPRIKA("Coinpaprika") {
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            // since we can't look up by symbol, first search for id
            val searchUrl = "https://api.coinpaprika.com/v1/search/?q=$coin&c=currencies&modifier=symbol_search"
            val id = getJsonObject(searchUrl)["currencies"]?.jsonArray?.firstOrNull {
                it.jsonObject["symbol"].asString == coin
            }?.jsonObject?.get("id").asString ?: return null
            val url = "https://api.coinpaprika.com/v1/tickers/$id?quotes=$currency"
            return getJsonObject(url)["quotes"]?.jsonObject?.get(currency)?.jsonObject?.get("price").asString
        }

    },
    COINSPH("Coins.ph") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val type = if (priceType == SPOT) "price" else "bookTicker"
            val url = "https://api.pro.coins.ph/openapi/quote/v1/ticker/$type?symbol=$coin$currency"
            val data = getJsonObject(url)
            return when (priceType) {
                SPOT -> data["price"]
                BID -> data["bidPrice"]
                ASK -> data["askPrice"]
            }.asString
        }
    },
    COINTREE("Cointree") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://trade.cointree.com/api/prices/AUD/change/24h?symbols=$coin"
            val data = getJsonArray(url)[0].jsonObject
            return when (priceType) {
                SPOT -> data["spot"]
                BID -> data["bid"]
                ASK -> data["ask"]
            }.asString
        }
    },
    CRYPTO("Crypto.com") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.crypto.com/exchange/v1/public/get-tickers?instrument_name=${coin}_$currency"
            val data = getJsonObject(url)["result"]?.jsonObject?.get("data")?.jsonArray?.get(0)?.jsonObject ?: return null
            return when (priceType) {
                SPOT -> data["a"]
                BID -> data["b"]
                ASK -> data["k"]
            }.asString
        }
    },
    DIGIFINEX("Digifinex") {
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://openapi.digifinex.com/v3/ticker?symbol=${coin}_$currency"
            val data = getJsonObject(url)["ticker"]?.jsonArray?.get(0)?.jsonObject ?: return null
            return when (priceType) {
                SPOT -> data["last"]
                BID -> data["buy"]
                ASK -> data["sell"]
            }.asString
        }
    },
    EGERA("Egera") {
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.egera.com/stats/ticker.json"
            val obj = getJsonObject(url)
            val data = obj["${coin.lowercase()}_${currency.lowercase()}"]?.jsonObject ?: return null
            return when (priceType) {
                SPOT -> data["last"]
                BID -> data["bid"]
                ASK -> data["ask"]
            }.asString
        }
    },
    EXMO("Exmo") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val pair = "${coin}_$currency"
            val url = "https://api.exmo.com/v1.1/ticker"
            val data = getJsonObject(url)[pair]?.jsonObject ?: return null
            return when (priceType) {
                SPOT -> data["last_trade"]
                BID -> data["buy_price"]
                ASK -> data["sell_price"]
            }.asString
        }
    },
    FIWIND("Fiwind") {
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            return getCriptoYaValue("fiwind", coin, currency, priceType)
        }
    },
    FOXBIT("FoxBit") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.foxbit.com.br/rest/v3/markets/$coin$currency/ticker/24hr"
            val data = getJsonObject(url)["data"]?.jsonArray[0]?.jsonObject ?: return null
            return when (priceType) {
                SPOT -> data["last_trade"]?.jsonObject["price"]
                BID -> data["best"]?.jsonObject["bid"]?.jsonObject["price"]
                ASK -> data["best"]?.jsonObject["ask"]?.jsonObject["price"]
            }.asString
        }
    },
    GATEIO("Gate.io") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.gateio.ws/api/v4/spot/tickers?currency_pair=${coin}_$currency"
            val data = getJsonArray(url)[0].jsonObject
            return when(priceType) {
                SPOT -> data["last"]
                BID -> data["highest_bid"]
                ASK -> data["lowest_ask"]
            }.asString
        }
    },
    GEMINI("Gemini") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.gemini.com/v2/ticker/$coin$currency"
            val data = getJsonObject(url)
            return when (priceType) {
                SPOT -> data["close"]
                BID -> data["bid"]
                ASK -> data["ask"]
            }.asString
        }
    },
    HASHKEY("Hashkey") {
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val type = if (priceType == SPOT) "price" else "bookTicker"
            val url = "https://api-pro.hashkey.com/quote/v1/ticker/$type?symbol=$coin$currency"
            val data = getJsonArray(url)[0].jsonObject
            return when (priceType) {
                SPOT -> data["p"]
                BID -> data["b"]
                ASK -> data["a"]
            }.asString
        }
    },
    HITBTC("HitBTC") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.hitbtc.com/api/3/public/ticker?symbols=$coin$currency"
            val data = getJsonObject(url)["$coin$currency"]?.jsonObject ?: return null
            return when (priceType) {
                SPOT -> data["last"]
                BID -> data["bid"]
                ASK -> data["ask"]
            }.asString
        }
    },
    HUOBI("Huobi") {
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val pair = "${coin}$currency".lowercase()
            val url = "https://api.huobi.pro/market/detail/merged?symbol=$pair"
            val data = getJsonObject(url)["tick"]?.jsonObject ?: return null
            return when (priceType) {
                SPOT -> data["close"]
                BID -> data["bid"]?.jsonArray[0]
                ASK -> data["ask"]?.jsonArray[0]
            }.asString
        }
    },
    INDEPENDENT_RESERVE("Independent Reserve", "Ind. Reserve") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val query = "primaryCurrencyCode=${coin.lowercase()}&secondaryCurrencyCode=${currency.lowercase()}"
            val url = "https://api.independentreserve.com/Public/GetMarketSummary?$query"
            val data = getJsonObject(url)
            return when (priceType) {
                SPOT -> data["LastPrice"]
                BID -> data["CurrentHighestBidPrice"]
                ASK -> data["CurrentLowestOfferPrice"]
            }.asString
        }
    },
    INDODAX("Indodax") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val pair = "${coin}_$currency".lowercase()
            val url = "https://indodax.com/api/$pair/ticker"
            val data = getJsonObject(url)["ticker"]?.jsonObject ?: return null
            return when (priceType) {
                SPOT -> data["last"]
                BID -> data["buy"]
                ASK -> data["sell"]
            }.asString
        }
    },
    ITBIT("ItBit") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.paxos.com/v2/markets/$coin$currency/ticker"
            val data = getJsonObject(url)
            return when (priceType) {
                SPOT -> data["last_execution"]?.jsonObject?.get("price")
                BID -> data["best_bid"]?.jsonObject?.get("price")
                ASK -> data["best_ask"]?.jsonObject?.get("price")
            }.asString
        }
    },
    KORBIT("Korbit") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val pair = "${coin}_$currency".lowercase()
            val url = "https://api.korbit.co.kr/v2/tickers?symbol=$pair"
            val data = getJsonObject(url)["data"]?.jsonArray[0]?.jsonObject ?: return null
            return when(priceType) {
                SPOT -> data["close"]
                BID -> data["bestBidPrice"]
                ASK -> data["bestAskPrice"]
            }.asString
        }
    },
    KRAKEN("Kraken") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val obj = getJsonObject("https://api.kraken.com/0/public/Ticker?pair=$coin$currency")
            val obj2 = obj["result"]?.jsonObject
            val key = obj2?.keys?.first() ?: return null
            val data = obj2[key]?.jsonObject ?: return null
            return when(priceType) {
                SPOT -> data["c"]?.jsonArray?.get(0)
                BID -> data["b"]?.jsonArray?.get(0)
                ASK -> data["a"]?.jsonArray?.get(0)
            }.asString
        }
    },
    KUCOIN("Kucoin") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.kucoin.com/api/v1/market/orderbook/level1?symbol=$coin-$currency"
            val data = getJsonObject(url)["data"]?.jsonObject ?: return null
            return when(priceType) {
                SPOT -> data["price"]
                BID -> data["bestBid"]
                ASK -> data["bestAsk"]
            }.asString
        }
    },
    LBANK("LBank") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val pair = "${coin}_$currency".lowercase()
            val type = if (priceType == SPOT) "price" else "bookTicker"
            val url = "https://api.lbkex.com/v2/supplement/ticker/$type.do?symbol=$pair"
            val data = getJsonObject(url)["data"] ?: return null
            return when (priceType) {
                SPOT -> data.jsonArray[0].jsonObject["price"]
                BID -> data.jsonObject["bidPrice"]
                ASK -> data.jsonObject["askPrice"]
            }.asString
        }
    },
    LEMONCASH("Lemon Cash") {
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            return getCriptoYaValue("lemoncashp2p", coin, currency, priceType)
        }
    },
    LUNO("Luno") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.luno.com/api/1/ticker?pair=$coin$currency"
            val data = getJsonObject(url)
            return when (priceType) {
                SPOT -> data["last_trade"]
                BID -> data["bid"]
                ASK -> data["ask"]
            }.asString
        }
    },
    MERCADO("Mercado Bitcoin", "Mercado") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.mercadobitcoin.net/api/v4/tickers?symbols=$coin-$currency"
            val data = getJsonArray(url)[0].jsonObject
            return when (priceType) {
                SPOT -> data["last"]
                BID -> data["buy"]
                ASK -> data["sell"]
            }.asString
        }
    },
    MEXC("MEXC") {
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val type = if (priceType == SPOT) "price" else "bookTicker"
            val url = "https://api.mexc.com/api/v3/ticker/$type?symbol=$coin$currency"
            val data = getJsonObject(url)
            return when(priceType) {
                SPOT -> data["price"]
                BID -> data["bidPrice"]
                ASK -> data["askPrice"]
            }.asString
        }
    },
    NDAX("NDAX") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://core.ndax.io/v1/ticker"
            val data = getJsonObject(url)["${coin}_$currency"]?.jsonObject ?: return null
            return when(priceType) {
                SPOT -> data["last"]
                BID -> data["highestBid"]
                ASK -> data["lowestAsk"]
            }.asString
        }
    },
    NEXCHANGE("Nexchange") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.n.exchange/en/api/v1/price/$coin$currency/latest/?format=json"
            val data = getJsonArray(url)[0].jsonObject
            return when (priceType) {
                SPOT -> data["rate"]
                BID -> data["ticker"]?.jsonObject["bid"]
                ASK -> data["ticker"]?.jsonObject["ask"]
            }.asString
        }
    },
    OKX("OKX") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://www.okx.com/api/v5/market/ticker?instId=$coin-$currency"
            val data = getJsonObject(url)["data"]?.jsonArray?.get(0)?.jsonObject ?: return null
            return when (priceType) {
                SPOT -> data["last"]
                BID -> data["bidPx"]
                ASK -> data["askPx"]
            }.asString
        }
    },
    P2PB2B("P2PB2B") {
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.p2pb2b.com/api/v2/public/ticker?market=${coin}_$currency"
            val data = getJsonObject(url)["result"]?.jsonObject ?: return null
            return when (priceType) {
                SPOT -> data["last"]
                BID -> data["bid"]
                ASK -> data["ask"]
            }.asString
        }
    },
    PARIBU("Paribu") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://www.paribu.com/ticker"
            val data = getJsonObject(url)["${coin}_$currency"]?.jsonObject ?: return null
            return when (priceType) {
                SPOT -> data["last"]
                BID -> data["highestBid"]
                ASK -> data["lowestAsk"]
            }.asString
        }
    },
    PAYMIUM("Paymium") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://paymium.com/api/v1/data/${currency.lowercase()}/ticker"
            val data = getJsonObject(url)
            return when (priceType) {
                SPOT -> data["price"]
                BID -> data["bid"]
                ASK -> data["ask"]
            }.asString
        }
    },
    PHEMEX("Phemex") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.phemex.com/md/spot/ticker/24hr?symbol=s${coin}$currency"
            val data = getJsonObject(url)["result"]?.jsonObject ?: return null
            val value = when (priceType) {
                SPOT -> data["lastEp"]
                BID -> data["bidEp"]
                ASK -> data["askEp"]
            }.asString ?: "0"
            return (value.toDouble() / 10.0.pow(8)).toString()
        }
    },
    POLONIEX("Poloniex") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.poloniex.com/markets/${coin}_$currency/ticker24h"
            val data = getJsonObject(url)
            return when(priceType) {
                SPOT -> data["price"]
                BID -> data["bid"]
                ASK -> data["ask"]
            }.asString
        }
    },
    PROBIT("ProBit") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.probit.com/api/exchange/v1/ticker?market_ids=$coin-$currency"
            return getJsonObject(url)["data"]?.jsonArray?.get(0)?.jsonObject?.get("last").asString
        }
        override val hasSpotPriceOnly = true
    },
    SATOSHI_TANGO("Satoshi Tango") {
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            return getCriptoYaValue("satoshitango", coin, currency, priceType)
        }
    },
    UPHOLD("Uphold") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.uphold.com/v0/ticker/$coin-$currency"
            val obj = getJsonObject(url)
            val bid = obj["bid"].asString?.toDouble() ?: return null
            val ask = obj["ask"].asString?.toDouble() ?: return null
            return when (priceType) {
                SPOT -> ((bid + ask) / 2)
                BID -> bid
                ASK -> ask
            }.toString()
        }
    },
    VBTC("VBTC") {

        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            return getBlinkTradeValue(coin, currency, priceType)
        }
    },
    WHITEBIT("WhiteBIT") {
        // v3 and v4 api does not allow for ticker on a single market
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://whitebit.com/api/v1/public/ticker?market=${coin}_$currency"
            val data = getJsonObject(url)["result"]?.jsonObject ?: return null
            return when(priceType) {
                SPOT -> data["last"]
                BID -> data["bid"]
                ASK -> data["ask"]
            }.asString
        }
    },
    XT("XT.com") {
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val type = if (priceType == SPOT) "price" else "book"
            val url = "https://sapi.xt.com/v4/public/ticker/$type?symbol=${coin.lowercase()}_${currency.lowercase()}"
            val data = getJsonObject(url)["result"]?.jsonArray?.get(0)?.jsonObject ?: return null
            return when (priceType) {
                SPOT -> data["p"]
                BID -> data["bp"]
                ASK -> data["ap"]
            }.asString
        }

    },
    YADIO("Yadio") {
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.yadio.io/exrates/$currency"
            return getJsonObject(url)["BTC"].asString
        }
        override val hasSpotPriceOnly = true
    },
    YOBIT("YoBit") {
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val pair = "${coin}_$currency".lowercase()
            val url = "https://yobit.net/api/3/ticker/$pair"
            val data = getJsonObject(url)[pair]?.jsonObject ?: return null
            return when (priceType) {
                SPOT -> data["last"]
                BID -> data["buy"]
                ASK -> data["sell"]
            }.asString
        }
    },
    ZONDA("Zonda") {
        override fun getValue(coin: String, currency: String, priceType: PriceType): String? {
            val url = "https://api.zondacrypto.exchange/rest/trading/ticker/$coin-$currency"
            val data = getJsonObject(url)["ticker"]?.jsonObject ?: return null
            return when (priceType) {
                SPOT -> data["rate"]
                BID -> data["highestBid"]
                ASK -> data["lowestAsk"]
            }.asString
        }
    };

    val shortName: String = shortName ?: exchangeName

    fun getBlinkTradeValue(coin: String, currency: String, priceType: PriceType): String? {
        val url = "https://api.blinktrade.com/api/v1/$currency/ticker?crypto_currency=$coin"
        val data = getJsonObject(url)
        return when (priceType) {
            SPOT -> data["last"]
            BID -> data["buy"]
            ASK -> data["sell"]
        }.asString
    }

    fun getCriptoYaValue(exchange: String, coin: String, currency: String, priceType: PriceType): String? {
        val url = "https://criptoya.com/api/$exchange/$coin/$currency/1"
        val data = getJsonObject(url)
        val bid = data["bid"].asString ?: return null
        val ask = data["ask"].asString ?: return null
        return when (priceType) {
            SPOT -> ((bid.toDouble() + ask.toDouble()) / 2).toString()
            BID -> bid
            ASK -> ask
        }
    }

    abstract fun getValue(coin: String, currency: String, priceType: PriceType): String?

    open val hasSpotPriceOnly = false


    companion object {

        private val ALL_EXCHANGE_NAMES = entries.map { it.name}.toMutableList()

        fun getAllExchangeNames(): MutableList<String> {
            return ALL_EXCHANGE_NAMES.toMutableList()
        }
    }
}
