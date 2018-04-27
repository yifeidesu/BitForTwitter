package com.robyn.bitty.utils

import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.robyn.bitty.R
import com.robyn.bitty.data.DataSource
import com.robyn.bitty.detail.SoloActivity
import com.twitter.sdk.android.core.models.Tweet

/**
 * todo on click change favorite and update view color
 */
fun favorite(imageView: View, id:Long) {
    makeSnackbar(imageView.rootView, "click!")
    DataSource.INSTANCE.favoriteSingle(id).subscribe(
        {
            // if 200, toggle view color

            it?.favorited?.let { (imageView as ImageView).toggleColor(it) }
        },
        { err -> Log.e(DataSource.TAG, err.message) }
    )
}

fun ImageView.toggleColor(favo: Boolean) {
    if (favo) {
        val colorInt = ContextCompat.getColor(context, R.color.favoRed)
        setColorFilter(colorInt, PorterDuff.Mode.SRC_IN)
    } else {
        clearColorFilter()
    }
}

fun ImageView.toggleColor(tweet: Tweet?) {
    if (tweet == null) return
    favorite(tweet)
}

fun favorite(tweet: Tweet?) {
    if (tweet == null) return
}

// todo move to proper place
fun gotoSolo(view:View, tweet: Tweet?) {
    if (tweet == null) return

    val intent = SoloActivity.newIntent(view.context, tweet.id)
    view.context.startActivity(intent)
}

