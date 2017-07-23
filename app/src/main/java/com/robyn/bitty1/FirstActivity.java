package com.robyn.bitty1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.AccountService;

import retrofit2.Call;

public class FirstActivity extends AppCompatActivity {
    private static final String TAG = FirstActivity.class.getSimpleName();
    private TwitterLoginButton mLoginButton;

    final Callback<User> userCallback = new Callback<User>() {
        @Override
        public void success(Result<User> result) {

            startActivity(MainActivity.newIntent(getApplicationContext()));
            Log.i(TAG, result.data.name);
        }

        @Override
        public void failure(TwitterException exception) {

            startActivity(LoginActivity.newIntent(getApplicationContext()));
            Log.i(TAG, exception.getMessage());
        }
    };

    public static Intent newIntent(Context context) {
        return new Intent(context, FirstActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        TwitterApiClient client = TwitterCore.getInstance().getApiClient();
        AccountService accountService = client.getAccountService();
        Call<User> call = accountService.verifyCredentials(false, true, false);
        call.enqueue(userCallback);

    }

}
