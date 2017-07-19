package com.robyn.bitty;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.FixedTweetTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yifei on 7/18/2017.
 */

public class HomeTimelineFragment extends Fragment {
    private static final String TAG = HomeTimelineFragment.class.getSimpleName();

    private List<Tweet> mTweets;

    private ListView mHomeTimeLine;

    private MyTask mMyTask = new MyTask();


    public static HomeTimelineFragment newInstance() {
        
        Bundle args = new Bundle();
        
        HomeTimelineFragment fragment = new HomeTimelineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMyTask.execute();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hometimeline, container, false);

        mHomeTimeLine = view.findViewById(R.id.home_timeline);
        Log.i(TAG, "oncreateview called");

        return view;
    }


    public class MyTask extends AsyncTask<Void,Void,List<Tweet>> {

        @Override
        protected List<Tweet> doInBackground(Void... voids) {
            final List<Tweet> tweets = new ArrayList<>();

            TwitterApiClient client = TwitterCore.getInstance().getApiClient();
            StatusesService statusesService = client.getStatusesService();
            retrofit2.Call<List<Tweet>> call = statusesService.homeTimeline(30, null, null,
                    false, true, true, true);
            call.enqueue(new Callback<List<Tweet>>() {
                @Override
                public void success(Result<List<Tweet>> result) {
                    mTweets = result.data;
                    final FixedTweetTimeline timeline = new FixedTweetTimeline.Builder()
                            .setTweets(mTweets)
                            .build();

                    final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(getActivity())
                            .setTimeline(timeline)
                            .build();

                    mHomeTimeLine.setAdapter(adapter);
                }

                @Override
                public void failure(TwitterException exception) {
                    Log.i(TAG, exception.getMessage());

                }
            });
            return tweets;
        }

        @Override
        protected void onPostExecute(List<Tweet> tweets) {
            super.onPostExecute(tweets);
            Log.i(TAG, "onPostExecute called");
            mTweets = tweets;


        }
    }
}
