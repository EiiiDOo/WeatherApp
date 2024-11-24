package com.example.weatherapp.ui.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.weatherapp.R
import com.example.weatherapp.model.StateGeneric
import com.example.weatherapp.databinding.FragmentFavouriteBinding
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.model.dialogs.ConfirmationDeleteDialog
import com.example.weatherapp.model.pojo.CustomSaved
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.repo.RepoImpl
import com.example.weatherapp.ui.main.MainActivity
import com.example.weatherapp.ui.main.MainViewModel
import com.example.weatherapp.ui.main.MainViewModelFactory
import kotlinx.coroutines.launch

class FavouriteFragment : Fragment(), FavListner,OnDeleteClickListener {
    lateinit var mainViewModel: MainViewModel

    private lateinit var binding: FragmentFavouriteBinding

    val adapter = FavAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showAppBar(true)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel =
            ViewModelProvider(
                requireActivity(),
                MainViewModelFactory(
                    RepoImpl.getInstance(
                        RemoteDataSourceImpl,
                        LocalDataSourceImpl(requireContext())
                    )
                )
            )[MainViewModel::class.java]
        mainViewModel.getFavouriteWeatherData()
        lifecycleScope.launch {
            mainViewModel.allFavourite.collect {
                when (it) {
                    is StateGeneric.Error -> {
                        binding.progressBar2.visibility = View.GONE
                    }

                    StateGeneric.Loading -> {
                        binding.apply {
                            progressBar2.visibility = View.VISIBLE
                            dataGroup.visibility = View.INVISIBLE
                        }
                    }

                    is StateGeneric.Success -> {
                        adapter.submitList(it.data)
                        binding.apply {
                            binding.rvFav.adapter = adapter
                            progressBar2.visibility = View.GONE
                            dataGroup.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        binding.floatingActionButton.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_nav_fav_to_mapsFragment)

        }


    }

    override fun onDeleteClick(position: CustomSaved) {
        ConfirmationDeleteDialog(this,position).show(
            childFragmentManager,
            "dialog"
        )
    }

    override fun onDetailsClick(position: CustomSaved) {
        mainViewModel.emitDetails(position)

        Navigation.findNavController(requireView())
            .navigate(R.id.action_nav_fav_to_detailsFragment)

    }

    override fun onDeleteClickDialog(position: CustomSaved) {
        lifecycleScope.launch {
            val res = mainViewModel.deleteWeatherData(position)
        }
    }


}