package com.example.weatherapp.data.repo

import com.example.weatherapp.data.local.ILocalDataSource
import com.example.weatherapp.model.pojo.CustomSaved
import com.example.weatherapp.model.pojo.OsmResponseItem
import com.example.weatherapp.model.pojo.WeatherData
import com.example.weatherapp.model.pojo.WeatherForecastFiveDays
import com.example.weatherapp.data.remote.IRemotedataSource
import com.example.weatherapp.model.pojo.DateDTO
import com.example.weatherapp.model.pojo.DateDtoForRoom
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class RepoImpl private constructor(val remote: IRemotedataSource, val local: ILocalDataSource) :
    IRepo {
    companion object {
        fun getInstance(remote: IRemotedataSource, local: ILocalDataSource): IRepo {
            return RepoImpl(remote, local)
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

    override suspend fun search(
        query: String
    ): Response<List<OsmResponseItem>> = remote.search(query)

    override fun getFavWeatherData(): Flow<List<CustomSaved>> = local.getFavWeatherData()

    override fun getHomeWeatherData(): Flow<List<CustomSaved>> = local.getHomeWeatherData()

    override suspend fun insert(customSaved: CustomSaved): Long = local.insert(customSaved)
    override suspend fun insert(alarm: DateDtoForRoom): Long {
        return local.insert(alarm)
    }

    override suspend fun delete(customSaved: CustomSaved): Int = local.delete(customSaved)
    override suspend fun delete(alarm: DateDtoForRoom): Int {
        return local.delete(alarm)
    }

    override suspend fun getActiveAlarms(): Flow<List<DateDtoForRoom>> {
        return local.getActiveAlarms()
    }
}