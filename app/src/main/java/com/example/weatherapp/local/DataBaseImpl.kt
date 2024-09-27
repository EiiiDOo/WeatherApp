package com.example.weatherapp.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherapp.model.ConverterList
import com.example.weatherapp.model.CustomSaved

@Database(entities = [CustomSaved::class], version = 1)
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