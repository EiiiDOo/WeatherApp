package com.example.weatherapp.ui.favourite

import com.example.weatherapp.model.pojo.CustomSaved

interface FavListner {
    fun onDeleteClick(position: CustomSaved)
    fun onDetailsClick(position: CustomSaved)
}