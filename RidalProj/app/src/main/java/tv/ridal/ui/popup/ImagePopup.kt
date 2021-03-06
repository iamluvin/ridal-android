package tv.ridal.ui.popup

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import tv.ridal.App
import tv.ridal.ui.asBitmap
import tv.ridal.ui.layout.Layout
import tv.ridal.ui.msg
import tv.ridal.util.Theme
import tv.ridal.util.Utils

class ImagePopup(context: Context) : Popup(context)
{
    private lateinit var popupView: FrameLayout
    private lateinit var imageView: ImageView


    init
    {
        dim = 0.5F

        createUI()
    }

    private fun createUI()
    {
        imageView = ImageView(context).apply {
            background = Theme.rect( Theme.mainColor )
            scaleType = ImageView.ScaleType.CENTER_CROP

            isClickable = true
            setOnClickListener {
                dismiss()
            }
        }

        popupView = FrameLayout(context).apply {
//            background = Theme.rect( Theme.mainColor )

            addView(imageView, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT
            ))
        }

        val w = Utils.displayWidth - Utils.dp(40 * 2)
        val h = (w * 1.58).toInt()

        setContentView(popupView, Layout.frame(
            w, h,
            Gravity.CENTER
        ))
    }

    fun setImageDrawable(drawable: Drawable)
    {
        val rd = RoundedBitmapDrawableFactory.create(
            App.instance().resources,
            drawable.asBitmap()
        ).apply {
            cornerRadius = Utils.dp(15F)
        }

        imageView.setImageDrawable(drawable)
    }

}


































//