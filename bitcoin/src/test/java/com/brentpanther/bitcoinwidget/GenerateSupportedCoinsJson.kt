package com.brentpanther.bitcoinwidget

import com.brentpanther.bitcoinwidget.exchange.Exchange
import com.brentpanther.bitcoinwidget.exchange.ExchangeData.JsonCoin
import com.brentpanther.bitcoinwidget.exchange.ExchangeData.JsonExchange
import com.brentpanther.bitcoinwidget.exchange.ExchangeData.JsonExchangeObject
import com.brentpanther.bitcoinwidget.exchange.ExchangeHelper.asString
import com.jayway.jsonpath.JsonPath
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Test
import java.nio.file.Paths
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.Currency
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.io.path.writeText

class GenerateSupportedCoinsJson {

    private lateinit var allCurrencies: Set<String>
    private val allCoins = Coin.entries.filterNot { it == Coin.CUSTOM }.map { it.getSymbol() }
    private val allCoinOverrides = mapOf("BCHABC" to "BCH", "BCC" to "BCH", "BCHSV" to "BSV", "XBT" to "BTC",
            "XDG" to "DOGE", "MIOTA" to "IOTA", "STR" to "XLM", "DSH" to "DASH", "IOT" to "IOTA",
            "BAB" to "BCH", "ALG" to "ALGO", "ATO" to "ATOM", "QTM" to "QTUM", "DRK" to "DASH", "NEM" to "XEM",
            "XZC" to "FIRO")
    private val allCurrencyOverrides = mapOf("USDT" to "USD", "TUSD" to "USD", "USDC" to "USD", "TL" to "TRY", "NIS" to "ILS").plus(allCoinOverrides)

    private val allExchanges =
        listOf(this::ascendex, this::bibox, this::bigone, this::binance, this::binance_us, this::bingx, this::bit2c,
            this::bitbank, this::bitclude, this::bitcoinde, this::bitfinex, this::bitflyer,
            this::bithumb, this::bitmart, this::bitpanda, this::bitpay, this::bitso, this::bitstamp,
            this::bittrex, this::bitrue, this::bitvavo, this::btcbox, this::btcmarkets, this::btcturk,
            this::bybit, this::cexio, this::chilebit, this::coinbase, this::coinbasepro, this::coindesk, this::coingecko,
            this::coinjar, this::coinmate, this::coinone, this::coinsbit, this::coinsph, this::cointree,
            this::cryptocom, this::deversifi, this::digifinex, this::exmo, this::foxbit, this::gateio, this::gemini,
            this::hashkey, this::hitbtc, this::huobi, this::independent_reserve, this::indodax, this::itbit,
            this::korbit, this::kraken, this::kucoin, this::kuna, this::lbank, this::liquid, this::luno,
            this::mercado, this::mexc, this::ndax, this::nexchange, this::okcoin, this::okx, this::p2pb2b,
            this::paribu, this::paymium, this::phemex, this::pocketbits, this::poloniex, this::probit,
            this::tradeogre, this::uphold, this::vbtc, this::whitebit, this::xt, this::yadio,
            this::yobit, this::zonda
        ).zip(Exchange.entries.toTypedArray()).associate {
            Pair(it.second) { it.first.invoke() }
        }

    // these exchanges do not allow API requests from the united states
    private val nonUSExchanges = listOf(Exchange.BYBIT, Exchange.BINANCE)

    @Test
    fun generateAll() = generate(allExchanges)

    @Test
    fun generateUS() {
        generate(allExchanges.filterNot { it.key in nonUSExchanges })
    }

