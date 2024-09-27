package com.example.weatherapp.repo

import com.example.weatherapp.local.ILocalDataSource
import com.example.weatherapp.model.CustomSaved
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.model.WeatherForecastFiveDays
import com.example.weatherapp.remote.IRemotedataSource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class RepoImpl private constructor(val remote: IRemotedataSource,val local: ILocalDataSource) : IRepo {
    companion object{
        fun getInstance(remote: IRemotedataSource,local: ILocalDataSource): IRepo {
            return RepoImpl(remote,local)
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

    override fun getFavWeatherData(): Flow<List<CustomSaved>> =local.getFavWeatherData()

    override fun getHomeWeatherData(): Flow<CustomSaved> =local.getHomeWeatherData()

    override suspend fun insert(customSaved: CustomSaved) : Long =local.insert(customSaved)

    override suspend fun delete(customSaved: CustomSaved): Int = local.delete(customSaved)
}