package com.robyn.bitty.utils

import com.twitter.sdk.android.core.models.Tweet
import java.util.regex.Pattern

/**
 * Add a prefix "@" before this TextView's text
 *
 * Typical use for make "@user.screenName" string
 */

fun prefixAt(text: String?): String {
    return "@$text"
}

fun atScreenName(tweet: Tweet?): String? {

    with(tweetToShow(tweet)) {
        if (this == null) return null
        return prefixAt(this.user?.screenName)
    }
}

fun userName(tweet: Tweet?): String? {
    with(tweetToShow(tweet)) {
        if (this == null) return null
        return this.user?.name
    }
}

fun replyTo(tweet: Tweet?): String? {
    with(tweet?.inReplyToScreenName) {
        if (this == null) return null
        return "Replying to @$this"
    }
}

fun tweetToShow(tweet: Tweet?): Tweet? {

    return when {
        tweet == null -> null
        tweet.quotedStatus != null -> tweet
        tweet.retweetedStatus != null -> tweet.retweetedStatus
        else -> tweet
    }
}



fun retweetBy(tweet: Tweet?): String? {
    if (tweet?.retweetedStatus != null) {
        return tweet.user?.name
    }
    return null
}

/**
 *  build a joda time w/
 *  year mon day hour min
 *  if period <　24 hours
 *
 *  show 1, xxx min ago ; 2. xx hours ago
 *
 */
fun createAtFormatted(timeString: String?): String? {
    if (timeString == null) return null

    val timeCharArray = timeString.split(" ")
    if (timeCharArray.size < 5) return ""
    val year = timeCharArray[5]
    val month = timeCharArray[1]
    val day = timeCharArray[2]
    val date = " • $month $day, $year "

    return date
}

fun createAtFormatted(tweet: Tweet?): String? {
    with(tweetToShow(tweet)) {
        if (this == null) return null
        return createAtFormatted(this.createdAt)
        //return this.createdAt.toString()
    }
}

/**
 * Trim out the http link at the end of tweet text
 */
fun trimLinks(text: String?): String {
    if (text == null) return ""

    val regex =
        "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?+-=\\\\.&]*)"
    val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)

    return pattern.matcher(text).replaceAll("")
}

fun trimLinks(tweet: Tweet?): String? {
    val tweetToShow = tweetToShow(tweet) ?: return null

    return trimLinks(tweetToShow.text)
}