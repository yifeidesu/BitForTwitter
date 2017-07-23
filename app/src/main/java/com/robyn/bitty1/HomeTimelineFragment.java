package com.robyn.bitty1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import static android.support.v7.widget.RecyclerView.*;


/**
 * Created by yifei on 7/18/2017.
 */

public class HomeTimelineFragment extends Fragment {
    private static final String TAG = HomeTimelineFragment.class.getSimpleName();

    private List<Tweet> mTweets = new ArrayList<>();
    private List<Tweet> mTweetsUpdate = new ArrayList<>();
    private RecyclerView mRecyclerViewHome;
    private HomeAdapter mAdapter;

    private long mostRecentId = 0;
    private long leastRecentId = 0;

    private Button mButtonToTop;
    private Button mButtonLoadMore;
    

    public static HomeTimelineFragment newInstance() {
        
        Bundle args = new Bundle();
        
        HomeTimelineFragment fragment = new HomeTimelineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new RefreshTask().execute();
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
        updateUI(mTweets);

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

                m.smoothScrollToPosition(mRecyclerViewHome, null, 15);
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
     * recycler view classes
     */
    private class HomeHolder extends ViewHolder
            implements View.OnClickListener {
        private LinearLayout itemLayout;
        private LinearLayout mLinearLayout;
        private ImageView reply;

        public HomeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item, parent, false));

            itemView.setOnClickListener(this);

            itemLayout = itemView.findViewById(R.id.list_item_layout);

            mLinearLayout = itemView.findViewById(R.id.my_tweet_layout);
            reply = itemView.findViewById(R.id.reply);

        }

        @Override
        public void onClick(View view) {
            Toast.makeText(getContext(), "item clicked", Toast.LENGTH_LONG).show();

        }
    }

    private class HomeAdapter extends RecyclerView.Adapter<HomeHolder> {
        public HomeAdapter(List<Tweet> tweets) {
            mTweets = tweets;
        }

        @Override
        public HomeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            return new HomeHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(HomeHolder holder, int position) {

            // remove previous view if the holder is not empty,
            // otherwise it shows multiple tweets in one single holder
            if (holder.mLinearLayout.getChildCount() != 0) {
                holder.mLinearLayout.removeAllViews();
            }

            final Tweet tweet = mTweets.get(position);
            holder.mLinearLayout.addView(new TweetView(getContext(), tweet,
                    R.style.tw__TweetLightWithActionsStyle));



            // to remove defualt listener comes w/ the tweet obj
            holder.mLinearLayout.getChildAt(0).setOnClickListener(null);

            // TODO: 7/22/2017   the whole holder's onclicklistener

            OnClickListener onClickShowTweetListener = new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG,"onClickShowTweetListener invoked");
                    long tweetId = tweet.getId();
                    startActivity(ShowTweetActivity.newIntent(getContext(), tweetId));
                }
            };

            holder.itemLayout.setOnClickListener(onClickShowTweetListener);
            Log.i(TAG,"ln 204");


            // TODO: 7/22/2017 reply button listenr

            holder.reply.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    TwitterApiClient client = TwitterCore.getInstance().getApiClient();
                    StatusesService statusesService = client.getStatusesService();
                    retrofit2.Call<Tweet> updateCall = statusesService.update(
                            "test", // TODO: 7/21/2017 replyActivity
                            tweet.getId(),
                            null,null,null,null,null,null,null);
                    updateCall.enqueue(new Callback<Tweet>() {
                        @Override
                        public void success(Result<Tweet> result) {
                            Log.i(TAG, "updateCall success");
                        }

                        @Override
                        public void failure(TwitterException exception) {

                        }
                    });
                }
            });

            // TODO: 7/22/2017 retweet button listener - dial



            Log.i(TAG, "onBindViewHolder called");
            Log.i(TAG, "onBindViewHolder called, position = " + String.valueOf(position));
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
                        updateUI(mTweets);
                        setMostRecentId();
                        setLeastRecentId();
                    }

                    Log.i(TAG, "mTweetsUpdate.size() = " + String.valueOf(result.data.size()));
                    Log.i(TAG, mTweets.get(0).text);
                    Log.i(TAG, String.valueOf(mTweets.get(0).getId()));
                    Log.i(TAG, "home timeline call success");
                    Log.i(TAG, "max  id = " + String.valueOf(leastRecentId));
                    Log.i(TAG, "sinceid = " + String.valueOf(mostRecentId));
                }
                @Override
                public void failure(TwitterException exception) {
                    Toast.makeText(getContext(), "Tweets arriving in 15 min", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "call for hometimeline failed --> " + exception.getMessage());
                }
            });
            return mTweets;
        }

        @Override
        protected void onPostExecute(List<Tweet> tweets) {
            super.onPostExecute(tweets);
            Log.i(TAG, "RefreshTask onPostExecute called");
        }
    }

    /**
     * Call to get previous home timeline tweets
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
                        updateUI(mTweets);
                        setMostRecentId();
                        setLeastRecentId();
                    }

                    Log.i(TAG, "mTweetsUpdate.size() = " + String.valueOf(result.data.size()));
                    Log.i(TAG, mTweets.get(0).text);
                    Log.i(TAG, String.valueOf(mTweets.get(0).getId()));
                    Log.i(TAG, "home timeline call success");
                    Log.i(TAG, "max  id = " + String.valueOf(leastRecentId));
                    Log.i(TAG, "sinceid = " + String.valueOf(mostRecentId));
                }
                @Override
                public void failure(TwitterException exception) {
                    Toast.makeText(getContext(), "Tweets arriving in 15 min", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "call for hometimeline failed --> " + exception.getMessage());
                }
            });


            return mTweets;
        }

        @Override
        protected void onPostExecute(List<Tweet> tweets) {
            super.onPostExecute(tweets);
            Log.i(TAG, "PullPrevTask onPostExecute called");
            // TODO: 7/21/2017
        }
    }

    /**
     *
     * @param tweets the tweets to load to the recycler view by the adapter
     */
    private void updateUI(List<Tweet> tweets) {
        if (mAdapter == null) {
            mAdapter = new HomeAdapter(tweets);
            mRecyclerViewHome.setAdapter(mAdapter);
        } else {
            mAdapter.notifyItemRangeChanged(0, mTweets.size());
        }
    }

    /**
     * most recent tweet's id
     */
    private void setMostRecentId() {
        if (mTweets != null) {
            long[] ids = new long[mTweets.size()];
            for (int i = 0; i < mTweets.size(); i ++) {
                ids[i] = mTweets.get(i).getId();
            }
            mostRecentId = ids[0];
            for (long id : ids) {
                if (id > mostRecentId) {
                    mostRecentId = id;
                }
            }
        }
    }

    /**
     * least recent tweet's id
     */
    private void setLeastRecentId() {
        if (mTweets != null) {
            long[] ids = new long[mTweets.size()];
            for (int i = 0; i < mTweets.size(); i ++) {
                ids[i] = mTweets.get(i).getId();
            }
            leastRecentId = ids[0];
            for (long id : ids) {
                if (id < leastRecentId) {
                    leastRecentId = id;
                }
            }
        }
    }
}
