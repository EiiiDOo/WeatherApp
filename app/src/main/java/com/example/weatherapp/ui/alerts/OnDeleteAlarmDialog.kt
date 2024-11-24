package com.example.weatherapp.ui.alerts

import com.example.weatherapp.model.pojo.DateDtoForRoom

interface OnDeleteAlarmDialog {
    fun deleteClick(alarm:DateDtoForRoom)
}