package com.example.weatherapp


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.weatherapp.databinding.InitialdialogBinding
import com.example.weatherapp.ui.OnOkClickListner
import com.google.android.material.snackbar.Snackbar

class DialogCustom() : DialogFragment() {
    lateinit var binding: InitialdialogBinding
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
        binding.radioButtonGps.setOnClickListener {
            binding.radioButtonMap.isChecked = false
        }
        binding.radioButtonMap.setOnClickListener {
            binding.radioButtonGps.isChecked = false
        }
        binding.button.setOnClickListener {
            if (counter>=0){
                if (binding.radioButtonGps.isChecked || binding.radioButtonMap.isChecked){
                    listner.onOkClick()
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
                listner.onOkClick()
                dismiss()
            }

        }

    }


}



