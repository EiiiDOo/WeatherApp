package com.example.weatherapp.remote

import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.model.WeatherForecastFiveDays
import retrofit2.Response

interface IRemotedataSource {
    suspend fun getWeatherByCountryName(q: String, appid: String): Response<WeatherData>
    suspend fun getWeatherForLocation(
        lat: Double,
        lon: Double,
        lang: String ,
        units: String,
        appid: String
    ): Response<WeatherData>

    suspend fun getWeatherEveryThreeHours(
        lat: Double,
        lon: Double,
        lang: String ,
        units: String ,
        appid: String
    ): Response<WeatherForecastFiveDays>
}