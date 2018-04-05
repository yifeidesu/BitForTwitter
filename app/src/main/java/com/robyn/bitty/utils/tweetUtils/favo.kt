package com.robyn.bitty.utils.tweetUtils

import android.content.Context
import android.graphics.PorterDuff
import android.widget.ImageView
import com.robyn.bitty.R

import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.services.FavoriteService

/**
 * Created by yifei on 8/12/2017.
 *
 * Functions for Favo / unfavo a tweet
 */

/**
 * not for click. to show favo status before click action
 * click means to sent a call(create/destroy favo) and toggle color
 *
 * @param isFavoed from the completed callback
 * @param favoImage
 * @param context
 */
fun showHeartColor(isFavoed: Boolean, favoImage: ImageView, context: Context) {
    if (isFavoed) {
        favoImage.drawable.setColorFilter(
            context.resources.getColor(R.color.favoRed), PorterDuff.Mode.SRC_IN
        )
    } else {
        favoImage.drawable.clearColorFilter()
    }
}

/**
 * For click actions.
 * @param tweetId sent a create/destroy favo call for the tweet of this id
 * @param favoImage
 * @param context
 */
fun toggleFavo(
    tweetId: Long,
    favoImage: ImageView,
    context: Context
) {

    val client = TwitterCore.getInstance().apiClient
    val favoriteService = client.favoriteService

    favoTweet(
        tweetId,
        favoImage,
        context,
        favoriteService
    )
}

private fun favoTweet(
    tweetId: Long,
    favoImage: ImageView,
    context: Context,
    favoriteService: FavoriteService
) {
    val favoCall = favoriteService.create(tweetId, null)
    favoCall.enqueue(object : Callback<Tweet>() {
        override fun success(result: Result<Tweet>) {
            favoImage.setColorFilter(
                context
                    .resources
                    .getColor(R.color.favoRed), PorterDuff.Mode.SRC_IN
            )
        }

        override fun failure(exception: TwitterException) {
            unFavoTweet(
                tweetId,
                favoImage,
                favoriteService
            )
        }
    })
}

private fun unFavoTweet(tweetId: Long, favoImage: ImageView, favoriteService: FavoriteService) {
    val unFavoCall = favoriteService.destroy(tweetId, null)
    unFavoCall.enqueue(object : Callback<Tweet>() {
        override fun success(result: Result<Tweet>) {
            favoImage.clearColorFilter()
        }

        override fun failure(exception: TwitterException) {
            exception.stackTrace
        }
    })
}

