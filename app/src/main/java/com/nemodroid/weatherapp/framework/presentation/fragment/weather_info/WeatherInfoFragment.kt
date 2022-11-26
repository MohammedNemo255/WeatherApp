package com.nemodroid.weatherapp.framework.presentation.fragment.weather_info

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.nemodroid.weatherapp.BuildConfig
import com.nemodroid.weatherapp.R
import com.nemodroid.weatherapp.business.domain.api_response.Weather
import com.nemodroid.weatherapp.business.domain.api_response.WeatherResponse
import com.nemodroid.weatherapp.business.domain.data_model.City
import com.nemodroid.weatherapp.business.domain.data_model.WeatherHistory
import com.nemodroid.weatherapp.databinding.FragmentWeatherInfoBinding
import com.nemodroid.weatherapp.framework.offline_database.DatabaseInstance
import com.nemodroid.weatherapp.framework.presentation.activity.BaseActivity
import com.nemodroid.weatherapp.framework.presentation.fragment.BaseFragment
import com.nemodroid.weatherapp.utils.callbacks.ResultCallback
import com.nemodroid.weatherapp.utils.capitalizeWord
import com.nemodroid.weatherapp.utils.convertToDateTime
import com.nemodroid.weatherapp.utils.handler.ErrorModel
import com.nemodroid.weatherapp.utils.handler.LoadModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class WeatherInfoFragment : BaseFragment(), ResultCallback {

    private var bindingProperty: FragmentWeatherInfoBinding? = null

    private val binding get() = bindingProperty!!

    private val databaseInstance by lazy { DatabaseInstance.getInstance(requireActivity()) }

    private lateinit var viewModel: WeatherViewModel

    private lateinit var weatherResponse: WeatherResponse

    private lateinit var loadModel: LoadModel
    private lateinit var errorModel: ErrorModel

    private var cityModel: City? = null
    private var historyModel: WeatherHistory? = null

    private var loadFromHistory = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindingProperty = FragmentWeatherInfoBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getExtraArguments()
        setDefaults()

        if (!loadFromHistory)
            initViewModel()
    }

    private fun getExtraArguments() {
        arguments?.let {
            cityModel =
                Gson().fromJson(it.getString(getString(R.string.gsonCityObject)), City::class.java)
            historyModel =
                Gson().fromJson(
                    it.getString(getString(R.string.gsonHistoryObject)),
                    WeatherHistory::class.java
                )

            cityModel?.let {
                loadFromHistory = false
            }

            historyModel?.let {
                loadFromHistory = true
            }

            (requireActivity() as BaseActivity).supportActionBar?.title =
                when (loadFromHistory) {
                    true -> {
                        capitalizeWord(historyModel?.city.toString())
                    }
                    else -> {
                        capitalizeWord(cityModel?.cityName.toString())
                    }
                }
        }
    }

    private fun setDefaults() {
        weatherResponse = WeatherResponse()

        loadModel = LoadModel()
        errorModel = ErrorModel()

        bindHandler()
        bindWeatherObject()
    }

    private fun bindHandler() {
        binding.loadModel = loadModel
        binding.errorModel = errorModel
    }

    private fun bindWeatherObject() {
        if (loadFromHistory) {
            weatherResponse = WeatherResponse()

            weatherResponse.city = historyModel?.city.toString()
            weatherResponse.dt = historyModel?.time
            weatherResponse.timezone = 0

            weatherResponse.main?.let {
                it.toCelsius = historyModel?.toCelsius == true
                it.temp = historyModel?.temp
                it.humidity = historyModel?.humidity
            }

            weatherResponse.wind?.let {
                it.speed = historyModel?.wind
            }

            weatherResponse.weather = arrayListOf()
            val weather = Weather()
            weather.description = historyModel?.desc
            weather.iconUrl = historyModel?.iconUrl.toString()
            weatherResponse.weather.add(weather)

            binding.tvCityWeather.text = String.format(
                getString(R.string.textWeatherCityInfo),
                historyModel?.city.toString(),
                convertToDateTime(
                    weatherResponse.dt?.toLong() ?: System.currentTimeMillis(),
                    weatherResponse.timezone?.toLong() ?: 0,
                    getString(R.string.constantDateTimeToFormat)
                )
            )
        } else {

            weatherResponse.city = cityModel?.cityName.toString()
            weatherResponse.main?.let {
                it.toCelsius = true
            }

            if (weatherResponse.weather.isNotEmpty())
                weatherResponse.weather[0].let {
                    it.iconUrl = String.format(BuildConfig.WEATHER_ICON_URL, it.icon)
                }

            binding.tvCityWeather.text = String.format(
                getString(R.string.textWeatherCityInfo),
                cityModel?.cityName.toString(),
                convertToDateTime(
                    weatherResponse.dt?.toLong() ?: System.currentTimeMillis(),
                    weatherResponse.timezone?.toLong() ?: 0,
                    getString(R.string.constantDateTimeToFormat)
                )
            )
        }

        binding.dataModel = weatherResponse
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[WeatherViewModel::class.java]
        viewModel.setViewModel(
            requireActivity(),
            isArabic(),
            this
        )

        lifecycleScope.launch(Dispatchers.IO) {
            cityModel?.let {
                viewModel.retrieveWeatherInfo(it)
            }
        }
    }

    //region callback
    override fun onLoad(title: String, message: String) {
        loadModel.showLayout = true
        errorModel.withError = false
        bindHandler()
    }

    override fun onSuccess(message: String, any: Any?) {
        loadModel.showLayout = false
        errorModel.withError = false

        if (message.contains("SUCCESS_LOAD_CURRENT_WEATHER", true)) {
            when (any) {
                is WeatherResponse -> {
                    weatherResponse = any
                    bindWeatherObject()
                    insertToHistory()
                }
            }
        }

        bindHandler()
    }

    private fun insertToHistory() {
        viewModel.insertNewWeather(databaseInstance, weatherResponse.convertToWeatherHistory())
    }

    override fun onFailure(message: String) {
        loadModel.showLayout = false


        setErrorModel(
            errorModel,
            message.substring("FAILURE_LOAD_CURRENT_WEATHER".length),
            0,
            R.raw.error_404_2,
            true,
            getString(R.string.actionAddNewCity)
        )

        bindHandler()
    }
    //endregion

    override fun onDestroyView() {
        super.onDestroyView()
        bindingProperty = null
    }
}