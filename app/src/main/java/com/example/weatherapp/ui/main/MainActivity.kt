package com.example.weatherapp.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.repo.RepoImpl
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    lateinit var appBar: Toolbar
    val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(
            this,
            MainViewModelFactory(
                RepoImpl.getInstance(
                    RemoteDataSourceImpl,
                    LocalDataSourceImpl(this)
                )
            )
        )[MainViewModel::class.java]
    }
    companion object{
        const val SHARED_PREFERENCE_NAME = "MyPref"
        const val IS_HOME_SAVED_BEFORE = "fromDatabase"//
        const val FIRST_TIME = "firstTime"//
        const val LANGUAGE = "language"//
        const val ARABIC = "ar"
        const val ENGLISH = "en"
        const val LOCATION = "location"//
        const val GPS = "gps"
        const val MAP = "map"
        const val WIND_SPEED = "windSpeed"//
        const val METER_SEC = "ms"
        const val MILE_HOUR = "mh"
        const val NOTIFICATION = "notification"//
        const val NOTIFICATION_ON = "on"
        const val NOTIFICATION_OFF = "off"
        const val TEMPERATURE = "temperature"//
        const val CELSIUS = "celsius"
        const val FAHRENHEIT = "fahrenheit"
        const val KELVIN = "kelvin"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        appBar = binding.appBarMain.toolbar
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            supportActionBar?.title = destination.label
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_fav, R.id.nav_alerts, R.id.nav_settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    fun showAppBar(flag: Boolean) {
        if (flag)
            appBar.visibility = View.VISIBLE
        else
            appBar.visibility = View.GONE
    }


}