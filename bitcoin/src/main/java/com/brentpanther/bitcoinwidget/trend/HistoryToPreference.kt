package com.brentpanther.bitcoinwidget.trend

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class HistoryToPreference(val context: Context) {

    fun saveToPreferences(history: History): Boolean {
        val mPrefs: SharedPreferences = context.getSharedPreferences(PREFERENCES_KEY_HISTORY_FILE, Context.MODE_PRIVATE)

        val prefsEditor = mPrefs.edit()
        val json = Gson().toJson(history.getEvents())
        prefsEditor.putString(PREFERENCES_KEY_HISTORY, json)
        prefsEditor.apply()

        return true
    }

    fun retrieveFromPreferences(): History {
        val mPrefs = context.getSharedPreferences(PREFERENCES_KEY_HISTORY_FILE, Context.MODE_PRIVATE)
        val json = mPrefs.getString(PREFERENCES_KEY_HISTORY, "")
        val type: Type = object : TypeToken<List<CoinValue?>?>() {}.type
        var coinValues: ArrayList<CoinValue> =  Gson().fromJson(json, type)

        return History(TimeUnit.HOURS.toMillis(24), coinValues)
    }

    companion object {
        const val PREFERENCES_KEY_HISTORY = "intent_key_history"
        const val PREFERENCES_KEY_HISTORY_FILE = "intent_key_history_file"
    }

}