package com.robyn.bitty;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import butterknife.ButterKnife;

public class ShowTweetActivity extends AppCompatActivity {
    private static final String TAG = ShowTweetActivity.class.getSimpleName();

    private static final String EXTRA_TWEET_ID = "tweet_id";
    private static final String EXTRA_SCREEN_NAME = "tweet_screen_name";

    private Tweet mTweet;
    private long mTweetId;
    private String mScreenName;

    private ScrollView mScrollView;



    public static Intent newIntent(Context context, long tweetId) {
        Intent intent = new Intent(context, ShowTweetActivity.class);
        intent.putExtra(EXTRA_TWEET_ID, tweetId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_tweet);

        new ShowTweetTask().execute();

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.tweet);
        }

        toolbar.setNavigationIcon(R.drawable.ic_nav_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() { // already set parent ac, do i still need this?
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mScrollView = (ScrollView) findViewById(R.id.scrollView);


// TODO: 7/31/2017 custom tweet view
        // todo inflate a fg showing the aciton buttons w/o focus, by clicking the tweetview

        // TODO: 7/25/2017 delete reply box views

    }

    private class ShowTweetTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Log.i(TAG, "ShowTweetTask");

            mTweetId = getIntent().getLongExtra(EXTRA_TWEET_ID, 510908133917487104L);

            TweetUtils.loadTweet(mTweetId, new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    mTweet = result.data;
                    mScreenName = mTweet.user.screenName;

                    TweetView tweetView = new TweetView(getApplicationContext(), mTweet);
                    tweetView.setOnClickListener(null);
                    tweetView.removeViewAt(4);
                    mScrollView.addView(tweetView);
                }

                @Override
                public void failure(TwitterException exception) {
                    Toast.makeText(getApplicationContext(), "failed",Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "TweetUtils.loadTweet failed --> " + exception.getMessage());
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container_actions);
            if (fragment == null) {
                fragment = TweetActionsFragment.newInstance(
                        mTweetId, mScreenName);
                fragmentManager
                        .beginTransaction()
                        .add(R.id.fragment_container_actions, fragment)
                        .commit();
            }
            cancel(false);
        }
    }
}
