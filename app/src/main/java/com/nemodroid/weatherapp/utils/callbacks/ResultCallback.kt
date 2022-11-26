package com.nemodroid.weatherapp.utils.callbacks

interface ResultCallback {
    fun onLoad(title: String, message: String)

    fun onSuccess(message: String, any: Any? = null)

    fun onFailure(message: String)
}