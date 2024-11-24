package com.example.weatherapp.model.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.weatherapp.R
import com.example.weatherapp.databinding.AcceptionDialogBinding
import com.example.weatherapp.ui.map.OnSaveClickListner

class ConfirmationSaveDialog(val listner : OnSaveClickListner):DialogFragment() {
    lateinit var binding: AcceptionDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AcceptionDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (dialog != null && (dialog?.window != null)) {
            dialog?.window?.setBackgroundDrawableResource(R.drawable.shape)
        }
        binding.apply {
            ok.setOnClickListener{
                listner.onSaveClick()
                dismiss()
            }
            canc.setOnClickListener{
                dismiss()
            }
        }
    }
}