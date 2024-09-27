package com.example.weatherapp

import android.icu.text.SimpleDateFormat
import android.os.Build
import com.example.weatherapp.model.Item0
import com.example.weatherapp.remote.RemoteDataSourceImpl
import com.example.weatherapp.remote.WeatherRetrofit
import com.example.weatherapp.repo.RepoImpl
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun main(){
   /* runBlocking {
        val jop = GlobalScope.launch {
            val repo = RepoImpl.getInstance(RemoteDataSourceImpl)
            val response =repo.getWeatherEveryThreeHours(30.06263, 31.24967)
            if(response.isSuccessful)
                println(response.body()?: "")
        }
        jop.join()
    }*/

    val list =  mutableListOf<Int>()
    repeat(40){
        list.add(it)
    }
        println(getFiveDay(list))
}
fun getFiveDay(list : List<Int>): List<Int>{
    return list.filterIndexed{index,_ -> (index != 0 && index % 8 == 0)}}

fun Long.toDateTime(pattern: String): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        java.time.format.DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
            .format(Instant.ofEpochSecond(this).atZone(ZoneId.of("UTC")))
    } else {
        val sdf = SimpleDateFormat(pattern, Locale.US)
        val netDate = Date(this * 1000)
        sdf.format(netDate)
    }
}
fun Long.convertUnixToDay(format : String): String {
    val instant = Instant.ofEpochSecond(this)

    val dateTime = instant.atZone(ZoneId.systemDefault())

    val formatter = DateTimeFormatter.ofPattern(format)

    return dateTime.format(formatter)
}

fun Long.convertMilliSecondsToTime(milliSeconds: Long, pattern: String): String
{
    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())


    return dateFormat.format(milliSeconds)
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