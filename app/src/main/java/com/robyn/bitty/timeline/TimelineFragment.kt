package com.robyn.bitty.timeline

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.*
import com.robyn.bitty.R
import com.robyn.bitty.databinding.TimelineFgBinding
import com.robyn.bitty.utils.EndlessRecyclerOnScrollListener
import com.robyn.bitty.utils.makeSnackbar

/**
 * Home timeline
 *
 */

class TimelineFragment : Fragment(), TimelineContract.View {

    override lateinit var mPresenter: TimelineContract.Presenter

    lateinit var mCallback: TimelineFragmentCallback
    lateinit var mTimelineRecyclerView: RecyclerView
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    // To command the hosting activity
    interface TimelineFragmentCallback {
        fun setActionbarSubtitle(title: String)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        mCallback = context as TimelineFragmentCallback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //val view = inflater.inflate(R.layout.timeline_fg, container, false)

        val binding: TimelineFgBinding = DataBindingUtil
            .inflate(inflater, R.layout.timeline_fg, container, false)

        setHasOptionsMenu(true)

        // setup recyclerView
        mTimelineRecyclerView = binding.timelineRecyclerview

        val layoutManager = LinearLayoutManager(activity)
        mTimelineRecyclerView.layoutManager = layoutManager

        // At the end of the recyclerview list, swipe up to load more
        mTimelineRecyclerView.addOnScrollListener(object :
            EndlessRecyclerOnScrollListener(layoutManager) {
            override fun onLoadMore(current_page: Int) {
                // todo: fetch, max = last call's min / oldest
                // mpresenter
            }
        })

        // set divider for recyclerView items
        val dividerItemDecoration = DividerItemDecoration(
            mTimelineRecyclerView.context,
            layoutManager.orientation
        )
        mTimelineRecyclerView.addItemDecoration(dividerItemDecoration)

        waitPresenter()

        mPresenter.setAdapterToRecyclerView(mTimelineRecyclerView)

        // Load tweets to fragment
        mPresenter.start()

        mSwipeRefreshLayout = binding.swipeRefreshLayout

        // At the start of the recyclerview list / swipe layout, swipe down to load new
        mSwipeRefreshLayout.setOnRefreshListener {
            mPresenter.loadNew()
        }

        return binding.getRoot()
        //return view
    }

    override fun onDestroy() {

        mPresenter.disposeDisposables()

        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

        if (mPresenter.mTimelineTypeCode != TimelinePresenter.SEARCH_TIMELINE_CODE) return

        inflater?.inflate(R.menu.menu_search_timeline, menu)

        val searchMenuItem = menu?.findItem(R.id.search_view) // MenuItem
        val searchView = searchMenuItem?.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.queryHint = "Search..."

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { mPresenter.loadTweets(query) }

                searchView.isIconified = true // Clear text
                searchView.isIconified = true // Close collapse

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // todo give history hint
                return true
            }
        })
    }

    /**
     * Set adapter to this fragment's RecyclerView
     */
    override fun setAdapter(adapter: TimelineAdapter) {
        mTimelineRecyclerView.adapter = adapter
    }

    override fun updateRecyclerViewData() {
        mPresenter.updateRecyclerViewUI(mTimelineRecyclerView)
    }

    override fun snackbarShowUpdateSize(msg: String) {
        // todo temp snackbar shows new list size
        view?.let { makeSnackbar(it, msg) }
    }

    override fun stopLoadingAnim() {
        mSwipeRefreshLayout.isRefreshing = false
    }

    override fun setActionbarSubtitle(subtitle: String) {
        mCallback.setActionbarSubtitle(subtitle)
    }

    private fun waitPresenter() {
//        if (!(::mPresenter.isInitialized)) {
//            Thread.sleep(500)
//        }

        if (!(::mPresenter.isInitialized)) {
            Thread.sleep(100)
        }
    }

    companion object {
        private val TAG = TimelineFragment::class.java.simpleName

        fun newInstance(): TimelineFragment {
            return TimelineFragment()
        }
    }
}


