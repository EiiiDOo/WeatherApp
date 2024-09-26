package com.example.weatherapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.weatherapp.DialogCustom
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentSplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() ,OnOkClickListner {
    lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            //TODO("check here if the user is already run the app one time at least")
            if(true){
                Navigation.findNavController(requireView()).navigate(R.id.action_splashFragment_to_nav_home)
            }else{
                delay(3000)
                DialogCustom(this@SplashFragment).apply{ isCancelable = false }.show(childFragmentManager,"")
            }

        }
    }

    override fun onOkClick() {
        Navigation.findNavController(requireView()).navigate(R.id.action_splashFragment_to_nav_home)
    }

}