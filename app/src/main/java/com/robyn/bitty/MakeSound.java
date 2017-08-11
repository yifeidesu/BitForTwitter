package com.robyn.bitty;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

/**
 * Created by yifei on 7/30/2017.
 *
 * to notify when build finishes. Remove before product release
 */

public class MakeSound {

    public void playSound(Context context) {
        SoundPool soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

        int soundId = soundPool.load(context, R.raw.correct, 1);

        soundPool.play(soundId, 1, 1, 0, 0, 1);

        MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.correct);
        mPlayer.start();
    }
}
