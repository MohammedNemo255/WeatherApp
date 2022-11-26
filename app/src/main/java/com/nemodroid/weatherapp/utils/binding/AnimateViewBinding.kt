package com.nemodroid.weatherapp.utils.binding

import android.view.View
import android.view.animation.TranslateAnimation
import androidx.databinding.BindingAdapter
import android.text.Spanned

import android.widget.TextView

object AnimateViewBinding {

    private const val time: Long = 500

    //region slide down and up
    @JvmStatic
    @BindingAdapter(value = ["animateVisibilitySlideUpDown"], requireAll = false)
    fun animateVisibilitySlideUpDown(view: View, visible: Boolean) {
        if (visible) {
            slideUp(view)
        } else {
            slideDown(view)
        }
    }

    private fun slideUp(view: View) {
        val animate = TranslateAnimation(
            0f,
            0f,
            0f,
            view.height.toFloat()
        )

        animate.duration = time
        animate.fillAfter = true
        view.startAnimation(animate)
        view.visibility = View.VISIBLE
    }

    private fun slideDown(view: View) {
        val animate = TranslateAnimation(
            0f,
            0f,
            view.height.toFloat(),
            0f
        )

        animate.duration = time
        animate.fillAfter = true
        view.startAnimation(animate)
        view.visibility = View.GONE
    }

    //endregion

    //region slide left and right
    @JvmStatic
    @BindingAdapter(value = ["animateVisibilityLeftRight"], requireAll = false)
    fun animateVisibilityLeftRight(view: View?, boolean: Boolean) {
        animateVisibilityShown(view!!, boolean, time)
    }

    private fun animateVisibilityShown(view: View, visible: Boolean, time: Long) {
        view.animate().alpha(if (visible) 1f else 0f).setDuration(time).start()
        view.animate().alpha(if (visible) 1f else 0f).setDuration(time).start()
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }
    //endregion
}