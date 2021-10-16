package com.brentpanther.bitcoinwidget.db

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.brentpanther.bitcoinwidget.exchange.Exchange

@Database(version = 3, entities = [Widget::class, Configuration::class], exportSchema = true)
abstract class WidgetDatabase : RoomDatabase() {

    abstract fun widgetDao() : WidgetDao

    companion object {

        private val TAG = WidgetDatabase::class.java.simpleName

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Widget ADD COLUMN widgetType TEXT NOT NULL DEFAULT 'PRICE'")
                database.execSQL("ALTER TABLE Widget ADD COLUMN showAmountLabel INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE Widget ADD COLUMN amountHeld REAL")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Widget ADD COLUMN useInverse INTEGER NOT NULL DEFAULT 0")
            }
        }

        @Volatile
        private var INSTANCE: WidgetDatabase? = null

        fun getInstance(context: Context): WidgetDatabase {
            val callback = object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    DatabaseInitializer.create(db,
                        PreferenceManager.getDefaultSharedPreferences(context),
                        context.getSharedPreferences("bitcoinwidget", Context.MODE_PRIVATE))
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    fixRemovedExchanges(db)
                    super.onOpen(db)
                }
            }
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, WidgetDatabase::class.java, "widgetdb"
                ).addCallback(callback)
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun fixRemovedExchanges(db: SupportSQLiteDatabase) {
            val cursor = db.query("SELECT id, exchange FROM Widget ORDER BY id")
            val allExchanges = Exchange.values().map { it.name }
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
}

