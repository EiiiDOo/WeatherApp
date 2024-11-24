package com.example.weatherapp.model

sealed class StateGeneric<out T> {

    data class Success<T>(val data: T) : StateGeneric<T>()
    data class Error<T>(val exception: String) : StateGeneric<T>()
    data object Loading : StateGeneric<Nothing>()
}