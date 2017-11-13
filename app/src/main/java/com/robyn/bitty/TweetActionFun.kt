package com.robyn.bitty

import android.content.Context
import android.util.Log
import android.widget.ImageView
import com.twitter.sdk.android.core.models.Tweet

/**
 * Created by yifei on 9/18/2017.
 */

val TAG_TWEET_ACTIONS = "TweetActionFuns"

/**
 * tweet
 */
fun favoAction(context: Context, tweet: Tweet, favoImageView: ImageView) {
    ColorToggle.showHeartColor(tweet.favorited, favoImageView, context)

    favoImageView.setOnClickListener {
        ColorToggle.toggleFavo(tweet.id, favoImageView, context)
        Log.i(TAG_TWEET_ACTIONS, "toggle color called")
    }
}

/**
 *  tweetId + isFavo
 */
fun favoAction(context: Context, tweetId: Long,
               isFavo: Boolean, favoImageView: ImageView) {
    ColorToggle.showHeartColor(isFavo, favoImageView, context)

    favoImageView.setOnClickListener {
        ColorToggle.toggleFavo(tweetId, favoImageView, context)
        Log.i(com.robyn.bitty.TAG, "toggle color called")
    }
}