package com.example.weatherapp

fun String.toDrawable() : Int {
    when (this) {
        "01d" -> return R.drawable.img_sun
        "01n" -> return R.drawable.img_moon
        "02d" -> return R.drawable.img_sun_clouds
        "02n"-> return R.drawable.img_moon_clouds
        "03d","03n" -> return R.drawable.img_clouds
        "04d" ,"04n" -> return R.drawable.img_broken
        "09d" ,"09n" -> return R.drawable.img_rainy
        "10d","10n" -> return R.drawable.img_moon_clouds_rain
        "11d","11n" -> return R.drawable.img_storm
        "13d" ,"13n" -> return R.drawable.img_clouds_snow
        "50d" ,"50n" -> return R.drawable.img_mist
        else -> return R.drawable.img_clouds
    }
}
fun String.toDrawable2X() : Int {
    when (this) {
        "01d" -> return R.drawable.img_sun_2x
        "01n" -> return R.drawable.img_moon_2x
        "02d" ,"02n" -> return R.drawable.img_moon_clouds_2x
        "03d" ,"03n" -> return R.drawable.img_clouds_2x
        "04d" ,"04n" -> return R.drawable.img_broken_2x
        "09d" ,"09n" -> return R.drawable.img_rainy_2x
        "10d" -> return R.drawable.img_sun_clouds_rain_2x
        "10n" -> return R.drawable.img_moon_clouds_rain_2x
        "11d" ,"11n" -> return R.drawable.img_storm_2x
        "13d","13n" -> return R.drawable.img_clouds_snow_2x
        "50d" ,"50n" -> return R.drawable.img_mist_2x
        else -> return R.drawable.img_sun_clouds_2x
    }
}