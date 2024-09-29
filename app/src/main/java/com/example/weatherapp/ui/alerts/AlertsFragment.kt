package com.example.weatherapp.ui.alerts

import android.Manifest
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.SyncStateContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentAleretBinding
import com.example.weatherapp.ui.main.MainActivity
import java.util.Calendar

class AlertsFragment : Fragment() {
    companion object {
        private const val POST_NOTIFICATION_PERMISSION_REQUEST_CODE = 0
        private const val SCHEDULE_EXACT_ALARM_PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var binding: FragmentAleretBinding
    private val alarmManager: AlarmManager by lazy {
        requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAleretBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showAppBar(true)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton2.setOnClickListener {

            if (!isShowOnOtherAppsPermissionGranted()) {
                requestShowOnOtherAppsPermission()
                Toast.makeText(
                    requireContext(),
                    "Show on other apps permission not granted.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun isPostNotificationsPermissionGranted(): Boolean {
        return (requireActivity().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestPostNotificationsPermission() {
        if (requireActivity().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                POST_NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    fun isShowOnOtherAppsPermissionGranted(): Boolean {
        return Settings.canDrawOverlays(requireActivity())
    }

    fun requestShowOnOtherAppsPermission() {
        val intent =
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${requireContext().packageName}")
            )
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun isScheduleExactAlarmPermissionGranted(): Boolean {
        return (requireActivity().checkSelfPermission(Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED)
    }

    fun requestScheduleExactAlarmPermission() {
        if (requireActivity().checkSelfPermission(Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.SCHEDULE_EXACT_ALARM,
                    Manifest.permission.USE_EXACT_ALARM
                ), SCHEDULE_EXACT_ALARM_PERMISSION_REQUEST_CODE
            )
        }
    }


}
