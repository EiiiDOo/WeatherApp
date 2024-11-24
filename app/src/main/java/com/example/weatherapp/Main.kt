package com.example.weatherapp

import android.icu.text.SimpleDateFormat
import android.os.Build
import com.example.weatherapp.data.remote.NominatimRetrofit
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun main() {
    var i = 5
    GlobalScope.launch {
        NominatimRetrofit.hitApi.search("London")
    }
}

fun getFiveDay(list: List<Int>): List<Int> {
    return list.filterIndexed { index, _ -> (index != 0 && index % 8 == 0) }
}


//private fun getDaysList(it: ForecastDTO): ForecastDTO {
//    var min: WeatherDTO
//    var max: WeatherDTO
//    return if (it.list.isEmpty()) {
//        it
//    } else {
//        val tempList = it.list.fold(ArrayList<ArrayList<WeatherDTO>>()) { list, item ->
//            list.apply {
//                if (isEmpty() || last().last().date.toDateTime("EEEE") != item.date.toDateTime("EEEE")) {
//                    add(arrayListOf(item))
//                }
//                else {
//                    last().add(item)
//                }
//            }
//        }
//        val result = mutableListOf<WeatherDTO>()
//        for(list in tempList){
//            min = list.minBy { it.minTemperature }
//            max = list.maxBy { it.maxTemperature }
//            result.add(max.copy(minTemperature = min.minTemperature))
//        }
//        it.copy(count = result.count(), list = result)
//    }
//}