package com.robyn.bitty.timeline

import android.support.v7.widget.RecyclerView
import android.util.Log
import com.robyn.bitty.data.DataSource
import com.robyn.bitty.timeline.drawer.DrawerActivity
import com.robyn.bitty.utils.schedule
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetcomposer.ComposerActivity
import io.reactivex.disposables.CompositeDisposable
import kotlin.math.max

class TimelinePresenter(
    val view: TimelineContract.View, private val dataSource: DataSource,
    override var mTimelineTypeCode: Int = 0
) :
    TimelineContract.Presenter {

    private var mCompositeDisposable = CompositeDisposable()

    var mAdapter: TimelineAdapter? = null
    var mTweets = ArrayList<Tweet>()
    var mTweetsUpdate = ArrayList<Tweet>()

    var mMostRecent: Long? = null // latest / most recent
    var mOldest: Long? = null // oldest

    private var mQueryString: String = ""

    init {
        view.mPresenter = this
        //mAdapter = TimelineAdapter(mTweets)
    }

    override fun setAdapterToRecyclerView(recyclerView: RecyclerView) {
        if (mAdapter == null) mAdapter = TimelineAdapter(mTweets)
        recyclerView.adapter = mAdapter
    }

    override fun disposeDisposables() {

        mCompositeDisposable.dispose()
    }

    // Initial loading. check if list not empty, use existing
    override fun start() {
        if (mTweets.isEmpty()) {
            loadTweets()
        } else {
            mAdapter?.notifyDataSetChanged()

            setActionbarSubtitle()
        }
    }

    private fun setActionbarSubtitle() {
        val subtitle = when (mTimelineTypeCode) {
            0 -> "Home"
            1 -> mQueryString
            else -> ""
        }

        view.setActionbarSubtitle(subtitle)
    }

    override fun composeTweet(activity: DrawerActivity) {

        val session = TwitterCore.getInstance().sessionManager.activeSession
        val intent = ComposerActivity.Builder(activity)
            .session(session)
            .createIntent()
        activity.startActivity(intent)
    }

    // home load new
    override fun loadNew() {
        loadTweets( sinceId = mMostRecent, maxId = null)
    }

    override fun loadTweets(q: String, sinceId: Long?, maxId: Long?) {

        setActionbarSubtitle("Loading...")

        when (mTimelineTypeCode) {
            HOME_TIMELINE_CODE -> {
                subscribeHome(sinceId, maxId)
            }

            SEARCH_TIMELINE_CODE -> {
                subscribeSearch(q)
            }
        }
    }

    private fun subscribeSearch(q: String) {
        mQueryString = q

        val searchTimelineDisposable = dataSource.searchObservable(q).schedule().subscribe(
            { data ->
                data?.tweets?.let {
                    if (it.isEmpty()) return@subscribe

                    setIdRange(it)
                    it.forEach { mTweets.add(0, it) }

                    view.updateRecyclerViewData()
                }
            },
            { err ->
                Log.e(TAG, err.message)
                setActionbarSubtitle()
                // todo view.show error msg
            },
            { setActionbarSubtitle() }
        )
        mCompositeDisposable.add(searchTimelineDisposable)
    }

    private fun setIdRange(it: List<Tweet>) {
        mMostRecent = it[0].id
        mOldest = it[it.lastIndex].id
    }

    override fun updateRecyclerViewUI(recyclerView: RecyclerView) {

        mAdapter?.updateRecyclerUI(mTweets, recyclerView)
    }

    private fun subscribeHome(sinceId: Long?, maxId: Long?) {
        val disposable = dataSource.homeObservable(sinceId, maxId).schedule().subscribe(
            { data ->
                data?.let {
                    it.forEach { mTweets.add(0, it) }
                    view.updateRecyclerViewData()
                    view.snackbarShowUpdateSize("${it.size} new tweets")
                    setIdRange(it)
                }
            },
            { err ->
                Log.e(TAG, err.message)
                setActionbarSubtitle()
                view.stopLoadingAnim()
            },
            {
                setActionbarSubtitle()
                view.stopLoadingAnim()
            }
        )
        mCompositeDisposable.add(disposable)
    }

    private fun setActionbarSubtitle(subtitle: String) {
        view.setActionbarSubtitle(subtitle)
    }

    fun setMaxId(maxId: Long) {
        mMostRecent = maxId
    }

    fun setMinId(minId: Long) {
        mOldest = minId
    }

    fun linkToId(url: String): String {
        val arr = url.split("/")
        val size = arr.size
        return arr[size - 1]
    }

    companion object {
        const val TAG = "TimelinePresenter"

        const val HOME_TIMELINE_CODE = 0
        const val SEARCH_TIMELINE_CODE = 1
    }
}