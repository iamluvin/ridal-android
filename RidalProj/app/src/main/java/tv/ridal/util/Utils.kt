package tv.ridal.util

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import kotlin.math.ceil
import kotlin.math.floor

class Utils
{
    companion object
    {
        var density: Float = 1F //

        var displayWidth: Int = 0 //
        var displayHeight: Int = 0 //

        fun checkDisplaySize(context: Context)
        {
            val metrics = DisplayMetrics()
            (context as Activity).windowManager.defaultDisplay.getMetrics(metrics)

            density = metrics.density

            displayWidth = metrics.widthPixels
            displayHeight = metrics.heightPixels
        }

        fun dp(value: Int) : Int
        {
            if (value == 0) {
                return 0
            }
            return floor(density * value).toInt()
        }

        fun dp(value: Float) : Float
        {
            if (value == 0F) {
                return 0F
            }
            return ceil(density * value)
        }

        fun px(value: Int) : Int
        {
            if (value == 0) {
                return 0
            }
            return ceil(value / density).toInt()
        }

        fun px(value: Float) : Float
        {
            if (value == 0F) {
                return 0F
            }
            return ceil(value / density)
        }

        fun mapToFloat(v: Int, start: Int, end: Int) : Float
        {
            return (v - start) * 1F / (end - start)
        }
    }
}





































//