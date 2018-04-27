package com.robyn.bitty.data

import com.twitter.sdk.android.core.TwitterApiClient
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.models.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

class MyTwitterApiClient(session: TwitterSession) : TwitterApiClient(session) {

    val customService: MyService
        get() = getService(MyService::class.java)
}




