package com.robyn.bitty;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
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

import retrofit2.Call;

// TODO: 7/23/2017 drawer
// TODO: 7/23/2017  nav bar search/ direct msg
// TODO: 7/26/2017 move toolbar to fragment

public class MainActivity extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private BottomNavigationView mBottomNavigationView;
    private ProgressBar mProgressBar;
    private Toolbar mToolbar;
    private URL userImageUrl = null;
    private String userImageUrlString;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    updateFragment(mBottomNavigationView.getSelectedItemId());
                    return true;
                case R.id.nav_create:
                    composeTweet();
                    return true;
                case R.id.search:
                    updateFragment(mBottomNavigationView.getSelectedItemId());
                    return true;
            }
            return true;
        }
    };

    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    public void composeTweet() {
        final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                .getActiveSession();
        final Intent intent = new ComposerActivity.Builder(MainActivity.this)
                .session(session)
                .createIntent();
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        new CheckAuthTask().execute();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Home");
        }

        mProgressBar = (ProgressBar) findViewById(R.id.process_bar);

/**
 * nav drawer_layout layout
 */
        DrawerLayout drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawer_toggle = new ActionBarDrawerToggle(
                this, drawer_layout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_layout.setDrawerListener(drawer_toggle);
        drawer_toggle.syncState();

        // drawer_layout content
        NavigationView drawer_nav = (NavigationView) findViewById(R.id.nav_view);
        drawer_nav.setNavigationItemSelectedListener(this);

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }

    public void createFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = HomeTimelineFragment.newInstance(userImageUrlString);
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        } else {
            updateFragment(mBottomNavigationView.getSelectedItemId());
        }
    }

    private void updateFragment(int itemId) {
        Log.i(TAG, "updateFragment called");

        Fragment fragment;

        switch (mBottomNavigationView.getSelectedItemId()) {
            case R.id.nav_home:
                fragment = HomeTimelineFragment.newInstance(userImageUrlString);
                break;
            case R.id.search:
                fragment = SearchFragment.newInstance();
                break;
            default:
                return;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();


    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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

    public class CheckAuthTask extends AsyncTask<Void, Void, Void> {
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
                                Drawable profileDrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(resource, 100, 100, true));
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
                    mProgressBar.setVisibility(View.GONE);

                }

                @Override
                public void failure(TwitterException exception) {
                    startActivity(LoginActivity.newIntent(getApplicationContext()));
                    Log.i(TAG, exception.getMessage());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            createFragment();
            Log.i(TAG, "url = " + userImageUrlString);
            cancel(false);
        }
    }
}
