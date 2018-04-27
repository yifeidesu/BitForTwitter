package com.robyn.bitty.timeline.drawer

import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.robyn.bitty.data.DataSource
import com.robyn.bitty.utils.schedule
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.models.User
import com.twitter.sdk.android.tweetcomposer.ComposerActivity
import io.reactivex.disposables.CompositeDisposable

class DrawerPresenter(
    val view: DrawerContract.View,
    private val dataSource: DataSource
) :
    DrawerContract.Presenter {

    override fun start() {
    }

    var mCompositeDisposable = CompositeDisposable()
    lateinit var mUser: User

    /**
     * From the passed-in activity, go to the sdk's compose activity
     */
    override fun composeTweet(activity: AppCompatActivity) {
        val session = TwitterCore.getInstance().sessionManager.activeSession
        val intent = ComposerActivity.Builder(activity)
            .session(session)
            .createIntent()
        activity.startActivity(intent)
    }

    override fun verifyCredentials() {

        dataSource.verifyCredentialsObs().schedule().subscribe(
            { data ->
                data?.let { view.customUI(it)}
            },
            { err ->
                Log.e(TAG, err.message)

            }

        ).also { mCompositeDisposable.add(it) }
    }

    companion object {
        const val TAG = "DrawerPresenter"
    }
}