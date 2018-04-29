package com.robyn.bitty.timeline

import android.support.v7.widget.RecyclerView
import com.robyn.bitty.BasePresenter
import com.robyn.bitty.BaseView
import com.robyn.bitty.timeline.drawer.DrawerActivity

/**
 * Created by yifei on 11/27/2017.
 */

interface TimelineContract {
    interface View : BaseView<Presenter> {

        fun updateRecyclerViewData() // ?

        fun setActionbarTitle(subtitle: String)

        fun setAdapter(adapter: TimelineAdapter)

        fun stopLoadingAnim()
        fun snackbarShowUpdateSize(msg:String)
    }

    interface Presenter : BasePresenter {

        fun composeTweet(activity: DrawerActivity)

        fun disposeDisposables()

        fun loadTweets(q: String = "cat", maxId: Long? = null, sinceId: Long? = null)
        fun updateRecyclerViewUI(recyclerView: RecyclerView)
        fun setAdapterToRecyclerView(recyclerView: RecyclerView)

        var mTimelineTypeCode: Int

        fun loadNew()
        fun loadMore()
    }
}