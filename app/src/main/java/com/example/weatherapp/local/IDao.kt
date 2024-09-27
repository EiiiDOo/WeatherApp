package com.example.weatherapp.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.model.CustomSaved
import kotlinx.coroutines.flow.Flow

@Dao
interface IDao {
    @Query("SELECT * FROM customSaved Where isFavourite = 1")
    fun getFavWeatherData(): Flow<List<CustomSaved>>

    @Query("SELECT * FROM customSaved  Where isHome = 1")
    fun getHomeWeatherData(): Flow<CustomSaved>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customSaved: CustomSaved) : Long

    @Delete
    suspend fun delete(customSaved: CustomSaved) : Int
}