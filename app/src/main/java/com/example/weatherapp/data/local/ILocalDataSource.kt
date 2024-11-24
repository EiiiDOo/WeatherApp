package com.example.weatherapp.data.local

import com.example.weatherapp.model.pojo.CustomSaved
import com.example.weatherapp.model.pojo.DateDTO
import com.example.weatherapp.model.pojo.DateDtoForRoom
import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {

    fun getFavWeatherData(): Flow<List<CustomSaved>>

    fun getHomeWeatherData(): Flow<List<CustomSaved>>

    suspend fun insert(customSaved: CustomSaved) : Long

    suspend fun delete(customSaved: CustomSaved): Int

    suspend fun insert(alarm: DateDtoForRoom):Long

    suspend fun delete(alarm: DateDtoForRoom):Int

    suspend fun getActiveAlarms():Flow<List<DateDtoForRoom>>

    suspend fun deleteSpecificAlarm(id: Int):Int
}