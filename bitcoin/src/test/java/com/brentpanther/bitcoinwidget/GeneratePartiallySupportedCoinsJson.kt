package com.brentpanther.bitcoinwidget

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.jayway.jsonpath.JsonPath
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Test
import java.lang.Exception
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class GeneratePartiallySupportedCoinsJson {

    private val allSupportedCoins = Coin.values().filterNot { it == Coin.CUSTOM }.map { it.name }

    data class CoinGeckoCoin(val id: String, val symbol: String, val name: String, val icon: String, val score: Double) {

        fun toMap() : Map<String, String> =
            mapOf("id" to id,
                "symbol" to symbol,
                "name" to name,
                "score" to score.toString(),
                "icon" to icon.replace("https://assets.coingecko.com/coins/images/", ""))
    }

    private val listUrl = "https://api.coingecko.com/api/v3/coins/list?include_platform=false"
    private val dataUrl = "https://api.coingecko.com/api/v3/coins/"
    private val dataUrl2 = "?localization=false&tickers=false&market_data=false&community_data=false&developer_data=false"
    private val scoreLimit = 1

    @Test
    fun generate() {
        val allCoins = Gson().fromJson(get(listUrl), JsonArray::class.java)
        println("Filtering ${allCoins.size()} coins..")
        val coins = mutableListOf<Map<String, String>>()
        val failed = mutableListOf<JsonObject>()
        for (it in allCoins.withIndex().map { IndexedValue(it.index, it.value as JsonObject) }) {
            if (it.index % 100 == 0) {
                println(it.index)
            }
            addCoin(it.value, coins, failed)
        }
        val retryFailed = ArrayList(failed)
        failed.clear()
        for (coin in retryFailed) {
            addCoin(coin, coins, failed)
        }
        println("Found ${coins.size} total coins.")
        println("Found ${coins.count { it["score"]?.toDouble() ?: 0.0 > 1 }} total coins greater than 1.")
        println("Found ${coins.count { it["score"]?.toDouble() ?: 0.0 > 2 }} total coins greater than 2.")
        println("Failed: ${failed.joinToString { it.get("id").asString }}")
        println(Gson().toJson(coins))
    }

    private fun addCoin(obj: JsonObject, coins: MutableList<Map<String, String>>, failed: MutableList<JsonObject>) {
        var coin: CoinGeckoCoin? = null
        var tries = 0
        while (coin == null) {
            try {
                coin = getCoinData(obj)
                if (coin.score >= scoreLimit) {
                    coins.add(coin.toMap())
                }
            } catch (ignored: SocketTimeoutException) {
                if (tries++ == 3) {
                    failed.add(obj)
                    return
                }
            } catch (e: Exception) {
                println("Unknown exception: ${e.message}")
                failed.add(obj)
                return
            }
            Thread.sleep(2000)
        }
    }


    private fun getCoinData(it: JsonObject): CoinGeckoCoin {
        val id = it.get("id").asString
        val name = it.get("name").asString
        val obj = Gson().fromJson(get(dataUrl + id + dataUrl2), JsonObject::class.java)
        val score = obj.get("coingecko_score").asDouble
        val symbol = it.get("symbol").asString
        val icon = obj.get("image").asJsonObject.get("large").asString
        return CoinGeckoCoin(id, symbol, name, icon, score)
    }

    private fun parse(url: String, path: String) = JsonPath.read(get(url), path) as List<String>
    private fun get(value: String): String =
        OkHttpClient.Builder().retryOnConnectionFailure(true).readTimeout(0, TimeUnit.SECONDS)
            .connectTimeout(0, TimeUnit.SECONDS).callTimeout(0, TimeUnit.SECONDS)
            .writeTimeout(0, TimeUnit.SECONDS).build()
            .newCall(Request.Builder().url(value).build()).execute().body!!.string()

}