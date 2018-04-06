package com.robyn.bitty.timeline

import android.support.v7.widget.RecyclerView
import android.util.Log
import com.robyn.bitty.data.DataSource
import com.robyn.bitty.timeline.drawer.DrawerActivity
import com.robyn.bitty.utils.myLog
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetcomposer.ComposerActivity
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable

class TimelinePresenter(
    val view: TimelineContract.View, private val dataSource: DataSource,
    var mTimelineTypeCode: Int = 0
) :
    TimelineContract.Presenter {

    var mCompositeDisposable = CompositeDisposable()

    var mAdapter: TimelineAdapter? = null
    var mTweets = ArrayList<Tweet>()
    var mTweetsUpdate = ArrayList<Tweet>()

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

    private fun <T> Observable<T>.schedule(): Observable<T> {

        return this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    // Initial loading. check if list not empty, use existing
    override fun start() {
        if (mTweets.isEmpty()) {
            loadTweets()
        } else {
            mAdapter?.notifyDataSetChanged()

            setSubtitle()
        }
    }

    private fun setSubtitle() {
        val subtitle = when (mTimelineTypeCode) {
            0 -> "Home"
            1 -> mQueryString
            else -> ""
        }

        view.setActionbarSubtitle(subtitle)
    }

    override fun isVerified(): Observable<Boolean> {

        val observable = Observable.defer(Callable<ObservableSource<Boolean>> {
            try {
                return@Callable Observable.just(checkAuth())
            } catch (e: Exception) {
                myLog(this.toString(), e.toString())
                return@Callable null
            }
        })

        mCompositeDisposable.add(observable as Disposable) // todo can i ?

        return observable
    }

    override fun checkAuth(): Boolean {
        return true
    }

    override fun composeTweet(activity: DrawerActivity) {

        val session = TwitterCore.getInstance().sessionManager.activeSession
        val intent = ComposerActivity.Builder(activity)
            .session(session)
            .createIntent()
        activity.startActivity(intent)
    }

    override fun loadTweets() {

        setActionbarSubtitle("Loading...")

        when (mTimelineTypeCode) {
            HOME_TIMELINE_CODE -> {
                subscribeHome()
            }

            SEARCH_TIMELINE_CODE -> {
                subscribeSearch()
            }
        }
    }

    private fun subscribeSearch(q: String = "cat") {
        mQueryString = q

        val searchTimelineDisposable = dataSource.searchObservable(q).schedule().subscribe(
            { data ->
                data?.tweets?.let {
                    it.forEach { mTweets.add(0, it) }
                    view.updateRecyclerViewData()
                }
            },
            { err ->
                Log.e(TAG, err.message)
                setSubtitle()
                // todo view.show error msg
            },
            { setSubtitle() }
        )
        mCompositeDisposable.add(searchTimelineDisposable)
    }

    override fun updateRecyclerViewUI(recyclerView: RecyclerView) {

        mAdapter?.updateRecyclerUI(mTweets, recyclerView)
    }

    private fun subscribeHome() {
        val disposable = dataSource.homeObservable().schedule().subscribe(
            { data ->
                data?.let {
                    it.forEach { mTweets.add(0, it) }
                    view.updateRecyclerViewData()
                }
            },
            { err ->
                Log.e(TAG, err.message)
                //view.setActionbarSubtitle("Home")
                setSubtitle()
            },
            {
                //setActionbarSubtitle("Home")
                setSubtitle()

            }
        )
        mCompositeDisposable.add(disposable)
    }

    private fun setActionbarSubtitle(subtitle: String) {
        view.setActionbarSubtitle(subtitle)
    }

    companion object {
        const val TAG = "TimelinePresenter"

        const val HOME_TIMELINE_CODE = 0
        const val SEARCH_TIMELINE_CODE = 1
    }
}