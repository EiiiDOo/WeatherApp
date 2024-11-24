package com.example.weatherapp.model.dialogs

import android.app.Dialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.text.format.DateFormat
import android.os.Bundle
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.weatherapp.R
import com.example.weatherapp.ui.alerts.OnAlarmReady

class TimePicker(private var selectedDate: Calendar, private val listener: OnAlarmReady) : DialogFragment(),
    TimePickerDialog.OnTimeSetListener {
    private val calendar = Calendar.getInstance()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return TimePickerDialog(
            requireActivity(),
            this,
            hour,
            minute,
            DateFormat.is24HourFormat(requireActivity())
        )
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        selectedDate.apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 59)
            Toast.makeText(
                requireActivity(),
                "${hourOfDay}:${minute}",
                Toast.LENGTH_LONG
            ).show()
        }
        listener.onAlarmReady(selectedDate)
    }
}