package com.robyn.bitty.utils

import android.content.Context
import android.net.Uri

import com.google.android.exoplayer2.ExoPlayerFactory

import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector

import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter

import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.util.Util.getUserAgent
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.twitter.sdk.android.core.models.Tweet
import java.net.URL

/**
 * Methods to play a video contained in a Tweet
 *
 * Created by yifei on 12/4/2017.
 */

fun getPlayer(context: Context, uri: Uri): SimpleExoPlayer {

    // Create the player with all default params
    val bandwidthMeter = DefaultBandwidthMeter()
    val trackSelector = DefaultTrackSelector(bandwidthMeter)
    val player = ExoPlayerFactory.newSimpleInstance(context, trackSelector)

    // Get data source from uri
    val dataSourceFactory = DefaultDataSourceFactory(context,
            Util.getUserAgent(context, "videodemoapp"), bandwidthMeter)

    val extractorsFactory = DefaultExtractorsFactory()

    val videoSource = ExtractorMediaSource(uri,
            dataSourceFactory, extractorsFactory, null, null)

    // Set the data source to the player
    player.prepare(videoSource)

    return player
}

/**
 * This method decides if the [tweet] has video url.
 * If it does, it has a player to player the video on the url
 *
 * Currently works for mp4 only, executor type?
 */
fun playVideo(tweet: Tweet, context: Context, playerView: SimpleExoPlayerView) {

    getVideoUrl(tweet)?.apply {
        playerView.player = getPlayer(context, this)
        myLog(msg = "player's video url = ${this}")
    }
}

fun getVideoUrl(tweet: Tweet): Uri? {
    var uri: Uri? = null

//    with(tweet.extendedEntities.media) {
//        if (this.isEmpty()) return@with
//        myLog(msg = "media size = ${this.size}")
//
//    }

    // rewrite logic
    tweet.extendedEntities?.apply {
        if (!this.media.isEmpty()) {
            with(tweet.extendedEntities.media.get(0).videoInfo) {
                this?.apply {
                    if (!this.variants.isEmpty()) {
                        this.variants[0].url.apply {
                            uri = Uri.parse(this)
                        }
                    }
                }
            }
        }

    }

    return uri
}