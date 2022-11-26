package com.nemodroid.weatherapp.business.domain.api_response

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    @SerializedName("lat") var lat: Double? = null,
    @SerializedName("lon") var lon: Double? = null,
    @SerializedName("timezone") var timezone: String? = null,
    @SerializedName("timezone_offset") var timezoneOffset: Int? = null,
    @SerializedName("current") var current: Current? = Current(),
    @SerializedName("hourly") var hourly: ArrayList<Hourly> = arrayListOf(),
)

data class Current(
    @SerializedName("dt") var dt: Int? = null,
    @SerializedName("sunrise") var sunrise: Int? = null,
    @SerializedName("sunset") var sunset: Int? = null,
    @SerializedName("temp") var temp: Double? = null,
    @SerializedName("feels_like") var feelsLike: Int? = null,
    @SerializedName("pressure") var pressure: Int? = null,
    @SerializedName("humidity") var humidity: Int? = null,
    @SerializedName("dew_point") var dewPoint: Double? = null,
    @SerializedName("uvi") var uvi: Double? = null,
    @SerializedName("clouds") var clouds: Int? = null,
    @SerializedName("visibility") var visibility: Int? = null,
    @SerializedName("wind_speed") var windSpeed: Double? = null,
    @SerializedName("wind_deg") var windDeg: Int? = null,
    @SerializedName("wind_gust") var windGust: Double? = null,
    @SerializedName("weather") var weather: ArrayList<Weather> = arrayListOf()
)

data class Hourly(
    @SerializedName("dt") var dt: Int? = null,
    @SerializedName("temp") var temp: Double? = null,
    @SerializedName("feels_like") var feelsLike: Double? = null,
    @SerializedName("pressure") var pressure: Int? = null,
    @SerializedName("humidity") var humidity: Int? = null,
    @SerializedName("dew_point") var dewPoint: Double? = null,
    @SerializedName("uvi") var uvi: Double? = null,
    @SerializedName("clouds") var clouds: Int? = null,
    @SerializedName("visibility") var visibility: Int? = null,
    @SerializedName("wind_speed") var windSpeed: Double? = null,
    @SerializedName("wind_deg") var windDeg: Int? = null,
    @SerializedName("wind_gust") var windGust: Double? = null,
    @SerializedName("weather") var weather: ArrayList<Weather> = arrayListOf(),
    @SerializedName("pop") var pop: Int? = null
)
