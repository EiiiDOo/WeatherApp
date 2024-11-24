package com.example.weatherapp.data.remote

import com.example.weatherapp.model.pojo.OsmResponseItem
import com.example.weatherapp.model.pojo.WeatherData
import com.example.weatherapp.model.pojo.WeatherForecastFiveDays
import retrofit2.Response

interface IRemotedataSource {
    suspend fun getWeatherByCountryName(q: String, appid: String): Response<WeatherData>
    suspend fun getWeatherForLocation(
        lat: Double,
        lon: Double,
        lang: String,
        units: String,
        appid: String
    ): Response<WeatherData>

    suspend fun getWeatherEveryThreeHours(
        lat: Double,
        lon: Double,
        lang: String,
        units: String,
        appid: String
    ): Response<WeatherForecastFiveDays>

    suspend fun search(
        query: String
    ): Response<List<OsmResponseItem>>
}