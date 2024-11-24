package com.example.weatherapp.model.dialogs

import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.weatherapp.R
import com.example.weatherapp.ui.alerts.OnAlarmReady
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker

class MaterialPicker(private val manager: FragmentManager, private val listener: OnAlarmReady) {
    val calendar = Calendar.getInstance()

    fun showDatePicker() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        picker.apply {
            show(manager, null)
            addOnPositiveButtonClickListener {
                calendar.timeInMillis = it
                Toast.makeText(requireContext(), "$it", Toast.LENGTH_SHORT).show()
                listener.onAlarmReady(calendar)
            }
        }

    }

    fun showTimePicker(format: Int) {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(format)
            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(Calendar.MINUTE))
            .setTitleText(String.format("Select Alarm Time"))
            .build()
        picker.apply {
            show(manager, null)
            addOnPositiveButtonClickListener {
                calendar.apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }
//                showDatePicker()
                listener.onAlarmReady(calendar)
                Toast.makeText(requireContext(), "$hour:$minute", Toast.LENGTH_SHORT).show()
            }
        }
    }
}