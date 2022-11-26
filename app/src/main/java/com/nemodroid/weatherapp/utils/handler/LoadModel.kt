package com.nemodroid.weatherapp.utils.handler

import android.view.animation.Animation
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.nemodroid.weatherapp.BR
import com.nemodroid.weatherapp.R

class LoadModel : BaseObservable() {

    @Bindable
    var isLoad = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.isLoad)
        }

    @Bindable
    var showLayout = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.showLayout)
        }

    @Bindable
    var iconColor = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.iconColor)
        }

    @Bindable
    var textColor = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.textColor)
        }


    @Bindable
    var loadStatus = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.loadStatus)
        }

    companion object {
        @JvmStatic
        @BindingAdapter("app:setLoadResID")
        fun setLoadResID(animationView: LottieAnimationView, isLoad: Boolean) {
            animationView.setAnimation(if (isLoad) R.raw.loading_1 else R.raw.error_404_2)
            animationView.repeatCount = Animation.INFINITE
            animationView.playAnimation()
        }
    }
}