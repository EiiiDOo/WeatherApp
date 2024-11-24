package com.example.weatherapp.data.remote

import com.example.weatherapp.model.pojo.WeatherData
import com.example.weatherapp.model.pojo.WeatherForecastFiveDays
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object WeatherRetrofit {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val hitApi = retrofit.create(IWeatherApi::class.java)

}
interface IWeatherApi {
    @GET("forecast")
    suspend fun getWeatherEveryThreeHours(
        @Query("lat")lat : Double,
        @Query("lon")lon : Double,
        @Query("lang")lang : String ,
        @Query("units")units : String ,
        @Query("appid")appid: String
    ) : Response<WeatherForecastFiveDays>

    @GET("weather")
    suspend fun getWeatherForLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String,
        @Query("units") units: String,
        @Query("appid") appid: String
    ) : Response<WeatherData>

    @GET("weather")
    suspend fun getWeatherByCountryName(
        q : String,
        appid: String
    ) : Response<WeatherData>
}