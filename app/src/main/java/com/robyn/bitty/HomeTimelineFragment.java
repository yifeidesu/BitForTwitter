package com.robyn.bitty;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.TweetView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

import static android.support.v7.widget.RecyclerView.*;


/**
 * Created by yifei on 7/18/2017.
 */

public class HomeTimelineFragment extends Fragment
         {
    private static final String TAG = HomeTimelineFragment.class.getSimpleName();
             private static final String ARG_PROFILE_IMG_URL = "arg_profile_img_url";

             private List<Tweet> mTweets = new ArrayList<>();
    private List<Tweet> mTweetsUpdate = new ArrayList<>();
    private RecyclerView mRecyclerViewHome;
    private HomeAdapter mAdapter;

    private long mostRecentId = 0;
    private long leastRecentId = 0;
    private String mProfileImgUrlString;

    private Toolbar mToolbar;
             private ProgressBar mProgressBar;
    private Button mButtonLoadMore;

    public static HomeTimelineFragment newInstance() {
        
        Bundle args = new Bundle();
        //args.putString(ARG_PROFILE_IMG_URL, );
        
        HomeTimelineFragment fragment = new HomeTimelineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        new RefreshTask().execute();

        mProfileImgUrlString = getArguments().getString(ARG_PROFILE_IMG_URL);
        Log.i(TAG, "url = " + mProfileImgUrlString);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_hometimeline, container, false);


        // setup recyclerView
        mRecyclerViewHome = view.findViewById(R.id.home_timeline);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewHome.setLayoutManager(layoutManager );
        mRecyclerViewHome.setItemAnimator(null);
        new UpdateUITask().execute();

        // set divider for recyclerView items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerViewHome.getContext(),
                layoutManager.getOrientation());
        mRecyclerViewHome.addItemDecoration(dividerItemDecoration);

        // setup swipe refresh layout
        final SwipeRefreshLayout swipeLayout = view.findViewById(R.id.refresh_layout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new RefreshTask().execute();
                swipeLayout.setRefreshing(false);
            }
        });

        // TODO recyclerview set on scroll listener for scroll down to load prev tweets


        mButtonLoadMore = (Button) view.findViewById(R.id.load_more);
        mButtonLoadMore.setVisibility(GONE);
        mButtonLoadMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new PullPrevTask().execute();
                mButtonLoadMore.setVisibility(GONE);
                LinearLayoutManager m = new LinearLayoutManager(getActivity());
