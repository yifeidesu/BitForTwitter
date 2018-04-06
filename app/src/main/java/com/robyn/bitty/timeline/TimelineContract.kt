package com.robyn.bitty.timeline

import android.support.v7.widget.RecyclerView
import com.robyn.bitty.BasePresenter
import com.robyn.bitty.BaseView
import com.robyn.bitty.timeline.drawer.DrawerActivity
import io.reactivex.Observable

/**
 * Created by yifei on 11/27/2017.
 */

interface TimelineContract {
    interface View : BaseView<Presenter> {

        fun updateRecyclerViewData() // ?

        fun setActionbarSubtitle(subtitle: String)

        fun setAdapter(adapter: TimelineAdapter)

        fun stopLoadingAnim()
    }

    interface Presenter : BasePresenter {

        fun isVerified(): Observable<Boolean>
        fun checkAuth(): Boolean
        fun composeTweet(activity: DrawerActivity)

        fun disposeDisposables()

        fun loadTweets(q:String = "cat")
        fun updateRecyclerViewUI(recyclerView: RecyclerView)
        fun setAdapterToRecyclerView(recyclerView: RecyclerView)

        var mTimelineTypeCode:Int
    }
}