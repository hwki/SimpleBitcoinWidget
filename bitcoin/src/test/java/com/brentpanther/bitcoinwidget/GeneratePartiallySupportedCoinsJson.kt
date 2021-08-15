package com.brentpanther.bitcoinwidget

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Test
import java.io.InputStreamReader
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class GeneratePartiallySupportedCoins {

    private val configuration = Configuration.defaultConfiguration().mappingProvider(GsonMappingProvider())
    private val allSupportedCoins = Coin.values().filterNot { it == Coin.CUSTOM }.map { it.name }

    private val fullySupportedCoinsJsonPath = JsonPath.parse(ClassLoader.getSystemResourceAsStream("raw/cryptowidgetcoins.json"))

    class CoinGeckoCoin(val id: String, val symbol: String, val name: String, var icon: String, val score: Double) {

        init {
            icon = icon.replace("https://assets.coingecko.com/coins/images/", "")
        }
    }

    private val listUrl = "https://api.coingecko.com/api/v3/coins/list?include_platform=false"
    private val dataUrl = "https://api.coingecko.com/api/v3/coins/"
    private val dataUrl2 = "?localization=false&tickers=false&market_data=false&community_data=false&developer_data=false"
    private val scoreLimit = 3

    @Test
    fun generate() {
        val allCoins = Gson().fromJson(get(listUrl), JsonArray::class.java)
        val existing = getExistingCoins()
        val initialCount = existing.count()
        println("Filtering coins..")
        val failed = mutableListOf<JsonObject>()
        val total = allCoins.count()
        var index = 0
        for (obj in allCoins.map { it.asJsonObject }.stream()) {
            index++
            if (index % 100 == 0) {
                println("${(index * 100.0 / total).roundToInt()}%")
            }
            val id = obj.get("id").asString
            if (existing.containsKey(id)) {
                continue
            }
            getCoin(obj)?.let {
                if (it.second) {
                    existing[it.first.id] = it.first
                }
            } ?: failed.add(obj)
        }
        println("Finished processing. Retrying ${failed.count()} failed counts.")
        val stillFailed = mutableListOf<JsonObject>()
        for (obj in failed) {
            getCoin(obj)?.let {
                if (it.second) {
                    existing[it.first.id] = it.first
                }
            } ?: stillFailed.add(obj)
        }
        println("Found ${existing.count() - initialCount} new coins.")
        println("Failed: ${failed.joinToString { it.get("id").asString }}")
        println("json:")
        println(Gson().toJson(existing.values))
    }

    private fun getExistingCoins(): MutableMap<String, CoinGeckoCoin> {
        val existingCoins = ClassLoader.getSystemResourceAsStream("raw/othercoins.json")
        val coins : List<CoinGeckoCoin> = Gson().fromJson(InputStreamReader(existingCoins), object : TypeToken<List<CoinGeckoCoin>>() {}.type)
        return coins.associateBy { it.id }.toMutableMap()
    }

    private fun getCoin(obj: JsonObject): Pair<CoinGeckoCoin, Boolean>? {
        var coin: CoinGeckoCoin? = null
        var tries = 0
        while (coin == null) {
            Thread.sleep(1300)
            try {
                coin = getCoinData(obj)
                return Pair(coin, coin.score >= scoreLimit)
            } catch (ignored: SocketTimeoutException) {
                if (tries++ == 3) {
                    return null
                }
            } catch (e: Exception) {
                println("Failure with coin ${obj.get("id").asString}: ${e.message}")
                return null
            }
        }
        return null
    }


    private fun getCoinData(it: JsonObject): CoinGeckoCoin {
        val id = it.get("id").asString
        val name = it.get("name").asString
        val obj = Gson().fromJson(get(dataUrl + id + dataUrl2), JsonObject::class.java)
        val score = obj.get("coingecko_score").asDouble
        val symbol = it.get("symbol").asString
        val icon = obj.get("image").asJsonObject.get("large").asString
        return CoinGeckoCoin(id, symbol, name,icon, score)
    }

    private fun parse(url: String, path: String) = JsonPath.read(get(url), path) as List<String>
    private fun get(value: String): String =
        OkHttpClient.Builder().retryOnConnectionFailure(true).readTimeout(0, TimeUnit.SECONDS)
            .connectTimeout(0, TimeUnit.SECONDS).callTimeout(0, TimeUnit.SECONDS)
            .writeTimeout(0, TimeUnit.SECONDS).build()
            .newCall(Request.Builder().url(value).build()).execute().body!!.string()
}