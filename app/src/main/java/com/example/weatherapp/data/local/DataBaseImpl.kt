package com.example.weatherapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherapp.model.pojo.DateDTO
import com.example.weatherapp.model.pojo.ConverterList
import com.example.weatherapp.model.pojo.CustomSaved
import com.example.weatherapp.model.pojo.DateDtoForRoom

@Database(entities = [CustomSaved::class, DateDtoForRoom::class], version = 1)
@TypeConverters(ConverterList::class)
abstract class DataBaseImpl : RoomDatabase() {
    abstract fun getDao(): IDao
    companion object {
        @Volatile
        private var _INSTANCE: DataBaseImpl? = null
        fun getInstance(context: Context): DataBaseImpl {
            return _INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DataBaseImpl::class.java,
                    "Weather_database"
                ).build()
                _INSTANCE = instance
                instance
            }
        }
    }
}