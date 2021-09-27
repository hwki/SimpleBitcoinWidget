package com.brentpanther.bitcoinwidget

import com.brentpanther.bitcoinwidget.exchange.Exchange
import com.google.gson.Gson
import com.jayway.jsonpath.JsonPath
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Test

class GenerateSupportedCoinsJson {

    private val allCoins = Coin.values().filterNot { it == Coin.CUSTOM }.map { it.name }
    private var allCurrencies = setOf<String>()
    private val json = JsonPath.parse(ClassLoader.getSystemResourceAsStream("raw/cryptowidgetcoins.json"))
    private val allCoinOverrides = mapOf("BCHABC" to "BCH", "BCC" to "BCH", "BCHSV" to "BSV", "XBT" to "BTC",
            "XDG" to "DOGE", "MIOTA" to "IOTA", "STR" to "XLM", "DSH" to "DASH", "IOT" to "IOTA",
            "BAB" to "BCH", "ALG" to "ALGO", "ATO" to "ATOM", "QTM" to "QTUM", "DRK" to "DASH", "NEM" to "XEM",
            "XZC" to "FIRO")
    private val allCurrencyOverrides = mapOf("USDT" to "USD", "TUSD" to "USD", "TL" to "TRY", "NIS" to "ILS").plus(allCoinOverrides)

    @Test
    fun generate() {
        allCurrencies = (json.read("$..currencies.*") as List<String>).toSortedSet()

        val exchanges =
                listOf(this::abucoins, this::ascendex, this::bibox, this::bigone, this::binance, this::binance_us, this::bit2c,
                        this::bitbank, this::bitbay, this::bitcambio, this::bitclude,
                        this::bitcoinde, this::bitfinex, this::bitflyer, this::bithumb, this::bithumbpro, this::bitmex,
                        this::bitpay, this::bitso, this::bitstamp, this::bittrex, this::bitvavo, this::bleutrade,
                        this::btcbox, this::btcmarkets, this::btcturk, this::bybit, this::cexio,
                        this::chilebit, this::coinbase, this::coinbasepro, this::coinbene, this::coindesk, this::coingecko,
                        this::coinjar, this::coinmate, this::coinone, this::coinsbit, this::coinsph, this::cointree,
                        this::cryptocom, this::deversifi, this::exmo, this::ftx, this::ftx_us, this::foxbit, this::gateio, this::gemini, this::hitbtc,
                        this::huobi, this::independent_reserve, this::indodax, this::itbit, this::korbit, this::kraken, this::kucoin,
                        this::kuna, this::lbank, this::liquid, this::luno, this::mercado, this::ndax,
                        this::nexchange, this::okcoin, this::okex, this::p2pb2b, this::paribu, this::paymium, this::phemex,
                        this::pocketbits, this::poloniex, this::probit, this::therock, this::tradeogre, this::uphold,
                        this::urdubit, this::vbtc, this::whitebit, this::wyre, this::yobit, this::zb, this::zbg
                ).zip(Exchange.values())

        val jsonMap = mutableMapOf<String, List<*>>()
        val jsonExchanges = mutableListOf<Map<*, *>>()
        val potentialCoinAdds = mutableMapOf<String, Int>()
        for ((exchange, name) in exchanges.asSequence()) {
            try {
                val coinOverrides = mutableMapOf<String, String>()
                val currencyOverrides = mutableMapOf<String, String>()
                val existing = getExistingPairs(name.name)
                var pairs = extractOverrides(normalize(exchange.invoke()), coinOverrides, currencyOverrides)
                pairs = pairs.filterNot {
                   it.substringBefore("_") == it.substringAfter("_")
                }
                val removed = existing.minus(pairs).sorted()
                if (removed.isNotEmpty()) {
                    System.err.println("$name: Removed: ${removed.joinToString()}")
                }
                // remove coins and currencies we don't know about
                otherCoins(pairs, potentialCoinAdds)
                pairs = removeUnknowns(pairs)
                if (pairs.count() > existing.count() - removed.count()) {
                    println("$name: ${pairs.count() + removed.count() - existing.count()} pairs added")
                }
                jsonExchanges.add(buildExchange(name, pairs, currencyOverrides, coinOverrides))
            } catch (e: Exception) {
                System.err.println("$name: Error: ${e.message}")
                // add previous exchange data
                jsonExchanges.add(json.read("$.exchanges[?(@.name=='$name')]", List::class.java)[0] as Map<*, *>)
            }
        }
        jsonMap["exchanges"] = jsonExchanges
        println(Gson().toJson(jsonMap))
        println("Potential coins to add:")
        potentialCoinAdds.entries.sortedByDescending { it.value }.take(10).forEach {
            println("${it.key} (${it.value} exchanges)")
        }
    }

