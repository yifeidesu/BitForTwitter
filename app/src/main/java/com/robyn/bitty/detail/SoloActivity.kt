package com.robyn.bitty.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import com.robyn.bitty.R
import com.robyn.bitty.data.DataSource
import com.robyn.bitty.utils.replaceFragment

/**
 * This Activity is to show a stand-alone tweet
 *
 *
 * todo add a fragment in this
 */
class SoloActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.solo_ac)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar?.setTitle(R.string.tweet)
        }

        toolbar.setNavigationIcon(R.drawable.ic_nav_back)
        toolbar.setNavigationOnClickListener { finish() } // todo remove

        val dataSource = DataSource.INSTANCE

        val tweetId = intent.getLongExtra(EXTRA_TWEET_ID, 510908133917487104L)

        val soloFragment = SoloFragment.newInstance().also {
            Runnable {
                SoloPresenter(it, dataSource, tweetId)
            }.run()
        }

        // Fragment transaction ... commit!
        replaceFragment(
            soloFragment,
            R.id.fg_container_solo
        )
    }

    companion object {
        private val TAG = SoloActivity::class.java.simpleName

        val EXTRA_TWEET_ID = "tweet.id"

        fun newIntent(context: Context, tweetId: Long): Intent {
            return Intent(context, SoloActivity::class.java)
                .putExtra(EXTRA_TWEET_ID, tweetId)
        }
    }
}