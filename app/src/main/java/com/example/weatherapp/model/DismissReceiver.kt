package com.example.weatherapp.model

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class DismissReceiver : BroadcastReceiver() {

        companion object {
            private const val TAG = "DismissReceiver"
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            val notificationId = intent?.getIntExtra("notification_id", -1)
            Log.d(TAG, "Notification dismissed with ID: $notificationId")

            val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId ?: 0) // Cancel the notification
        }

}