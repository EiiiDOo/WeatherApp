package com.example.weatherapp.local

import android.content.Context
import com.example.weatherapp.model.CustomSaved
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class LocalDataSourceImpl(private val ctx: Context) : ILocalDataSource {

    private val iDao = DataBaseImpl.getInstance(ctx).getDao()

    override  fun getFavWeatherData(): Flow<List<CustomSaved>> = iDao.getFavWeatherData()

    override fun getHomeWeatherData(): Flow<CustomSaved> = iDao.getHomeWeatherData()

    override suspend fun insert(customSaved: CustomSaved) : Long = iDao.insert(customSaved)

    override suspend fun delete(customSaved: CustomSaved) : Int = iDao.delete(customSaved)
}