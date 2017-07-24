package com.robyn.bitty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

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
// TODO: 7/23/2017  actionbar profile photo
// TODO: 7/23/2017  nav bar search/ direct msg



public class MainActivity extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private BottomNavigationView mBottomNavigationView;
    private Button mButtonLoginScreen;

    private ImageView mImageViewUser;

    private URL userImageUrl;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    HomeTimelineFragment.newInstance();
                    return true;
                case R.id.nav_create:
                    composeTweet();
                    // after compose set seleteditem to home, and refresh
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
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
                .text("Love where you work")
                .hashtags("#twitter")
                .createIntent();
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        TwitterApiClient client = TwitterCore.getInstance().getApiClient();
        AccountService accountService = client.getAccountService();
        Call<User> call = accountService.verifyCredentials(false, true, false);
        call.enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> result) {
                //startActivity(MainActivity.newIntent(getApplicationContext()));
                try {
                    userImageUrl = new URL(result.data.profileImageUrlHttps);
                    // TODO: 7/21/2017 add user image to tool bar
//                    mImageViewUser = (ImageView) findViewById(R.id.user_icon);
//                    Glide.with(getApplicationContext()).load(userImageUrl)
//                            .apply(RequestOptions.circleCropTransform())
//                            .into(mImageViewUser);
                    Log.i(TAG, "userImageUrl = " + userImageUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, result.data.name);
            }

            @Override
            public void failure(TwitterException exception) {
                startActivity(LoginActivity.newIntent(getApplicationContext()));
                Log.i(TAG, exception.getMessage());
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

/**
 * nav drawer_layout layout
 */
        DrawerLayout drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawer_toggle = new ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_layout.setDrawerListener(drawer_toggle);
        drawer_toggle.syncState();

        // drawer_layout content
        NavigationView drawer_nav = (NavigationView) findViewById(R.id.nav_view);
        drawer_nav.setNavigationItemSelectedListener(this);

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }

        createFragment();
    }

    private Fragment createFragment() {
        switch (mBottomNavigationView.getSelectedItemId()) {
            case R.id.nav_home:
                Log.i(TAG, "createFragment-home called");
                return HomeTimelineFragment.newInstance();
            default:
                return HomeTimelineFragment.newInstance();
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
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
}
