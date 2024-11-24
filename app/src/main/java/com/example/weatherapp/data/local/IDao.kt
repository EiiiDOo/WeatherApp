package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.model.pojo.DateDTO
import com.example.weatherapp.model.pojo.CustomSaved
import com.example.weatherapp.model.pojo.DateDtoForRoom
import kotlinx.coroutines.flow.Flow

@Dao
interface IDao {
    @Query("SELECT * FROM customSaved Where isFavourite = 1")
    fun getFavWeatherData(): Flow<List<CustomSaved>>

    @Query("SELECT * FROM customSaved  Where isHome = 1")
    fun getHomeWeatherData(): Flow<List<CustomSaved>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customSaved: CustomSaved) : Long

    @Delete
    suspend fun delete(customSaved: CustomSaved) : Int

    // Alarms Entity

    @Insert
    suspend fun insert(alarm: DateDtoForRoom):Long

    @Delete
    suspend fun delete(alarm: DateDtoForRoom):Int

    @Query("SELECT * FROM alarms")
    fun getActiveAlarms():Flow<List<DateDtoForRoom>>

    @Query("DELETE FROM alarms WHERE id = :id")
    suspend fun deleteSpecificAlarm(id: Int):Int

}