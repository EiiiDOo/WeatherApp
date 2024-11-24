package com.example.weatherapp.ui.details

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.R
import com.example.weatherapp.model.convertUnixToDay
import com.example.weatherapp.databinding.FragmentDetailsBinding
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.model.pojo.CustomSaved
import com.example.weatherapp.model.pojo.Item0
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.repo.RepoImpl
import com.example.weatherapp.model.NetworkUtils
import com.example.weatherapp.model.toDateTime
import com.example.weatherapp.model.toDrawable
import com.example.weatherapp.model.toDrawable2X
import com.example.weatherapp.model.toFahrenheit
import com.example.weatherapp.model.toKelvin
import com.example.weatherapp.model.toMilesPerHour
import com.example.weatherapp.ui.home.HomeFragment.Companion.TAG
import com.example.weatherapp.ui.home.HourlyAdapter
import com.example.weatherapp.ui.main.MainActivity
import com.example.weatherapp.ui.main.MainViewModel
import com.example.weatherapp.ui.main.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetailsFragment : Fragment() {
    lateinit var binding: FragmentDetailsBinding
    lateinit var adapterHourly: HourlyAdapter
    lateinit var sp: SharedPreferences
    lateinit var lang: String
    lateinit var tempUnit: String
    lateinit var windUnit: String
    lateinit var customSaved: CustomSaved
    val mainViewModel: MainViewModel by lazy {
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sp = requireActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCE_NAME, 0)
        tempUnit = sp.getString(MainActivity.TEMPERATURE, MainActivity.CELSIUS).toString()
        windUnit = sp.getString(MainActivity.WIND_SPEED, MainActivity.METER_SEC).toString()
        lang = sp.getString(MainActivity.LANGUAGE, MainActivity.ENGLISH).toString()
        binding.swipeRefresh.setOnRefreshListener {
            if(NetworkUtils.isNetworkAvailable(requireContext())){
                lifecycleScope.launch {
                    mainViewModel.fitchAndSave(customSaved.lat, customSaved.lon, false, true, lang)
                    delay(2000)
                    binding.swipeRefresh.isRefreshing = false
                }
            }else
                Snackbar.make(binding.root, getString(R.string.no_internet), Snackbar.LENGTH_SHORT).show()

        }
        lifecycleScope.launch {
            mainViewModel.details.collect { item ->
                if (item != null) {
                    customSaved = item
                    showWeatherFromDatabase(item)
                    showForecast(item.list)
                }
            }
        }


    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showAppBar(true)
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

    fun getFiveDay(list: List<Item0>): List<Item0> {
        return list.filterIndexed { index, _ -> (index % 8 == 0) }
    }
}
