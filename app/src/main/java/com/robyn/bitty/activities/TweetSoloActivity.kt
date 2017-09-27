package com.robyn.bitty.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View

import com.robyn.bitty.R
import com.robyn.bitty.favoAction
import com.robyn.bitty.atScreenName
import com.robyn.bitty.createdAtTime
import com.robyn.bitty.loadOriginalProfileImage
//import com.robyn.bitty.ui.timelines.*
import kotlinx.android.synthetic.main.fragment_tweet_actions.*

import kotlinx.android.synthetic.main.tweet_layout_solo.*

class TweetSoloActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_tweet)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setTitle(R.string.tweet)
        }

        toolbar.setNavigationIcon(R.drawable.ic_nav_back)
        toolbar.setNavigationOnClickListener { finish() }

        /**
         * Get extras
         */

        val tweetId = intent.getLongExtra(EXTRA_TWEET_ID, 510908133917487104L)
        val isFavo = intent.getBooleanExtra(EXTRA_TWEET_IS_FAVO, false)

        val userProfileUrl = intent.getStringExtra(EXTRA_PROFILE_IMAGE_URL)
        val createAtString = intent.getStringExtra(EXTRA_CREATE_AT)


        /* bind data to solo tweet's layout */
        user_name_solo.text = intent.getStringExtra(EXTRA_USER_NAME)
        user_screen_name_solo.text = atScreenName(intent.getStringExtra(EXTRA_SCREEN_NAME))

        Log.i("solo", userProfileUrl)
        loadOriginalProfileImage(applicationContext, userProfileUrl, user_profile_image_solo, 0)

        tweet_text_solo.text = intent.getStringExtra(EXTRA_ENTITY_TEXT)

        create_at_solo.text = createdAtTime(createAtString)

        // tweet actions
        favoAction(applicationContext, tweetId, isFavo, favo_image)
    }

    companion object {
        private val TAG = TweetSoloActivity::class.java.simpleName

        val EXTRA_TWEET_ID = "tweet.id"
        val EXTRA_TWEET_IS_FAVO = "tweet.favoed"

        val EXTRA_USER_NAME = "tweet.userName"
        val EXTRA_SCREEN_NAME = "tweet.screenName"
        val EXTRA_PROFILE_IMAGE_URL = "tweet.profileImageUrl"
        val EXTRA_ENTITY_TEXT = "tweet.entities"
        val EXTRA_CREATE_AT = "tweet.createAt"
        val EXTRA_NOTES_COUNT = "tweet.notesCount"

        /**
         * no tweet id, no new request
         */
        fun newIntent(context: Context,
                      tweetId: Long,
                      isFavo: Boolean,
                      userName:String,
                      screenName:String? = null,
                      profileImageUrl:String,
                      entityText:String,
                      createAt: String): Intent {
            val intent = Intent(context, TweetSoloActivity::class.java)
            intent.putExtra(EXTRA_TWEET_ID, tweetId)
            intent.putExtra(EXTRA_TWEET_IS_FAVO, isFavo)
            intent.putExtra(EXTRA_USER_NAME, userName)
            intent.putExtra(EXTRA_SCREEN_NAME, screenName)
            intent.putExtra(EXTRA_PROFILE_IMAGE_URL, profileImageUrl)
            intent.putExtra(EXTRA_ENTITY_TEXT, entityText)
            intent.putExtra(EXTRA_CREATE_AT, createAt)
            return intent
        }
    }
}
