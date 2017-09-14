package com.robyn.bitty

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.ImageViewTarget
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.models.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_header.*
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Hours
import org.joda.time.Period

/**
 * package functions
 *
 *
 * Created by yifei on 9/13/2017.
 */


/**
 *  build a joda time w/
 *  year mon day hour min
 *  if period <　24 hours
 *
 *  show 1, xxx min ago ; 2. xx hours ago
 *
 */


fun tweetCreatedAt(dateTime: DateTime): String {


    return ""
}

fun tweetTimeToDate (timeString: String): String {
    val arr = timeString.split(" ")
    Log.i("joda", arr.toString())
    val year = arr[5]
    val month = arr[1]
    val day = arr[2]
    val date = "$month $day, $year "

    return date
}

/* add prefix at sign to user screen name*/
fun atScreenName(user:User) = " @${user.screenName}"

//fun loadProfileImage(context:Context, user: User, profileImage: ImageView, size: Int) {
//    var roundNavIconDrawable:RoundedBitmapDrawable? = null
//
////    Glide.with(context).loadProfileImage(user.profileImageUrl)
////            .asBitmap().into<ImageViewTarget<Bitmap>>(object : BitmapImageViewTarget(profileImage) {
////        override fun setResource(resource: Bitmap) {
////            val imageBitmap = Bitmap.createScaledBitmap(resource, size, size, true)
////            roundNavIconDrawable = RoundedBitmapDrawableFactory.create(context.resources, imageBitmap)
////            roundNavIconDrawable!!.isCircular = true
////
////            val headerProfileBitmap = Bitmap.createScaledBitmap(resource, 160, 200, true)
////
////            val roundHeaderDrawable = RoundedBitmapDrawableFactory.create(context.resources, headerProfileBitmap)
////            roundHeaderDrawable.isCircular = true
////
////            toolbar_main.navigationIcon = roundNavIconDrawable
////            profile_img_drawer!!.setImageDrawable(roundHeaderDrawable)
//        }
//    })
//
//}

fun loadProfileImage(context: Context, user: User, imageView: ImageView, sizeMultiplier: Float = 1f) {
    Glide.with(context).load(user.profileImageUrl)
            .apply(RequestOptions().sizeMultiplier(sizeMultiplier).circleCrop())
            .into(imageView)
}


