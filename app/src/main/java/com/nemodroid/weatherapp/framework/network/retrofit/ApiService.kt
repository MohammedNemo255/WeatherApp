package com.nemodroid.weatherapp.framework.network.retrofit

import com.nemodroid.weatherapp.business.domain.api_response.ForecastResponse
import com.nemodroid.weatherapp.business.domain.api_response.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") cityName: String?,
        @Query("APPID") appId: String?
    ): WeatherResponse

    @GET("onecall")
    fun getWeatherForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("exclude") exclude: String,
        @Query("appid") apiKey: String,
        @Query("units") unit: String = "metric",
    ): ForecastResponse
}