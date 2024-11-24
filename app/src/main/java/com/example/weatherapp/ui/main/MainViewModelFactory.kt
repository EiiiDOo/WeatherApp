package com.example.weatherapp.ui.main

import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.data.repo.IRepo

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(private val _repo: IRepo) : ViewModelProvider.Factory {

    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(_repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}