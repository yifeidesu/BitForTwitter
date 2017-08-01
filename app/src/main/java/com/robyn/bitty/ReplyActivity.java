package com.robyn.bitty;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class ReplyActivity extends AppCompatActivity {
    private static final String EXTRA_TWEET_ID = "extra_tweet_id";

    private long mTweetId;

    public static Intent newIntent(Context context, long tweetId) {
        Intent intent = new Intent(context, ReplyActivity.class);
        intent.putExtra(EXTRA_TWEET_ID, tweetId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_clear);
        toolbar.setTitle(R.string.reply);

        mTweetId = getIntent().getLongExtra(EXTRA_TWEET_ID, 510908133917487104L);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(ShowTweetActivity.newIntent(getApplicationContext(), mTweetId));
            }
        });
    }
}
