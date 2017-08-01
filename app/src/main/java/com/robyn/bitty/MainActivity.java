package com.robyn.bitty;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.AccountService;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;

import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

// TODO: 7/23/2017 drawer

// TODO: 7/29/2017 runtime add in the search view

public class MainActivity extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private URL userImageUrl = null;
    private String userImageUrlString;

    private FragmentManager mFragmentManager;
    private Fragment mFragment;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.networking_wrong_msg) TextView mNetworkingWrongMsg;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mToolbar.getChildAt(0).setVisibility(View.VISIBLE);

                    replaceFragment(HomeTimelineFragment.newInstance());
                    return true;
                case R.id.navigation_compose:
                    composeTweet();
                    return true;
                case R.id.navigation_search:
                    mToolbar.getChildAt(0).setVisibility(View.GONE);
                    replaceFragment(SearchFragment.newInstance());
                    return true;
            }
            return false;
        }

    };

    void replaceFragment(Fragment fragment) {
        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        new CheckAuthTask().execute();

        ButterKnife.bind(this);

        mNetworkingWrongMsg.setVisibility(View.GONE);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Home");
        }

        DrawerLayout drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawer_toggle = new ActionBarDrawerToggle(
                this, drawer_layout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_layout.setDrawerListener(drawer_toggle);
        drawer_toggle.syncState();

        // drawer_layout content
        NavigationView drawer_nav = (NavigationView) findViewById(R.id.drawer_nav_view);
        drawer_nav.setNavigationItemSelectedListener(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mFragmentManager = getSupportFragmentManager();
        mFragment = mFragmentManager.findFragmentById(R.id.fragment_container);
        if (mFragment == null) {
            mFragment = HomeTimelineFragment.newInstance();
            mFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, mFragment)
                    .commit();
        }
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        return super.onPrepareOptionsMenu(menu);
//
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // try runtime do this
//        Log.i(TAG, "menu create" + menu.size());
//
//        menu.clear();
//        if (menu.size()<1) {
//            getMenuInflater().inflate(R.menu.menu_search, menu);
//
//            MenuItem searchItem = menu.findItem(R.id.menu_item_search);
//            final SearchView searchView = (SearchView) searchItem.getActionView();
//
//            SearchManager searchManager =
//                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//
//            searchView.setSearchableInfo(
//                    searchManager.getSearchableInfo(getComponentName()));
//        }
//
//        return false;
//    }


    public void composeTweet() {
        final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                .getActiveSession();
        final Intent intent = new ComposerActivity.Builder(MainActivity.this)
                .session(session)
                .createIntent();
        startActivity(intent);
    }

    // for drawer
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // the drawer nav
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            startActivity(LoginActivity.newIntent(getApplicationContext()));
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class CheckAuthTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            TwitterApiClient client = TwitterCore.getInstance().getApiClient();
            AccountService accountService = client.getAccountService();
            final Call<User> call = accountService.verifyCredentials(false, true, false);
            call.enqueue(new Callback<User>() {
                @Override
                public void success(Result<User> result) {
                    try {
                        userImageUrl = new URL(result.data.profileImageUrlHttps);
                        userImageUrlString = userImageUrl.toString();
                        String string = result.response.toString();
                        Log.i(TAG, string);

                        ImageView profileImage = (ImageView) mToolbar.getChildAt(1);

                        Glide.with(getApplicationContext()).load(userImageUrl)
                                .asBitmap().into(new BitmapImageViewTarget(profileImage) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                Bitmap big = Bitmap.createScaledBitmap(resource, 90, 90, true);

                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), big);
                                circularBitmapDrawable.setCircular(true);

                                mToolbar.setNavigationIcon(circularBitmapDrawable);
                            }
                        });

                        Log.i(TAG, "userImageUrl = " + userImageUrl);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, result.data.name);
                    new MakeSound().playSound(getApplicationContext());
                    mNetworkingWrongMsg.setVisibility(View.GONE);
                }

                @Override
                public void failure(TwitterException exception) {
                    startActivity(LoginActivity.newIntent(getApplicationContext()));
                    mNetworkingWrongMsg.setVisibility(View.VISIBLE);
                    Log.i(TAG, exception.getMessage());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i(TAG, "url = " + userImageUrlString);
            cancel(false);
        }
    }
}
