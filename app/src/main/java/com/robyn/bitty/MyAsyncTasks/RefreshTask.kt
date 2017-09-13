package com.robyn.bitty.MyAsyncTasks

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import com.robyn.bitty.R
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import java.util.*

/**
 * anystask for refresh
 *
 * all tweet list type is list. when use, convert type to mutable only once
 *
 * Created by yifei on 9/11/2017.
 */


class RefreshTask() : AsyncTask<RefreshTask.MyParams, Unit, List<Tweet>>() {
    val TAG = javaClass.simpleName
    var mTweetsUpdate: List<Tweet> = ArrayList()
    var mostRecentId: Long? = null
    var leastRecentId: Long? = null

    interface RefreshResponse {
        fun fetchComplete(list: List<Tweet>)
    }

    var refreshResponse:RefreshResponse? = null

    /*
    for RefreshTask constructor
     */
    class MyParams(val myContext: Context,
                   var refreshResponse: RefreshResponse)

    override fun doInBackground (vararg p0: MyParams): List<Tweet> {
        Log.i(TAG, "RefreshTask doInBackground called")

        return fetchTweetList(p0)
    }

    private fun RefreshTask.fetchTweetList(p0: Array<out MyParams>): List<Tweet> {
        val context = p0[0].myContext
        refreshResponse = p0[0].refreshResponse

        val client = TwitterCore.getInstance().apiClient
        val statusesService = client.statusesService
        val call = statusesService.homeTimeline(null,
                mostRecentId,
                null, // last call's sinceid = current call's max(least recent) id
                false, false, true, true)// Returns results with an ID greater than, more recent than this
        val callback = object : Callback<List<Tweet>>() {
            override fun success(result: Result<List<Tweet>>) {
                Log.i(TAG, "RefreshTask call success result.size = " + result.data.size)

                if (result.data.isEmpty()) {

                    Toast.makeText(context, context.getString(R.string.no_new_tweets), Toast.LENGTH_LONG).show()
                } else {
                    mTweetsUpdate = result.data


                    Log.i(TAG, "call enq doinbg fetch complete size = ${mTweetsUpdate.size}")

                    //UpdateUITask().execute()
                    setMostRecentId()
                    //setLeastRecentId()

                }
            }

            override fun failure(exception: TwitterException) {
                Toast.makeText(context,
                        "Something wrong with networking.", Toast.LENGTH_LONG).show()
                Log.i(TAG, "hometimeline get tweets fails --> " + exception.message)
            }
        }

        call.enqueue(callback)

        Log.i(TAG, "doinbg fetch complete size = ${mTweetsUpdate.size}")


        return mTweetsUpdate
    }

    override fun onPostExecute(tweets: List<Tweet>) {
        super.onPostExecute(tweets)

        Log.i(TAG, "fetch complete size = ${tweets.size}")
        Log.i(TAG, "RefreshTask onPostExecute called")
        if (refreshResponse != null) {

            Log.i(TAG, "refresh response is not empty")
            refreshResponse!!.fetchComplete(tweets)

            Log.i(TAG, "fetch complete size = ${tweets.size}")
        } else {
            Log.i(TAG, "refresh response is empty")
        }

        //mProgressBar.setVisibility(View.GONE)
        cancel(false)
    }

    private fun setMostRecentId() {
        mostRecentId = mTweetsUpdate[0].id
    }

//    private fun setLeastRecentId() {
//        leastRecentId = mTweetsUpdate[.size - 1].id
//    }
}