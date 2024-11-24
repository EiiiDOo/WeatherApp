package com.example.weatherapp.ui.settings

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.databinding.FragmentSettingsBinding
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.repo.RepoImpl
import com.example.weatherapp.ui.main.MainActivity
import com.example.weatherapp.ui.main.MainViewModel
import com.example.weatherapp.ui.main.MainViewModelFactory
import java.util.Locale


class SettingsFragment : Fragment() {
    companion object{
        const val LOCATION_PERMISSION_REQUEST_CODE = 0
    }

    private lateinit var binding: FragmentSettingsBinding
    lateinit var sp: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var loc: String
    lateinit var lang: String
    lateinit var speedUnit: String
    lateinit var tempUnit: String
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sp = requireActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCE_NAME, 0)
        editor = sp.edit()
        sp.apply {
            loc = getString(MainActivity.LOCATION, MainActivity.GPS)!!
            lang = getString(MainActivity.LANGUAGE, MainActivity.ENGLISH)!!
            speedUnit = getString(MainActivity.WIND_SPEED, MainActivity.METER_SEC)!!
            tempUnit = getString(MainActivity.TEMPERATURE, MainActivity.CELSIUS)!!
        }
        binding.apply {
            setLocation()
            setLanguage()
            setSpeedUnit()
            setTempUnit()
            rdArabic.setOnClickListener {
                editor.putString(MainActivity.LANGUAGE, MainActivity.ARABIC)
                editor.apply()
                changLanguage("ar")
            }
            rdEnglish.setOnClickListener {
                editor.putString(MainActivity.LANGUAGE, MainActivity.ENGLISH)
                editor.apply()
                changLanguage("en")
            }
            rdGps.setOnClickListener {
                if(checkLocationPermissions() && isLocationEnabled()) {
                    editor.putString(MainActivity.LOCATION, MainActivity.GPS)
                    editor.apply()
                }else {
                    rdMap.isChecked = true
                    requestLocationPermission()
                    if (!isLocationEnabled())
                        enableLocationServicess()
                }
                ///////////////////
            }
            rdMap.setOnClickListener {
                editor.putString(MainActivity.LOCATION, MainActivity.MAP)
                editor.apply()
            }
            rdC.setOnClickListener {
                editor.putString(MainActivity.TEMPERATURE, MainActivity.CELSIUS)
                editor.apply()
            }
            rdF.setOnClickListener {
                editor.putString(MainActivity.TEMPERATURE, MainActivity.FAHRENHEIT)
                editor.apply()
            }
            rdK.setOnClickListener {
                editor.putString(MainActivity.TEMPERATURE, MainActivity.KELVIN)
                editor.apply()
            }
            rdMS.setOnClickListener {
                editor.putString(MainActivity.WIND_SPEED, MainActivity.METER_SEC)
                editor.apply()
            }
            MH.setOnClickListener {
                editor.putString(MainActivity.WIND_SPEED, MainActivity.MILE_HOUR)
                editor.apply()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showAppBar(true)

    }

    fun changLanguage(code: String) {
        val local = Locale(code)
        Locale.setDefault(local)
        val config = Configuration()
        config.setLocale(local)
        resources.updateConfiguration(config, resources.displayMetrics)
        requireActivity().recreate()
    }

    fun setLocation() {
        binding.apply {
            when (loc) {
                MainActivity.GPS -> {
                    rdGps.isChecked = true
                }
                MainActivity.MAP -> {
                    rdMap.isChecked = true
                }
            }
        }
    }

    fun setLanguage() {
        binding.apply {
            when (lang) {
                MainActivity.ENGLISH -> {
                    rdEnglish.isChecked = true
                }

                MainActivity.ARABIC -> {
                    rdArabic.isChecked = true
                }
            }
        }
    }

    fun setSpeedUnit() {
        binding.apply {
            when (speedUnit) {
                MainActivity.METER_SEC -> {
                    rdMS.isChecked = true
                }

                MainActivity.MILE_HOUR -> {
                    MH.isChecked = true
                }
            }
        }
    }

    fun setTempUnit() {
        binding.apply {
            when (tempUnit) {
                MainActivity.CELSIUS -> {
                    rdC.isChecked = true
                }

                MainActivity.FAHRENHEIT -> {
                    rdF.isChecked = true
                }

                MainActivity.KELVIN -> {
                    rdK.isChecked = true
                }
            }
        }
    }
    fun checkLocationPermissions(): Boolean {
        return requireActivity().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                return requireActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermission()
    {
        requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE)
    }
    fun isLocationEnabled(): Boolean {
        val locationManager =
            requireActivity().getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    fun enableLocationServicess() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

}