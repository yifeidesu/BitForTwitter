package com.robyn.bitty.timeline.drawer

import android.support.v7.app.AppCompatActivity
import android.view.View
import com.robyn.bitty.BasePresenter
import com.robyn.bitty.BaseView
import com.twitter.sdk.android.core.models.User

interface DrawerContract {

    interface View : BaseView<Presenter> {
        fun loadProfileImage(urlString: String, compressionQuality: Int)
        fun customUI(user: User)
        fun login()
    }

    interface Presenter : BasePresenter {
        fun composeTweet(activity: AppCompatActivity)
        fun verifyCredentials()
    }
}