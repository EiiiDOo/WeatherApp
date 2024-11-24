package com.example.weatherapp.ui.favourite

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ItemFavBinding
import com.example.weatherapp.model.pojo.CustomSaved

class FavAdapter(val listner: FavListner) :
    ListAdapter<CustomSaved, FavAdapter.ViewHolder>(
        MyDiffUtilClass()
    ) {
    private lateinit var binding: ItemFavBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemFavBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)

        holder.viewBinding.apply {
            cityName.text = currentItem.city
            txtLonLat.text = currentItem.lon.toString() + ", " + currentItem.lat.toString()
            imgDelete.setOnClickListener {
                listner.onDeleteClick(currentItem)
            }
            card2.setOnClickListener {
                listner.onDetailsClick(currentItem)
            }
        }
    }
    class ViewHolder(var viewBinding: ItemFavBinding) : RecyclerView.ViewHolder(viewBinding.root)
}

class MyDiffUtilClass : DiffUtil.ItemCallback<CustomSaved>() {
    override fun areItemsTheSame(oldItem: CustomSaved, newItem: CustomSaved): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: CustomSaved, newItem: CustomSaved): Boolean {
        return oldItem == newItem
    }
}