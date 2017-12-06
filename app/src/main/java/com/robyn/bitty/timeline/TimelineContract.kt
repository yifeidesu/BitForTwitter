package com.robyn.bitty.timeline

import com.robyn.bitty.BasePresenter
import com.robyn.bitty.BaseView
import io.reactivex.Observable

/**
 * Created by yifei on 11/27/2017.
 */

interface TimelineContract {
    interface View : BaseView<Presenter>
    interface Presenter : BasePresenter {
        //fun composeTweet(context: Context)
        fun isVerified(): Observable<Boolean>
        fun checkAuth():Boolean
        fun composeTweet(activity: TimelineActivity)
    }
}