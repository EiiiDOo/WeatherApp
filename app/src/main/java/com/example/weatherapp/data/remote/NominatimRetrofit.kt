package com.example.weatherapp.data.remote

import com.example.weatherapp.model.pojo.OsmResponseItem
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object NominatimRetrofit {
    val baseUrl = "https://nominatim.openstreetmap.org/"
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val hitApi = retrofit.create(NominatimService::class.java)
}

interface NominatimService {
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("format") format: String = "json"
    ): Response<List<OsmResponseItem>>
}