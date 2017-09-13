package com.robyn.bitty.ui.timelines;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.robyn.bitty.ColorToggle;
import com.robyn.bitty.R;
import com.robyn.bitty.ui.ReplyActivity;
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

/**
 * actions in ShowTweetActivity
 * Created by yifei on 7/31/2017.
 */

public class TweetActionsFragment extends Fragment {
    private static final String ARGS_TWEET_ID = "args_tweet_id";
    private static final String ARGS_SCREEN_NAME = "args_screen_name";
    private static final String ARGS_ISFAVOED = "args_isfavoed";

    private long mTweetId;
    private String mScreenName;

    private Tweet mTweet;
    private boolean mIsFavoed;

    @BindView(R.id.reply_layout) LinearLayout mReplyLayout;
    @BindView(R.id.retweet_layout) LinearLayout mRetweetLayout;
    @BindView(R.id.favo_layout) LinearLayout mFavoLayout;
    @BindView(R.id.share_layout) LinearLayout mShareLayout;
    @BindView(R.id.favo) ImageView mFavoImage;

    public static TweetActionsFragment newInstance(
            long tweetId, boolean isFavoed, String screenName) {

        Bundle args = new Bundle();
        args.putLong(ARGS_TWEET_ID, tweetId);
        args.putBoolean(ARGS_ISFAVOED, isFavoed);
        args.putString(ARGS_SCREEN_NAME, screenName);

        TweetActionsFragment fragment = new TweetActionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTweetId = getArguments().getLong(ARGS_TWEET_ID);
        mIsFavoed = getArguments().getBoolean(ARGS_ISFAVOED);
        mScreenName = getArguments().getString(ARGS_SCREEN_NAME);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet_actions, container, false);
        ButterKnife.bind(this, view);

        ColorToggle.INSTANCE.showHeartColor(mIsFavoed, mFavoImage, getContext());

        /**
         * Four tweet action buttons in total
         *
         * 1/4 Reply
         */
        mReplyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(ReplyActivity.newIntent(getContext(),
                        mTweetId, mScreenName)); // mTweetId = the update's in reply to status id
            }
        });

        /**
         * 2/4 Retweet
         */
        mRetweetLayout.setOnClickListener(new View.OnClickListener(){
            TwitterApiClient client = TwitterCore.getInstance().getApiClient();
            StatusesService statusesService = client.getStatusesService();

            @Override
            public void onClick(View v) {
                Call<Tweet> retweetCall = statusesService.retweet(mTweetId, false);
                retweetCall.enqueue(new Callback<Tweet>() {
                    @Override
                    public void success(Result<Tweet> result) {
                        Toast.makeText(getContext(),
                                "Retweet successfully!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Toast.makeText(getContext(),
                                "Retweet failed!", Toast.LENGTH_SHORT).show();
                    }
                });

                // TODO: 8/2/2017 next version: replace w/ the following dial: retweet + quote
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                final View dialView = LayoutInflater.from(getActivity())
//                        .inflate(R.layout.dial_retweet_choice, null);
//                builder.setView(dialView).create();
//                final AlertDialog retweetDial = builder.show();
//
//                Button retweetButton = dialView.findViewById(R.id.retweet);
//                retweetButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Call<Tweet> retweetCall = statusesService.retweet(mTweetId, false);
//                        retweetCall.enqueue(new Callback<Tweet>() {
//                            @Override
//                            public void success(Result<Tweet> result) {
//                                Toast.makeText(getContext(),
//                                        "Retweet successfully!", Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void failure(TwitterException exception) {
//                                Toast.makeText(getContext(),
//                                        "Retweet failed!", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                        retweetDial.dismiss();
//                    }
//                });
//
//                final Button quoteButton = (Button) dialView.findViewById(R.id.retweet_quote);
//                quoteButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Call<Tweet> retweetQuoteCall = statusesService.(mTweetId,true);
//
//                        retweetQuoteCall.enqueue(new Callback<Tweet>() {
//                            @Override
//                            public void success(Result<Tweet> result) {
//                                String tweetUrl = "https://twitter.com/"
//                                        + mScreenName + "/status/" + String.valueOf(mTweetId);
//                            }
//
//                            @Override
//                            public void failure(TwitterException exception) {
//
//                            }
//                        });
//
//                    }
//                });
            }
        });

        /**
         * 3/4 Favo
         */

        mFavoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorToggle.INSTANCE.toggleHeartColor(
                        mTweetId,
                        mFavoImage, getContext());
            }
        });

        /**
         * 4/4 Share
         */
        mShareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);

                // TODO: 7/23/2017 encode
                String tweetUrl = "https://twitter.com/"
                        + mScreenName + "/status/" + String.valueOf(mTweetId);

                shareIntent.putExtra(Intent.EXTRA_TEXT, "sharing from BittyForTwitter: " + tweetUrl);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.app_name)));
                ShareActionProvider mShareActionProvider = new ShareActionProvider(getContext());
                mShareActionProvider.setShareIntent(shareIntent);
            }
        });

        return view;
    }
}