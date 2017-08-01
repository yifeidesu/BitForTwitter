package com.robyn.bitty;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * actions in ShowTweetActivity
 * Created by yifei on 7/31/2017.
 */

public class TweetActionsFragment extends Fragment {
    private static final String ARGS_TWEET_ID = "args_tweet_id";

    @BindView(R.id.reply_layout) LinearLayout mReplyLayout;
    @BindView(R.id.retweet_layout) LinearLayout mRetweetLayout;
    @BindView(R.id.like_layout) LinearLayout mLikeLayout;
    @BindView(R.id.share_layout) LinearLayout mShareLayout;

    public static TweetActionsFragment newInstance(long tweetId) {

        Bundle args = new Bundle();
        args.putLong(ARGS_TWEET_ID, tweetId);

        TweetActionsFragment fragment = new TweetActionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet_actions, container, false);
        ButterKnife.bind(this, view);

        mReplyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }
}
