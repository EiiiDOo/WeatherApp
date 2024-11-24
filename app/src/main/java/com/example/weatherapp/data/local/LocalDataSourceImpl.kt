package com.example.weatherapp.data.local

import android.content.Context
import com.example.weatherapp.model.pojo.CustomSaved
import com.example.weatherapp.model.pojo.DateDTO
import com.example.weatherapp.model.pojo.DateDtoForRoom
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl(private val ctx: Context) : ILocalDataSource {

    private val iDao = DataBaseImpl.getInstance(ctx).getDao()

    override  fun getFavWeatherData(): Flow<List<CustomSaved>> = iDao.getFavWeatherData()

    override fun getHomeWeatherData(): Flow<List<CustomSaved>> = iDao.getHomeWeatherData()

    override suspend fun insert(customSaved: CustomSaved) : Long = iDao.insert(customSaved)

    override suspend fun insert(alarm: DateDtoForRoom): Long {
        return iDao.insert(alarm)
    }

    override suspend fun delete(customSaved: CustomSaved) : Int = iDao.delete(customSaved)
    override suspend fun delete(alarm: DateDtoForRoom): Int {
        return iDao.delete(alarm)
    }

    override suspend fun getActiveAlarms(): Flow<List<DateDtoForRoom>> {
        return iDao.getActiveAlarms()
    }

    override suspend fun deleteSpecificAlarm(id: Int): Int {
        return iDao.deleteSpecificAlarm(id)
    }
}