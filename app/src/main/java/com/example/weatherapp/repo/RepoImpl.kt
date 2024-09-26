package com.example.weatherapp.repo

import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.model.WeatherForecastFiveDays
import com.example.weatherapp.remote.IRemotedataSource
import retrofit2.Response

class RepoImpl private constructor(val remote: IRemotedataSource) : IRepo {
    companion object{
        fun getInstance(remote: IRemotedataSource): IRepo {
            return RepoImpl(remote)
        }
    }
    override suspend fun getWeatherByCountryName(q: String, appid: String): Response<WeatherData> {
        return remote.getWeatherByCountryName(q, appid)
    }

    override suspend fun getWeatherForLocation(
        lat: Double,
        lon: Double,
        lang: String,
        units: String,
        appid: String
    ): Response<WeatherData> {
        return remote.getWeatherForLocation(lat, lon, lang, units, appid)
    }

    override suspend fun getWeatherEveryThreeHours(
        lat: Double,
        lon: Double,
        lang: String,
        units: String,
        appid: String
    ): Response<WeatherForecastFiveDays> {
        return remote.getWeatherEveryThreeHours(lat, lon, lang, units, appid)
    }

}