package com.robyn.bitty;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

/**
 * Created by yifei on 7/30/2017.
 */

public class MakeSound {
    public void playSound(Context context) {
        SoundPool sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

        /** soundId for Later handling of sound pool **/
        int soundId = sp.load(context,
                R.raw.correct, 1); // in 2nd param u have to pass your desire ringtone

        sp.play(soundId, 1, 1, 0, 0, 1);

        MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.correct); // in 2nd param u have to pass your desire ringtone
        //mPlayer.prepare();
        mPlayer.start();
    }
}
