package com.example.weatherapp.ui.alerts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.MyAlarmManager
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.repo.RepoImpl
import com.example.weatherapp.databinding.FragmentAleretBinding
import com.example.weatherapp.model.StateGeneric
import com.example.weatherapp.model.dialogs.ConfirmationDeleteDialogAlarm
import com.example.weatherapp.model.dialogs.MaterialPicker
import com.example.weatherapp.model.pojo.DateDtoForRoom
import com.example.weatherapp.ui.main.MainActivity
import com.example.weatherapp.ui.main.MainViewModel
import com.example.weatherapp.ui.main.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.launch
import kotlin.math.abs

class AlertsFragment : Fragment(), OnAlarmReady, OnDeleteAlarm, OnDeleteAlarmDialog{
    companion object {
        private const val TAG = "AlertsFragment"
    }


    private lateinit var binding: FragmentAleretBinding
    private val myAlarmManager: MyAlarmManager by lazy { MyAlarmManager(requireContext()) }
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(
            this,
            MainViewModelFactory(
                RepoImpl.getInstance(
                    RemoteDataSourceImpl,
                    LocalDataSourceImpl(requireContext())
                )
            )
        )[MainViewModel::class.java]
    }
    private lateinit var adapter: AlertAdapter


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
            if (!Settings.canDrawOverlays(context)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + requireActivity().packageName)
                )
                requireActivity().startActivity(intent)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    requestPostNotificationsPermission()
//                DatePicker(this).show(childFragmentManager, "TimePickerDialog")
                MaterialPicker(childFragmentManager, this).apply {
                    val format = if (DateFormat.is24HourFormat(requireContext()))
                        TimeFormat.CLOCK_24H
                    else
                        TimeFormat.CLOCK_12H
                    showTimePicker(format)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.getAllAlarm()
            viewModel.allAlarms.collect {
                when (it) {
                    is StateGeneric.Success -> {
                        binding.apply {
                            if (it.data.isEmpty()){
                                rvAlarm.visibility = View.GONE
                                textView14.visibility = View.VISIBLE
                            }else {
                                rvAlarm.visibility = View.VISIBLE
                                textView14.visibility = View.GONE
                            }
                            adapter = AlertAdapter(it.data, this@AlertsFragment)
                            rvAlarm.adapter = adapter
                        }
                    }

                    else -> {
//                        Snackbar.make(binding.root, "Error", Snackbar.LENGTH_SHORT)
//                            .show()
                    }
                }
            }
        }
    }

    private fun createAndScheduleAlarm(alarm: Calendar) {
        val day = alarm.get(Calendar.DAY_OF_MONTH)
        val month = alarm.get(Calendar.MONTH)
        val hour = alarm.get(Calendar.HOUR_OF_DAY)
        val minute = alarm.get(Calendar.MINUTE)

        lifecycleScope.launch {
            viewModel.apply {
                insertAlarm(
                    DateDtoForRoom(
                        alarm.timeInMillis.hashCode(),
                        alarm.timeInMillis,
                        String.format("$month/$day  ${abs(hour - 12)}:$minute")
                    )
                )
                launch {
                    insertAlarm.collect {
                        when (it) {
                            is StateGeneric.Success -> {
                                myAlarmManager.scheduleAlarm(alarm)
                                Log.d(TAG, "createAndScheduleAlarm Success: ${it.data}")
                            }

                            else -> Snackbar.make(binding.root, "Error", Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
                launch {
                    deletedAlarm.collect {
                        if (it)
                            Snackbar.make(binding.root, "Alarm Deleted", Snackbar.LENGTH_SHORT)
                                .show()
                        else
                            Snackbar.make(binding.root, "Error", Snackbar.LENGTH_SHORT)
                                .show()
                    }
                }
            }
        }
    }


    override fun onAlarmReady(calendar: Calendar) {
        createAndScheduleAlarm(calendar)
        Log.d(TAG, "onAlarmReady:${calendar.get(Calendar.MINUTE)} ")
    }

    override fun deleteClick(alarm: DateDtoForRoom) {
        viewModel.deleteAlarm(alarm)
        myAlarmManager.cancelAlarm(alarm.id)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestPostNotificationsPermission() {
        if (requireActivity().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
    }

    override fun onDeleteClick(alarm: DateDtoForRoom) {
        ConfirmationDeleteDialogAlarm(this, alarm).show(childFragmentManager, "ConfirmationDeleteDialog")
    }

    /*@RequiresApi(Build.VERSION_CODES.TIRAMISU)
        fun isPostNotificationsPermissionGranted(): Boolean {
            return (requireActivity().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
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

        @SuppressLint("InlinedApi")
        fun requestScheduleExactAlarmPermission() {
            if (requireActivity().checkSelfPermission(Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.SCHEDULE_EXACT_ALARM,
                        Manifest.permission.USE_EXACT_ALARM
                    ), SCHEDULE_EXACT_ALARM_PERMISSION_REQUEST_CODE
                )
            }
        }*/

}