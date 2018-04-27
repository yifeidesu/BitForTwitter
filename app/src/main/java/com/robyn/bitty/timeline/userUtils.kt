package com.robyn.bitty.timeline

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.twitter.sdk.android.core.models.User

fun User.loadProfileImage(
    context: Context,
    imageView: ImageView,
    sizeMultiplier: Float = 1f,
    quality: Int = 90
) {

    Glide.with(context).load(this.profileImageUrl)
        .apply(
            RequestOptions()
                .encodeQuality(quality)
                .sizeMultiplier(sizeMultiplier)
                .circleCrop()
        )
        .into(imageView)
}

/**
 * Modify url to get resized
 */
fun urlBigger(url: String): String {
    return url.replace("_normal.png", "_bigger.png")
}

fun User.loadBiggerProfileImage(
    context: Context,
    imageView: ImageView,
    sizeMultiplier: Float = 1f,
    quality: Int = 90
) {

    val urlBigger = urlBigger(this.profileImageUrl)
    Glide.with(context).load(urlBigger)
        .apply(
            RequestOptions()
                .encodeQuality(quality)
                .sizeMultiplier(sizeMultiplier)
                .circleCrop()
        )
        .into(imageView)
}

/**
 * As toolbar navigation icon
 */
fun User.loadProfileImage(
    context: Context,
    imageView: ImageView,
    sizeMultiplier: Float = 1f
) {

    Glide.with(context).load(this.profileImageUrl)
        .apply(
            RequestOptions()
                .encodeQuality(0) // 0 = no compression
                .sizeMultiplier(sizeMultiplier)
                .circleCrop()
        )
        .into(imageView)
}

fun User.loadBannerImage(context: Context, imageView: ImageView, quality: Int = 50) {
    Glide.with(context)
        .load(this.profileBannerUrl)
        .apply(RequestOptions().encodeQuality(quality))
        .into(imageView)


}

/**
 * Add prefix "@" for either a user: User or a userName: String
 */
fun User.atScreenName() = atScreenName(this.screenName)

fun atScreenName(screenName: String) = "@$screenName"
