package com.robyn.bitty;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReplyActivity extends AppCompatActivity {
    private static final String EXTRA_TWEET_ID = "extra_tweet_id";
    private static final String EXTRA_SCREEN_NAME = "extra_screen_name";

    private long mTweetId;
    private String mScreenName;

    @BindView(R.id.user_screen_id)
    TextView mScreenNameTextView;

    @BindView(R.id.reply_button)
    Button mReplyButton;

    public static Intent newIntent(Context context, long tweetId, String userScreenId) {
        Intent intent = new Intent(context, ReplyActivity.class);
        intent.putExtra(EXTRA_TWEET_ID, tweetId);
        intent.putExtra(EXTRA_SCREEN_NAME, userScreenId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_clear);
        toolbar.setTitle(R.string.reply);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(ShowTweetActivity.newIntent(getApplicationContext(), mTweetId));
            }
        });

        mTweetId = getIntent().getLongExtra(EXTRA_TWEET_ID, 510908133917487104L);
        mScreenName = getIntent().getStringExtra(EXTRA_SCREEN_NAME);

        String atScreenName = "@" + mScreenName;
        mScreenNameTextView.setText(atScreenName);






    }
}
