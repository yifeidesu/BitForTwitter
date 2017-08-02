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
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class ReplyActivity extends AppCompatActivity {
    private static final String TAG = ReplyActivity.class.getSimpleName();

    private static final String EXTRA_SCREEN_NAME = "extra_screen_name";
    private static final String EXTRA_IN_REPLY_TO_ID = "extra_in_reply_to_id";

    private long mInReplyToStatusId;
    private String mScreenName;
    private String mReplyTextInput;

    @BindView(R.id.user_screen_id)
    TextView mScreenNameTextView;

    @BindView(R.id.reply_button)
    Button mReplyButton;

    @BindView(R.id.reply_edittext)
    TextInputEditText mReplyEditText;




    public static Intent newIntent(Context context,
                                   long inReplyToStatusId,
                                   String userScreenId) {
        Intent intent = new Intent(context, ReplyActivity.class);
        intent.putExtra(EXTRA_IN_REPLY_TO_ID, inReplyToStatusId);
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
                startActivity(ShowTweetActivity.newIntent(getApplicationContext(), mInReplyToStatusId));
            }
        });

        mInReplyToStatusId = getIntent().getLongExtra(EXTRA_IN_REPLY_TO_ID, 510908133917487104L);
        mScreenName = getIntent().getStringExtra(EXTRA_SCREEN_NAME);

        String atScreenName = "@" + mScreenName;
        mScreenNameTextView.setText(atScreenName);

        mReplyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mReplyTextInput = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        TwitterApiClient client = TwitterCore.getInstance().getApiClient();
        final StatusesService statusesService = client.getStatusesService();

        mReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Tweet> replyCall = statusesService.update(
                        mReplyTextInput,
                        mInReplyToStatusId,
                        null, null, null, null, null, null, null);
                replyCall.enqueue(new Callback<Tweet>() {
                    @Override
                    public void success(Result<Tweet> result) {
                        Toast.makeText(getApplicationContext(),
                                "Reply sent!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Log.i(TAG, exception.getMessage());
                        Toast.makeText(getApplicationContext(),
                                "Reply failed!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });


    }
}


