package tv.ridal.ui.layout

import android.content.Context
import tv.ridal.ui.cell.CheckCell

class SingleCheckGroup(context: Context) : VLinearLayout(context)
{
    private var checkCells: ArrayList<CheckCell> = ArrayList()

    init
    {

    }

    override fun setEnabled(enabled: Boolean)
    {
        super.setEnabled(enabled)

        checkCells.forEach {
            it.isEnabled = enabled
        }
    }

    fun addCheck(text: String, onCheck: (() -> Unit)? = null)
    {
        val checkCell = CheckCell(context).apply {
            this.text = text

            setOnClickListener {
                if (this.isChecked) return@setOnClickListener

                onCheck?.invoke()
                check(this.text)
            }
        }
        addView(checkCell)

        checkCells.add(checkCell)
    }

    fun check(text: String, animated: Boolean = true)
    {
        val checkedCell = checkCells.find {
            it.isChecked
        }
        checkedCell?.setChecked(false, animated)

        val toCheck = checkCells.find {
            it.text == text
        }
        toCheck?.setChecked(true, animated)
    }

    fun setCheckColor(color: Int)
    {
        checkCells.forEach {
            it.checkColor = color
        }
    }

    fun setTextColor(color: Int)
    {
        checkCells.forEach {
            it.textColor = color
        }
    }

    fun setTextColorChecked(color: Int)
    {
        checkCells.forEach {
            it.textColorChecked = color
        }
    }

    fun moveCheckedOnTop()
    {
        val checkedCell = checkCells.find { it.isChecked }
        if ( indexOfChild(checkedCell) == 0 ) return

        val prevChecked = getChildAt(0) as CheckCell
        removeView(prevChecked)
        addView( prevChecked, checkCells.indexOf(prevChecked) )

        removeView(checkedCell)
        addView( checkedCell, 0 )
    }

    fun currentChecked(): String
    {
        val found = checkCells.find {
            it.isChecked
        }

        return found?.text ?: "null"
    }

    fun size(): Int = checkCells.size
}





































//