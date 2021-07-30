package com.brentpanther.bitcoinwidget.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WidgetDao {

    @Query("SELECT * FROM configuration LIMIT 1")
    fun config() : Configuration

    @Query("SELECT * FROM widget WHERE widgetId = :widgetId")
    fun getByWidgetId(widgetId: Int): Widget?

    @Query("SELECT MIN(portraitTextSize) AS portrait, MIN(landscapeTextSize) AS landscape FROM widget")
    fun getSmallestSizes() : SmallestWidgetSizes

    @Update
    suspend fun update(widget: Widget)

    @Query("SELECT * FROM widget ORDER BY widgetId")
    suspend fun getAll() : List<Widget>

    @Query("SELECT * FROM widget ORDER BY widgetId")
    fun getAllAsFlow() : Flow<List<Widget>>

    @Query("UPDATE widget SET lastUpdated = :time WHERE widgetId = :widgetId")
    suspend fun markUpdated(widgetId: Int, time: Long = System.currentTimeMillis())

    @Query("UPDATE widget SET lastValue = null AND lastUpdated = 0")
    suspend fun resetWidgets()

    @Query("DELETE FROM widget WHERE widgetId IN(:widgetIds)")
    suspend fun delete(widgetIds: IntArray)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(widget: Widget)

    @Update
    suspend fun update(config: Configuration)

}