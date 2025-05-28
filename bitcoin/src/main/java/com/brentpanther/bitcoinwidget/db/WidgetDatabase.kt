package com.brentpanther.bitcoinwidget.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.brentpanther.bitcoinwidget.WidgetApplication
import java.io.File

@Database(
    version = 7,
    entities = [Widget::class, Configuration::class],
    exportSchema = true,
    autoMigrations = [
        AutoMigration (from = 5, to = 6)
    ]
)
abstract class WidgetDatabase : RoomDatabase() {

    abstract fun widgetDao(): WidgetDao

    companion object {

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Widget ADD COLUMN widgetType TEXT NOT NULL DEFAULT 'PRICE'")
                db.execSQL("ALTER TABLE Widget ADD COLUMN showAmountLabel INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE Widget ADD COLUMN amountHeld REAL")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Widget ADD COLUMN useInverse INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // not all devices support rename column yet
                db.execSQL("""
                    CREATE TABLE `Widget_New` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `widgetId` INTEGER NOT NULL, `widgetType` TEXT NOT NULL, `exchange` TEXT NOT NULL,
                    `coin` TEXT NOT NULL, `currency` TEXT NOT NULL, `coinCustomId` TEXT, `coinCustomName` TEXT, `currencyCustomName` TEXT, `showExchangeLabel` INTEGER NOT NULL, 
                    `showCoinLabel` INTEGER NOT NULL, `showIcon` INTEGER NOT NULL, `numDecimals` INTEGER NOT NULL, `currencySymbol` TEXT, `theme` TEXT NOT NULL, 
                    `nightMode` TEXT NOT NULL, `coinUnit` TEXT, `currencyUnit` TEXT, `customIcon` TEXT, `portraitTextSize` INTEGER, `landscapeTextSize` INTEGER, `lastValue` TEXT, 
                    `amountHeld` REAL, `showAmountLabel` INTEGER NOT NULL, `useInverse` INTEGER NOT NULL, `lastUpdated` INTEGER NOT NULL, `state` TEXT NOT NULL)
                """)
                db.execSQL("""
                    INSERT INTO Widget_New (id, widgetId, widgetType, exchange, coin, currency, coinCustomId, coinCustomName, currencyCustomName,
                    showExchangeLabel, showCoinLabel, showIcon, numDecimals, currencySymbol, theme, nightMode, coinUnit, currencyUnit,
                    customIcon, portraitTextSize, landscapeTextSize, lastValue, amountHeld, showAmountLabel, useInverse, lastUpdated, state)
                    SELECT id, widgetId, widgetType, exchange, coin, currency, coinCustomId, coinCustomName, currencyCustomName,
                    showExchangeLabel, showCoinLabel, showIcon, -1, currencySymbol, theme, nightMode, coinUnit, currencyUnit,
                    customIcon, portraitTextSize, landscapeTextSize, lastValue, amountHeld, showAmountLabel, useInverse, lastUpdated, state 
                    FROM Widget
                """)
                db.execSQL("DROP TABLE Widget")
                db.execSQL("ALTER TABLE Widget_New RENAME TO Widget")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Widget_widgetId` ON `Widget` (`widgetId`)")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val cursor = db.query("SELECT coinCustomId, customIcon FROM Widget ORDER BY id")
                while (cursor.moveToNext()) {
                    val coinCustomId = cursor.getString(0)
                    val customIcon = cursor.getString(1)
                    val file = File(WidgetApplication.instance.filesDir, "icons/$customIcon")
                    if (file.exists()) {
                        val newFile = File(WidgetApplication.instance.filesDir, "icons/$coinCustomId")
                        file.renameTo(newFile)
                    }
                }
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Widget ADD COLUMN priceType TEXT NOT NULL DEFAULT 'SPOT'")
            }
        }

        @Volatile
        private var INSTANCE: WidgetDatabase? = null

        fun getInstance(context: Context): WidgetDatabase {
            val callback = object : Callback() {

                override fun onCreate(db: SupportSQLiteDatabase) {
                    val values = ContentValues().apply {
                        put("refresh", 15)
                        put("consistentSize", false)
                        put("dataMigrationVersion", 1)
                    }
                    db.insert("configuration", SQLiteDatabase.CONFLICT_REPLACE, values)
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    DataMigration.migrate(db)
                }
            }
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, WidgetDatabase::class.java, "widgetdb"
                ).addCallback(callback)
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .addMigrations(MIGRATION_3_4)
                    .addMigrations(MIGRATION_4_5)
                    .addMigrations(MIGRATION_6_7)
                    .build()
                INSTANCE = instance
                instance
            }
        }


    }
}

