package com.example.weatherapp.model

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import com.example.weatherapp.R
import com.example.weatherapp.model.pojo.MyCalendar
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

const val MESSAGE = "msg"
const val ALARM_ITEM = "alarmItem"
fun String.toDrawable(): Int {
    when (this) {
        "01d" -> return R.drawable.img_sun
        "01n" -> return R.drawable.img_moon
        "02d" -> return R.drawable.img_sun_clouds
        "02n" -> return R.drawable.img_moon_clouds
        "03d", "03n" -> return R.drawable.img_clouds
        "04d", "04n" -> return R.drawable.img_broken
        "09d", "09n" -> return R.drawable.img_rainy
        "10d", "10n" -> return R.drawable.img_moon_clouds_rain
        "11d", "11n" -> return R.drawable.img_storm
        "13d", "13n" -> return R.drawable.img_clouds_snow
        "50d", "50n" -> return R.drawable.img_mist
        else -> return R.drawable.img_clouds
    }
}

fun String.toDrawable2X(): Int {
    when (this) {
        "01d" -> return R.drawable.img_sun_2x
        "01n" -> return R.drawable.img_moon_2x
        "02d", "02n" -> return R.drawable.img_moon_clouds_2x
        "03d", "03n" -> return R.drawable.img_clouds_2x
        "04d", "04n" -> return R.drawable.img_broken_2x
        "09d", "09n" -> return R.drawable.img_rainy_2x
        "10d" -> return R.drawable.img_sun_clouds_rain_2x
        "10n" -> return R.drawable.img_moon_clouds_rain_2x
        "11d", "11n" -> return R.drawable.img_storm_2x
        "13d", "13n" -> return R.drawable.img_clouds_snow_2x
        "50d", "50n" -> return R.drawable.img_mist_2x
        else -> return R.drawable.img_sun_clouds_2x
    }
}

fun Long.toDateTime(pattern: String): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        DateTimeFormatter.ofPattern(pattern)
            .format(Instant.ofEpochSecond(this).atZone(ZoneId.of("UTC")))
    } else {
        val sdf = SimpleDateFormat(pattern, Locale.US)
        val netDate = Date(this * 1000)
        sdf.format(netDate)
    }
}

fun Long.convertUnixToDay(format: String): String {
    val instant = Instant.ofEpochSecond(this)

    val dateTime = instant.atZone(ZoneId.systemDefault())

    val formatter = DateTimeFormatter.ofPattern(format)

    return dateTime.format(formatter)
}

fun Double.toMilesPerHour(): Double {
    val conversionFactor = 2.23694
    return Math.round(this * conversionFactor * 100.0) / 100.0
}

fun Double.toKelvin(): Double {
    return Math.round((this + 273.15) * 100.0) / 100.0
}

fun Double.toFahrenheit(): Double {
    return Math.round(((this * 9 / 5) + 32) * 100.0) / 100.0
}

fun Double.toTwoDecimalPlaces(): String {
    return String.format("%.2f", this)
}

fun Long.convertMilliSecondsToTime(milliSeconds: Long, pattern: String): String {
    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return dateFormat.format(milliSeconds)
}

fun Calendar.toMyCalendar():MyCalendar{
    val myCalendar:MyCalendar
    this.apply {
        myCalendar = MyCalendar(
            get(Calendar.YEAR),
            get(Calendar.MONTH),
            get(Calendar.DAY_OF_MONTH),
            get(Calendar.HOUR_OF_DAY),
            get(Calendar.MINUTE),
        )
    }
    return myCalendar
}