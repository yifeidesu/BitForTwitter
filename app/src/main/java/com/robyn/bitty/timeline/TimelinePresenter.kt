package com.robyn.bitty.timeline

import android.support.v7.widget.RecyclerView
import android.util.Log
import com.robyn.bitty.data.DataSource
import com.robyn.bitty.timeline.drawer.DrawerActivity
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.models.Search
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetcomposer.ComposerActivity
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TimelinePresenter(
    val view: TimelineContract.View, private val dataSource: DataSource,
    override var mTimelineTypeCode: Int = 0
) :
    TimelineContract.Presenter {

    private var mCompositeDisposable = CompositeDisposable()

    var mAdapter: TimelineAdapter? = null
    var mTweets = ArrayList<Tweet>()

    var mMostRecent: Long? = null // latest / most recent
    var mLeastRecent: Long? = null // oldest

    private var mQueryString: String = ""

    init {
        view.mPresenter = this
    }

    override fun setAdapterToRecyclerView(recyclerView: RecyclerView) {
        if (mAdapter == null) mAdapter = TimelineAdapter(mTweets)
        recyclerView.adapter = mAdapter
    }

    // Invoke in ac onStop()
    override fun disposeDisposables() {
        mCompositeDisposable.dispose()
    }

    // Initial loading. check if list not empty, use existing
    override fun start() {
        if (mTweets.isEmpty()) {
            loadTweets()
        } else {
            mAdapter?.notifyDataSetChanged()
            setActionbarTitle()
        }
    }

    /**
     * set actionbar title based on timeline type: home/search
     */
    private fun setActionbarTitle() {
        val subtitle = when (mTimelineTypeCode) {
            HOME_TIMELINE_CODE -> "Home"
            SEARCH_TIMELINE_CODE -> mQueryString
            else -> ""
        }

        view.setActionbarTitle(subtitle)
    }

    /**
     * go to the compose activity to compose a tweet.
     *
     */
    override fun composeTweet(activity: DrawerActivity) {

        val session = TwitterCore.getInstance().sessionManager.activeSession
        val intent = ComposerActivity.Builder(activity)
            .session(session)
            .createIntent()
        activity.startActivity(intent)
    }

    // refresh timeline for new items
    override fun loadNew() {
        loadTweets(sinceId = mMostRecent)
    }

    override fun loadMore() {
        loadTweets(maxId = mLeastRecent)
    }

    /**
     * @param q query string. optional
     * @param maxId fetch tweets earlier than this time
     * @param sinceId fetch tweets since this time
     */
    override fun loadTweets(q: String, maxId: Long?, sinceId: Long?) {

        setActionbarTitle("Loading...")

        when (mTimelineTypeCode) {
            HOME_TIMELINE_CODE -> {
                subscribeHome(sinceId, maxId)
            }

            SEARCH_TIMELINE_CODE -> {
                subscribeSearch(q)
            }
        }

        printListRange(mTweets)
    }

    /**
     * debug method. print current [mTweets] id range.
     */
    private fun printListRange(list: List<Tweet>, msg: String = "") {
        if (list.isEmpty()) return
        val first = list[0].id
        val last = list[list.lastIndex].id
        println("$msg\n")
        Log.d(
            TAG, "printListRange\n" +
                    "list.size  = ${list.size}\n" +
                    "first.id   = ${first}\n" +
                    "last.id    = ${last}\n"
        )
    }

    /**
     * @param q query string
     */
    private fun subscribeSearch(q: String) {
        mQueryString = q

        dataSource.searchCall(q).enqueue(object : Callback<Search?> {
            override fun onFailure(call: Call<Search?>?, t: Throwable?) {
                t?.let { handleError(it) }
            }

            override fun onResponse(call: Call<Search?>?, response: Response<Search?>?) {
                response?.body()?.tweets?.let {

                    (it as List<Tweet>).sortedByDescending { it.id }
                    updateList(it)
                    setIdRange()

                    setActionbarTitle()
                    view.updateRecyclerViewData()
                    view.stopLoadingAnim()
                }
            }
        })

//        val searchTimelineDisposable = dataSource.searchSingle(q)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { data ->
//                    data?.tweets?.let {
//                        if (it.isEmpty()) return@subscribe
//                        it.forEach { mTweets.add(0, it) }
//
//                        setIdRange()
//
//                        view.updateRecyclerViewData()
//                    }
//                },
//                { err ->
//                    handleError(err)
//                }
//
//            )
//        mCompositeDisposable.add(searchTimelineDisposable)
    }

    /**
     * update tweets' time range
     * @param list is the updated tweet list
     */
//    private fun setIdRange(list: List<Tweet>) {
//        if (list.isEmpty()) return
//
//        mMostRecent = list[0].createdAt.toLong()
//        mLeastRecent = list[list.lastIndex].createdAt.toLong()
//
//    }

    private fun setIdRange() {
        if (mTweets.isEmpty()) return
        mMostRecent = mTweets[0].id
        mLeastRecent = mTweets[mTweets.lastIndex].id
    }

    /**
     * update recyclerview.adapter with updated tweet list.
     * First update this presenter's mTweets field,
     * then call this methods to update the recyclerView.adapter's data
     *
     * @param recyclerView this recyclerView will update ui with mTweets
     *
     */
    override fun updateRecyclerViewUI(recyclerView: RecyclerView) {
        mAdapter?.updateRecyclerUI(mTweets)
    }

    /**
     * subscribe to home Observable.
     *
     * @param sinceId   fetch tweets after this time
     * @param maxId     fetch tweets earlier than this time
     *
     */
    private fun subscribeHome(sinceId: Long?, maxId: Long?) {
        dataSource.homeCall(sinceId = sinceId, maxId = maxId)
            .enqueue(object : Callback<MutableList<Tweet>> {
                override fun onFailure(call: Call<MutableList<Tweet>>?, t: Throwable?) {
                    t?.let { handleError(it) }
                }

                override fun onResponse(
                    call: Call<MutableList<Tweet>>?,
                    response: Response<MutableList<Tweet>>?
                ) {
                    response?.body()?.let {
                        it.let {

                            (it as List<Tweet>).sortedByDescending { it.id }
                            updateList(it)

                            printListRange(it, "update list")


                            setIdRange()
                            printListRange(mTweets, "home")

                            mAdapter?.setTweets(mTweets)

                            view.updateRecyclerViewData()
                            view.stopLoadingAnim()

                            setActionbarTitle()
                        }
                    }
                }
            })
    }

    private fun updateList(it: MutableList<Tweet>) {
        it.forEach {
            if (mTweets.size < 100) {
                mTweets.add(0, it)
            } else {
                mTweets.removeAt(mTweets.lastIndex)
                mTweets.add(0, it)
            }
        }
    }

//        val disposable = dataSource.homeSingle(sinceId, maxId)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                {
//                    it?.let {
//                        setIdRange(mTweets)
//
//                        it.forEach {
//                            mTweets.add(it)
//
//                            if (mTweets.size >= 100) {
//                                mTweets.removeAt(mTweets.lastIndex)
//                            }
//                        }
//                        view.updateRecyclerViewData()
//                        setActionbarTitle()
//                        view.stopLoadingAnim()
//                    }
//                },
//                {
//                    handleError(it)
//                }
//            )
//
//        mCompositeDisposable.add(disposable)

    private fun handleError(err: Throwable) {
        Log.e(TAG, "onError ${err.message}")
        setActionbarTitle()
        view.stopLoadingAnim()
    }

    /**
     * Update action bar title by fg's callback.
     * i.e. when start loading new tweets, set title to "Loading",
     *      when loading finishes or network error, set title back to "Home" or query string.
     *
     * @param title is the actionbar title
     */
    private fun setActionbarTitle(title: String) {
        view.setActionbarTitle(title)
    }

    fun setMaxId(maxId: Long) {
        mMostRecent = maxId
    }

    fun setMinId(minId: Long) {
        mLeastRecent = minId
    }

    fun linkToId(url: String): String {
        val arr = url.split("/")
        val size = arr.size
        return arr[size - 1]
    }

    companion object {
        const val TAG = "TimelinePresenter"

        // Timeline code indicates what type of timeline is currently showing
        const val HOME_TIMELINE_CODE = 0
        const val SEARCH_TIMELINE_CODE = 1
    }
}