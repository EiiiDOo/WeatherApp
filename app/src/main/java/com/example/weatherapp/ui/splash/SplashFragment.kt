package com.example.weatherapp.ui.splash

import android.content.SharedPreferences
import android.content.res.Configuration
import com.example.weatherapp.ui.main.MainActivity.Companion.LOCATION
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.weatherapp.model.dialogs.DialogCustom
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentSplashBinding
import com.example.weatherapp.ui.main.MainActivity
import com.example.weatherapp.ui.main.MainActivity.Companion.CELSIUS
import com.example.weatherapp.ui.main.MainActivity.Companion.ENGLISH
import com.example.weatherapp.ui.main.MainActivity.Companion.FIRST_TIME
import com.example.weatherapp.ui.main.MainActivity.Companion.GPS
import com.example.weatherapp.ui.main.MainActivity.Companion.IS_HOME_SAVED_BEFORE
import com.example.weatherapp.ui.main.MainActivity.Companion.LANGUAGE
import com.example.weatherapp.ui.main.MainActivity.Companion.MAP
import com.example.weatherapp.ui.main.MainActivity.Companion.METER_SEC
import com.example.weatherapp.ui.main.MainActivity.Companion.NOTIFICATION
import com.example.weatherapp.ui.main.MainActivity.Companion.NOTIFICATION_OFF
import com.example.weatherapp.ui.main.MainActivity.Companion.SHARED_PREFERENCE_NAME
import com.example.weatherapp.ui.main.MainActivity.Companion.TEMPERATURE
import com.example.weatherapp.ui.main.MainActivity.Companion.WIND_SPEED
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class SplashFragment : Fragment(), OnOkClickListner {
    lateinit var binding: FragmentSplashBinding
    lateinit var sp: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sp = requireActivity().getSharedPreferences(SHARED_PREFERENCE_NAME, 0)
        editor = sp.edit()
        val lang = sp.getString(LANGUAGE, ENGLISH)!!

        lifecycleScope.launch {
            //TODO("check here if the user is already run the app one time at least")
            delay(2000)
            val flag = sp.getBoolean(FIRST_TIME, true)
            if (flag)
                DialogCustom(this@SplashFragment).apply { isCancelable = false }
                    .show(childFragmentManager, "")
            else
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_splashFragment_to_nav_home)


        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showAppBar(false)

    }

    override fun onOkClick(str: String) {
        when (str) {
            "gps" -> {
                editor.apply {
                    putString(LOCATION, GPS)
                    putBoolean(IS_HOME_SAVED_BEFORE, false)
                    putBoolean(FIRST_TIME, false)
                    putString(LANGUAGE, ENGLISH)
                    putString(WIND_SPEED, METER_SEC)
                    putString(TEMPERATURE, CELSIUS)
                    putString(NOTIFICATION, NOTIFICATION_OFF)
                }
                editor.apply()

                Navigation.findNavController(requireView())
                    .navigate(R.id.action_splashFragment_to_nav_home)
            }

            "map" -> {
                editor.putString(LOCATION, MAP)
                editor.apply()
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_splashFragment_to_mapsFragment)

            }

            "default" -> {
                editor.apply {
                    putString(LOCATION, GPS)
                    putBoolean(IS_HOME_SAVED_BEFORE, true)
                    putBoolean(FIRST_TIME, false)
                    putString(LANGUAGE, ENGLISH)
                    putString(WIND_SPEED, METER_SEC)
                    putString(TEMPERATURE, CELSIUS)
                    putString(NOTIFICATION, NOTIFICATION_OFF)
                    apply()
                }
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_splashFragment_to_nav_home)
            }
        }


    }

    fun changLanguage(code: String) {
        val local = Locale(code)
        Locale.setDefault(local)
        val config = Configuration()
        config.setLocale(local)
        resources.updateConfiguration(config, resources.displayMetrics)
        requireActivity().recreate()
    }
}