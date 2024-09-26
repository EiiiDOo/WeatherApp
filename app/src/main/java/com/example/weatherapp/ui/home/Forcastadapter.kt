package com.example.weatherapp.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ItemdayBinding
import com.example.weatherapp.model.Item0
import com.example.weatherapp.toDateTime
import com.example.weatherapp.toDrawable

class Forcastadapter (var Weather: List<Item0>) :
    RecyclerView.Adapter<Forcastadapter.ViewHolder>() {
    private lateinit var binding: ItemdayBinding

    override fun onBindViewHolder(holder: Forcastadapter.ViewHolder, position: Int) {
        val item = Weather[position]
        binding.apply {
            if (position == 0)
                txtDay.text = "Today"
            else
                txtDay.text = item.dt.toDateTime("EEEE")
            "${item.main.temp_min}-${item.main.temp_max}".also { txtTemp.text = it }
            imgIcon.setImageResource(item.weather[0].icon.toDrawable())
            txtDesSky.text = item.weather[0].description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemdayBinding.inflate(inflater, parent, false)
        return ViewHolder(binding.root)
    }


    override fun getItemCount(): Int {
        return Weather.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(Weather: List<Item0>) {
        this.Weather = Weather
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}