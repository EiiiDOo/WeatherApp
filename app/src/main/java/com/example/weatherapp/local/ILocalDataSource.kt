package com.example.weatherapp.local


import com.example.weatherapp.model.CustomSaved
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ILocalDataSource {

    fun getFavWeatherData(): Flow<List<CustomSaved>>

    fun getHomeWeatherData(): Flow<CustomSaved>

    suspend fun insert(customSaved: CustomSaved) : Long

    suspend fun delete(customSaved: CustomSaved): Int
}