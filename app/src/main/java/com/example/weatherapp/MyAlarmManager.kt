package com.example.weatherapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import com.example.weatherapp.model.ALARM_ITEM
import com.example.weatherapp.model.AlarmReceiver
import com.example.weatherapp.model.pojo.DateDTO
import com.example.weatherapp.model.toMyCalendar
import com.google.gson.Gson

class MyAlarmManager(
    private val ctx: Context
) {
    private val alarmManager: AlarmManager =
        ctx.getSystemService(AlarmManager::class.java)


    fun scheduleAlarm(alarmData: Calendar){
        val dateDTO = DateDTO(alarmData.timeInMillis.hashCode(),alarmData.toMyCalendar())
        val pendingIntent = PendingIntent.getBroadcast(
            ctx,
            alarmData.timeInMillis.hashCode(),
            Intent(ctx, AlarmReceiver::class.java).apply {
                putExtra(ALARM_ITEM,Gson().toJson(dateDTO))
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmData.timeInMillis,
            pendingIntent
        )

    }
    fun cancelAlarm(id:Int){
        val pendingIntent = PendingIntent.getBroadcast(
            ctx,
            id,
            Intent(ctx, AlarmReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }
}