    private fun otherCoins(pairs: Sequence<String>, map: MutableMap<String, Int>)  {
        pairs.map{ it.split("_")[0] }.distinct().filterNot { allCoins.contains(it) }.forEach {
            map[it] = map.getOrDefault(it, 0) + 1
        }
    }

    // region helpers

    private fun buildExchange(exchange: Exchange, pairs: Sequence<String>, currencyOverrides: Map<String, String>,
                              coinOverrides: Map<String, String>): Map<String, Any> {
        val map = mutableMapOf<String, Any>("name" to exchange.name)
        val pairsMap = pairs.map {
            it.split("_")
        }.groupBy( { it[0] }, {it[1]}).toSortedMap()
        map["coins"] = pairsMap.map {
            mapOf("name" to it.key, "currencies" to it.value.sorted())
        }
        if (currencyOverrides.isNotEmpty()) {
            map["currency_overrides"] = currencyOverrides.toSortedMap()
        }
        if (coinOverrides.isNotEmpty()) {
            map["coin_overrides"] = coinOverrides.toSortedMap()
        }
        return map
    }

    private fun removeUnknowns(pairs: Sequence<String>): Sequence<String> {
        return pairs.map {
            it.split("_")
        }.filter {
            allCoins.contains(it[0]) && allCurrencies.plus(allCoins).contains(it[1])
        }.map {
            it.joinToString("_")
        }
    }

    private fun extractOverrides(pairs: List<String>, coinOverrides: MutableMap<String, String>,
                                 currencyOverrides: MutableMap<String, String>) : Sequence<String> {
        val splitPairs = pairs.asSequence().map { it.split("_") }
        val partitioned = splitPairs.flatten().withIndex().partition { it.index % 2 == 0 }
        val coins = partitioned.first.map { it.value }
        val currencies = partitioned.second.map { it.value }
        return splitPairs.map {
            // should only match overrides if default doesn't exist
            when (val match = allCoinOverrides[it[0]]) {
                null -> it
                else -> {
                    if (coins.contains(match)) {
                        it
                    } else {
                        coinOverrides[match] = it[0]
                        listOf(match, it[1])
                    }
                }
            }
        }.map {
            when (val match = allCurrencyOverrides[it[1]]) {
                null -> it
                else -> {
                    if (currencies.contains(match)) {
                        it
                    } else {
                        currencyOverrides[match] = it[1]
                        listOf(it[0], match)
                    }
                }
            }
        }.map { it.joinToString("_") }
    }

    private fun normalize(pairs: List<String>): List<String> {
        return pairs.asSequence().map { it.uppercase() }
                .map { it.split("-", "/", "_") }
                .map {
                    if (it.size == 1) {
                        // some mash the coin and currency together, so we need to split it up
                        // based on any coin its found to start with, including overrides
                        // sort by descending length so we don't skip say, PAXG by matching PAX
                        val coin = allCoins.plus(allCoinOverrides.keys).sortedByDescending {
                            c -> c.length }
                                .firstOrNull { coinName ->
                            it[0].startsWith(coinName)
                        } ?: "XXXXX"
                        listOf(coin, it[0].substringAfter(coin))
                    } else {
                        it
                    }
                }.filterNot {
                    // remove pairs with same symbols
                    it[0] == it[1]
                }.map {
                    it[0] + "_" + it[1]
                }.toList()
    }

