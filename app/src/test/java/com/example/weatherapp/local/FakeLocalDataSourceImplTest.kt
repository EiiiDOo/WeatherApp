package com.example.weatherapp.local

import com.example.weatherapp.data.local.ILocalDataSource
import com.example.weatherapp.model.pojo.CustomSaved
import com.example.weatherapp.model.pojo.DateDTO
import com.example.weatherapp.remote.FakeRemoteDataSourceImplTest
import kotlinx.coroutines.flow.Flow


class FakeLocalDataSourceImplTest : ILocalDataSource {
    companion object{

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
    1.0, 1.0, emptyList()

    )
    }
    override fun getFavWeatherData(): Flow<List<CustomSaved>> {
        TODO("Not yet implemented")
    }

    override fun getHomeWeatherData(): Flow<List<CustomSaved>> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(customSaved: CustomSaved): Long {
        if(customSaved.date != null)
            return 1
        else
            return 0
    }

    override suspend fun insert(alarm: DateDTO): Long {
        TODO("Not yet implemented")
    }

    override suspend fun delete(customSaved: CustomSaved): Int {
        return if(customSaved.date == FakeLocalDataSourceImplTest.customSaved.date)
            1
        else
            0
    }

    override suspend fun delete(alarm: DateDTO): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getActiveAlarms(): Flow<List<DateDTO>> {
        TODO("Not yet implemented")
    }

}