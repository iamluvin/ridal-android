package tv.ridal.ui.cell

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import tv.ridal.util.Theme
import tv.ridal.ui.listener.InstantPressListener
import tv.ridal.ui.layout.Layout
import tv.ridal.R
import tv.ridal.util.Utils
import tv.ridal.ui.view.RTextView

class FilterCell(context: Context) : FrameLayout(context)
{
    private var nameView: RTextView
    private var valueView: RTextView
    private var pointerView: ImageView

    var filterName: String = ""
        set(value) {
            field = value

            nameView.text = filterName
        }

    var filterValue: String = ""
        set(value) {
            field = value

            valueView.text = filterValue
        }

    init
    {
        isClickable = true
        setOnTouchListener(InstantPressListener(this))

        setPadding(Utils.dp(20), Utils.dp(10), Utils.dp(15), Utils.dp(10))

        nameView = RTextView(context).apply {
            setTextColor(Theme.color(Theme.color_text))
            textSize = 17F
            typeface = Theme.typeface(Theme.tf_bold)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(nameView, Layout.ezFrame(
            Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
            Gravity.START or Gravity.CENTER_VERTICAL))

        valueView = RTextView(context).apply {
            setTextColor(Theme.color(Theme.color_main))
            textSize = 16.5F
            typeface = Theme.typeface(Theme.tf_normal)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(valueView, Layout.ezFrame(
            Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
            Gravity.END or Gravity.CENTER_VERTICAL,
            0, 0, 24, 0))

        val pointer = Theme.drawable(R.drawable.pointer_forward, Theme.color_main)
        pointerView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER

            setImageDrawable(pointer)
        }
        addView(pointerView, Layout.ezFrame(
            24, 24,
            Gravity.END or Gravity.CENTER_VERTICAL))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.UNSPECIFIED)
        )

        val availableWidth = measuredWidth - paddingLeft - paddingRight - Utils.dp(24)
        var width = availableWidth / 2

        valueView.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
            0
        )

        width = availableWidth - valueView.measuredWidth - Utils.dp(10)

        nameView.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
            0
        )
    }

}





































//