    @Suppress("UNCHECKED_CAST")
    private fun getExistingPairs(name: String): List<String> {
        val existing = json.read("$.exchanges[?(@.name=='${name}')]", List::class.java).firstOrNull() as? Map<String, *>
                ?: return listOf()
        val coins = existing["coins"] as List<Map<String, *>>
        return coins.map { coin ->
            (coin["currencies"] as List<String>).map { currency ->
                "${coin["name"].toString()}_$currency"
            }
        }.flatten().sorted()
    }

    private fun parseKeys(url: String, path: String) = (JsonPath.read(get(url), path) as Map<String, *>).keys.map { it }
    private fun parse(url: String, path: String) = JsonPath.read(get(url), path) as List<String>
    private fun get(value: String): String = OkHttpClient().newCall(Request.Builder().url(value).build()).execute().body!!.string()

    //endregion

    // region exchange methods

    private fun abucoins(): List<String> {
        return parse("https://api.abucoins.com/products", "$[*].id")
    }

    private fun ascendex(): List<String> {
        return parse("https://ascendex.com/api/pro/v1/products", "$.data[?(@.status=='Normal')].symbol")
    }

    private fun bibox(): List<String> {
        return parse("https://api.bibox.com/v1/mdata?cmd=pairList", "$.result[*].pair")
    }

    private fun bigone(): List<String> {
        return parse("https://big.one/api/v3/asset_pairs", "$.data[*].name")
    }

    private fun binance(): List<String> {
        return parse("https://api.binance.com/api/v3/exchangeInfo", "$.symbols[*].symbol")
    }

    private fun binance_us(): List<String> {
        return parse("https://api.binance.us/api/v3/exchangeInfo", "$.symbols[*].symbol")
    }

    private fun bit2c(): List<String> {
        val data = get("https://bit2c.co.il/Exchanges/bad/Ticker.json")
        return data.substringAfterLast("Supported pairs are: ").substringBeforeLast(".")
                .split(",")
    }

    private fun bitbank(): List<String> {
        return parse("https://api.bitbank.cc/v1/spot/pairs", "$.data.pairs[*].name")
    }

    private fun bitbay(): List<String> {
        return parse("https://api.bitbay.net/rest/trading/stats", "$.items..m")
    }

    private fun bitcambio(): List<String> {
        return listOf("BTC_BRL")
    }

    private fun bitclude(): List<String> {
        return parseKeys("https://api.bitclude.com/stats/ticker.json", "$")
    }

    private fun bitcoinde(): List<String> {
        return listOf("BTC-EUR")
    }

    private fun bitfinex(): List<String> {
        return parse("https://api-pub.bitfinex.com/v2/tickers?symbols=ALL", "$[*][0]").map {
            it.removePrefix("t")
        }
    }

    private fun bitflyer(): List<String> {
        val pairs = mutableListOf<String>()
        pairs.addAll(parse("https://api.bitflyer.jp/v1/markets", "$[*].product_code"))
        pairs.addAll(parse("https://api.bitflyer.jp/v1/markets/usa", "$[*].product_code"))
        pairs.addAll(parse("https://api.bitflyer.jp/v1/markets/eu", "$[*].product_code"))
        return pairs
    }

    private fun bithumb(): List<String> {
        return parseKeys("https://api.bithumb.com/public/ticker/ALL", "$.data").map { "${it}_KRW" }
    }

    private fun bithumbpro() : List<String> {
        return parse("https://global-openapi.bithumb.pro/openapi/v1/spot/ticker?symbol=ALL", "$.data[*].s")
    }

