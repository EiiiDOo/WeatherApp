package com.example.weatherapp.ui.alerts

import android.icu.util.Calendar


interface OnAlarmReady {
    fun onAlarmReady(calendar: Calendar)
}