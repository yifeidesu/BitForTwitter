package com.robyn.bitty1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private BottomNavigationView mBottomNavigationView;
    private Button mButtonAction;
    private Button mButtonLoginScreen;

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
        setContentView(R.layout.activity_main);

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

        mButtonAction = (Button) findViewById(R.id.action_button);
        mButtonAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(EmbeddedTweetsActivity.newIntent(getApplicationContext()));
            }
        });

        mButtonLoginScreen = (Button) findViewById(R.id.gologin);
        mButtonLoginScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(LoginActivity.newIntent(getApplicationContext()));
            }
        });
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
}
