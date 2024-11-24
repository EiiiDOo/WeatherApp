package com.example.weatherapp.ui.alerts

import android.content.Context
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.AlarmItemBinding
import com.example.weatherapp.model.pojo.DateDtoForRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AlertAdapter(private var list: List<DateDtoForRoom>, private val listener: OnDeleteAlarm) :
    RecyclerView.Adapter<AlertAdapter.ViewHolder>() {
    private lateinit var binding: AlarmItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertAdapter.ViewHolder {
        val inflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = AlarmItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: AlertAdapter.ViewHolder, position: Int) {
        val item = list[position]
        binding.apply {
            var difference = (item.milliSeconds - Calendar.getInstance().timeInMillis)/1000
            setUnit(difference)
            CoroutineScope(Dispatchers.Main).launch {
                while (difference>0){
                    delay(1000)
                    difference -= 1
                    setUnit(difference)
                }
            }
            txtTime.text = item.date
            imageButton.setOnClickListener {
                listener.onDeleteClick(item)
            }
        }
    }
    private fun setUnit(difference:Long){
        if (difference > 60) {
            val min = if (difference % 60 == 0L) {
                difference / 60
            } else {
                difference / 60 + 1
            }
            binding.txtCounter.text = String.format("$min min")
        }
        else
            binding.txtCounter.text = String.format("$difference sec")

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}