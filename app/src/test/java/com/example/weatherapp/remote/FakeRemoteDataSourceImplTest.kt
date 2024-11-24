package com.example.weatherapp.remote

import com.example.weatherapp.data.remote.IRemotedataSource
import com.example.weatherapp.model.pojo.OsmResponseItem
import com.example.weatherapp.model.pojo.WeatherData
import com.example.weatherapp.model.pojo.WeatherForecastFiveDays
import retrofit2.Response


class FakeRemoteDataSourceImplTest: IRemotedataSource {
    override suspend fun getWeatherByCountryName(q: String, appid: String): Response<WeatherData> {
        TODO("Not yet implemented")
    }

    override suspend fun getWeatherForLocation(
        lat: Double,
        lon: Double,
        lang: String,
        units: String,
        appid: String
    ): Response<WeatherData> {
        TODO("Not yet implemented")
    }

    override suspend fun getWeatherEveryThreeHours(
        lat: Double,
        lon: Double,
        lang: String,
        units: String,
        appid: String
    ): Response<WeatherForecastFiveDays> {
        TODO("Not yet implemented")
    }

    override suspend fun search(query: String): Response<List<OsmResponseItem>> {
        return if (query.isEmpty())
            Response.success(emptyList())
        else
            Response.success(listOf(OsmResponseItem(
                "q",
                "1.0",
                "1",
                "1.0",
                "1.0"
            )))
    }
}