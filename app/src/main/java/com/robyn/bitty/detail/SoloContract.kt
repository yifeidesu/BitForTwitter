package com.robyn.bitty.detail

import com.robyn.bitty.BaseView
import com.twitter.sdk.android.core.models.Tweet

interface SoloContract {

    interface View : BaseView<Presenter> {
        fun load(tweet: Tweet)
    }

    interface Presenter {
        fun load()
        fun getTweet():Tweet
    }
}