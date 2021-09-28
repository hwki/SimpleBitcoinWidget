package com.brentpanther.bitcoinwidget.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WidgetDao {

    @Query("SELECT refresh, consistentSize, MIN(portraitTextSize) AS portrait, MIN(landscapeTextSize) AS landscape FROM configuration, widget LIMIT 1")
    fun configWithSizes() : ConfigurationWithSizes

    @Query("SELECT refresh, consistentSize, MIN(portraitTextSize) AS portrait, MIN(landscapeTextSize) AS landscape FROM configuration, widget LIMIT 1")
    fun configWithSizesAsFlow() : Flow<ConfigurationWithSizes>

    @Query("SELECT * FROM configuration LIMIT 1")
    fun configAsFlow(): Flow<Configuration>

    @Query("SELECT * FROM widget WHERE widgetId = :widgetId")
    fun getByWidgetId(widgetId: Int): Widget?

    @Query("SELECT * FROM widget WHERE widgetId = :widgetId")
    fun getByWidgetIdFlow(widgetId: Int): Flow<Widget?>

    @Update
    suspend fun update(widget: Widget)

    @Query("SELECT * FROM widget ORDER BY widgetId")
    suspend fun getAll() : List<Widget>

    @Query("SELECT * FROM widget ORDER BY widgetId")
    fun getAllAsFlow() : Flow<List<Widget>>

    @Query("DELETE FROM widget WHERE widgetId IN(:widgetIds)")
    suspend fun delete(widgetIds: IntArray)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(widget: Widget)

    @Update
    suspend fun update(config: Configuration)

    @Query("DELETE FROM widget")
    suspend fun clear()


}