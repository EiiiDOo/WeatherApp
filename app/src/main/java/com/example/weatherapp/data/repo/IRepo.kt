package com.example.weatherapp.data.repo

import com.example.weatherapp.BuildConfig
import com.example.weatherapp.model.pojo.CustomSaved
import com.example.weatherapp.model.pojo.DateDTO
import com.example.weatherapp.model.pojo.DateDtoForRoom
import com.example.weatherapp.model.pojo.OsmResponseItem
import com.example.weatherapp.model.pojo.WeatherData
import com.example.weatherapp.model.pojo.WeatherForecastFiveDays
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface IRepo {
    suspend fun getWeatherByCountryName(q: String, appid: String = BuildConfig.API_KEY): Response<WeatherData>
    suspend fun getWeatherForLocation(
        lat: Double,
        lon: Double,
        lang: String = "en",
        units: String = "metric",
        appid: String = BuildConfig.API_KEY
    ): Response<WeatherData>

    suspend fun getWeatherEveryThreeHours(
        lat: Double,
        lon: Double,
        lang: String = "en",
        units: String = "metric",
        appid: String = BuildConfig.API_KEY
    ): Response<WeatherForecastFiveDays>

    suspend fun search(
        query: String
    ): Response<List<OsmResponseItem>>

    fun getFavWeatherData(): Flow<List<CustomSaved>>

    fun getHomeWeatherData(): Flow<List<CustomSaved>>

    suspend fun insert(customSaved: CustomSaved) : Long

    suspend fun delete(customSaved: CustomSaved) : Int

    suspend fun insert(alarm: DateDtoForRoom):Long

    suspend fun delete(alarm: DateDtoForRoom):Int

    suspend fun getActiveAlarms():Flow<List<DateDtoForRoom>>
}