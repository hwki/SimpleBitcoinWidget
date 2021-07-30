package com.brentpanther.bitcoinwidget.db

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(version = 1, entities = [Widget::class, Configuration::class], exportSchema = true)
abstract class WidgetDatabase : RoomDatabase() {

    abstract fun widgetDao() : WidgetDao

    companion object {

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
                ).addCallback(callback).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