    private fun bitmex(): List<String> {
        return parse("https://www.bitmex.com/api/v1/instrument/active", "$.[*].symbol")
    }

    private fun bitpay(): List<String> {
        return parse("https://bitpay.com/currencies", "$.data[*].code").map {
            "BTC_$it"
        }
    }

    private fun bitso(): List<String> {
        return parse("https://api.bitso.com/v3/available_books", "$.payload[*].book")
    }

    private fun bitstamp(): List<String> {
        return parse("https://www.bitstamp.net/api/v2/trading-pairs-info", "$[*].name")
    }

    private fun bittrex(): List<String> {
        return parse("https://api.bittrex.com/v3/markets", "$[*].symbol")
    }

    private fun bitvavo(): List<String> {
        return parse("https://api.bitvavo.com/v2/markets", "$[*].market")
    }

    private fun bleutrade(): List<String> {
        return parse("https://bleutrade.com/api/v3/public/getmarkets", "$.result[*].MarketName")
    }

    private fun btcbox(): List<String> {
        return parseKeys("https://www.btcbox.co.jp/api/v1/tickers", "$")
    }

    private fun btcmarkets(): List<String> {
        return parse("https://api.btcmarkets.net/v3/markets", "$[*].marketId")
    }

    private fun btcturk(): List<String> {
        return parse("https://api.btcturk.com/api/v2/ticker", "$.data[*].pairNormalized")
    }

    private fun bybit(): List<String> {
        return parse("https://api.bybit.com/v2/public/tickers", "$.result[*].symbol")
    }

    private fun cexio(): List<String> {
        val pairs = (Gson().fromJson(get("https://cex.io/api/currency_limits"), Map::class.java)["data"] as Map<*, *>)["pairs"]

        @Suppress("UNCHECKED_CAST")
        return (pairs as List<Map<String, String>>).map {
            it["symbol1"] + "_" + it["symbol2"]
        }
    }

    private fun chilebit(): List<String> {
        return listOf("BTC_CLP")
    }

    @Suppress("UNCHECKED_CAST")
    private fun coinbase(): List<String> {
        val currencies = parse("https://api.coinbase.com/v2/currencies", "$.data[*].id")
        return parseKeys("https://api.coinbase.com/v2/exchange-rates", "$.data.rates").map {
            coin -> currencies.map { "${coin}_$it" }
        }.flatten()
    }

    private fun coinbasepro(): List<String> {
        return parse("https://api.pro.coinbase.com/products", "$[*].id")
    }

    private fun coinbene() : List<String> {
        return parse("https://openapi-exchange.coinbene.com/api/spot/market/summary", "$[*].trading_pairs")
    }

    private fun coindesk(): List<String> {
        val currencies = parse("https://api.coindesk.com/v1/bpi/supported-currencies.json", "$[*].currency")
        return currencies.map { "BTC_$it" }
    }

    private fun coingecko(): List<String> {
        val currencies = parse("https://api.coingecko.com/api/v3/simple/supported_vs_currencies", "[*]")
        return Coin.values().map { coin -> currencies.map { coin.name + "_" + it } }.flatten()
    }

    private fun coinjar(): List<String> {
        return parse("https://api.exchange.coinjar.com/products", "$[*].name")
    }

    private fun coinmate(): List<String> {
        return parse("https://coinmate.io/api/tradingPairs", "$.data[*].name")
    }

    private fun coinone(): List<String> {
        return parse("https://tb.coinone.co.kr/api/v1/tradepair/", "$.tradepairs[*].target_coin_symbol").map {
            it + "_KRW"
        }
    }

    private fun coinsbit(): List<String> {
        return parse("https://coinsbit.io/api/v1/public/products", "$.result[*].id")
    }

    private fun coinsph(): List<String> {
        val pairs1 = parse("https://quote.coins.ph/v2/markets", "$.markets[*].symbol")
        val pairs2 = parse("https://quote.coins.ph/v2/markets?region=TH", "$.markets[*].symbol")
        return pairs1 + pairs2
    }

