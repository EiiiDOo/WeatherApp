package com.example.weatherapp.model.pojo


import androidx.room.Entity

@Entity(tableName = "customSaved",primaryKeys = ["lon","lat"])
data class CustomSaved(
    val city : String,
    val temp : Double,
    val date : Long,
    val skyStateDescription : String,
    val iconId : String,
    val visibility: Int,
    val windSpeed: Double,
    val humidity: Int,
    val pressure: Int,
    val clouds: Int,
    val isHome : Boolean,
    val isFavourite: Boolean,
    val lon: Double,
    val lat: Double,
    var list : List<Item0>
)





