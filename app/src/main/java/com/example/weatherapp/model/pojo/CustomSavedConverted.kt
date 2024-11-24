package com.example.weatherapp.model.pojo


import androidx.room.TypeConverter
import com.google.gson.Gson

class ConverterList{
    @TypeConverter
    fun fromList(list: List<Item0>):String{
        return Gson().toJson(list)
    }
    @TypeConverter
    fun toList(list: String):List<Item0>{
        return Gson().fromJson(list,Array<Item0>::class.java).toList()
    }
}

