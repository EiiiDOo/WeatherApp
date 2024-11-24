package com.example.weatherapp.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.StateGeneric
import com.example.weatherapp.model.pojo.CustomSaved
import com.example.weatherapp.model.pojo.OsmResponseItem
import com.example.weatherapp.model.pojo.WeatherData
import com.example.weatherapp.model.pojo.WeatherForecastFiveDays
import com.example.weatherapp.data.repo.IRepo
import com.example.weatherapp.model.pojo.DateDTO
import com.example.weatherapp.model.pojo.DateDtoForRoom
import com.example.weatherapp.ui.home.toCustomSave
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MainViewModel(private val _repo: IRepo) : ViewModel() {

    private val _networkState = MutableStateFlow(false)
    val networkState = _networkState

    private val _responseWeatherOfLocationByLongAndlat: MutableStateFlow<StateGeneric<WeatherData?>> =
        MutableStateFlow(StateGeneric.Loading)
    val responseOfWeather = _responseWeatherOfLocationByLongAndlat

    private val _responseForecastOfLocationByLongAndlat: MutableStateFlow<StateGeneric<WeatherForecastFiveDays?>> =
        MutableStateFlow(StateGeneric.Loading)
    val responseOfForecast = _responseForecastOfLocationByLongAndlat

    private val _allFavourites: MutableStateFlow<StateGeneric<List<CustomSaved>>> =
        MutableStateFlow(StateGeneric.Loading)
    val allFavourite = _allFavourites

    private val _homeData: MutableStateFlow<StateGeneric<CustomSaved>> =
        MutableStateFlow(StateGeneric.Loading)
    val homeData = _homeData

    private val _currentLonLat: MutableStateFlow<MutableMap<String, Double>> = MutableStateFlow(
        mutableMapOf("lon" to 31.2001, "lat" to 29.9187)
    )
    val currentLonLat = _currentLonLat

    private val _HomeLonLat: MutableStateFlow<MutableMap<String, Double>> = MutableStateFlow(
        mutableMapOf("lon" to 31.2001, "lat" to 29.9187)
    )
    val homeLonLat = _currentLonLat

    private val _details = MutableStateFlow<CustomSaved?>(null)
    val details = _details


    fun getWeatherByLongitudeAndLatitude(
        lat: Double,
        lon: Double,
        lang: String,
        withHome: Boolean
    ) {
        viewModelScope.launch {
            _currentLonLat.value = mutableMapOf("lon" to lon, "lat" to lat)
            if (withHome)
                _HomeLonLat.value = mutableMapOf("lon" to lon, "lat" to lat)

            val res = _repo.getWeatherForLocation(lat, lon, lang)
            if (res.isSuccessful) {
                _responseWeatherOfLocationByLongAndlat.value = StateGeneric.Success(res.body())
            } else {
                _responseWeatherOfLocationByLongAndlat.value = StateGeneric.Error(res.message())
            }
        }
    }

    fun getForecastByLongitudeAndLatitude(lat: Double, lon: Double, lang: String) {

        viewModelScope.launch {
            val res = _repo.getWeatherEveryThreeHours(lat, lon, lang)
            if (res.isSuccessful) {
                _responseForecastOfLocationByLongAndlat.value = StateGeneric.Success(res.body())
                Log.d("TAG", "getForecastByLongitudeAndLatitude: " + res.body().toString())
            } else {
                _responseForecastOfLocationByLongAndlat.value = StateGeneric.Error(res.message())
            }
        }
    }

    suspend fun saveWeatherData(weatherData: CustomSaved): Long = _repo.insert(weatherData)

    suspend fun deleteWeatherData(weatherData: CustomSaved): Int = _repo.delete(weatherData)

    fun getHomeWeatherData() {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.getHomeWeatherData().collect {
//                _currentLonLat.value = mutableMapOf("lon" to it.lon, "lat" to it.lat)
                if (!it.isEmpty()) {
                    var cust = it[0]
                    for (item in it) {
                        if (item.date > cust.date)
                            cust = item
                    }
                    _HomeLonLat.value = mutableMapOf("lon" to cust.lon, "lat" to cust.lat)
                    _homeData.value = StateGeneric.Success(cust)
                }
            }
        }
    }

    fun getFavouriteWeatherData() {
        viewModelScope.launch(Dispatchers.IO) {
            //.shareIn(this, started = SharingStarted.Lazily)
            _repo.getFavWeatherData().collect {
                _allFavourites.value = StateGeneric.Success(it)
            }
        }

    }

    suspend fun search(q: String): Flow<StateGeneric<List<OsmResponseItem>>> {
        val res = _repo.search(q)
        return if (res.isSuccessful)
            flow {
                emit(StateGeneric.Success(res.body()!!))
            }
        else
            flow { emit(StateGeneric.Error(res.message())) }

    }

    fun fitchAndSave(lat: Double, lon: Double, isHome: Boolean, isFav: Boolean, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = _repo.getWeatherEveryThreeHours(lat, lon, lang)
            if (res.isSuccessful) {
                val resTwo = _repo.getWeatherForLocation(lat, lon, lang)
                if (resTwo.isSuccessful) {
                    val custom = toCustomSave(
                        resTwo.body()!!,
                        res.body()!!,
                        isHome,
                        isFav
                    )
                    _repo.insert(
                        custom
                    )
                    emitDetails(custom)
                }
            }
        }
    }

    fun emitDetails(data: CustomSaved?) {
        _details.value = data
    }

    fun changeCurrent(lat: Double, lon: Double) {
        _currentLonLat.value = mutableMapOf("lon" to lon, "lat" to lat)
    }

    fun changeHomeLat(lat: Double, lon: Double) {
        _HomeLonLat.value = mutableMapOf("lon" to lon, "lat" to lat)
    }

    fun changeNetworkState(state: Boolean) {
        _networkState.value = state
    }

    private val _deletedAlarm : MutableSharedFlow<Boolean> = MutableSharedFlow()
    val deletedAlarm  = _deletedAlarm.asSharedFlow()
    fun deleteAlarm(alarm:DateDtoForRoom){
        viewModelScope.launch{
            val res =  _repo.delete(alarm)
            if (res>0)
                _deletedAlarm.emit(true)
            else
                _deletedAlarm.emit(false)
        }
    }
    private val _insertAlarm : MutableSharedFlow<StateGeneric<Long>> = MutableSharedFlow()
    val insertAlarm  = _insertAlarm.asSharedFlow()
    fun insertAlarm(alarm: DateDtoForRoom){
        viewModelScope.launch{
            val res =  _repo.insert(alarm)
            if (res>0)
                _insertAlarm.emit(StateGeneric.Success(res))
            else
                _insertAlarm.emit(StateGeneric.Error("error"))
        }
    }
    private val _allAlarms : MutableStateFlow<StateGeneric<List<DateDtoForRoom>>> = MutableStateFlow(StateGeneric.Loading)
    val allAlarms  = _allAlarms.asStateFlow()
    fun getAllAlarm(){
        viewModelScope.launch{
            _repo.getActiveAlarms().collect{
                _allAlarms.value = StateGeneric.Success(it)
            }
        }
    }


}