    private fun cointree(): List<String> {
        return parse("https://trade.cointree.com/api/prices/AUD/change/24h", "$[*].symbol").map {
            "${it}_AUD"
        }
    }

    private fun cryptocom(): List<String> {
        return parse("https://api.crypto.com/v2/public/get-instruments", "$.result.instruments[*].instrument_name")
    }

    private fun deversifi(): List<String> {
        return parse("https://api.deversifi.com/bfx/v2/tickers?symbols=ALL", "$[*].[0]").map {
            it.removePrefix("t")
        }
    }

    private fun exmo(): List<String> {
        return parseKeys("https://api.exmo.com/v1.1/ticker", "$")
    }

    private fun ftx(): List<String> {
        return parse("https://ftx.com/api/markets", "$.result[*].name")
    }

    private fun ftx_us(): List<String> {
        return parse("https://ftx.us/api/markets", "$.result[*].name")
    }

    private fun foxbit(): List<String> {
        return parse("https://watcher.foxbit.com.br/api/Ticker/", "$[?(@.exchange == 'Foxbit')].currency").map {
            it.replaceFirst("X", "_").split("_").reversed().joinToString("_")
        }
    }

    private fun gateio(): List<String> {
        return parse("https://api.gateio.ws/api/v4/spot/currency_pairs", "$[*].id")
    }

    private fun gemini(): List<String> {
        return parse("https://api.gemini.com/v1/symbols", "$[*]")
    }

    private fun hitbtc(): List<String> {
        return parse("https://api.hitbtc.com/api/2/public/symbol", "$[*].id")
    }

    private fun huobi(): List<String> {
        return parse("https://api.huobi.pro/market/tickers", "$.data[*].symbol")
    }

    private fun independent_reserve(): List<String> {
        val coins = parse("https://api.independentreserve.com/Public/GetValidPrimaryCurrencyCodes", "$[*]")
        val currencies = parse("https://api.independentreserve.com/Public/GetValidSecondaryCurrencyCodes", "$[*]")
        return coins.map { coin -> currencies.map { "${coin.uppercase()}_${it.uppercase()}" } }.flatten()
    }

    private fun indodax(): List<String> {
        return parse("https://indodax.com/api/pairs", "$[*].ticker_id")
    }

    private fun itbit(): List<String> {
        return listOf("XBTUSD", "XBTSGD", "XBTEUR", "ETHUSD", "ETHEUR", "ETHSGD", "PAXGUSD", "BCHUSD", "LTCUSD")
    }

    @Suppress("UNCHECKED_CAST")
    private fun korbit(): List<String> {
        return parseKeys("https://api.korbit.co.kr/v1/constants", "$.exchange")
    }

    private fun kraken(): List<String> {
        return parse("https://api.kraken.com/0/public/AssetPairs", "$.result[*]..altname")
    }

    private fun kucoin(): List<String> {
        return parse("https://api.kucoin.com/api/v1/symbols", "$.data[*].symbol")
    }

    private fun kuna(): List<String> {
        return parse("https://api.kuna.io/v3/markets", "$[*].id")
    }

    private fun lbank(): List<String> {
        return parse("https://api.lbkex.com/v2/currencyPairs.do", "$.data[*]")
    }

    private fun liquid(): List<String> {
        return parse("https://api.liquid.com/products", "$[?(@.disabled==false)].currency_pair_code")
    }

    private fun livecoin(): List<String> {
        return parse("https://api.livecoin.net/exchange/ticker", "$[*].symbol")
    }

    private fun luno(): List<String> {
        return parse("https://api.luno.com/api/1/tickers", "$.tickers[*].pair")
    }

    private fun mercado(): List<String> {
        return listOf("BCH_BRL", "BTC_BRL", "ETH_BRL", "LTC_BRL", "XRP_BRL")
    }

