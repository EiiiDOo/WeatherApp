package com.example.weatherapp.ui.alerts

import com.example.weatherapp.model.pojo.DateDtoForRoom

interface OnDeleteAlarm {
    fun onDeleteClick(alarm:DateDtoForRoom)
}