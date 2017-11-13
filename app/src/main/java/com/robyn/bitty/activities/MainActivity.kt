package com.robyn.bitty.activities

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView

import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.User
import com.twitter.sdk.android.tweetcomposer.ComposerActivity

import java.net.MalformedURLException
import java.net.URL

import com.robyn.bitty.*
import com.robyn.bitty.fragments.HomeTimelineFragment
import com.robyn.bitty.fragments.SearchFragment

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_activity_main.*
import kotlinx.android.synthetic.main.drawer_header.*
import java.util.concurrent.Callable

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var userImageUrl: URL? = null
    private var userImageUrlString: String? = null

    private var mFragmentManager: FragmentManager? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                toolbar_main!!.getChildAt(0).visibility = View.VISIBLE
                replaceFragment(HomeTimelineFragment.newInstance())

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_compose -> {
                composeTweet()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_search -> {
                toolbar_main!!.getChildAt(0).visibility = View.GONE
                replaceFragment(SearchFragment.newInstance())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    internal fun replaceFragment(fragment: Fragment) {
        mFragmentManager!!.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer_activity_main)

        //to CheckAuth
        isVertified()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()) // receive on main thread
                .subscribe(
                        { result ->
                            Log.i(TAG, "vertify result = ${result}")
                            // fun updateProfileImg()
                        },
                        { err -> Log.e(TAG, err.message) },
                        {
                            // fun updateProfileImg()
                        })

        // top bar
        setSupportActionBar(toolbar_main)
        if (supportActionBar != null) {
            supportActionBar!!.title = "Home"

        }

        // bottom bar
        bottom_nav_view.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        // fragment in middle
        networking_wrong_msg!!.visibility = View.GONE

        mFragmentManager = supportFragmentManager
        var fragment: Fragment? = mFragmentManager!!.findFragmentById(R.id.fragment_container)
        if (fragment == null) {
            fragment = HomeTimelineFragment.newInstance()
            mFragmentManager!!
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit()
        }


        // the drawer
        val drawer_toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar_main, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout!!.setDrawerListener(drawer_toggle)
        drawer_toggle.syncState()

        drawer_nav_view!!.setNavigationItemSelectedListener(this)
    }

    fun isVertified(): Observable<Boolean> {
        return Observable.defer(Callable<ObservableSource<Boolean>> {
            try {
                return@Callable Observable.just(checkAuth())
            } catch (e: Exception) {
                Log.e(TAG, e.message)
                return@Callable null
            }
        })
    }

    fun composeTweet() {
        val session = TwitterCore.getInstance().sessionManager
                .activeSession
        val intent = ComposerActivity.Builder(this@MainActivity)
                .session(session)
                .createIntent()
        startActivity(intent)
    }

    // for drawer
    override fun onBackPressed() {
        if (drawer_layout!!.isDrawerOpen(GravityCompat.START)) {
            drawer_layout!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // the drawer nav
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.drawer_login) {
            startActivity(LoginActivity.newIntent(applicationContext))
        } else if (id == R.id.drawer_feedback) {
            val intent = Intent(Intent.ACTION_SEND)

            val appName = Bitty::class.java.simpleName
            val androidVersion = "Android Version: " + Build.VERSION.SDK_INT.toString() + "\n"
            val manufacturer = "Manufacturer: " + Build.MANUFACTURER + "\n"
            val model = "Model: " + Build.MODEL + "\n"
            val version = "App Version: " + BuildConfig.VERSION_CODE + BuildConfig.VERSION_NAME + "\n\n"

            intent.type = "plain/text"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("feedback.dayplus@gmail.com"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback to " + appName)
            intent.putExtra(Intent.EXTRA_TEXT,
                    androidVersion + manufacturer + model + version
                            + "======\n")

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(Intent.createChooser(intent, "Send Email"))
            }
        }

        drawer_layout!!.closeDrawer(GravityCompat.START)
        return true
    }

    private fun checkAuth(): Boolean {
        var isVertified = false
        val client = TwitterCore.getInstance().apiClient
        val accountService = client.accountService

        val call = accountService.verifyCredentials(false, true, false)

        call.enqueue(object : Callback<User>() {
            override fun success(result: Result<User>) {
                isVertified = true
                try {
                    val user = result.data

                    userImageUrl = URL(user.profileImageUrlHttps)
                    userImageUrlString = userImageUrl!!.toString()

                    val string = result.response.toString()
                    Log.i(TAG, string)


                    val profileImage = toolbar_main.getChildAt(1) as ImageView

                    val profileImageDrawer = drawer_layout.findViewById<ImageView>(R.id.profile_img_drawer)

                    user_name_drawer.text = user.name
                    screen_name_drawer.text = atScreenName(user)
                    follows_count.text = user.friendsCount.toString()// ???
                    follower_count.text = user.followersCount.toString()

                    // follower_count.text = user.followersCount.toString()

                    // tool bar nav icon
                    loadProfileImage(applicationContext, user, profileImage, 0.6f)

                    // drawer profile image
                    loadOriginalProfileImage(applicationContext, originalProfileImageUrl(user.profileImageUrl), profileImageDrawer, 0)

                    // drawer banner image
                    loadBannerImage(applicationContext, user, banner_image)

                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }

                Log.i(TAG, result.data.name)

                MakeSound().playSound(applicationContext)
                networking_wrong_msg!!.visibility = View.GONE

            }

            override fun failure(exception: TwitterException) {
                startActivity(LoginActivity.newIntent(applicationContext))
                networking_wrong_msg!!.visibility = View.VISIBLE
                Log.e(TAG, exception.message)
            }
        })
        return isVertified
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
