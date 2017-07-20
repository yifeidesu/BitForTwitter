package com.robyn.bitty1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthException;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

public class EmbeddedTweetsActivity extends AppCompatActivity {

    // launch the login activity when a guest user tries to favorite a Tweet
    final Callback<Tweet> actionCallback = new Callback<Tweet>() {
        @Override
        public void success(Result<Tweet> result) {
            // Intentionally blank
        }

        @Override
        public void failure(TwitterException exception) {
            if (exception instanceof TwitterAuthException) {
                // launch custom login flow
                startActivity(LoginActivity.newIntent(getApplicationContext()));
            }
        }
    };

    public static Intent newIntent(Context context) {
        return new Intent(context, EmbeddedTweetsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions);

        final LinearLayout myLayout
                = (LinearLayout) findViewById(R.id.my_tweet_layout);

        final long tweetId = 510908133917487104L;
        TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                final TweetView tweetView = new TweetView(EmbeddedTweetsActivity.this, result.data,
                        R.style.tw__TweetDarkWithActionsStyle);
                tweetView.setOnActionCallback(actionCallback);
                myLayout.addView(tweetView);
            }

            @Override
            public void failure(TwitterException exception) {
                // Toast.makeText(...).show();
            }
        });
    }
}
