package com.nemodroid.weatherapp.utils.handler

import android.graphics.PorterDuff
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.nemodroid.weatherapp.BR
import com.nemodroid.weatherapp.R

class ErrorModel : BaseObservable() {

    @Bindable
    var actionText = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.actionText)
        }

    @Bindable
    var errorMessage = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.errorMessage)
        }

    @Bindable
    var lottieRes = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.lottieRes)
        }

    @Bindable
    var textColor = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.textColor)
        }

    @Bindable
    var tintColor = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.tintColor)
        }

    @Bindable
    var withError = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.withError)
        }

    @Bindable
    var withAction = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.withAction)
        }


    @Bindable
    var withLottie = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.withLottie)
        }

    @Bindable
    var errorImageRes = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.errorImageRes)
        }


    companion object {
        @JvmStatic
        @BindingAdapter("app:setErrorLottieRes")
        fun setErrorLottieRes(animationView: LottieAnimationView, lottieRes: Int) {
            animationView.setAnimation(if (lottieRes == 0) R.raw.error_404_1 else lottieRes)
            animationView.playAnimation()
        }

        @JvmStatic
        @BindingAdapter(value = ["errorImageRes", "imageResTint"], requireAll = false)
        fun setErrorRes(imageView: AppCompatImageView, resourceID: Int, tintID: Int) {
            imageView.setImageResource(resourceID)
            if (tintID != 0) imageView.setColorFilter(
                ContextCompat.getColor(imageView.context, tintID),
                PorterDuff.Mode.SRC_IN
            )
        }

        @JvmStatic
        @BindingAdapter(value = ["errorMessage", "colorMessage"], requireAll = false)
        fun setErrorMessage(view: View, errorMessage: String?, colorMessage: Int) {
            if (view is AppCompatTextView) {
                view.text = errorMessage
                if (colorMessage != 0) view.setTextColor(
                    ContextCompat.getColor(
                        view.getContext(),
                        colorMessage
                    )
                )
            } else if (view is MaterialButton) {
                view.text = errorMessage
                if (colorMessage != 0) view.setTextColor(
                    ContextCompat.getColor(
                        view.getContext(),
                        colorMessage
                    )
                )
            }
        }
    }
}