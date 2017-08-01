package com.robyn.bitty;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.FixedTweetTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by yifei on 7/26/2017.
 */

public class SearchFragment extends Fragment {
    private static final String TAG = SearchFragment.class.getSimpleName();

    private String mQuery;
    private List<Tweet> mTweets = new ArrayList<>();
    private ListView mListView;
    private RecyclerView mRecyclerView;

    public static SearchFragment newInstance() {

        Bundle args = new Bundle();

        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        new PopularTweetsTask().execute();
        Log.i(TAG, "poptask exe");

        // TODO: 7/26/2017 searchTask.execute();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);




        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);



        // TODO: 7/30/2017 check book how search oncreateopetionmenu is wroten
//        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
//        final SearchView searchView = (SearchView) searchItem.getActionView();
//
//        SearchManager searchManager =
//                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
//
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getActivity().getComponentName()));

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, "query submit: " + query);
                mQuery = query;
                updateItem();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void updateItem() {
        new SearchTask().execute();
    }

    private class PopularTweetsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            TwitterApiClient client = TwitterCore.getInstance().getApiClient();
            SearchService searchService = client.getSearchService();
            Call<Search> searchCall = searchService.tweets("", null, null, null,
                    "popular", null, null, null, null, true);
            searchCall.enqueue(new Callback<Search>() {
                @Override
                public void success(Result<Search> result) {
                    Log.i(TAG, "search call success");
                    mTweets = result.data.tweets;

                    final FixedTweetTimeline timeline = new FixedTweetTimeline.Builder()
                            .setTweets(mTweets)
                            .build();

                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    final TweetTimelineRecyclerViewAdapter adapter =
                            new TweetTimelineRecyclerViewAdapter.Builder(getContext())
                                    .setTimeline(timeline)
                                    .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
                                    .build();

                    mRecyclerView.setAdapter(adapter);
                }

                @Override
                public void failure(TwitterException exception) {

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            cancel(false);
        }
    }

    // TODO: 7/31/2017 custom recycler
    private class SearchTask extends AsyncTask<Void, Void, Search> {

        @Override
        protected Search doInBackground(Void... voids) {
            TwitterApiClient client = TwitterCore.getInstance().getApiClient();
            SearchService searchService = client.getSearchService();
            Call<Search> searchCall = searchService.tweets(mQuery, null, null, null,
                    null, null, null, null, null, true);
            searchCall.enqueue(new Callback<Search>() {
                @Override
                public void success(Result<Search> result) {
                    Log.i(TAG, "search call success");
                    mTweets = result.data.tweets;

                    final FixedTweetTimeline timeline = new FixedTweetTimeline.Builder()
                            .setTweets(mTweets)
                            .build();

                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));




                    final TweetTimelineRecyclerViewAdapter adapter =
                            new TweetTimelineRecyclerViewAdapter.Builder(getContext())
                                    .setTimeline(timeline)
                                    .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
                                    .build();



                    mRecyclerView.setAdapter(adapter);

                }

                @Override
                public void failure(TwitterException exception) {

                }
            });
            return null;

        }

        @Override
        protected void onPostExecute(Search search) {
            super.onPostExecute(search);
            cancel(false);
        }
    }
}
