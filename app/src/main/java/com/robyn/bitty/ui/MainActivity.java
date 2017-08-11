package com.robyn.bitty.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.robyn.bitty.Bitty;
import com.robyn.bitty.BuildConfig;
import com.robyn.bitty.MakeSound;
import com.robyn.bitty.R;
import com.robyn.bitty.ui.timelines.HomeTimelineFragment;
import com.robyn.bitty.ui.timelines.SearchFragment;
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

public class MainActivity extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private URL userImageUrl = null;
    private String userImageUrlString;

    private FragmentManager mFragmentManager;

    private ImageView mDrawerProfileImg;
    private TextView mDrawerScreenName;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.networking_wrong_msg) TextView mNetworkingWrongMsg;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.drawer_nav_view)
    NavigationView mDrawerNavView;
    @BindView(R.id.bottom_nav_view)
    BottomNavigationView mBottomNavView;

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
                .replace(R.id.fragment_container_actions, fragment)
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

        // top bar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Home");
        }

        // bottom bar
        mBottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // fragment in middle
        mNetworkingWrongMsg.setVisibility(View.GONE);

        mFragmentManager = getSupportFragmentManager();
        Fragment fragment = mFragmentManager.findFragmentById(R.id.fragment_container_actions);
        if (fragment == null) {
            fragment = HomeTimelineFragment.newInstance();
            mFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container_actions, fragment)
                    .commit();
        }


        // the drawer
        ActionBarDrawerToggle drawer_toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(drawer_toggle);
        drawer_toggle.syncState();

        mDrawerNavView.setNavigationItemSelectedListener(this);
        View drawerHeader = mDrawerNavView.getChildAt(0);
        mDrawerProfileImg = (ImageView) drawerHeader.findViewById(R.id.profile_img_drawer);

    }

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
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // the drawer nav
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.drawer_login) {
            startActivity(LoginActivity.newIntent(getApplicationContext()));
        } else if (id == R.id.drawer_feedback) {
            Intent intent = new Intent(Intent.ACTION_SEND);

            String appName = Bitty.class.getSimpleName();
            String androidVersion = "Android Version: " + String.valueOf(Build.VERSION.SDK_INT)  + "\n";
            String manufacturer = "Manufacturer: " + Build.MANUFACTURER + "\n";
            String model = "Model: " + Build.MODEL + "\n";
            String version = "App Version: " + BuildConfig.VERSION_CODE + BuildConfig.VERSION_NAME + "\n\n";

            intent.setType("plain/text");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "feedback.dayplus@gmail.com" });
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback to " + appName);
            intent.putExtra(Intent.EXTRA_TEXT,
                    androidVersion + manufacturer + model + version
                            + "======\n");

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
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

                        View drawerHeader = mDrawerNavView.getChildAt(0);
                        mDrawerProfileImg = drawerHeader.findViewById(R.id.profile_img_drawer);

                        Glide.with(getApplicationContext()).load(userImageUrl)
                                .asBitmap().into(new BitmapImageViewTarget(profileImage) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                Bitmap navIconBitmap = Bitmap.createScaledBitmap(resource, 90, 90, true);

                                RoundedBitmapDrawable roundNavIconDrawable =
                                        RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), navIconBitmap);
                                roundNavIconDrawable.setCircular(true);

                                Bitmap headerProfileBitmap = Bitmap.createScaledBitmap(resource, 200, 200, true);

                                RoundedBitmapDrawable roundHeaderDrawable =
                                        RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), headerProfileBitmap);
                                roundHeaderDrawable.setCircular(true);

                                mToolbar.setNavigationIcon(roundNavIconDrawable);
                                mDrawerProfileImg.setImageDrawable(roundHeaderDrawable);
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
