package com.robyn.bitty.detail

import android.util.Log
import com.robyn.bitty.data.DataSource
import com.twitter.sdk.android.core.models.Tweet
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SoloPresenter(
    val view: SoloContract.View,
    val dataSource: DataSource,
    val tweetId: Long
) : SoloContract.Presenter {

    lateinit var mTweet: Tweet

    init {
        view.mPresenter = this
        //val user = User
    }

    fun tweetToShow_temp(tweet: Tweet?){ // todo temp

            tweet?.quotedStatus.also { println("qqq quote = ${it?.text}") }

            tweet?.retweetedStatus.also { println("qqq retweet = ${it?.text}") }

            tweet?.also { println("qqq tweet = ${it.text}") }

    }

    override fun getTweet(): Tweet {
        return mTweet
    }

    override fun load() {
        val single = dataSource.getTweetSingle(tweetId)

        single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
            { data ->
                tweetToShow_temp(data) // todo delete

                mTweet = data
                view.load(mTweet)

            },
            { err ->
                Log.e(TAG, err.message.toString())
            }
        )
    }

    companion object {
        const val TAG = "SoloPresenter"
    }
}