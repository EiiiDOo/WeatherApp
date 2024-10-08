package com.example.weatherapp.repo

import com.example.weatherapp.BuildConfig
import com.example.weatherapp.model.CustomSaved
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.model.WeatherForecastFiveDays
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface IRepo {
    suspend fun getWeatherByCountryName(q: String, appid: String = BuildConfig.API_KEY): Response<WeatherData>
    suspend fun getWeatherForLocation(
        lat: Double,
        lon: Double,
        lang: String = "en",
        units: String = "standard",
        appid: String = BuildConfig.API_KEY
    ): Response<WeatherData>

    suspend fun getWeatherEveryThreeHours(
        lat: Double,
        lon: Double,
        lang: String = "en",
        units: String = "standard",
        appid: String = BuildConfig.API_KEY
    ): Response<WeatherForecastFiveDays>

    fun getFavWeatherData(): Flow<List<CustomSaved>>

    fun getHomeWeatherData(): Flow<CustomSaved>

    suspend fun insert(customSaved: CustomSaved) : Long

    suspend fun delete(customSaved: CustomSaved) : Int
}