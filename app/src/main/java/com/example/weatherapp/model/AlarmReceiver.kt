package com.example.weatherapp.model

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.weatherapp.R
import com.example.weatherapp.model.pojo.DateDTO
import com.example.weatherapp.ui.main.MainActivity
import com.google.gson.Gson


class AlarmReceiver : BroadcastReceiver() {
    companion object{
        private const val TAG = "AlarmReceiver"
    }
    private lateinit var alarmItem: String
    private lateinit var alarmData: DateDTO
    override fun onReceive(context: Context?, intent: Intent?) {
        alarmItem = intent?.getStringExtra(ALARM_ITEM)!!
        alarmData = Gson().fromJson(alarmItem,DateDTO::class.java)
        showNotification2(context!!)
//        createNotificationChannel(context)
        if (Settings.canDrawOverlays(context))
            showAlarm(context)
    }

    private fun showAlarm(ctx: Context) {
        Log.d("TAG", "showAlarm: ")
        val intent = Intent(ctx, OverLayService::class.java).apply {
            putExtra(ALARM_ITEM,alarmItem)
        }
        ctx.startService(intent)
    }
    private fun showNotification(context: Context){
        Log.d(TAG, "showNotification: i am from show notification")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, "alarm_channel").setContentText("Check the weather now!")
            .setContentTitle("Alarm")
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .addAction(R.drawable.ic_alerts, "Dismiss", null)
            .build()
        notificationManager.notify(alarmData.id, notification)
    }
    private fun createNotificationChannel(context: Context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",  // Channel ID
                "Alarm Notifications", // Name
                NotificationManager.IMPORTANCE_HIGH // Importance
            ).apply {
                description = "Notifications for alarm events"
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun showNotification2(context: Context) {
        Log.d(TAG, "showNotification: I am from show notification")

        val channelId = "alarm_channel"
        val channelName = "Alarm Notifications"

        // Create a notification channel for Android 8.0+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val dismissIntent = Intent(context, DismissReceiver::class.java).apply {
            putExtra("notification_id", alarmData.id)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmData.id,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // **Open App Action**: Create a PendingIntent to open the app
        val appIntent = Intent(context, MainActivity::class.java) // Replace with your main activity
        appIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val appPendingIntent = PendingIntent.getActivity(
            context,
            0,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentText("Check the weather now!")
            .setContentTitle("Alarm")
            .setSmallIcon(R.mipmap.ic_launcher) // Ensure this icon exists!
            .setContentIntent(appPendingIntent) // Set the PendingIntent for the app action
            .addAction(R.drawable.ic_alerts, "Dismiss", dismissPendingIntent) // Ensure this drawable exists!
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(alarmData.id, notification)
    }
}