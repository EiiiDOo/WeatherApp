package com.example.weatherapp.ui.home

import HourlyAdapter
import android.annotation.SuppressLint
import android.content.Intent
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
import com.example.weatherapp.model.Item0
import com.example.weatherapp.model.Weather
import com.example.weatherapp.remote.RemoteDataSourceImpl
import com.example.weatherapp.repo.RepoImpl
import com.example.weatherapp.toDateTime
import com.example.weatherapp.toDrawable2X
import com.example.weatherapp.ui.main.MainViewModel
import com.example.weatherapp.ui.main.MainViewModelFactory
import com.google.android.gms.location.*
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    val REQUEST_CODE = 1
    lateinit var binding: FragmentHomeBinding
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var mainViewModel: MainViewModel
    private val adapterHourly = HourlyAdapter(emptyList())
    private val adapterDays = Forcastadapter(emptyList())
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
            MainViewModelFactory(RepoImpl.getInstance(RemoteDataSourceImpl))
        ).get(MainViewModel::class.java)

        mainViewModel.getWeatherByLongitudeAndLatitude(31.2001, 29.9187)
        mainViewModel.getForecastByLongitudeAndLatitude(31.2001, 29.9187)
        lifecycleScope.launch {
            mainViewModel.responseOfWeather.collect { res ->
                when (res) {
                    is StateGeneric.Error -> {

                    }

                    is StateGeneric.Loading -> {

                    }

                    is StateGeneric.Success -> {
                        binding.apply {
                            res.data?.let {
                                txtCity.text = it.name
                                txtTemp.text = it.main.temp.toString()
                                txtDate.text = it.dt.convertUnixToDay("EEEE, dd-MM-yyyy, hh:mm a")
                                txtSkyState.text = it.weather[0].description
                                imageView4.setImageResource(it.weather[0].icon.toDrawable2X())
                                txtValueOfVisibility.text = it.visibility.toString()
                                txtValueOfCloud.text = it.clouds.all.toString()
                                txtValueofHumidity.text = it.main.humidity.toString()
                                txtValueOfPressure.text = it.main.pressure.toString()
                                txtValueOfWind.text = it.wind.speed.toString()
                            }
                        }
                    }
                }

            }
        }
        lifecycleScope.launch {
            mainViewModel.responseOfForecast.collect { res ->
                when (res) {
                    is StateGeneric.Error -> {}

                    is StateGeneric.Loading -> {}

                    is StateGeneric.Success -> {
                        binding.apply {
                            res.data?.let {
                                adapterHourly.updateList(it.list.take(8))
                                rvByHour.adapter = adapterHourly
                                adapterDays.updateList(getFiveDay(it.list))
                                recyclerView.adapter = adapterDays
                            }
                        }
                    }
                }
            }
        }



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

    fun getFiveDay(list : List<Item0>): List<Item0>{
        return list.filterIndexed{index,_ -> ( index % 8 == 0)}
    }
}