    private fun ndax(): List<String> {
        return parseKeys("https://core.ndax.io/v1/ticker", "$")
    }

    private fun nexchange(): List<String> {
        return parse("https://api.n.exchange/en/api/v1/pair/?format=json", "$[?(@.disabled==false)].name")
    }

    private fun okcoin(): List<String> {
        return parse("https://www.okcoin.com/api/spot/v3/instruments", "$[*].instrument_id")
    }

    private fun okex(): List<String> {
        return parse("https://www.okex.com/api/spot/v3/instruments", "$[*].instrument_id")
    }

    private fun p2pb2b(): List<String> {
        return parse("https://api.p2pb2b.io/api/v2/public/markets", "$.result[*].name")
    }

    private fun paribu(): List<String> {
        return parseKeys("https://www.paribu.com/ticker", "$")
    }

    private fun paymium(): List<String> {
        return listOf("BTC_EUR")
    }

    private fun phemex(): List<String> {
        return listOf("BTC_USDT", "XRP_USDT", "ETH_USDT", "LINK_USDT", "XTZ_USDT", "LTC_USDT")
    }

    private fun pocketbits(): List<String> {
        return parse("https://ticker.pocketbits.in/api/v1/ticker", "$[*].symbol")
    }

    private fun poloniex(): List<String> {
        return parseKeys("https://poloniex.com/public?command=returnTicker", "$").map {
            it.split("_").reversed().joinToString("_")
        }
    }

    private fun probit(): List<String> {
        return parse("https://api.probit.com/api/exchange/v1/market", "$.data[*].id")
    }

    private fun therock(): List<String> {
        return parse("https://api.therocktrading.com/v1/funds/tickers", "$.tickers[*].fund_id")
    }

    private fun tradeogre(): List<String> {
        val list = JsonPath.read(get("https://tradeogre.com/api/v1/markets"), "$[*]") as List<Map<String, *>>
        return list.map { it.keys.first().split("-").reversed().joinToString("_") }
    }

    @Suppress("UNCHECKED_CAST")
    private fun uphold(): List<String> {
        val all = Gson().fromJson(get("https://api.uphold.com/v0/reserve/statistics"), List::class.java) as List<Map<String, Any>>
        // uphold lists all its currencies in tiers. first crypto, then fiat, then stocks. we need to only pull the first tiers
        // the only good way to do this is check alphabetical order
        val pairs = mutableListOf<String>()
        for (entry in all) {
            var tier = 0
            var lastCharacter = 'A'
            for (value in entry["values"] as List<Map<String, String>>) {
                val char = value["currency"]?.first() ?: 'A'
                if (char < lastCharacter) {
                    tier++
                }
                if (tier == 2) break
                lastCharacter = char
                pairs.add("${entry["currency"].toString()}_${value["currency"]}")
            }
        }
        return pairs
    }

    private fun urdubit(): List<String> {
        return listOf("BTC_PKR")
    }

    private fun vbtc(): List<String> {
        return listOf("BTC_VND")
    }

    private fun whitebit(): List<String> {
        return parse("https://whitebit.com/api/v1/public/symbols", "$.result[*]")
    }

    private fun wyre(): List<String> {
        // is in currency - coin format
        val list = parseKeys("https://api.sendwyre.com/v3/rates", "$")
        val split1 =  list.map { it.substring(3, it.length) + "_" + it.substring(0, 3)  }
        val split2 = list.map { it.substring(4, it.length) + "_" + it.substring(0, 4)  }
        return split1.plus(split2).distinct()
    }

    private fun yobit(): List<String> {
        return parseKeys("https://yobit.net/api/3/info", "$.pairs")
    }

    private fun zb(): List<String> {
        return parseKeys("http://api.zb.land/data/v1/markets", "$")
    }

    private fun zbg(): List<String> {
        return parse("https://www.zbg.com/exchange/api/v1/common/symbols", "$.datas[*].symbol")
    }

    //endregion

}
