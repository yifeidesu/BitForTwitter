package com.robyn.bitty.utils.tweetUtils

import android.content.Context
import android.widget.ImageView
import com.twitter.sdk.android.core.models.Tweet

/**
 * Created by yifei on 9/18/2017.
 */

fun favoAction(context: Context, tweet: Tweet, favoImageView: ImageView) {
    showHeartColor(tweet.favorited, favoImageView, context)

    favoImageView.setOnClickListener {
        toggleFavo(tweet.id, favoImageView, context)
    }
}

/**
 *  tweetId + isFavo
 */
fun favoAction(
    context: Context, tweetId: Long,
    isFavo: Boolean, favoImageView: ImageView
) {
    showHeartColor(isFavo, favoImageView, context)

    favoImageView.setOnClickListener {
        toggleFavo(tweetId, favoImageView, context)
    }
}