package com.robyn.bitty

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool

/**
 * Created by yifei on 7/30/2017.
 *
 * To notify when fetching finishes
 */

class MakeSound {

    fun playSound(context: Context) {
        val soundPool = SoundPool(5, AudioManager.STREAM_MUSIC, 0)

        val soundId = soundPool.load(context, R.raw.correct, 1)

        soundPool.play(soundId, 1f, 1f, 0, 0, 1f)

        val mPlayer = MediaPlayer.create(context, R.raw.correct)
        mPlayer.start()
    }
}
