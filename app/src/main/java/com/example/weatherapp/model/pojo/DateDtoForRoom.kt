package com.example.weatherapp.model.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")

data class DateDtoForRoom(
    @PrimaryKey val id :Int,
    val milliSeconds : Long,
    val date :String
)