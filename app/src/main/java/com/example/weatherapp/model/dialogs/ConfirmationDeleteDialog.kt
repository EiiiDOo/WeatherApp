package com.example.weatherapp.model.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ConfirmationDialogBinding
import com.example.weatherapp.model.pojo.CustomSaved
import com.example.weatherapp.ui.favourite.OnDeleteClickListener

class ConfirmationDeleteDialog(val onOkClickListner: OnDeleteClickListener, val customSaved: CustomSaved): DialogFragment() {
    lateinit var binding : ConfirmationDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ConfirmationDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (dialog != null && (dialog?.window != null)) {
            dialog?.window?.setBackgroundDrawableResource(R.drawable.shape)
        }
        binding.apply {
            ok.setOnClickListener{
                onOkClickListner.onDeleteClickDialog(customSaved)
                dismiss()
            }
            canc.setOnClickListener{
                dismiss()
            }
        }
    }
}