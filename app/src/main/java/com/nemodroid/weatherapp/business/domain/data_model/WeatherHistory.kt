package com.nemodroid.weatherapp.business.domain.data_model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class WeatherHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    var city: String? = "",
    var iconUrl: String? = "",
    var desc: String? = "",
    var time: Int? = 0,
    var humidity: Int? = 0,
    var temp: Double? = 0.0,
    var wind: Double? = 0.0,
    var toCelsius: Boolean? = true,
)