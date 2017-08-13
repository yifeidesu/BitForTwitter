package com.robyn.bitty;

import android.content.Context;
import android.graphics.PorterDuff;
import android.widget.ImageView;

/**
 * Created by yifei on 8/12/2017.
 */

public class ColorToggle {

    /**
     * this method sets the heart image color based on callback result.
     *
     * @param isFavoed based on callback result. isFavoed = result.data.favoed;
     * @param favoImage
     * @param context
     */
    public static void setColorFilter(boolean isFavoed, ImageView favoImage, Context context) {
        if (isFavoed) {
            favoImage.getDrawable()
                    .setColorFilter(context.getResources()
                            .getColor(R.color.favoRed), PorterDuff.Mode.SRC_IN);
        } else {
            favoImage.getDrawable().clearColorFilter();
        }
    }
}
