package com.robyn.bitty.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import android.support.v7.widget.RecyclerView.*
import com.robyn.bitty.*
import com.robyn.bitty.timeline.TimelineAdapter
import com.robyn.bitty.utils.Fetch
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_hometimeline.*
import kotlinx.android.synthetic.main.fragment_hometimeline.view.*


/**
 * Home timeline
 *
 * Created by yifei on 7/18/2017.
 */

class HomeTimelineFragment : Fragment() {

    //lateinit var mRecyclerViewHome: RecyclerView
//    var mTweets: MutableList<Tweet> = ArrayList<Tweet>()
    lateinit var mAdapter: TimelineAdapter

//
//    // max / min id of current tweet list
//    private var mMaxId: Long? = null
//    private var mMinId: Long? = null

    private var mButtonLoadMore: Button? = null


//    var disposable: Disposable =
//    lateinit var disposable2: Disposable
    var disposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!disposable.isDisposed)
            disposable.dispose()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_hometimeline, container, false)

        Fetch.fetchTweets(context, null, true, view.home_timeline, view.progress_bar)

        // setup recyclerView
        val mHomeTimeline = view.home_timeline

        val layoutManager = LinearLayoutManager(activity)
        mHomeTimeline.layoutManager = layoutManager

        // set divider for recyclerView items
        val dividerItemDecoration = DividerItemDecoration(mHomeTimeline.context,
                layoutManager.orientation)
        mHomeTimeline.addItemDecoration(dividerItemDecoration)

        mHomeTimeline.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })

        view.swipe_refresh_layout.setOnRefreshListener {
            // Fetch new tweets, current max = this.min
            Fetch.fetchTweets(context, null, true, view.home_timeline, view.progress_bar)
            Log.i(TAG, "swipe_refresh_layout listener" )
            swipe_refresh_layout.isRefreshing = false
        }

        mButtonLoadMore = view.findViewById(R.id.load_more)
        mButtonLoadMore!!.visibility = View.GONE
        mButtonLoadMore!!.setOnClickListener {
            //mPullPrevTask.execute();
            mButtonLoadMore!!.visibility = View.GONE
            val m = LinearLayoutManager(activity)
            Log.i(TAG, "PullPrevTask executed")
        }
//
        with(view) {
            if (progress_bar!=null) {
                progress_bar.visibility = View.GONE
            }
        }

        return view
    }

    companion object {
        private val TAG = HomeTimelineFragment::class.java.simpleName

        fun newInstance(): HomeTimelineFragment {

            val args = Bundle()

            val fragment = HomeTimelineFragment()
            fragment.arguments = args
            return fragment
        }
    }

}


