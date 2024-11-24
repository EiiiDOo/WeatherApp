package com.example.weatherapp.repo

import com.example.weatherapp.data.repo.IRepo
import com.example.weatherapp.model.pojo.City
import com.example.weatherapp.model.pojo.CustomSaved
import com.example.weatherapp.model.pojo.DateDTO
import com.example.weatherapp.model.pojo.OsmResponseItem
import com.example.weatherapp.model.pojo.WeatherData
import com.example.weatherapp.model.pojo.WeatherForecastFiveDays
import com.example.weatherapp.ui.home.toCustomSave
import com.squareup.okhttp.ResponseBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class FakeRepoImplTest : IRepo {
    companion object {

        val customSaved = CustomSaved(
            "",
            1.10,
            10L,
            "q",
            "q",
            1,
            1.0,
            1,
            1,
            1,
            false,
            true,
            1.0, 1.1, emptyList()
        )
        val osresponse = OsmResponseItem(
            "q",
            "1.0",
            "1",
            "1.0",
            "1.0"
        )
    }

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
        return Response.success(null)
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
        if (!query.isEmpty())
            return Response.success(listOf(osresponse))
        else
            return Response.success(emptyList())
    }

    override fun getFavWeatherData(): Flow<List<CustomSaved>> {
        TODO("Not yet implemented")
    }

    override fun getHomeWeatherData(): Flow<List<CustomSaved>> {
        return flow{
            emit(listOf(customSaved).toMutableList())
        }
    }

    override suspend fun insert(customSaved: CustomSaved): Long {
        TODO("Not yet implemented")
    }

    override suspend fun insert(alarm: DateDTO): Long {
        TODO("Not yet implemented")
    }

    override suspend fun delete(customSaved: CustomSaved): Int {
        if (FakeRepoImplTest.customSaved == customSaved)
            return 1
        else
            return 0
    }

    override suspend fun delete(alarm: DateDTO): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getActiveAlarms(): Flow<List<DateDTO>> {
        TODO("Not yet implemented")
    }

}