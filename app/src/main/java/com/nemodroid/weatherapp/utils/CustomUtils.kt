package com.nemodroid.weatherapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

//region GSON
fun getObject(gson: Gson, `object`: Any?, aClass: Class<*>?): Any? {
    return gson.fromJson(gson.toJson(`object`), aClass)
}

internal inline fun <reified T> Gson.fromJson(json: String) =
    fromJson<T>(json, object : TypeToken<T>() {}.type)

//endregion

//region conversion
fun milesToKilometer(miles: Double): Double {
    return miles * 1.609344
}

fun capitalizeWord(word: String): String {
    return word.toString().replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()
        ) else it.toString()
    }
}
//endregion

//region date conversion
@SuppressLint("SimpleDateFormat")
fun convertFromTimestamp(millisecond: Long, toFormat: String): String {
    return DateFormat.format(toFormat, Date(millisecond)).toString()
}

fun convertToDateTime(milliseconds: Long, timeZoneOffset: Long, toFormat: String): String {
    val time: Long = (milliseconds * 1000L) + (timeZoneOffset * 1000)
    val date = Date(time)
    val timeZone = TimeZone.getDefault()
    timeZone.rawOffset = timeZoneOffset.toInt()
    val formatter: java.text.DateFormat = SimpleDateFormat(toFormat, Locale.getDefault())
    formatter.timeZone = timeZone
    return formatter.format(date)
}
//endregion

fun showToast(context: Context, msg: String) =
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()