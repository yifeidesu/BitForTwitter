package com.robyn.bitty.timeline.drawer

import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.robyn.bitty.BasePresenter
import com.robyn.bitty.BaseView

interface DrawerContract {
    interface View:BaseView<Presenter> {

        fun loadProfileImage(urlString: String, compressionQuality: Int)
    }

    interface Presenter : BasePresenter {

        fun composeTweet(activity: AppCompatActivity)

        //fun loadProfileImage()
    }
}