package com.brentpanther.bitcoinwidget.db

import android.util.Log
import androidx.sqlite.db.SupportSQLiteDatabase
import com.brentpanther.bitcoinwidget.exchange.Exchange

object DataMigration {

    private val TAG = DataMigration::class.java.simpleName

    fun migrate(db: SupportSQLiteDatabase) {
        migrateBitBayToZonda(db)
        migrateOkexToOkx(db)
        migrateBithumbProToBitGlobal(db)
        fixRemovedExchanges(db)
    }

    private fun migrateBithumbProToBitGlobal(db: SupportSQLiteDatabase) {
        db.execSQL("UPDATE Widget SET exchange = 'BITGLOBAL' WHERE exchange = 'BITHUMB_PRO'")
    }

    private fun migrateOkexToOkx(db: SupportSQLiteDatabase) {
        db.execSQL("UPDATE Widget SET exchange = 'OKX' WHERE exchange = 'OKEX'")
    }

    private fun migrateBitBayToZonda(db: SupportSQLiteDatabase) {
        db.execSQL("UPDATE Widget SET exchange = 'ZONDA' WHERE exchange = 'BITBAY'")
    }

    private fun fixRemovedExchanges(db: SupportSQLiteDatabase) {
        val cursor = db.query("SELECT id, exchange FROM Widget ORDER BY id")
        val allExchanges = Exchange.entries.map { it.name }
        val errored = mutableListOf<Int>()
        while (cursor.moveToNext()) {
            val exchange = cursor.getString(1)
            if (!allExchanges.contains(exchange)) {
                val id = cursor.getInt(0)
                errored.add(id)
                Log.w(TAG, "Widget $id: has invalid exchange: $exchange")
            }
        }
        // fallback to coingecko for any broken exchange
        for (id in errored) {
            db.execSQL("UPDATE Widget SET exchange = 'COINGECKO' WHERE id = $id")
        }
    }
}