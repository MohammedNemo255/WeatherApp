package com.nemodroid.weatherapp.utils.binding

import android.annotation.SuppressLint
import androidx.databinding.BindingAdapter
import com.nemodroid.weatherapp.R
import com.nemodroid.weatherapp.utils.milesToKilometer
import com.nemodroid.weatherapp.utils.views.textview.AutoResizeTextView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

object LabelView {

    @SuppressLint("SetTextI18n")
    @JvmStatic
    @BindingAdapter(value = ["textToCapitalize"], requireAll = false)
    fun textToCapitalize(view: AutoResizeTextView, textToCapitalize: String?) {
        view.text = textToCapitalize.toString().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
    }

    @SuppressLint("SetTextI18n")
    @JvmStatic
    @BindingAdapter(value = ["temperature", "toCelsius"], requireAll = false)
    fun convertTemperature(view: AutoResizeTextView, temperature: Double?, toCelsius: Boolean) {
        if (toCelsius)
            view.text =
                "${
                    temperature?.minus(273.15)?.roundToInt()
                } ${view.context.getString(R.string.textCelsiusTemp)}"
        else
            view.text = "$temperature ${view.context.getString(R.string.textCelsiusTemp)}"
    }

    @SuppressLint("SetTextI18n")
    @JvmStatic
    @BindingAdapter(value = ["miles", "toKM"], requireAll = false)
    fun convertMilesToKM(view: AutoResizeTextView, miles: Double, toKM: Boolean) {
        if (toKM)
            view.text =
                "${milesToKilometer(miles)} ${view.context.getString(R.string.textKMH)}"
        else
            view.text =
                "$miles ${view.context.getString(R.string.textKMH)}"
//            view.text = "$miles ${view.context.getString(R.string.textMH)}"
    }

    @SuppressLint("SetTextI18n")
    @JvmStatic
    @BindingAdapter(value = ["milliseconds", "toFormat"], requireAll = false)
    fun convertTMDToDateTime(
        view: AutoResizeTextView,
        milliseconds: Int,
        toFormat: String
    ){
        val time: Long = (milliseconds * 1000L)
        val date = Date(time)
        val timeZone = TimeZone.getDefault()
        timeZone.rawOffset = 0
        val formatter: java.text.DateFormat = SimpleDateFormat(toFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        view.text = formatter.format(date)
    }
}