// TODO: 7/24/2017 scroll a bit upward when update finish
                Log.i(TAG, "PullPrevTask executed");
            }
        });

        mRecyclerViewHome.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    mButtonLoadMore.setVisibility(View.VISIBLE);
                    Log.i(TAG, "reach bottom detected");
                }
                // TODO: 7/21/2017 double tap to go back to top

            }
        });



        return view;
    }

    /**
     * recycler view adapter and holder
     */
    public class HomeHolder extends ViewHolder
            implements View.OnClickListener {
//        private LinearLayout myItemLayout;
//        private LinearLayout myTweetLayout;

        @BindView(R.id.my_item_layout) LinearLayout myItemLayout;
        @BindView(R.id.my_tweet_layout) LinearLayout myTweetLayout;

        @BindView(R.id.reply) ImageView replyButton;
        @BindView(R.id.retweet) ImageView reTweetButton;
        @BindView(R.id.like) ImageView likeButton;
        @BindView(R.id.share) ImageView shareButton;

        @BindView(R.id.reply_layout) LinearLayout replyLayout;
        @BindView(R.id.retweet_layout) LinearLayout retweetLayout;
        @BindView(R.id.like_layout) LinearLayout likeLayout;
        @BindView(R.id.share_layout) LinearLayout shareLayout;

        public HomeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item, parent, false));

            itemView.setOnClickListener(this);

            ButterKnife.bind(this, itemView); // must in public class
        }

        @Override
        public void onClick(View view) {
            //works
            Toast.makeText(getContext(), "my item clicked", Toast.LENGTH_SHORT).show();
        }
    }

    private class HomeAdapter extends RecyclerView.Adapter<HomeHolder> {

        HomeAdapter(List<Tweet> tweets) {
            mTweets = tweets;
        }

        @Override
        public HomeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            return new HomeHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(final HomeHolder holder, int position) {

            // remove previous view if the holder is not empty,
            // otherwise holder shows multiple tweets in one single holder
            if (holder.myTweetLayout.getChildCount() != 0) {
                holder.myTweetLayout.removeAllViews();
            }

            final Tweet tweet = mTweets.get(position);
            final long tweetId = tweet.getId();
            TweetView tweetView = new TweetView(getContext(), tweet);

            // remove the top_right twitter icon
            tweetView.removeViewAt(4);
            holder.myTweetLayout.addView(tweetView);

            // to remove defualt listener comes w/ the tweetView object
            holder.myTweetLayout.getChildAt(0).setOnClickListener(null);

            OnClickListener onClickShowTweetListener = new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG,"onClickShowTweetListener invoked");
                    startActivity(ShowTweetActivity.newIntent(getContext(), tweetId));
                }
            };

            holder.myTweetLayout.getChildAt(0).setOnClickListener(onClickShowTweetListener);

            holder.replyLayout.setOnClickListener(onClickShowTweetListener);

            holder.retweetLayout.setOnClickListener(new View.OnClickListener(){
                TwitterApiClient client = TwitterCore.getInstance().getApiClient();
                StatusesService statusesService = client.getStatusesService();

                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    final View dialView = LayoutInflater.from(getActivity())
                            .inflate(R.layout.dial_retweet_choice, null);
                    builder.setView(dialView).create();
                    final AlertDialog retweetDial = builder.show();

                    Button retweetButton = (Button) dialView.findViewById(R.id.retweet);
                    retweetButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Call<Tweet> retweetCall = statusesService.retweet(tweetId, false);
                            retweetCall.enqueue(new Callback<Tweet>() {
                                @Override
                                public void success(Result<Tweet> result) {

                                }

                                @Override
                                public void failure(TwitterException exception) {

                                }
                            });
                            retweetDial.dismiss();
                        }
                    });


                    // TODO: 7/24/2017 append the url for quoting
                    final Button retweetQuoteButton = (Button) dialView.findViewById(R.id.retweet_quote);
                    retweetQuoteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Call<Tweet> retweetQuoteCall = statusesService.retweet(tweetId,true);
                            retweetQuoteCall.enqueue(new Callback<Tweet>() {
                                @Override
                                public void success(Result<Tweet> result) {
                                    String tweetUrl = "https://twitter.com/"
                                            + tweet.inReplyToScreenName + "/status/" + tweet.idStr;


                                }

                                @Override
                                public void failure(TwitterException exception) {

                                }
                            });
                        }
                    });
                }
            });

            holder.shareLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);

                    // TODO: 7/23/2017 encode
                    String tweetUrl = "https://twitter.com/"
                            + tweet.inReplyToScreenName + "/status/" + tweet.idStr;

                    shareIntent.putExtra(Intent.EXTRA_TEXT, "sharing from BittyForTwitter: " + tweetUrl);
                    shareIntent.setType("text/plain");
                    startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.app_name)));
                    ShareActionProvider mShareActionProvider = new ShareActionProvider(getContext());
                    if (mShareActionProvider != null) {
                        mShareActionProvider.setShareIntent(shareIntent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mTweets.size();
        }
    }

    /**
     * when pull refresh, perform this task to get new tweets
     */
    private class RefreshTask extends AsyncTask<Void,Void,List<Tweet>> {
        @Override
        protected List<Tweet> doInBackground(Void... voids) {

            TwitterApiClient client = TwitterCore.getInstance().getApiClient();
            StatusesService statusesService = client.getStatusesService();
            retrofit2.Call<List<Tweet>> call = statusesService.homeTimeline(
                    null,
                    ((mostRecentId == 0)?null:(mostRecentId)), // Returns results with an ID greater than, more recent than this
                    null, // last call's sinceid = current call's max(least recent) id
                    false, false, true, true);
            call.enqueue(new Callback<List<Tweet>>() {
                @Override
                public void success(Result<List<Tweet>> result) {

                    if (result.data.size() == 0) {
                        Toast.makeText(getContext(), "no new tweets o_O", Toast.LENGTH_LONG).show();
                    } else {
                        if (mTweets == null) {
                            mTweets = result.data;
                        } else {
                            mTweetsUpdate = result.data;
                            mTweets.addAll(0, mTweetsUpdate); // insert from the beginning
                        }
                        new UpdateUITask().execute();
                        setMostRecentId();
                        setLeastRecentId();
                    }

                    Log.i(TAG, "mTweetsUpdate.size() = " + String.valueOf(result.data.size()));
                }
                @Override
                public void failure(TwitterException exception) {
                    Toast.makeText(getContext(),
                            "Something wrong with networking.", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "15 hometimeline get tweets fails --> " + exception.getMessage());
                }
            });
            return mTweets;
        }

        @Override
        protected void onPostExecute(List<Tweet> tweets) {
            super.onPostExecute(tweets);
            Log.i(TAG, "RefreshTask onPostExecute called");
            cancel(false);
        }
    }

    /**
     * Call this asynctask to get previous home timeline tweets
     */
    private class PullPrevTask extends AsyncTask<Void, Void, List<Tweet>> {
        @Override
        protected List<Tweet> doInBackground(Void... voids) {

            TwitterApiClient client = TwitterCore.getInstance().getApiClient();
            StatusesService statusesService = client.getStatusesService();
            retrofit2.Call<List<Tweet>> call = statusesService.homeTimeline(
                    null,
                    null,
                    ((leastRecentId == 0) ? null : (leastRecentId - 1)),
                    false, true, true, true);
            call.enqueue(new Callback<List<Tweet>>() {
                @Override
                public void success(Result<List<Tweet>> result) {

                    if (result.data.size() == 0) {
                        Toast.makeText(getContext(), "no new tweets o_O", Toast.LENGTH_LONG).show();
                    } else {
                        if (mTweets == null) {
                            mTweets = result.data;
                        } else {
                            mTweetsUpdate = result.data;
                            mTweets.addAll(mTweetsUpdate); // insert starting from the end of the list
                        }
                        new UpdateUITask().execute();
                        setMostRecentId();
                        setLeastRecentId();
                    }
                }
                @Override
                public void failure(TwitterException exception) {
                    Toast.makeText(getContext(), "Tweets arriving in 15 min", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "call for hometimeline failed --> " + exception.getMessage());
                    cancel(false);
                }
            });
            return mTweets;
        }

        @Override
        protected void onPostExecute(List<Tweet> tweets) {
            super.onPostExecute(tweets);
            Log.i(TAG, "PullPrevTask onPostExecute called");
        }
    }

    public class UpdateUITask extends AsyncTask<Void, Void, Void> {
        boolean isNewAdapter = true;
        @Override
        protected Void doInBackground(Void... voids) {
            if (mAdapter == null) {
                mAdapter = new HomeAdapter(mTweets);
            } else {
                isNewAdapter = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isNewAdapter) {
                mRecyclerViewHome.setAdapter(mAdapter);
            } else {
                mAdapter.notifyItemRangeChanged(0, mTweets.size());
            }
            cancel(false);
        }
    }

    private void setMostRecentId() {
        mostRecentId = mTweets.get(0).id;
    }

    private void setLeastRecentId() {
        leastRecentId = mTweets.get(mTweets.size() - 1).id;
    }
}
