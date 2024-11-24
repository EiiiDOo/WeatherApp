package com.example.weatherapp.model

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.example.weatherapp.R
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.databinding.AlarmUiBinding
import com.example.weatherapp.model.pojo.DateDTO
import com.example.weatherapp.model.pojo.DateDtoForRoom
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OverLayService : Service() {
    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null
    private var mediaPlayer: MediaPlayer? = null
    private var alarmItem: DateDTO? = null
    private lateinit var binding: AlarmUiBinding
    private lateinit var localDataSourceImpl: LocalDataSourceImpl

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Handle null intent gracefully
        val alarmItemJson = intent?.getStringExtra(ALARM_ITEM)
        if (alarmItemJson == null) {
            stopSelf() // Stop the service if critical data is missing
            return START_NOT_STICKY
        }

        // Deserialize alarm item
        alarmItem = Gson().fromJson(alarmItemJson, DateDTO::class.java)

        // Inflate the UI and initialize data source
        binding = AlarmUiBinding.inflate(LayoutInflater.from(this))
        localDataSourceImpl = LocalDataSourceImpl(this)
        overlayView = binding.root
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP
        }

        // Set up the UI interaction
        binding.apply {
            btnDone.setOnClickListener {
                stopSelf()
            }
        }

        // Collect and display weather data
        localDataSourceImpl.apply {
            CoroutineScope(Dispatchers.IO).launch {
                getHomeWeatherData().collect { weatherData ->
                    val weather = weatherData.firstOrNull() ?: return@collect
                    withContext(Dispatchers.Main) {
                        binding.imageView6.setImageResource(weather.iconId.toDrawable2X())
                        binding.txtMsg.text = String.format(
                            "       ${weather.city}\n${weather.date.convertUnixToDay("EEEE,dd-MM-yyyy")}\n${weather.temp}cْ  ${weather.skyStateDescription}"
                        )
                    }
                }
            }
        }

        // Add overlay view
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.addView(overlayView, layoutParams)

        // Play alarm sound
        mediaPlayer = MediaPlayer.create(this, R.raw.classic_alarm_sound).apply {
            isLooping = true
            start()
        }

        return START_STICKY // Restart service if killed
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove the overlay view safely
        overlayView?.let { windowManager.removeView(it) }

        // Stop media player safely
        mediaPlayer?.stop()
        mediaPlayer?.release()

        // Delete the specific alarm from the database
        alarmItem?.let { alarm ->
            CoroutineScope(Dispatchers.IO).launch {
                localDataSourceImpl.deleteSpecificAlarm(alarm.id)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