    @Test
    fun generateNonUS() {
        generate(allExchanges.filter { it.key in nonUSExchanges })
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json { encodeDefaults = true; explicitNulls = false }

    @OptIn(ExperimentalSerializationApi::class)
    private fun generate(exchanges: Map<Exchange, () -> List<String>>) {
        allCurrencies = Currency.getAvailableCurrencies().asSequence().map { it.currencyCode }
            .plus(allCoins).plus(allCoinOverrides.keys).toSet()

        val potentialCoinAdds = mutableMapOf<String, Int>()
        val stream = ClassLoader.getSystemResourceAsStream("raw/cryptowidgetcoins_v2.json")
        val allExchangeData = try {
            Json.decodeFromStream(stream)
        } catch (_: Exception) {
            JsonExchangeObject().apply {
                this.exchanges = mutableListOf()
            }
        }

        println()
        for ((exchange, func) in exchanges) {
            val exchangeData = allExchangeData.exchanges.firstOrNull { it.name == exchange.name } ?: JsonExchange().apply {
                name = exchange.name
                allExchangeData.exchanges.add(this)
            }
            try {
                loadExchange(exchangeData, exchange, func(), potentialCoinAdds)
            } catch (e: Exception) {
                System.err.println("$exchange: " + e.message)
                exchangeData.coins = listOf()

            }
        }
        allExchangeData.exchanges.sortBy { it.name }
        println()
        println("Potential coins to add:")
        potentialCoinAdds.entries.sortedByDescending { it.value }.take(10).forEach {
            println("${it.key} (${it.value} exchanges)")
        }
        val jsonString = json.encodeToString(allExchangeData)
        Paths.get("src", "main", "res", "raw", "cryptowidgetcoins_v2.json").writeText(jsonString)
    }

    private fun loadExchange(exchangeData: JsonExchange, exchange: Exchange, pairs: List<String>,
        potentialCoinsToAdd: MutableMap<String, Int>) {

        // normalize the coin/currency pairs
        var foundPairs = normalize(pairs)

        // find any coin and currency overrides
        val coinOverrides = mutableMapOf<String, String>()
        val currencyOverrides = mutableMapOf<String, String>()
        foundPairs = extractOverrides(foundPairs, coinOverrides, currencyOverrides)

        // populate any coins we might want to start supporting
        potentialNewCoins(foundPairs, potentialCoinsToAdd)

        // remove coins and currencies we don't know about
        removeUnknowns(foundPairs)

        // recalculate "all"
        val allCoins = foundPairs.map { it.first }.distinct()
        // all currencies are ones that have entries for all the coins
        val all = foundPairs.map { it.second }.filter { currency ->
            foundPairs.containsAll(allCoins.map { it to currency })
        }.toSet()

        // print out new and removed pairs
        logUpdates(exchangeData, foundPairs, exchange)

        // set fields
        with(exchangeData) {
            this.coinOverrides = coinOverrides.ifEmpty { null }
            this.currencyOverrides = currencyOverrides.ifEmpty { null }
            this.all = all.sorted().toList()
            this.coins = foundPairs.groupBy({ it.first }) {
                it.second
            }.map { (name, currencies) ->
                JsonCoin(name, currencies - all)
            }.sortedBy { it.name }
        }
    }

    private fun logUpdates(exchangeData: JsonExchange, foundPairs: MutableSet<Pair<String, String>>, exchange: Exchange) {
        val existingPairs = exchangeData.coins.flatMap { coin ->
            coin.currencies.plus(exchangeData.all).map { coin.name to it }
        }.toSet()
        val added = foundPairs - existingPairs
        if (added.isNotEmpty()) {
            println("$exchange: Added ${added.count()} new pairs: ${added.joinToString { "${it.first}_${it.second}" }}")
        }
        val removed = existingPairs - foundPairs
        if (removed.isNotEmpty()) {
            println("$exchange: Removed ${removed.count()} pairs: ${removed.joinToString { "${it.first}_${it.second}" }}")
        }
    }

    /**
     * Add coins we don't know about to the list of coins that we should consider adding support for
     */
    private fun potentialNewCoins(pairs: MutableSet<Pair<String, String>>, map: MutableMap<String, Int>)  {
        pairs.map { it.first }.distinct().filterNot { allCoins.contains(it) }.forEach {
            map.merge(it, 1) { a, _ -> a + 1 }
        }
    }

    /**
     * Removes coin/currency pairs that have coins or currencies we don't support
     */
    private fun removeUnknowns(pairs: MutableSet<Pair<String, String>>) {
        pairs.removeIf {(coin, currency) ->
            !allCoins.contains(coin) || !allCurrencies.plus(allCoins).contains(currency)
        }
    }

    /**
     * Find any overrides in the list of pairs, and update pairs to use them
     */
    private fun extractOverrides(pairs: MutableSet<Pair<String, String>>, coinOverrides: MutableMap<String, String>,
                                 currencyOverrides: MutableMap<String, String>) : MutableSet<Pair<String, String>> {
        // some exchanges have multiple possible overrides (e.g. USDT and USDC).
        // we are going to use only the most common ones
        val currencyOverrideWithCount = mutableMapOf<String, Int>()

        // some don't need overrides (e.g. have USD and USDT). don't use overrides in this case
        val currencyCount = mutableMapOf<String, Int>()

        val updatedPairs = pairs.map { (coin, currency) ->
            val newCoin = allCoinOverrides[coin]?.apply {
                coinOverrides[this] = coin
            } ?: coin
            val newCurrency = allCurrencyOverrides[currency]?.apply {
                currencyOverrideWithCount.merge(currency, 1) { i, _ -> i + 1}
            } ?: run {
                currencyCount.merge(currency, 1) { i, _ -> i + 1}
                currency
            }
            newCoin to newCurrency
        }.toMutableSet()
        // go through currency overrides, applying in reverse order of occurence
        currencyOverrideWithCount.entries.sortedBy { it.value }.forEach {
            val value = allCurrencyOverrides[it.key]!!
            // only set the override if there is more overrides than default value, e.g. more USDT than USD
            if (currencyCount.getOrDefault(value, 0) < it.value) {
                currencyOverrides[value] = it.key
            }
        }
        return updatedPairs
    }

    private fun normalize(pairs: List<String>): MutableSet<Pair<String, String>> {
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
                }.map {
                    it[0] to it[1]
                }.toMutableSet()
    }

