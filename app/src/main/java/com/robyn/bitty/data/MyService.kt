package com.robyn.bitty.data

import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.models.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MyService {

    @GET("/1.1/users/show.json")
    fun show(@Query("user_id") id: Long): Call<User>

    @GET("/1.1/search/tweets.json")
    fun search(@Query("q") query: String): Call<List<Tweet>>

    @GET("/1.1/statuses/sample.json?")
    fun realSample(
        @Query("delimited") delimited: Boolean?,
        @Query("stall_warnings") stall_warnings: Boolean?
    ): Call<List<Tweet>>
}