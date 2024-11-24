package com.example.weatherapp.model.pojo

data class MyCalendar(
    val year : Int?,
    val month :Int?,
    val day :Int?,
    var hour :Int?,
    var minute :Int?,
    val sec :Int = 0,
)