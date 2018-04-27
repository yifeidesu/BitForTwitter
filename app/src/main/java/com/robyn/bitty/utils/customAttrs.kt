package com.robyn.bitty.utils

import android.databinding.BindingAdapter
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.twitter.sdk.android.core.models.Tweet

/**
 * Custom attrs
 */

@BindingAdapter("retweetVisible")
fun View.retweetVisible(tweet: Tweet?) {

    visibility = if (tweet?.retweetedStatus != null) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

/**
 * ImageView's custom attr 'imageUrl' links to this ext member method
 *
 * Load image url to this ImageView
 *
 */
@BindingAdapter("imageUrl")
fun ImageView.loadImageUrl(imageUrl: String?) {
    Glide.with(context)
        .load(imageUrl)
        .apply(RequestOptions().circleCrop())
        .into(this)
}

/**
 * Load user profile image in timeline
 */
@BindingAdapter("loadImageCircle")
fun ImageView.loadImageCircle(tweet: Tweet?) {

    with(tweetToShow(tweet)) {
        if (this == null) return
        this.user?.profileImageUrl?.let {
            loadImageCircle(it)
        }
    }
}

/**
 * Load media image from url to this ImageView
 */
@BindingAdapter("loadImage")
fun ImageView.loadImage(tweet: Tweet?) {

//    with(tweetToShow(tweet)) {
//        this?.getImageUrl()?.let { loadImage(it) }
//    }
//
    val url = tweetToShow(tweet)?.getImageUrl()
    if (url != null) {
        loadImage(url)
    } else setImageDrawable(null)
}

/**
 * Return the url of the first image of the tweet media
 *
 */
fun Tweet.getImageUrl(): String? {

    entities?.media?.let {
        if (it.size > 0) {
            it[0]?.mediaUrl?.let { return it }
        }
    }
    return null
}

@BindingAdapter("setRetweet")
fun TextView.setRetweet(tweet: Tweet?) {
    text = retweetBy(tweet).also {
        visibility = if (it == null) {
            View.GONE
        } else {
            View.VISIBLE
        }

    }
}

@BindingAdapter("setQuoteVisibility")
fun TextView.setQuoteVisibility(tweet: Tweet?) {
    text = tweet?.quotedStatus?.text.also {
        this@setQuoteVisibility.visibility = if (it == null) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}

/**
 * Load image into a circle
 */
fun ImageView.loadImageCircle(url: String) {
    Glide.with(context)
        .load(url)
        .apply(RequestOptions().circleCrop())
        .into(this)
}

/**
 * Load image into a square
 */
fun ImageView.loadImage(url: String) {
    Glide.with(context)
        .load(url)
        .into(this)
}