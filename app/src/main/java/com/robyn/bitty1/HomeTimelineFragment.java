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


/**
 * Created by yifei on 7/18/2017.
 */

public class HomeTimelineFragment extends Fragment {
    private static final String TAG = HomeTimelineFragment.class.getSimpleName();

    private List<Tweet> mTweets;
    private RecyclerView mRecyclerViewHome;
    private HomeAdapter mAdapter;

    private long mostRecentId = 0;
    private long leastRecentId = 0;

    public static HomeTimelineFragment newInstance() {
        
        Bundle args = new Bundle();
        
        HomeTimelineFragment fragment = new HomeTimelineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new MyTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hometimeline, container, false);

        // setup recyclerView
        mRecyclerViewHome = view.findViewById(R.id.home_timeline);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewHome.setLayoutManager(layoutManager );
        updateUI();

        // set divider for recyclerView items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerViewHome.getContext(),
                layoutManager.getOrientation());
        mRecyclerViewHome.addItemDecoration(dividerItemDecoration);

        // setup swipe refresh layout
        final SwipeRefreshLayout swipeLayout = view.findViewById(R.id.refresh_layout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new MyTask().execute();
                swipeLayout.setRefreshing(false);
            }
        });
        return view;
    }

    /**
     * recycler view classes
     */
    private class HomeHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private LinearLayout mLinearLayout;

        public HomeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item, parent, false));

            itemView.setOnClickListener(this);
            mLinearLayout = itemView.findViewById(R.id.my_tweet_layout);
        }

        public void bind(Tweet tweet) {
            mLinearLayout.addView(new TweetView(getContext(), tweet, R.style.tw__TweetLightWithActionsStyle));
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
            Tweet tweet = mTweets.get(position);
            holder.bind(tweet);
        }

        @Override
        public int getItemCount() {
            return mTweets.size();
        }
    }

    public class MyTask extends AsyncTask<Void,Void,List<Tweet>> {

        /**
         * working thread
         * @param voids
         * @return return the List.Tweet the call requests
         */
        @Override
        protected List<Tweet> doInBackground(Void... voids) {
            final List<Tweet> tweets = new ArrayList<>();

            TwitterApiClient client = TwitterCore.getInstance().getApiClient();
            StatusesService statusesService = client.getStatusesService();
            retrofit2.Call<List<Tweet>> call = statusesService.homeTimeline(
                    null,
                    ((mostRecentId == 0)?null:(mostRecentId -1)), // Returns results with an ID greater than, more recent than this
                    null, // last call's sinceid = current call's max(least recent) id
                    false, true, true, true);
            call.enqueue(new Callback<List<Tweet>>() {
                @Override
                public void success(Result<List<Tweet>> result) {
                    mTweets = result.data;
                    setMostRecentId();
                    setLeastRecentId();
                    Log.i(TAG, mTweets.get(0).text);
                    Log.i(TAG, String.valueOf(mTweets.get(0).getId()));
                    Log.i(TAG, "home timeline call success");
                    Log.i(TAG, "max  id = " + String.valueOf(leastRecentId));
                    Log.i(TAG, "sinceid = " + String.valueOf(mostRecentId));

                    updateUI();
                }

                @Override
                public void failure(TwitterException exception) {
                    Log.i(TAG, "call for hometimeline failed --> " + exception.getMessage());
                }
            });
            return tweets;
        }

        @Override
        protected void onPostExecute(List<Tweet> tweets) {
            super.onPostExecute(tweets);
            Log.i(TAG, "onPostExecute called");
        }
    }

    private void updateUI() {
        if (mTweets != null ) {
            if (mAdapter == null) {
                mAdapter = new HomeAdapter(mTweets);
                mRecyclerViewHome.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
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
