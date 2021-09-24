package com.brentpanther.bitcoinwidget.db

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(version = 2, entities = [Widget::class, Configuration::class], exportSchema = true)
abstract class WidgetDatabase : RoomDatabase() {

    abstract fun widgetDao() : WidgetDao

    companion object {

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Widget ADD COLUMN widgetType TEXT NOT NULL DEFAULT 'PRICE'")
                database.execSQL("ALTER TABLE Widget ADD COLUMN showAmountLabel INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE Widget ADD COLUMN amountHeld REAL")
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
            }
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, WidgetDatabase::class.java, "widgetdb"
                ).addCallback(callback)
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

