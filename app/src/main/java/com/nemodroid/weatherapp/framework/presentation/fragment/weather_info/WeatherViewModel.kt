package com.nemodroid.weatherapp.framework.presentation.fragment.weather_info

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nemodroid.weatherapp.BuildConfig
import com.nemodroid.weatherapp.R
import com.nemodroid.weatherapp.business.domain.data_model.City
import com.nemodroid.weatherapp.business.domain.data_model.WeatherHistory
import com.nemodroid.weatherapp.framework.network.retrofit.ApiService
import com.nemodroid.weatherapp.framework.network.retrofit.RetrofitClient
import com.nemodroid.weatherapp.framework.offline_database.DatabaseInstance
import com.nemodroid.weatherapp.utils.callbacks.ResultCallback
import com.nemodroid.weatherapp.utils.coroutines_utiles.Resource
import com.nemodroid.weatherapp.utils.coroutines_utiles.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class WeatherViewModel : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context

    private lateinit var apiService: ApiService

    private lateinit var resultCallback: ResultCallback

    private var isArabic = false

    fun setViewModel(context: Context, isArabic: Boolean, resultCallback: ResultCallback) {
        this.context = context
        this.isArabic = isArabic
        this.resultCallback = resultCallback

        apiService = RetrofitClient.createApiService(ApiService::class.java, context)
    }

    suspend fun retrieveWeatherInfo(model: City) {
        flow {
            emit(Resource.loading(data = null, getMessage(R.string.waitPleaseWait)))
            try {
                emit(
                    Resource.success(
                        data = apiService.getCurrentWeather(model.cityName, BuildConfig.APP_ID),
                        ""
                    )
                )
            } catch (e: Exception) {
                emit(Resource.error(null, RetrofitClient.getExceptionMessage(context, e)))
            }
        }.onEach {
            it.let { resource ->
                when (resource.status) {
                    Status.LOADING -> {
                        resultCallback.onLoad(
                            "LOAD_CURRENT_WEATHER",
                            getMessage(R.string.waitPleaseWait)
                        )
                    }
                    Status.SUCCESS -> {
                        if (resource.data == null) {
                            resultCallback.onFailure("FAILURE_LOAD_CURRENT_WEATHER")
                        } else {
                            resultCallback.onSuccess("SUCCESS_LOAD_CURRENT_WEATHER", resource.data)
                        }
                    }
                    Status.ERROR -> {
                        resultCallback.onFailure("FAILURE_LOAD_CURRENT_WEATHER")
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun insertNewWeather(databaseInstance: DatabaseInstance, history: WeatherHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseInstance.weatherDAO().insertHistory(history)
        }
    }

    private fun getMessage(resID: Int) = context.getString(resID)
}