    private fun parseKeys(url: String, path: String) = (JsonPath.read(get(url), path) as Map<String, *>).keys.map { it }
    private fun parse(url: String, path: String) = JsonPath.read(get(url), path) as List<String>
    private fun get(value: String): String = OkHttpClient.Builder().ignoreAllSSLErrors().build().newCall(Request.Builder().url(value).build()).execute().body!!.string()

    private fun OkHttpClient.Builder.ignoreAllSSLErrors(): OkHttpClient.Builder {
        val naiveTrustManager = object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) = Unit
            override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) = Unit
        }

        val insecureSocketFactory = SSLContext.getInstance("TLSv1.2").apply {
            val trustAllCerts = arrayOf<TrustManager>(naiveTrustManager)
            init(null, trustAllCerts, SecureRandom())
        }.socketFactory

        sslSocketFactory(insecureSocketFactory, naiveTrustManager)
        hostnameVerifier { _, _ -> true }
        return this
    }

    //endregion

    // region exchange methods

    private fun ascendex(): List<String> {
        return parse("https://ascendex.com/api/pro/v1/products", "$.data[?(@.status=='Normal')].symbol")
    }

    private fun bibox(): List<String> {
        return parse("https://api.bibox.com/v3/mdata/pairList", "$.result[*].pair")
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

    private fun bingx(): List<String> {
        return parse("https://open-api.bingx.com/openApi/spot/v1/common/symbols", "$.data.symbols[*].symbol")
    }

    private fun bit2c(): List<String> {
        val data = get("https://bit2c.co.il/Exchanges/bad/Ticker.json")
        return data.substringAfterLast("Supported pairs are: ").substringBeforeLast(".")
                .split(",")
    }

    private fun bitbank(): List<String> {
        return parse("https://public.bitbank.cc/tickers", "$.data[*].pair")
    }

    private fun bitclude(): List<String> {
        return parseKeys("https://api.bitclude.com/stats/ticker.json", "$")
    }

    private fun bitcoinde(): List<String> {
        // exchange supports more but only can get ticker data for btc-eur
        return listOf("BTC-EUR")
    }

    private fun bitfinex(): List<String> {
        return parse("https://api-pub.bitfinex.com/v2/tickers?symbols=ALL", "$[*][0]").map {
            it.removePrefix("t")
        }
    }

    private fun bitflyer(): List<String> {
        val pairs = mutableListOf<String>()
        pairs.addAll(parse("https://api.bitflyer.com/v1/markets", "$[*].product_code"))
        pairs.addAll(parse("https://api.bitflyer.com/v1/markets/usa", "$[*].product_code"))
        pairs.addAll(parse("https://api.bitflyer.com/v1/markets/eu", "$[*].product_code"))
        return pairs
    }

    private fun bithumb(): List<String> {
        return parseKeys("https://api.bithumb.com/public/ticker/ALL", "$.data").map { "${it}_KRW" }
    }

    private fun bitmart(): List<String> {
        return parse("https://api-cloud.bitmart.com/spot/v1/symbols", "$.data.symbols.[*]")
    }

    private fun bitpanda(): List<String> {
        return Json.decodeFromString<JsonObject>(get("https://api.bitpanda.com/v1/ticker")).entries
            .flatMap { (coin, currencies) ->
                currencies.jsonObject.keys.map {
                    "${coin}_${it}"
                }
            }
    }

    private fun bitpay(): List<String> {
        val coins = parse("https://bitpay.com/currencies", "$.data[?(@.chain)].code").filterNot { it == "PAX" }
        val currencies = parse("https://bitpay.com/currencies", "$.data[*].code")
        return coins.flatMap { coin ->
            currencies.map { currency -> "${coin}_$currency"}
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

    private fun bitrue(): List<String> {
        val pairs = parse("https://openapi.bitrue.com/api/v1/exchangeInfo", "$.symbols[*].symbol")
        return pairs.filterNot { it.contains("USDC") }
    }

    private fun bitvavo(): List<String> {
        return parse("https://api.bitvavo.com/v2/markets", "$[*].market")
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
        return parse("https://api.bybit.com/v5/market/tickers?category=spot", "$.result.list[*].symbol")
    }

    private fun cexio(): List<String> {
        val obj = Json.decodeFromString<JsonObject>(get("https://cex.io/api/currency_limits"))
        val pairs = obj["data"]!!.jsonObject["pairs"]
        return pairs!!.jsonArray.map {
            val o = it.jsonObject
            "${o["symbol1"].asString}_${o["symbol2"].asString}"
        }
    }

    private fun chilebit(): List<String> {
        return listOf("BTC_CLP")
    }

    private fun coinbase(): List<String> {
        val currencies = parse("https://api.coinbase.com/v2/currencies", "$.data[*].id")
        return parseKeys("https://api.coinbase.com/v2/exchange-rates", "$.data.rates").filterNot {
            it == "XRP"
        }.flatMap {
            coin -> currencies.map { "${coin}_$it" }
        }
    }

    private fun coinbasepro(): List<String> {
        return parse("https://api.pro.coinbase.com/products", "$[*].id")
    }

    private fun coindesk(): List<String> {
        val currencies = parse("https://api.coindesk.com/v1/bpi/supported-currencies.json", "$[*].currency")
        return currencies.map { "BTC_$it" }
    }

    private fun coingecko(): List<String> {
        val currencies = parse("https://api.coingecko.com/api/v3/simple/supported_vs_currencies", "[*]")
        return Coin.entries.map { coin -> currencies.map { coin.getSymbol() + "_" + it } }.flatten()
    }

    private fun coinjar(): List<String> {
        return parse("https://api.exchange.coinjar.com/products", "$[*].name")
    }

    private fun coinmate(): List<String> {
        return parse("https://coinmate.io/api/tradingPairs", "$.data[*].name")
    }

    private fun coinone(): List<String> {
        val currencies = listOf("KRW")
        return currencies.flatMap { currency ->
            val list = parse("https://api.coinone.co.kr/public/v2/markets/$currency", "$.markets[*].target_currency")
            list.map { "${it}_$currency" }
        }
    }

    private fun coinsbit(): List<String> {
        return parse("https://coinsbit.io/api/v1/public/products", "$.result[*].id")
    }

    private fun coinsph(): List<String> {
        return parse("https://api.pro.coins.ph/openapi/v1/pairs", "$[*].symbol")
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

    private fun digifinex(): List<String> {
        return parse("https://openapi.digifinex.com/v3/ticker", "$.ticker.[*].symbol")
    }

    private fun exmo(): List<String> {
        return parseKeys("https://api.exmo.com/v1.1/ticker", "$")
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

    private fun hashkey(): List<String> {
        return parse("https://api-pro.hashkey.com/api/v1/exchangeInfo", "$.symbols[*].symbol")
    }

    private fun hitbtc(): List<String> {
        return parseKeys("https://api.hitbtc.com/api/3/public/symbol", "$")
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
        return parse("https://api.paxos.com/v2/markets", "$.markets[*].market")
    }

    private fun korbit(): List<String> {
        return parseKeys("https://api.korbit.co.kr/v1/ticker/detailed/all", "$")
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

    private fun luno(): List<String> {
        return parse("https://api.luno.com/api/1/tickers", "$.tickers[*].pair")
    }

    private fun mercado(): List<String> {
        return parse("https://api.mercadobitcoin.net/api/v4/symbols", "$.symbol.[*]")
    }

    private fun mexc(): List<String> {
        val pairs = parse("https://www.mexc.com/open/api/v2/market/symbols", "$.data.[*].symbol")
        return pairs.filterNot { it.contains("USDC") }
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

    private fun okx(): List<String> {
        val pairs = parse("https://www.okx.com/api/v5/public/instruments?instType=SPOT", "$.data[*].instId")
        return pairs.filterNot { it.contains("USDC") }
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
        val list = JsonPath.read(get("https://api.phemex.com/public/products"),
            "$.data.products.[?(@.status == 'Listed' && @.type == 'Spot')]") as List<Map<String, *>>
        return list.map { "${it["baseCurrency"]}-${it["quoteCurrency"]}" }
    }

    private fun pocketbits(): List<String> {
        return parse("https://ticker.pocketbits.in/api/v1/ticker", "$[*].symbol")
    }

    private fun poloniex(): List<String> {
        return parse("https://api.poloniex.com/markets", "$[*].symbol")
    }

    private fun probit(): List<String> {
        return parse("https://api.probit.com/api/exchange/v1/market", "$.data[*].id")
    }

    private fun tradeogre(): List<String> {
        val list = JsonPath.read(get("https://tradeogre.com/api/v1/markets"), "$[*]") as List<Map<String, *>>
        return list.map { it.keys.first().split("-").reversed().joinToString("_") }
    }

    private fun uphold(): List<String> {
        val currencies = parse("https://api.uphold.com/v0/assets?q=type:fiat", "$.[?(@.status == 'open')].code")
        val coins = parse("https://api.uphold.com/v0/assets", "$.[?(@.status == 'open' && @.type in ['cryptocurrency', 'utility_token', 'stablecoin'])].code")
        return coins.flatMap { i -> currencies.plus(coins).map { j -> "$i-$j" } }
    }

    private fun vbtc(): List<String> {
        return listOf("BTC_VND")
    }

    private fun whitebit(): List<String> {
        return parse("https://whitebit.com/api/v1/public/symbols", "$.result[*]")
    }

    private fun xt(): List<String> {
        return parse("https://sapi.xt.com/v4/public/symbol", "$.result.symbols[*].symbol")
    }

    private fun yadio(): List<String> {
        val currencies = parseKeys("https://api.yadio.io/currencies", "$")
        return currencies.map { "BTC-$it" }
    }

    private fun yobit(): List<String> {
        return parseKeys("https://yobit.net/api/3/info", "$.pairs")
    }

    private fun zonda(): List<String> {
        return parseKeys("https://api.zonda.exchange/rest/trading/ticker", "$.items")
    }

    //endregion

}
