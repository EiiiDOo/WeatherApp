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
import androidx.navigation.Navigation
import com.example.weatherapp.model.StateGeneric
import com.example.weatherapp.model.convertUnixToDay
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.model.pojo.CustomSaved
import com.example.weatherapp.model.pojo.Item0
import com.example.weatherapp.model.pojo.WeatherData
import com.example.weatherapp.model.pojo.WeatherForecastFiveDays
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.repo.RepoImpl
import com.example.weatherapp.model.toDateTime
import com.example.weatherapp.model.toDrawable
import com.example.weatherapp.model.toDrawable2X
import com.example.weatherapp.ui.main.MainActivity
import com.example.weatherapp.ui.main.MainViewModel
import com.example.weatherapp.ui.main.MainViewModelFactory
import com.google.android.gms.location.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.weatherapp.R
import com.example.weatherapp.main
import com.example.weatherapp.model.NetworkUtils
import com.example.weatherapp.model.toFahrenheit
import com.example.weatherapp.model.toKelvin
import com.example.weatherapp.model.toMilesPerHour
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {
    companion object {
        const val TAG = "HOME FRAGMENT"
    }

    private var isToolbarVisible = true
    lateinit var tempUnit: String
    lateinit var windUnit: String
    lateinit var lang: String
    lateinit var location: String
    lateinit var binding: FragmentHomeBinding
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            MainViewModelFactory(
                RepoImpl.getInstance(
                    RemoteDataSourceImpl,
                    LocalDataSourceImpl(requireContext())
                )
            )
        )[MainViewModel::class.java]
    }
    private lateinit var adapterHourly: HourlyAdapter
    lateinit var sharedPreferences: SharedPreferences
    lateinit var customSaved: CustomSaved
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

        sharedPreferences = requireActivity().getSharedPreferences(
            MainActivity.SHARED_PREFERENCE_NAME,
            MODE_PRIVATE
        )
        tempUnit =
            sharedPreferences.getString(MainActivity.TEMPERATURE, MainActivity.CELSIUS).toString()
        windUnit =
            sharedPreferences.getString(MainActivity.WIND_SPEED, MainActivity.METER_SEC).toString()
        lang = sharedPreferences.getString(MainActivity.LANGUAGE, MainActivity.ENGLISH)!!
        location = sharedPreferences.getString(MainActivity.LOCATION, MainActivity.GPS).toString()
        binding.swipeRefresh.setOnRefreshListener {
            lifecycleScope.launch {
                Log.d(TAG, "onViewCreated: ${location}")
                if (NetworkUtils.isNetworkAvailable(requireContext())) {
                    when (location) {
                        MainActivity.GPS -> {
                            getFreshLocation()
                        }

                        else -> {
                            Navigation.findNavController(requireView())
                                .navigate(R.id.action_nav_home_to_mapsFragment)
                        }
                    }
                } else
                    Snackbar.make(
                        binding.root,
                        "No internet connection",
                        Snackbar.LENGTH_SHORT
                    ).show()
                delay(1500)
                binding.swipeRefresh.isRefreshing = false
            }

        }
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            if (location == MainActivity.GPS)
                getFreshLocation()
        } else
            Snackbar.make(binding.root, "No internet connection", Snackbar.LENGTH_SHORT).show()

        mainViewModel.getHomeWeatherData()
        lifecycleScope.launch {
            mainViewModel.homeData.collect {
                when (it) {
                    is StateGeneric.Error -> {
                        Log.d(TAG, "onViewCreated: error")
                    }

                    is StateGeneric.Loading -> {
                        Log.d(TAG, "onViewCreated: loading")
                    }

                    is StateGeneric.Success -> {
                        Log.d(TAG, "onViewCreated: success${it.data}")
                        customSaved = it.data
                        showWeatherFromDatabase(it.data)
                        showForecast(it.data.list)
                    }
                }

            }
        }
        val appBar = (requireActivity() as MainActivity).appBar
        val scrollView = binding.homeScrollView
        var isAtEdge = false
        var cumulativeScroll = 0
        binding.homeScrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            val maxScrollY = scrollView.getChildAt(0).measuredHeight - scrollView.measuredHeight

            // Handle scrolling to the top
            if (scrollY <= 0 && !isAtEdge) {
                appBar.visibility = View.VISIBLE // Show the toolbar
                isAtEdge = true
                cumulativeScroll = 0
            }
            // Handle scrolling to the bottom
            else if (scrollY >= maxScrollY && !isAtEdge) {
                appBar.visibility = View.GONE // Optionally hide the toolbar
                isAtEdge = true
                cumulativeScroll = 0
            }
            // Handle normal scrolling
            else{
                val scrollDelta =scrollY - oldScrollY
                cumulativeScroll += scrollDelta
                if(cumulativeScroll > 30){
                    appBar.visibility = View.GONE
                    cumulativeScroll = 0
                    isAtEdge = false
                }else if(cumulativeScroll < -30){
                    appBar.visibility = View.VISIBLE
                    cumulativeScroll = 0
                    isAtEdge = false
                    val x =0
                }

            }
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showAppBar(true)

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
                    fusedLocationProviderClient.removeLocationUpdates(this)
                    val lang =
                        sharedPreferences.getString(MainActivity.LANGUAGE, MainActivity.ENGLISH)!!
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Geocoder(requireActivity()).getFromLocation(
                            locationResult.locations[0].latitude,
                            locationResult.locations[0].longitude,
                            1
                        )
                        {
                            locationResult.lastLocation?.let {
                                it.longitude
                                it.latitude
                            }
                            mainViewModel.fitchAndSave(
                                it.firstOrNull()?.latitude ?: 0.0,
                                it.firstOrNull()?.longitude ?: 0.0,
                                true, false, lang
                            )
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
                            mainViewModel.fitchAndSave(
                                geo?.get(0)?.latitude ?: 0.0,
                                geo?.get(0)?.longitude ?: 0.0,
                                true,
                                false,
                                lang
                            )
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


    fun getFiveDay(list: List<Item0>): List<Item0> {
        return list.filterIndexed { index, _ -> (index % 8 == 0) }
    }


    fun showWeather(weather: WeatherData) {
        binding.apply {
            weather.apply {
                txtCity.text = this.name
                txtDate.text = this.dt.convertUnixToDay("EEEE, dd-MM-yyyy, hh:mm a")
                txtSkyState.text = this.weather[0].description
                imageView4.setImageResource(this.weather[0].icon.toDrawable2X())
                txtValueOfVisibility.text = this.visibility.toString()
                txtValueOfCloud.text = this.clouds.all.toString()
                txtValueofHumidity.text = this.main.humidity.toString()
                txtValueOfPressure.text = this.main.pressure.toString()
                when (tempUnit) {
                    MainActivity.CELSIUS -> {
                        txtTemp.text = this.main.temp.toString()
                        txtTempUnit.text = getString(R.string.c)
                    }

                    MainActivity.FAHRENHEIT -> {
                        txtTemp.text = this.main.temp.toFahrenheit().toString()
                        txtTempUnit.text = getString(R.string.f)
                    }

                    MainActivity.KELVIN -> {
                        txtTemp.text = this.main.temp.toKelvin().toString()
                        txtTempUnit.text = getString(R.string.k)
                    }
                }
                when (windUnit) {
                    MainActivity.METER_SEC -> {
                        txtValueOfWind.text = this.wind.speed.toString()
                        txtWindUnit.text = getString(R.string.ms)
                    }

                    MainActivity.MILE_HOUR -> {
                        txtValueOfWind.text = this.wind.speed.toMilesPerHour().toString()
                        txtWindUnit.text = getString(R.string.mh)
                    }

                }
                progressBar.visibility = View.GONE
                dataOfWeatherGroup.visibility = View.VISIBLE
            }
        }
    }

    fun showForecast(forecast: List<Item0>) {
        binding.apply {
            forecast.apply {
                adapterHourly = HourlyAdapter(emptyList(), tempUnit)
                adapterHourly.updateList(this.take(8))
                rvByHour.adapter = adapterHourly
                val list = getFiveDay(this)
                var firstMin = list[0].main.temp_min
                var firstMax = list[0].main.temp_max
                var secondMin = list[1].main.temp_min
                var secondMax = list[1].main.temp_max
                var thirdMin = list[2].main.temp_min
                var thirdMax = list[2].main.temp_max
                var fourthMin = list[3].main.temp_min
                var fourthMax = list[3].main.temp_max
                var fifthMin = list[4].main.temp_min
                var fifthMax = list[4].main.temp_max
                when (tempUnit) {
                    MainActivity.CELSIUS -> {
                        txtTemUnit1.text = getString(R.string.c)
                        txtTemUnit2.text = getString(R.string.c)
                        txtTemUnit3.text = getString(R.string.c)
                        txtTemUnit4.text = getString(R.string.c)
                        txtTemUnit5.text = getString(R.string.c)
                    }

                    MainActivity.FAHRENHEIT -> {
                        firstMin = firstMin.toFahrenheit()
                        firstMax = firstMax.toFahrenheit()
                        secondMin = secondMin.toFahrenheit()
                        secondMax = secondMax.toFahrenheit()
                        thirdMin = thirdMin.toFahrenheit()
                        thirdMax = thirdMax.toFahrenheit()
                        fourthMin = fourthMin.toFahrenheit()
                        fourthMax = fourthMax.toFahrenheit()
                        fifthMin = fifthMin.toFahrenheit()
                        fifthMax = fifthMax.toFahrenheit()
                        txtTemUnit1.text = getString(R.string.f)
                        txtTemUnit2.text = getString(R.string.f)
                        txtTemUnit3.text = getString(R.string.f)
                        txtTemUnit4.text = getString(R.string.f)
                        txtTemUnit5.text = getString(R.string.f)
                    }

                    MainActivity.KELVIN -> {
                        firstMin = firstMin.toKelvin()
                        firstMax = firstMax.toKelvin()
                        secondMin = secondMin.toKelvin()
                        secondMax = secondMax.toKelvin()
                        thirdMin = thirdMin.toKelvin()
                        thirdMax = thirdMax.toKelvin()
                        fourthMin = fourthMin.toKelvin()
                        fourthMax = fourthMax.toKelvin()
                        fifthMin = fifthMin.toKelvin()
                        fifthMax = fifthMax.toKelvin()
                        txtTemUnit1.text = getString(R.string.k)
                        txtTemUnit2.text = getString(R.string.k)
                        txtTemUnit3.text = getString(R.string.k)
                        txtTemUnit4.text = getString(R.string.k)
                        txtTemUnit5.text = getString(R.string.k)
                    }
                }

                //today
                "${firstMin}-${firstMax}".also {
                    txtTempToday.text = it
                }
                imgIconToday.setImageResource(list[0].weather[0].icon.toDrawable())
                txtDesSkyToday.text = list[0].weather[0].description
                //day2
                txtDay2.text = list[1].dt.toDateTime("EEEE")
                "${secondMin}-${secondMax}".also {
                    txtTemp2.text = it
                }
                imgIcon2.setImageResource(list[1].weather[0].icon.toDrawable())
                txtDesSky2.text = list[1].weather[0].description
                //day3
                txtDay3.text = list[2].dt.toDateTime("EEEE")
                "${thirdMin}-${thirdMax}".also {
                    txtTemp3.text = it
                }
                imgIcon3.setImageResource(list[2].weather[0].icon.toDrawable())
                txtDesSky3.text = list[2].weather[0].description
                //day4
                txtDay4.text = list[3].dt.toDateTime("EEEE")
                "${fourthMin}-${fourthMax}".also {
                    txtTemp4.text = it
                }
                imgIcon4.setImageResource(list[3].weather[0].icon.toDrawable())
                txtDesSky4.text = list[3].weather[0].description
                //day5
                txtDay5.text = list[4].dt.toDateTime("EEEE")
                "${fifthMin}-${fifthMax}".also {
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
                txtDate.text = this.date.convertUnixToDay("EEEE, dd-MM-yyyy, hh:mm a")
                txtSkyState.text = this.skyStateDescription
                imageView4.setImageResource(this.iconId.toDrawable2X())
                txtValueOfVisibility.text = this.visibility.toString()
                when (tempUnit) {
                    MainActivity.CELSIUS -> {
                        txtTemp.text = this.temp.toString()
                        txtTempUnit.text = getString(R.string.c)
                    }

                    MainActivity.FAHRENHEIT -> {
                        txtTemp.text = this.temp.toFahrenheit().toString()
                        txtTempUnit.text = getString(R.string.f)
                    }

                    MainActivity.KELVIN -> {
                        txtTemp.text = this.temp.toKelvin().toString()
                        txtTempUnit.text = getString(R.string.k)
                    }
                }
                when (windUnit) {
                    MainActivity.METER_SEC -> {
                        txtValueOfWind.text = this.windSpeed.toString()
                        txtWindUnit.text = getString(R.string.ms)
                    }

                    MainActivity.MILE_HOUR -> {
                        txtValueOfWind.text = this.windSpeed.toMilesPerHour().toString()
                        txtWindUnit.text = getString(R.string.mh)
                    }

                }
                txtValueOfCloud.text = this.clouds.toString()
                txtValueofHumidity.text = this.humidity.toString()
                txtValueOfPressure.text = this.pressure.toString()
                progressBar.visibility = View.GONE
                dataOfWeatherGroup.visibility = View.VISIBLE
            }
        }
    }


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