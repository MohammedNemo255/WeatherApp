package com.nemodroid.weatherapp.utils.coroutines_utiles

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T, message: String): Resource<T> =
            Resource(status = Status.SUCCESS, data = data, message = message)

        fun <T> error(data: T?, message: String): Resource<T> =
            Resource(status = Status.ERROR, data = data, message = message)

        fun <T> loading(data: T?, message: String): Resource<T> =
            Resource(status = Status.LOADING, data = data, message = message)
    }
}

enum class Status {
    LOADING,
    SUCCESS,
    ERROR
}
