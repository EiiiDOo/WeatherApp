package com.example.weatherapp.data.remote

import com.example.weatherapp.model.pojo.OsmResponseItem
import com.example.weatherapp.model.pojo.WeatherData
import com.example.weatherapp.model.pojo.WeatherForecastFiveDays
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

    override suspend fun search(query: String): Response<List<OsmResponseItem>> {
        return NominatimRetrofit.hitApi.search(query)
    }
}