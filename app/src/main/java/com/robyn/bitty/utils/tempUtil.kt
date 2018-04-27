package com.robyn.bitty.utils

import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.models.User

fun User.getProperty() {
    this.createdAt
}

fun Tweet.getPpt() {
    this.text

}