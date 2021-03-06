package tv.ridal

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isEmpty
import androidx.core.widget.NestedScrollView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import tv.ridal.hdrezka.*
import tv.ridal.ui.cell.PointerCell
import tv.ridal.ui.cell.SearchResultCell
import tv.ridal.ui.layout.Layout
import tv.ridal.ui.layout.VLinearLayout
import tv.ridal.ui.setBackgroundColor
import tv.ridal.ui.view.SearchBar
import tv.ridal.util.Locale
import tv.ridal.util.Theme
import tv.ridal.util.Utils

class SearchInputFragment : BaseAppFragment()
{
    override val stableTag: String
        get() = "SearchInputFragment"


    private lateinit var rootFrame: FrameLayout
    private lateinit var searchBar: SearchBar
    private lateinit var frame: FrameLayout
    private lateinit var scroll: NestedScrollView

    private lateinit var layout: VLinearLayout
    private var layoutAnimator: ValueAnimator = ValueAnimator().apply {
        duration = 70

        addUpdateListener {
            val value = it.animatedValue as Float
            val scale = 0.99F + 0.01F * value

            layout.apply {
                alpha = value
                scaleX = scale
                scaleY = scale
            }
        }
    }

    private val requestQueue: RequestQueue = App.instance().requestQueue
    private val searchRequestTag: String = "searchRequestTag"


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        createUI()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return rootFrame
    }

    override fun onResume()
    {
        super.onResume()

        searchBar.showKeyboard()
    }

    override fun onPause()
    {
        super.onPause()

        searchBar.hideKeyboard()
    }


    private fun createUI()
    {
        createInputBar()

        layout = VLinearLayout(context).apply {
            setPadding(0, Utils.dp(8), 0, Utils.dp(8))
        }

        scroll = NestedScrollView(context).apply {
            addView(layout)
        }

        frame = FrameLayout(context).apply {
            addView(scroll)
        }

        rootFrame = FrameLayout(context).apply {
            setBackgroundColor( Theme.color_bg )

            addView(searchBar, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT
            ))

            addView(frame, Layout.frame(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT,
                Gravity.TOP,
                0, searchBar.measuredHeight, 0, 0
            ))
        }
    }

    private fun createInputBar()
    {
        searchBar = SearchBar(context).apply {
            setPadding(0, Utils.dp(30), 0, 0)

            setBackgroundColor(
                Theme.overlayColor( Theme.color_bg, 0.04F )
            )

            measure(0, 0)
        }

        searchBar.apply {
            onBack {
                finish()
            }

            onStopTyping {
                loadSearchResults(it)
            }

            onTextChange {
                requestQueue.cancelAll(searchRequestTag)
            }

            onTextClear {
                if (layout.isEmpty()) return@onTextClear

                if ( layoutAnimator.isRunning ) layoutAnimator.cancel()

                layoutAnimator.apply {
                    setFloatValues(layout.alpha, 0F)

                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?)
                        {
                            super.onAnimationEnd(animation)

                            layout.removeAllViews()

                            removeListener(this)
                        }
                    })

                    start()
                }
            }
        }
    }


    private fun loadSearchResults(searchText: String)
    {
        requestQueue.cancelAll(searchRequestTag)

        val url = "https://rezka.ag/engine/ajax/search.php"
        val request = object : StringRequest(
            Request.Method.POST,
            url,
            { response ->
                val results = Parser.parseSearchResults(response)

                results?.let {
                    showSearchResults(results = it.first, hasMore = it.second, searchText)
                }
            },
            {
                println("ERROR!")
            }
        )
        {
            override fun getParams(): MutableMap<String, String>
            {
                return HashMap<String, String>().apply {
                    put("q", searchText)
                }
            }
        }.apply {
            tag = searchRequestTag
        }

        requestQueue.add(request)
    }

    private fun showSearchResults(results: ArrayList<SearchResult>, hasMore: Boolean, searchText: String)
    {
        val views = ArrayList<View>()
        for (i in results.indices)
        {
            val r = results[i]

            val needDivider = hasMore || (i != results.lastIndex)
            val searchResultCell = SearchResultCell(context, needDivider).apply {
                movieName = r.movieName
                movieData = r.movieData
                movieRating = r.movieRating

                setOnClickListener { _ ->
                    openMovie(r)
                }
            }
            views.add(searchResultCell)
        }

        if (hasMore)
        {
            val allResultsCell = PointerCell(context).apply {
                text = Locale.string(R.string.viewAllResults)
                textColor = Theme.mainColor

                setOnClickListener {
                    openMovies(searchText)
                }
            }
            views.add(allResultsCell)
        }

        if ( layout.isEmpty() )
        {
            layoutAnimator.apply {
                setFloatValues(0F, 1F)

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?)
                    {
                        super.onAnimationStart(animation)

                        layout.alpha = 0F

                        views.forEach {
                            layout.addView(it)
                        }

                        removeListener(this)
                    }
                })
            }
        }
        else // if ( layout.isNotEmpty() )
        {
            layoutAnimator.apply {
                setFloatValues(1F, 0F)

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?)
                    {
                        super.onAnimationEnd(animation)

                        layout.removeAllViews()
                        views.forEach {
                            layout.addView(it)
                        }

                        setFloatValues(0F, 1F)
                        start()

                        removeListener(this)
                    }
                })
            }
        }

        layoutAnimator.start()
    }

    private fun openMovie(searchResult: SearchResult)
    {
        val movie = Movie(
            searchResult.movieName,
            searchResult.movieUrl
        )
        startFragment(
            MovieFragment.newInstance(movie)
        )
    }

    private fun openMovies(searchText: String)
    {
        val args = MoviesFragment.Arguments().apply {
            url = "https://rezka.ag/search/?do=search&subaction=search&q=${searchText}"
            title = searchText
            filters = Filters.NO_FILTERS
        }
        startFragment(
            MoviesFragment.newInstance(args)
        )
    }

}


































//