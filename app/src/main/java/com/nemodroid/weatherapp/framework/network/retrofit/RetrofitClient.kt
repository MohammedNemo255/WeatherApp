@file:Suppress("DEPRECATION")

package com.nemodroid.weatherapp.framework.network.retrofit

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nemodroid.weatherapp.BuildConfig
import com.nemodroid.weatherapp.R
import com.nemodroid.weatherapp.utils.NoInternetException
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RetrofitClient {

    private const val connectionTime = 5

    private val retrofitBuilder = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

    private fun okHTTPClient(context: Context): OkHttpClient {

        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            .writeTimeout(connectionTime.toLong(), TimeUnit.MINUTES)
            .readTimeout(connectionTime.toLong(), TimeUnit.MINUTES)
            .connectTimeout(connectionTime.toLong(), TimeUnit.MINUTES)

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(NetworkInterceptor(context))
        builder.addInterceptor(getHeaderInterceptor(context))
        builder.addInterceptor(interceptor)

        return builder.build()
    }

    private fun unSafeOkHttpClient(context: Context): OkHttpClient {
        val okHttpClient: OkHttpClient.Builder = OkHttpClient.Builder()
            .writeTimeout(connectionTime.toLong(), TimeUnit.MINUTES)
            .readTimeout(connectionTime.toLong(), TimeUnit.MINUTES)
            .connectTimeout(connectionTime.toLong(), TimeUnit.MINUTES)

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        okHttpClient.addInterceptor(NetworkInterceptor(context))
        okHttpClient.addInterceptor(getHeaderInterceptor(context))
        okHttpClient.addInterceptor(interceptor)

        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts: Array<TrustManager> = arrayOf(object : X509TrustManager {
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            if (trustAllCerts.isNotEmpty() && trustAllCerts.first() is X509TrustManager) {
                okHttpClient.sslSocketFactory(
                    sslSocketFactory,
                    trustAllCerts.first() as X509TrustManager
                )
                okHttpClient.hostnameVerifier { _, _ -> true } // change here
            }

            return okHttpClient.build()
        } catch (e: Exception) {
            return okHttpClient.build()
        }
    }


    private class NetworkInterceptor(var application: Context) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            if (!isInternetAvailable(application))
                throw NoInternetException(application.getString(R.string.noInternetConnection))
            return chain.proceed(chain.request())
        }
    }

    private fun getHeaderInterceptor(context: Context): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
            request.header("accept", "*/*")
            request.header("Content-Type", "application/json")
            chain.proceed(request.build())
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }

    private fun getGSONClient(): Gson {
        return GsonBuilder().setLenient().create()
    }

    fun createApiService(
        apiService: Class<ApiService>,
        context: Context
    ): ApiService {
        val retrofit = retrofitBuilder.client(unSafeOkHttpClient(context)).build()
        return retrofit.create(apiService)
    }

    fun getExceptionMessage(context: Context, e: Exception): String {
        return if (e is HttpException) {
            when {
                e.code() == 400 -> context.getString(R.string.errorBadRequest)
                e.code() == 401 -> { context.getString(R.string.authFailureError) }
                e.code() == 403 -> context.getString(R.string.errorForbidden)
                e.code() == 404 -> context.getString(R.string.errorRequestNotFound)
                e.code() == 500 -> context.getString(R.string.serverError)
                e.code() == 502 -> context.getString(R.string.errorBadGateway)
                else -> e.message()
            }
        } else {
            e.message ?: context.getString(R.string.textErrorMessage)
        }
    }

}