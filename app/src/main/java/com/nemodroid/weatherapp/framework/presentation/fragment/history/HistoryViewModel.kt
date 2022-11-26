package com.nemodroid.weatherapp.framework.presentation.fragment.history

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nemodroid.weatherapp.R
import com.nemodroid.weatherapp.business.domain.data_model.WeatherHistory
import com.nemodroid.weatherapp.framework.offline_database.DatabaseInstance
import com.nemodroid.weatherapp.utils.callbacks.ResultCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class HistoryViewModel : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context

    private lateinit var resultCallback: ResultCallback

    private lateinit var mutableObjectList: MutableStateFlow<MutableList<Any>>

    private var isArabic = false

    fun setViewModel(context: Context, isArabic: Boolean, resultCallback: ResultCallback) {
        this.context = context
        this.isArabic = isArabic
        this.resultCallback = resultCallback
    }

    suspend fun retrieveHistoryList(databaseInstance: DatabaseInstance, cityName: String) {
        resultCallback.onLoad("LOAD_HISTORY_LIST", getMessage(R.string.waitPleaseWait))
        viewModelScope.launch(Dispatchers.IO) {
            databaseInstance.weatherDAO().getAllHistoryByCity(cityName).collect {
                mutableObjectList.emit(it.toMutableList())

                withContext(Dispatchers.Main) {
                    when (it.isEmpty()) {
                        true -> {
                            resultCallback.onFailure("FAILURE_LOAD_HISTORY_LIST${getMessage(R.string.errorNoHistoryFound)}")
                        }
                        else -> {
                            resultCallback.onSuccess("SUCCESS_LOAD_HISTORY_LIST")
                        }
                    }
                }
            }
        }
    }

    //region init mutable list
    fun initMutableHistoryList() {
        mutableObjectList = MutableStateFlow(mutableListOf())
    }
    //endregion

    //region getter methods
    fun getMutableHistoryList(): MutableStateFlow<MutableList<Any>> {
        return mutableObjectList
    }
    //endregion

    private fun getMessage(resID: Int): String = context.getString(resID)
}
