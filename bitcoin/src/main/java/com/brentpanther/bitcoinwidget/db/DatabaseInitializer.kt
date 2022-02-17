package com.brentpanther.bitcoinwidget.db

import android.content.ContentValues
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import android.util.Log
import androidx.sqlite.db.SupportSQLiteDatabase
import com.brentpanther.bitcoinwidget.NightMode.*
import com.brentpanther.bitcoinwidget.Theme.SOLID
import com.brentpanther.bitcoinwidget.Theme.TRANSPARENT
import com.brentpanther.bitcoinwidget.WidgetApplication
import com.brentpanther.bitcoinwidget.WidgetState
import com.brentpanther.bitcoinwidget.WidgetType
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.util.*
import kotlin.math.min

// Used to upgrade prior shared preferences data storage to android room
object DatabaseInitializer {

    val TAG = DatabaseInitializer::class.simpleName

    fun create(db: SupportSQLiteDatabase, globalPrefs: SharedPreferences, prefs: SharedPreferences) {
        val widgets = WidgetApplication.instance.widgetIds
        var minRefresh = Int.MAX_VALUE
        for (widgetId in widgets) {
            val string = prefs.getString(widgetId.toString(), null) ?: continue
            try {
                val obj = Gson().fromJson(string, JsonObject::class.java)
                val themeMap = mapOf(
                    "Light" to SOLID, "Dark" to SOLID, "DayNight" to SOLID, "Transparent" to TRANSPARENT,
                    "Transparent Dark" to TRANSPARENT, "Transparent DayNight" to TRANSPARENT
                )
                val nightModeMap = mapOf(
                    "Light" to LIGHT, "Dark" to DARK, "DayNight" to SYSTEM, "Transparent" to LIGHT,
                    "Transparent Dark" to DARK, "Transparent DayNight" to SYSTEM
                )
                val unitMap = mapOf("μBTC / bits" to "Bit", "Satoshis" to "Sat", "lites" to "mŁ")
                val unit = unitMap[getString(obj, "units")] ?: getString(obj, "units")
                val values = ContentValues().apply {
                    put("widgetId", widgetId)
                    put("exchange", getString(obj, "exchange"))
                    put("coin", getString(obj, "coin"))
                    put("currency", getString(obj, "currency") ?: Currency.getInstance(Locale.getDefault()).currencyCode)
                    put("coinCustomId", getString(obj, "coin_custom"))
                    put("coinCustomName", getString(obj, "coin_custom"))
                    put("currencyCustomName", getString(obj, "currency_custom"))
                    put("showCoinLabel", false)
                    put("showExchangeLabel", getString(obj, "show_label") == "true")
                    put("showIcon", getString(obj, "icon")?.equals("false") ?: true)
                    put("numDecimals", "-1")
                    put("currencySymbol", getString(obj, "currency_symbol"))
                    put("theme", themeMap[getString(obj, "theme") ?: SOLID.name]!!.name)
                    put("nightMode", nightModeMap[getString(obj, "theme") ?: SYSTEM.name]!!.name)
                    put("coinUnit", unit)
                    put("customIcon", getString(obj, "custom_icon")?.substringBefore("/"))
                    put("lastValue", getString(obj, "last_value")?.replace(Regex("[^0-9\\\\.]+"), ""))
                    put("lastUpdated", 0)
                    put("state", WidgetState.CURRENT.name)
                    put("showAmountLabel", false)
                    put("useInverse", false)
                    put("widgetType", WidgetType.PRICE.name)
                    minRefresh = min(minRefresh, getString(obj, "refresh")?.toInt() ?: 15)
                }
                db.insert("widget", CONFLICT_REPLACE, values)
            } catch (e: Exception) {
                Log.e(TAG, "Exception caught when migrating to database.", e)
            }
        }
        val values = ContentValues().apply {
            put("refresh", if(minRefresh == Int.MAX_VALUE) 15 else minRefresh)
            put("consistentSize", globalPrefs.getBoolean("fixed_size", false))
            put("dataMigrationVersion", 1)
        }
        db.insert("configuration", CONFLICT_REPLACE, values)
        prefs.edit().clear().apply()
        globalPrefs.edit().clear().apply()
    }

    private fun getString(obj: JsonObject, key: String): String? {
        val el = obj.get(key)
        return if (el == null || el.isJsonNull) null else el.asString
    }

}
