package com.nemodroid.weatherapp.business.domain.data_model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class City(
    @PrimaryKey(autoGenerate = false)
    var cityName: String = ""
)