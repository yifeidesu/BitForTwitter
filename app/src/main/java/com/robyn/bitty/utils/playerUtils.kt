package com.robyn.bitty.utils

import android.content.Context
import android.net.Uri
import android.os.Handler
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.LoadControl
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
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
    val bandwidthMeter = DefaultBandwidthMeter()
    val trackSelector = DefaultTrackSelector(bandwidthMeter)
    val loadControl = DefaultLoadControl()

// 2. Create the player
    val player = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl)

    // prep the player with uri
    val dataSourceFactory = DefaultDataSourceFactory(context,
            Util.getUserAgent(context, "videodemoapp"), bandwidthMeter)

    val extractorsFactory = DefaultExtractorsFactory()

    val videoSource = ExtractorMediaSource(uri,
            dataSourceFactory, extractorsFactory, null, null)

    player.prepare(videoSource)


    return player
}

fun playVideo(tweet: Tweet, context: Context, playerView: SimpleExoPlayerView) {

    getVideoUrl(tweet)?.apply { playerView.player = getPlayer(context, this) }

    //playerView.player = getPlayer(context, uri)
}

fun getVideoUrl(tweet: Tweet): Uri? {
    return Uri.parse(tweet.extendedEntities?.media?.get(0)?.mediaUrl)
}