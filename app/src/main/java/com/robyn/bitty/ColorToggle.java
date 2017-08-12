package com.robyn.bitty;

import android.content.Context;
import android.graphics.PorterDuff;
import android.widget.ImageView;

/**
 * Created by yifei on 8/12/2017.
 */

public class ColorToggle {

    public static void setColorFilter(ImageView imageView, Context context) {
        imageView.getDrawable()
                .setColorFilter(context.getResources().getColor(R.color.favoRed), PorterDuff.Mode.SRC_IN);
    }

}
