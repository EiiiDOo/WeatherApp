package com.example.weatherapp.remote

import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.model.WeatherForecastFiveDays
import retrofit2.Response

object RemoteDataSourceImpl : IRemotedataSource {
    override suspend fun getWeatherByCountryName(q: String, API_KEY: String) : Response<WeatherData> {
        return WeatherRetrofit.hitApi.getWeatherByCountryName(q, API_KEY)
    }

    override suspend fun getWeatherForLocation(
        lat: Double,
        lon: Double,
        lang: String,
        units: String,
        API_KEY: String
    ): Response<WeatherData> {
        return WeatherRetrofit.hitApi.getWeatherForLocation(lat, lon, lang, units, API_KEY)
    }

    override suspend fun getWeatherEveryThreeHours(
        lat: Double,
        lon: Double,
        lang: String,
        units: String,
        API_KEY: String
    ): Response<WeatherForecastFiveDays> {
        return WeatherRetrofit.hitApi.getWeatherEveryThreeHours(lat, lon, lang, units, API_KEY)
    }
}