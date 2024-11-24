package com.example.weatherapp.ui.map

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.weatherapp.R
import com.example.weatherapp.model.StateGeneric
import com.example.weatherapp.databinding.FragmentMapsBinding
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.model.dialogs.ConfirmationSaveDialog
import com.example.weatherapp.model.pojo.OsmResponseItem
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.repo.RepoImpl
import com.example.weatherapp.model.NetworkUtils
import com.example.weatherapp.ui.main.MainActivity
import com.example.weatherapp.ui.main.MainActivity.Companion.CELSIUS
import com.example.weatherapp.ui.main.MainActivity.Companion.ENGLISH
import com.example.weatherapp.ui.main.MainActivity.Companion.FIRST_TIME
import com.example.weatherapp.ui.main.MainActivity.Companion.IS_HOME_SAVED_BEFORE
import com.example.weatherapp.ui.main.MainActivity.Companion.LANGUAGE
import com.example.weatherapp.ui.main.MainActivity.Companion.METER_SEC
import com.example.weatherapp.ui.main.MainActivity.Companion.NOTIFICATION
import com.example.weatherapp.ui.main.MainActivity.Companion.NOTIFICATION_OFF
import com.example.weatherapp.ui.main.MainActivity.Companion.TEMPERATURE
import com.example.weatherapp.ui.main.MainActivity.Companion.WIND_SPEED
import com.example.weatherapp.ui.main.MainViewModel
import com.example.weatherapp.ui.main.MainViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class MapsFragment : Fragment(), OnSaveClickListner {
    lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    var mapFragment: SupportMapFragment? = null
    var title = "Marker in Sydney"
    lateinit var sp: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var lang: String
    val sharedFlow = MutableSharedFlow<String>()

    companion object {
        private const val TAG = "MapsFragment"
    }

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
    lateinit var binding: FragmentMapsBinding
    var lat: Double = -34.0
    var lon: Double = 151.0
    private val callback = OnMapReadyCallback { googleMap ->
        lifecycleScope.launch {
            mainViewModel.currentLonLat.collect {
                googleMap.clear()
                lat = it["lat"]!!.toDouble()
                lon = it["lon"]!!.toDouble()
                val sydney = LatLng(lat, lon)
                googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f))
                googleMap.setOnMapClickListener { latLang ->

                    // When clicked on map
                    // Initialize marker options
                    val markerOptions = MarkerOptions()
                    // Set position of marker
                    markerOptions.position(latLang)
                    // Set title of marker
                    lat = latLang.latitude
                    lon = latLang.longitude
                    markerOptions.title("${lat}  ${lon}")
                    // Remove all marker
                    googleMap.clear()
                    // Animating to zoom the marker
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLang, 14f))
                    // Add marker on map
                    googleMap.addMarker(markerOptions)
                    ConfirmationSaveDialog(this@MapsFragment).show(childFragmentManager, null)
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Initialize SupportMapFragment
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        //Initialize ViewModel

        mapFragment?.getMapAsync(callback)

        sp = requireActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCE_NAME, 0)
        editor = sp.edit()

        lang = sp.getString(MainActivity.LANGUAGE, MainActivity.ENGLISH)!!

        binding.editTextText.setOnQueryTextListener(object :
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                ConfirmationSaveDialog(this@MapsFragment).show(childFragmentManager, null)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    binding.chipGroup.removeAllViews()
                    lifecycleScope.launch {
                        sharedFlow.emit(newText)
                    }
                }
                return true
            }
        })

        lifecycleScope.launch {
            sharedFlow.collect {
                if (NetworkUtils.isNetworkAvailable(requireContext())) {
                    mainViewModel.search(it).collect { stateGeneric ->
                        when (stateGeneric) {
                            is StateGeneric.Success -> {
                                for (res in stateGeneric.data)
                                    addChip(res)
                            }

                            is StateGeneric.Error -> {}
                            is StateGeneric.Loading -> {}
                        }
                    }
                } else
                    Snackbar.make(
                        requireView(),
                        getString(R.string.no_internet),
                        Snackbar.LENGTH_LONG
                    ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showAppBar(false)
    }

    override fun onSaveClick() {
        val des = Navigation.findNavController(requireView()).previousBackStackEntry
        if (des != null)
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                if (des.destination.label.toString().equals(getString(R.string.fragment_splash))) {
                    Log.d(TAG, "onSaveClick: true ${des.destination.label.toString()}")
                    mainViewModel.fitchAndSave(lat, lon, true, false, lang)
                    editor.apply {
                        putBoolean(IS_HOME_SAVED_BEFORE, true)
                        putBoolean(FIRST_TIME, false)
                        putString(LANGUAGE, ENGLISH)
                        putString(WIND_SPEED, METER_SEC)
                        putString(TEMPERATURE, CELSIUS)
                        putString(NOTIFICATION, NOTIFICATION_OFF)
                    }
                    editor.apply()
                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_mapsFragment_to_nav_home)
                } else if ((des.destination.label.toString()
                        .equals(getString(R.string.menu_home)))
                ) {
                    mainViewModel.fitchAndSave(lat, lon, true, false, lang)
                    Navigation.findNavController(requireView()).popBackStack()

                } else {
                    mainViewModel.fitchAndSave(lat, lon, false, true, lang)
                    Navigation.findNavController(requireView()).popBackStack()
                }
            } else
                Snackbar.make(requireView(), getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                    .show()

    }

    fun setChip(grupp: ChipGroup, title: String, lat: Double, lon: Double): Chip {
        return Chip(requireContext()).apply {
            text = getNameOfCity(title)
            setOnClickListener {
                mainViewModel.changeCurrent(
                    lat,
                    lon
                )
                this@MapsFragment.title = title
                binding.chipGroup.removeView(this@apply)
            }
        }
    }

    fun getNameOfCity(str: String): String {
        str.split(",").also {
            if (it.size >= 2)
                return it[0] + "," + it[1]
            else
                return it[0]
        }
    }

    fun addChip(response: OsmResponseItem) {
        if (response.addresstype == "city") {
            binding.chipGroup.apply {
                addView(
                    setChip(
                        this,
                        response.display_name,
                        response.lat.toDouble(),
                        response.lon.toDouble()
                    )
                )
            }
        }
    }

}