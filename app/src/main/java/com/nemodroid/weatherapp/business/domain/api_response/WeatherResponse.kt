package com.nemodroid.weatherapp.business.domain.api_response

import com.google.gson.annotations.SerializedName
import com.nemodroid.weatherapp.business.domain.data_model.WeatherHistory

data class WeatherResponse(
    @SerializedName("coord") var coord: Coordinates? = Coordinates(),
    @SerializedName("weather") var weather: ArrayList<Weather> = arrayListOf(),
    @SerializedName("main") var main: Main? = Main(),
    @SerializedName("wind") var wind: Wind? = Wind(),
    @SerializedName("dt") var dt: Int? = null,
    @SerializedName("timezone") var timezone: Int? = null
) {
    var city = ""

    fun convertToWeatherHistory(): WeatherHistory {
        val history = WeatherHistory()

        history.city = this.city
        history.time = this.dt

        this.weather.let {
            if (it.size > 0) {
                history.desc = it[0].description
                history.iconUrl = it[0].iconUrl
            }
        }

        this.wind?.let {
            history.wind = it.speed
        }

        this.main?.let {
            history.temp = it.temp
            history.humidity = it.humidity
            history.toCelsius = it.toCelsius
        }

        return history
    }
}

data class Coordinates(
    @SerializedName("lon") var lon: Double? = null,
    @SerializedName("lat") var lat: Double? = null
)

data class Weather(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("main") var main: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("icon") var icon: String? = null
) {
    var iconUrl = ""
}

data class Main(
    @SerializedName("temp") var temp: Double? = null,
    @SerializedName("humidity") var humidity: Int? = null,
) {
    var toCelsius = false
}

data class Wind(
    @SerializedName("speed") var speed: Double? = null,
) {
    var toKM = false
}