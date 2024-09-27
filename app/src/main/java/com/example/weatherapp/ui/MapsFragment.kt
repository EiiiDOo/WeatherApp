package com.example.weatherapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip

class MapsFragment : Fragment() {
    lateinit var binding: FragmentMapsBinding
    val list: List<String> by lazy {
        val list = mutableListOf<String>()
        repeat(40) {
            list.add(it.toString())
        }
        list
    }

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        googleMap.setOnMapClickListener {
            // When clicked on map
            // Initialize marker options
            val markerOptions = MarkerOptions()
            // Set position of marker
            markerOptions.position(it)
            // Set title of marker
            markerOptions.title("${it.latitude}  ${it.longitude}")
            // Remove all marker
            googleMap.clear()
            // Animating to zoom the marker
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 10f))
            // Add marker on map
            googleMap.addMarker(markerOptions)
        }
        binding.editTextText.setOnQueryTextFocusChangeListener { text,_ ->
            binding.chipGroup.removeAllViews()
            val list = list.filter {it.contains(text.toString()) }.toList()
            for (i in list) {
                binding.chipGroup.apply {
                    val chip: Chip = Chip(this.context)
                    chip.text = i
                    addView(chip)
                    chip.setOnClickListener {
                        binding.editTextText.setQuery(chip.text.toString(), false)
                    }
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
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}