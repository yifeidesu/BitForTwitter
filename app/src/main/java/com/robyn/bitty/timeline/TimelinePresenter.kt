package com.robyn.bitty.timeline

import com.robyn.bitty.data.RemoteDataSource
import com.robyn.bitty.utils.myLog
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.tweetcomposer.ComposerActivity
import io.reactivex.Observable
import io.reactivex.ObservableSource
import java.util.concurrent.Callable

/**
 * Created by yifei on 11/27/2017.
 */
class TimelinePresenter(view: TimelineContract.View, dataSource: RemoteDataSource)
    : TimelineContract.Presenter {

    init {
        view.presenter = this
    }

    override fun start() {
    }

    override fun isVerified(): Observable<Boolean> {
        return Observable.defer(Callable<ObservableSource<Boolean>> {
            try {
                return@Callable Observable.just(checkAuth())
            } catch (e: Exception) {
                myLog(this.toString(), e.toString())
                return@Callable null
            }
        })
    }

    override fun checkAuth(): Boolean {
        return true
    }

    override fun composeTweet(activity:MainActivity) {
        //    fun composeTweet() {
        val session = TwitterCore.getInstance().sessionManager.activeSession
        val intent = ComposerActivity.Builder(activity)
                .session(session)
                .createIntent()
        activity.startActivity(intent)
//    }
    }

    /**
     * Go to the default compose activity
     */
//    override fun composeTweet(context: Context) {
//        val session = TwitterCore.getInstance().sessionManager
//                .activeSession
//        val intent = ComposerActivity.Builder(context)
//                .session(session)
//                .createIntent()
//        startActivity(intent, context)
//    }
}