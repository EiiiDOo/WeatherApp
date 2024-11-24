package com.example.weatherapp.model.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.example.weatherapp.R
import com.example.weatherapp.ui.alerts.OnAlarmReady

class DatePicker(private val listener:OnAlarmReady):DialogFragment(),DatePickerDialog.OnDateSetListener {
    private val calendar = Calendar.getInstance()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendarNow = calendar
        val year = calendarNow.get(Calendar.YEAR)
        val month = calendarNow.get(Calendar.MONTH)
        val day = calendarNow.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(requireActivity(),this,year,month,day)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val dataSetCalendar = calendar
        dataSetCalendar.set(Calendar.YEAR,year)
        dataSetCalendar.set(Calendar.MONTH,month)
        dataSetCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
        TimePicker(dataSetCalendar,listener).show(requireActivity().supportFragmentManager,null)
    }
}