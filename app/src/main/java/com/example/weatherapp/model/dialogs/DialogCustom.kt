package com.example.weatherapp.model.dialogs


import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.weatherapp.databinding.InitialdialogBinding
import com.example.weatherapp.ui.splash.OnOkClickListner
import com.example.weatherapp.ui.main.MainActivity
import com.example.weatherapp.ui.main.MainActivity.Companion.GPS
import com.example.weatherapp.ui.main.MainActivity.Companion.LOCATION
import com.example.weatherapp.ui.settings.SettingsFragment.Companion.LOCATION_PERMISSION_REQUEST_CODE
import com.google.android.material.snackbar.Snackbar

class DialogCustom() : DialogFragment() {
    lateinit var binding: InitialdialogBinding
    lateinit var sp : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    var counter = 2
    lateinit var listner: OnOkClickListner
    constructor( listner: OnOkClickListner) : this(){
        this.listner = listner
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = InitialdialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sp = requireActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCE_NAME, 0)
        editor = sp.edit()
        binding.radioButtonGps.setOnClickListener {
            binding.radioButtonMap.isChecked = false
            if(checkLocationPermissions() && isLocationEnabled()) {
                editor.putString(LOCATION, GPS)
                editor.apply()
            }else {
                requestLocationPermission()
                if (!isLocationEnabled())
                    enableLocationServicess()
            }
        }
        binding.radioButtonMap.setOnClickListener {
            binding.radioButtonGps.isChecked = false
        }
        binding.button.setOnClickListener {
            if (counter>=0){
                if (binding.radioButtonGps.isChecked || binding.radioButtonMap.isChecked){
                    if (binding.switch1.isChecked)
                        Toast.makeText(requireContext(), "Checked", Toast.LENGTH_SHORT).show()
                    if (binding.radioButtonGps.isChecked)
                        listner.onOkClick("gps")
                    else
                        listner.onOkClick("map")
                    dismiss()
                }
                else
                    Snackbar.make(
                        binding.root,
                        "If you don't select any option, the app will be chosen Gps by default $counter times",
                        Snackbar.LENGTH_SHORT
                    ).show()
                counter--
            }else {
                listner.onOkClick("default")
                dismiss()
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
            LOCATION_PERMISSION_REQUEST_CODE
        )
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



