package tv.ridal

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tunjid.androidx.navigation.MultiStackNavigator
import com.tunjid.androidx.navigation.Navigator
import com.tunjid.androidx.navigation.multiStackNavigationController
import tv.ridal.util.Theme
import tv.ridal.ui.fade
import tv.ridal.ui.msg
import tv.ridal.ui.setBackgroundColor
import tv.ridal.ui.zoom
import tv.ridal.util.Utils

class AppActivity : BaseActivity()
{
    companion object
    {
        val tabs = intArrayOf(R.id.navigation, R.id.search)

        @Volatile
        private var INSTANCE: AppActivity? = null
        fun instance() =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppActivity().also {
                    INSTANCE = it
                }
            }

        fun currentFragment() : Fragment = instance().multiStackNavigator.current!!
    }

    val multiStackNavigator: MultiStackNavigator by multiStackNavigationController(
        tabs.size,
        R.id.content_container
    ) { index ->
        when(index)
        {
            0 -> {
                val f = CatalogFragment()
                f to f.stableTag
            }
            1 -> {
                val f = SearchFragment()
                f to f.stableTag
            }
            else -> Fragment() to "Fragment" // этого не произойдет
        }
    }

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        INSTANCE = this

        val contentView = LayoutInflater.from(this).inflate(R.layout.activity_application, null)
        setContentView(contentView)

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
            val insets = windowInsets.getInsets( WindowInsetsCompat.Type.navigationBars() )

            contentView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = insets.bottom
            }

            WindowInsetsCompat.CONSUMED
        }

        Utils.checkDisplaySize(this)
        updateStatusBar()

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.apply {
            multiStackNavigator.stackSelectedListener =
                { menu.findItem(tabs[it])?.isChecked = true }
            multiStackNavigator.transactionModifier = { incomingFragment ->
                val current = multiStackNavigator.current
                if (current is Navigator.TransactionModifier)
                {
                    current.augmentTransaction(
                        this,
                        incomingFragment
                    )
                }
                else {
                    zoom()
                }
            }
            multiStackNavigator.stackTransactionModifier = { fade() }

            setOnApplyWindowInsetsListener { v, insets -> insets }

            setOnItemSelectedListener {
                multiStackNavigator.show(tabs.indexOf(it.itemId)).let { true }
            }
            setOnItemReselectedListener {
                multiStackNavigator.activeNavigator.clear()
            }
        }

        bottomNavigationView.apply {
            backgroundTintList = ColorStateList.valueOf(
                Theme.color(Theme.color_bottomNavBg)
            )

            itemRippleColor = ColorStateList.valueOf(
                Theme.alphaColor(
                    Theme.mainColor,
                    0.05F
                )
            )

            itemIconTintList = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_checked)
                ),
                intArrayOf(
                    Theme.color(Theme.color_bottomNavIcon_inactive),
                    Theme.color(Theme.color_bottomNavIcon_active),
                )
            )
        }

        onBackPressedDispatcher.addCallback(this) { if ( ! multiStackNavigator.pop() ) finish() }
    }

    private fun updateStatusBar()
    {
        Theme.enableDarkStatusBar( window, ! Theme.isDark() )
    }
}

































//