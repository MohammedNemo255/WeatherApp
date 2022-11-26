package com.nemodroid.weatherapp.framework.presentation.fragment

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.Menu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nemodroid.weatherapp.R
import com.nemodroid.weatherapp.framework.presentation.activity.BaseActivity
import com.nemodroid.weatherapp.utils.handler.ErrorModel
import java.util.*

open class BaseFragment : Fragment() {

    //region default colors
    var toolbarMenuColor: Int = R.color.colorAccent
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initDefaults()
    }

    private fun initDefaults() {
        toolbarMenuColor = R.color.white
    }

    //region change action bar icon tint color
    fun menuItemsIconColor(menu: Menu, color: Int) {
        try {
            for (i in 0 until menu.size()) {
                val drawable = menu.getItem(i).icon
                if (drawable != null) {
                    drawable.mutate()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        drawable.colorFilter =
                            BlendModeColorFilter(
                                if (color == 0) ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.colorPrimary
                                ) else ContextCompat.getColor(requireActivity(), color),
                                BlendMode.SRC_IN
                            )
                    } else {
                        drawable.setColorFilter(
                            if (color == 0) ContextCompat.getColor(
                                requireActivity(),
                                R.color.colorPrimary
                            ) else ContextCompat.getColor(requireActivity(), color),
                            PorterDuff.Mode.SRC_IN
                        )
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    //endregion

    //region localization
     fun isArabic(): Boolean {
        return Locale.getDefault().language.toString().lowercase() == "AR".lowercase()
    }
    //endregion

    //region working with error model
    fun setErrorModel(
        errorModel: ErrorModel,
        msg: String,
        icon: Int,
        lottieRes: Int,
        withAction: Boolean? = false,
        actionText: String? = getString(R.string.actionRefresh)
    ) {
        var icon = icon
        var lottieRes = lottieRes
        errorModel.withError = true
        errorModel.actionText = actionText.toString()
        if (icon == 0) icon = R.drawable.no_data
        if (lottieRes == 0) lottieRes = R.raw.error_404_1
        errorModel.withLottie = true
        errorModel.errorImageRes = icon
        errorModel.lottieRes = lottieRes
        errorModel.errorMessage = msg
        errorModel.withAction = withAction == true
    }
    //endregion
}