package com.example.weatherapp.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.StateGeneric
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.model.WeatherForecastFiveDays
import com.example.weatherapp.repo.IRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val _repo : IRepo) : ViewModel() {
    private val _responseWeatherOfLocationByLongAndlat: MutableStateFlow<StateGeneric<WeatherData?>> = MutableStateFlow(StateGeneric.Loading)
    val responseOfWeather = _responseWeatherOfLocationByLongAndlat
   private val _responseForecastOfLocationByLongAndlat: MutableStateFlow<StateGeneric<WeatherForecastFiveDays?>> = MutableStateFlow(StateGeneric.Loading)
    val responseOfForecast = _responseForecastOfLocationByLongAndlat

    fun getWeatherByLongitudeAndLatitude(lat:Double,lon:Double){

        viewModelScope.launch {
            val res = _repo.getWeatherForLocation(lat,lon)
            if (res.isSuccessful) {
                _responseWeatherOfLocationByLongAndlat.value = StateGeneric.Success(res.body())
            }else{
                _responseWeatherOfLocationByLongAndlat.value = StateGeneric.Error(res.message())
            }
        }
    }
    fun getForecastByLongitudeAndLatitude(lat:Double,lon:Double){

        viewModelScope.launch {
            val res = _repo.getWeatherEveryThreeHours(lat,lon)
            if (res.isSuccessful) {
                _responseForecastOfLocationByLongAndlat.value = StateGeneric.Success(res.body())
                Log.d("TAG", "getForecastByLongitudeAndLatitude: "+res.body().toString())
            }else{
                _responseForecastOfLocationByLongAndlat.value = StateGeneric.Error(res.message())
            }
        }
    }
}