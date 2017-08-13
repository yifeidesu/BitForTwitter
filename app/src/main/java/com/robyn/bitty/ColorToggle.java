package com.robyn.bitty;

import android.content.Context;
import android.graphics.PorterDuff;
import android.widget.ImageView;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.FavoriteService;

import retrofit2.Call;

/**
 * Created by yifei on 8/12/2017.
 */

public class ColorToggle {

    /**
     * not for click. to show favo status before click action
     * click means to sent a call(create/destroy favo) and toggle color
     *
     * @param isFavoed from the completed callback
     * @param favoImage
     * @param context
     */
    public static void showHeartColor(boolean isFavoed, ImageView favoImage, Context context) {
        if (isFavoed) {
            favoImage.getDrawable().setColorFilter(
                    context.getResources().getColor(R.color.favoRed), PorterDuff.Mode.SRC_IN);
        } else {
            favoImage.getDrawable().clearColorFilter();
        }
    }


    /**
     * For click actions.
     *
     * @param isFavoed to decide if sent a create or destroy call
     * @param tweetId sent a create/destroy favo call for the tweet of this id
     * @param favoImage
     * @param context
     */
    public static void toggleHeartColor(boolean isFavoed,
                                        long tweetId,
                                        final ImageView favoImage,
                                        final Context context) {

        // check the newest isfavoed

        TwitterApiClient client = TwitterCore.getInstance().getApiClient();
        FavoriteService favoriteService = client.getFavoriteService();

        favoTweet(tweetId, favoImage, context, favoriteService);

    }

    private static void favoTweet(final long tweetId, final ImageView favoImage, final Context context, final FavoriteService favoriteService) {
        Call<Tweet> favoCall = favoriteService.create(tweetId, null);
        favoCall.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                colorHeart(favoImage, context);
            }

            @Override
            public void failure(TwitterException exception) {
                unFavoTweet(tweetId, favoImage, favoriteService);
            }
        });
    }

    private static void unFavoTweet(long tweetId, final ImageView favoImage, FavoriteService favoriteService) {
        Call<Tweet> unFavoCall = favoriteService.destroy(tweetId, null);
        unFavoCall.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                clearHeart(favoImage);
            }

            @Override
            public void failure(TwitterException exception) {

                exception.getStackTrace();
            }
        });
    }

    static void colorHeart(ImageView heartImage, Context context) {
        heartImage.setColorFilter(context
                .getResources()
                .getColor(R.color.tw__composer_red), PorterDuff.Mode.SRC_IN);
    }

    static void clearHeart(ImageView heartImage) {
        heartImage.clearColorFilter();
    }
}
