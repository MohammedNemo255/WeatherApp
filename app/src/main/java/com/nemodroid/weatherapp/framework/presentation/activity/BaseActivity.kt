package com.nemodroid.weatherapp.framework.presentation.activity

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.nemodroid.weatherapp.R

open class BaseActivity : AppCompatActivity() {

    lateinit var context: Context

    //region default colors
    var toolbarMenuColor: Int = R.color.colorAccent
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context = this

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
                                    context,
                                    R.color.colorPrimary
                                ) else ContextCompat.getColor(context, color),
                                BlendMode.SRC_IN
                            )
                    } else {
                        drawable.setColorFilter(
                            if (color == 0) ContextCompat.getColor(
                                context,
                                R.color.colorPrimary
                            ) else ContextCompat.getColor(context, color),
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

}