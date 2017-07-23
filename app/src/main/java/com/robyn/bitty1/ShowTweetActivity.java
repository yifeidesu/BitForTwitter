package com.robyn.bitty1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

public class ShowTweetActivity extends AppCompatActivity {

    private static final String EXTRA_TWEET_ID = "tweet_id";
    private static final String TAG = ShowTweetActivity.class.getSimpleName();

    private TextView mTextViewUserId;

    public static Intent newIntent(Context context, long tweetId) {
        Intent intent = new Intent(context, ShowTweetActivity.class);
        intent.putExtra(EXTRA_TWEET_ID, tweetId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_tweet);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO: 7/22/2017 get tweet by id

        mTextViewUserId = (TextView) findViewById(R.id.inReplyTo_userId);


        final LinearLayout myLayout
                = (LinearLayout) findViewById(R.id.tweet_layout);

        final long tweetId = getIntent().getLongExtra(EXTRA_TWEET_ID, 510908133917487104L);
        Log.i(TAG, String.valueOf(tweetId));


        TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                Tweet tweet = result.data;
                myLayout.addView(new TweetView(ShowTweetActivity.this, tweet));
                mTextViewUserId.setText("@" + tweet.inReplyToScreenName);

            }

            @Override
            public void failure(TwitterException exception) {
                // Toast.makeText(...).show();
            }
        });
    }
}
