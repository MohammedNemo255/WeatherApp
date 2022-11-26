package com.nemodroid.weatherapp.framework.presentation.fragment.cities

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.nemodroid.weatherapp.R
import com.nemodroid.weatherapp.business.domain.data_model.City
import com.nemodroid.weatherapp.framework.offline_database.DatabaseInstance
import com.nemodroid.weatherapp.utils.callbacks.ResultCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class CityViewModel : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context

    private lateinit var resultCallback: ResultCallback

    private lateinit var mutableCityList: MutableStateFlow<MutableList<Any>>

    private var isArabic = false

    fun setViewModel(context: Context, isArabic: Boolean, resultCallback: ResultCallback) {
        this.context = context
        this.isArabic = isArabic
        this.resultCallback = resultCallback
    }

    suspend fun retrieveCityList(databaseInstance: DatabaseInstance) {
        resultCallback.onLoad("LOAD_CITY_LIST", getMessage(R.string.waitPleaseWait))
        viewModelScope.launch(Dispatchers.IO) {
            databaseInstance.cityDAO().getAllCities().collect {
                mutableCityList.emit(it.toMutableList())

                withContext(Dispatchers.Main) {
                    when (it.isEmpty()) {
                        true -> {
                            resultCallback.onFailure("FAILURE_LOAD_CITY_LIST${getMessage(R.string.errorNoCitiesForNow)}")
                        }
                        else -> {
                            resultCallback.onSuccess("SUCCESS_LOAD_CITY_LIST")
                        }
                    }
                }
            }
        }
    }

    fun insertNewCity(databaseInstance: DatabaseInstance, city: String) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseInstance.cityDAO().insertCity(City(cityName = city))
        }
    }

    fun deleteCity(databaseInstance: DatabaseInstance, city: City) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseInstance.cityDAO().deleteCity(city)
            databaseInstance.weatherDAO().deleteAllHistoryByCity(city.cityName)
        }
    }

    //region init mutable list
    fun initMutableCityList() {
        mutableCityList = MutableStateFlow(mutableListOf())
    }
    //endregion

    //region getter methods
    fun getMutableCityList(): MutableStateFlow<MutableList<Any>> {
        return mutableCityList
    }
    //endregion

    private fun getMessage(resID: Int): String = context.getString(resID)
}
