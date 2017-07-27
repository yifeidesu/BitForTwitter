package com.robyn.bitty;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowTweetActivity extends AppCompatActivity {
    private static final String TAG = ShowTweetActivity.class.getSimpleName();

    private static final String EXTRA_TWEET_ID = "tweet_id";

    private Tweet mTweet;

    private LinearLayout mTweetLayout;
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

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_nav_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() { // already set parent ac, do i still need this?
            @Override
            public void onClick(View view) {
                startActivity(MainActivity.newIntent(getApplicationContext()));
            }
        });

        mTweetLayout = (LinearLayout) findViewById(R.id.tweet_layout);
        mScrollView = (ScrollView) findViewById(R.id.scrollView);

        new ShowTweetTask().execute();

        // todo inflate a fg showing the aciton buttons w/o focus, by clicking the tweetview

        // TODO: 7/25/2017 delete reply box views

    }

    public class ShowTweetTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Log.i(TAG, "ShowTweetTask");
//            final LinearLayout myLayout
//                    = (LinearLayout) findViewById(R.id.tweet_layout);

            final long tweetId = getIntent().getLongExtra(EXTRA_TWEET_ID, 510908133917487104L);

            TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    mTweet = result.data;
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




//
//            mButtonReply = (Button) findViewById(R.id.reply_button);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            cancel(false);
        }

        //        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            mEditTextReply.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                    mStringReply = charSequence.toString();
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable editable) {
//
//                }
//            });
//            mButtonReply.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    TwitterApiClient client = TwitterCore.getInstance().getApiClient();
//                    StatusesService statusesService = client.getStatusesService();
//                    retrofit2.Call<Tweet> updateCall = statusesService.update(
//                            mStringReply,
//                            mTweet.getId(),
//                            null,null,null,null,null,null,null);
//                    updateCall.enqueue(new Callback<Tweet>() {
//                        @Override
//                        public void success(Result<Tweet> result) {
//                            Toast.makeText(getApplicationContext(),
//                                    "Reply sent", Toast.LENGTH_SHORT).show();
//                            Log.i(TAG, "updateCall success");
//                            // TODO: 7/24/2017 dismiss current reply fg when sent out reply
//                        }
//
//                        @Override
//                        public void failure(TwitterException exception) {
//                            Toast.makeText(getApplicationContext(),
//                                    "Reply sent failed", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            });
//            cancel(false);
//
//
//        }
    }
}
