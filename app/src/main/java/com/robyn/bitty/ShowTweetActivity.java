package com.robyn.bitty;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

public class ShowTweetActivity extends AppCompatActivity {

    private static final String EXTRA_TWEET_ID = "tweet_id";
    private static final String TAG = ShowTweetActivity.class.getSimpleName();

    private TextView mTextViewUserId;
    private Button mButtonReply;
    private TextInputEditText mEditTextReply;
    private String mStringReply;

    private Tweet mTweet;

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

        mTextViewUserId = (TextView) findViewById(R.id.inReplyTo_userId);

        final LinearLayout myLayout
                = (LinearLayout) findViewById(R.id.tweet_layout);

        final long tweetId = getIntent().getLongExtra(EXTRA_TWEET_ID, 510908133917487104L);
        Log.i(TAG, String.valueOf(tweetId));


        TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                Tweet tweet = result.data;
                mTweet = tweet;
                myLayout.addView(new TweetView(ShowTweetActivity.this, mTweet));
                String screenName = "@" + tweet.inReplyToScreenName;
                mTextViewUserId.setText(screenName);
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getApplicationContext(), "failed",Toast.LENGTH_SHORT).show();
                Log.i(TAG, "TweetUtils.loadTweet failed --> " + exception.getMessage());
            }
        });

        mEditTextReply = (TextInputEditText) findViewById(R.id.reply_input);
        mEditTextReply.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mStringReply = charSequence.toString();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        // TODO: 7/22/2017 reply button

        mButtonReply = (Button) findViewById(R.id.reply_button);
        mButtonReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TwitterApiClient client = TwitterCore.getInstance().getApiClient();
                StatusesService statusesService = client.getStatusesService();
                retrofit2.Call<Tweet> updateCall = statusesService.update(
                        mStringReply,
                        mTweet.getId(),
                        null,null,null,null,null,null,null);
                updateCall.enqueue(new Callback<Tweet>() {
                    @Override
                    public void success(Result<Tweet> result) {
                        Toast.makeText(getApplicationContext(),
                                "Reply sent", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "updateCall success");
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Toast.makeText(getApplicationContext(),
                                "Reply sent failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
