package com.example.weatherapp.ui.home

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.StateGeneric
import com.example.weatherapp.convertUnixToDay
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.local.LocalDataSourceImpl
import com.example.weatherapp.model.CustomSaved
import com.example.weatherapp.model.Item0
import com.example.weatherapp.model.Weather
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.model.WeatherForecastFiveDays
import com.example.weatherapp.remote.RemoteDataSourceImpl
import com.example.weatherapp.repo.RepoImpl
import com.example.weatherapp.toDateTime
import com.example.weatherapp.toDrawable
import com.example.weatherapp.toDrawable2X
import com.example.weatherapp.ui.main.MainActivity
import com.example.weatherapp.ui.main.MainViewModel
import com.example.weatherapp.ui.main.MainViewModelFactory
import com.google.android.gms.location.*
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    companion object {
        const val TAG = "HOME FRAGMENT"
    }
    var insertion: Long = -1
    val REQUEST_CODE = 1
    lateinit var binding: FragmentHomeBinding
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var mainViewModel: MainViewModel
    private val adapterHourly = HourlyAdapter(emptyList())
    lateinit var weather: WeatherData
    lateinit var forecast: WeatherForecastFiveDays
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(
            requireActivity(),
            MainViewModelFactory(
                RepoImpl.getInstance(
                    RemoteDataSourceImpl,
                    LocalDataSourceImpl(requireContext())
                )
            )
        )[MainViewModel::class.java]
        sharedPreferences = requireActivity().getSharedPreferences("MyPref", MODE_PRIVATE)
        val fromDatabase = sharedPreferences.getBoolean("fromDatabase", false)
        if (fromDatabase) {
            Log.d(TAG, "onViewCreated: from database")
            lifecycleScope.launch {
                mainViewModel.getHomeWeatherData()
                    mainViewModel.homeData.collect {
                        when(it){
                            is StateGeneric.Error -> {
                                Log.d(TAG, "onViewCreated: error")
                            }
                            StateGeneric.Loading -> {
                                Log.d(TAG, "onViewCreated: loading")
                            }
                            is StateGeneric.Success -> {
                                Log.d(TAG, "onViewCreated: success")
                                showWeatherFromDatabase(it.data)
                                showForecast(it.data.list)
                            }
                        }

                }
            }
        } else {
            Log.d(TAG, "onViewCreated: from retrofit")
            mainViewModel.getWeatherByLongitudeAndLatitude(31.2001, 29.9187)
            mainViewModel.getForecastByLongitudeAndLatitude(31.2001, 29.9187)
            getWeather()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showAppBar(true)

    }


    override fun onStart() {
        super.onStart()

    }

    @SuppressLint("MissingPermission")
    fun getFreshLocation() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(0).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Geocoder(requireActivity()).getFromLocation(
                            locationResult.locations[0].latitude,
                            locationResult.locations[0].longitude,
                            1
                        )
                        {
                            it.firstOrNull()?.latitude
                            it.firstOrNull()?.longitude
                            Log.d(
                                "TAG",
                                "onLocationResult: ${it.firstOrNull()?.getAddressLine(0)}"
                            )
                        }
                    } else {
                        var geo: MutableList<Address>? = null
                        lifecycleScope.launch {
                            geo = Geocoder(requireActivity()).getFromLocation(
                                locationResult.locations[0].latitude,
                                locationResult.locations[0].longitude,
                                1
                            )
                        }.invokeOnCompletion {
                            geo?.get(0)?.latitude
                            geo?.get(0)?.longitude
                            Log.d(
                                "TAG",
                                "onLocationResult: ${geo?.get(0)?.getAddressLine(0)}"
                            )
                        }
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    fun enableLocationServicess() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    fun checkPermissions(): Boolean {
        return requireActivity().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                return requireActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun isLocationEnabled(): Boolean {
        val locationManager =
            requireActivity().getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    fun getFiveDay(list: List<Item0>): List<Item0> {
        return list.filterIndexed { index, _ -> (index % 8 == 0) }
    }

    fun toCustomSave(
        weather: WeatherData,
        forecast: WeatherForecastFiveDays,
        isHome: Boolean,
        isFav: Boolean
    ): CustomSaved {
        return CustomSaved(
            weather.name,
            weather.main.temp,
            weather.dt,
            weather.weather[0].description,
            weather.weather[0].icon,
            weather.visibility,
            weather.wind.speed,
            weather.main.humidity,
            weather.main.pressure,
            weather.clouds.all,
            isHome,
            isFav,
            weather.coord.lon,
            weather.coord.lat,
            forecast.list
        )
    }

    fun showWeather(weather: WeatherData) {
        binding.apply {
            weather.apply {
                txtCity.text = this.name
                txtTemp.text = this.main.temp.toString()
                txtDate.text = this.dt.convertUnixToDay("EEEE, dd-MM-yyyy, hh:mm a")
                txtSkyState.text = this.weather[0].description
                imageView4.setImageResource(this.weather[0].icon.toDrawable2X())
                txtValueOfVisibility.text = this.visibility.toString()
                txtValueOfCloud.text = this.clouds.all.toString()
                txtValueofHumidity.text = this.main.humidity.toString()
                txtValueOfPressure.text = this.main.pressure.toString()
                txtValueOfWind.text = this.wind.speed.toString()
                progressBar.visibility = View.GONE
                dataOfWeatherGroup.visibility = View.VISIBLE
            }
        }
    }

    fun showForecast(forecast: List<Item0>) {
        binding.apply {
            forecast.apply {
                adapterHourly.updateList(this.take(8))
                rvByHour.adapter = adapterHourly
                val list = getFiveDay(this)
                //today
                "${list[0].main.temp_min}-${list[0].main.temp_max}".also {
                    txtTempToday.text = it
                }
                imgIconToday.setImageResource(list[0].weather[0].icon.toDrawable())
                txtDesSkyToday.text = list[0].weather[0].description
                //day2
                txtDay2.text = list[1].dt.toDateTime("EEEE")
                "${list[1].main.temp_min}-${list[1].main.temp_max}".also {
                    txtTemp2.text = it
                }
                imgIcon2.setImageResource(list[1].weather[0].icon.toDrawable())
                txtDesSky2.text = list[1].weather[0].description
                //day3
                txtDay3.text = list[2].dt.toDateTime("EEEE")
                "${list[2].main.temp_min}-${list[2].main.temp_max}".also {
                    txtTemp3.text = it
                }
                imgIcon3.setImageResource(list[2].weather[0].icon.toDrawable())
                txtDesSky3.text = list[2].weather[0].description
                //day4
                txtDay4.text = list[3].dt.toDateTime("EEEE")
                "${list[3].main.temp_min}-${list[3].main.temp_max}".also {
                    txtTemp4.text = it
                }
                imgIcon4.setImageResource(list[3].weather[0].icon.toDrawable())
                txtDesSky4.text = list[3].weather[0].description
                //day5
                txtDay5.text = list[4].dt.toDateTime("EEEE")
                "${list[4].main.temp_min}-${list[4].main.temp_max}".also {
                    txtTemp5.text = it
                }
                imgIcon5.setImageResource(list[4].weather[0].icon.toDrawable())
                txtDesSky5.text = list[4].weather[0].description

                progressBar.visibility = View.GONE
                dataForForcastGroup.visibility = View.VISIBLE
            }
        }
    }

    fun showWeatherFromDatabase(weather: CustomSaved) {
        binding.apply {
            weather.apply {
                txtCity.text = this.city
                txtTemp.text = this.temp.toString()
                txtDate.text = this.date.convertUnixToDay("EEEE, dd-MM-yyyy, hh:mm a")
                txtSkyState.text = this.skyStateDescription
                imageView4.setImageResource(this.iconId.toDrawable2X())
                txtValueOfVisibility.text = this.visibility.toString()
                txtValueOfCloud.text = this.clouds.toString()
                txtValueofHumidity.text = this.humidity.toString()
                txtValueOfPressure.text = this.pressure.toString()
                txtValueOfWind.text = this.windSpeed.toString()
                progressBar.visibility = View.GONE
                dataOfWeatherGroup.visibility = View.VISIBLE
            }
        }
    }

    suspend fun getForeCast() {
        mainViewModel.responseOfForecast.collect { fore ->
            when (fore) {
                is StateGeneric.Error -> {
                    binding.progressBar.visibility = View.GONE
                }

                is StateGeneric.Loading -> {
                    binding.apply {
                        progressBar.visibility = View.VISIBLE
                        dataForForcastGroup.visibility = View.INVISIBLE
                    }
                }

                is StateGeneric.Success -> {
                    val res = fore.data!!
                    forecast = res
                    showForecast(res.list)
                    val job = lifecycleScope.launch {
                        insertion = mainViewModel.saveWeatherData(
                            toCustomSave(
                                weather,
                                forecast,
                                true,
                                false
                            )
                        )
                    }
                    job.join()
                    if (insertion != 0L) {
                        editor = sharedPreferences.edit()
                        editor.putBoolean("fromDatabase", true)
                        editor.apply()
                    }

                }
            }
        }
    }

    fun getWeather() {
        lifecycleScope.launch {
            lifecycleScope.launch {
                mainViewModel.responseOfWeather.collect { result ->
                    when (result) {
                        is StateGeneric.Error -> binding.progressBar.visibility = View.GONE

                        is StateGeneric.Loading -> {
                            binding.apply {
                                progressBar.visibility = View.VISIBLE
                                dataOfWeatherGroup.visibility = View.INVISIBLE
                            }
                        }

                        is StateGeneric.Success -> {
                            showWeather(result.data!!)
                            weather = result.data
                            getForeCast()
                        }
                    }
                }
            }
        }
    }
}