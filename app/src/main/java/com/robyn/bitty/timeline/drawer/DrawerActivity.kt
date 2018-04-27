package com.robyn.bitty.timeline.drawer

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.robyn.bitty.BitApplication
import com.robyn.bitty.BuildConfig
import com.robyn.bitty.R
import com.robyn.bitty.data.DataSource
import com.robyn.bitty.login.LoginActivity
import com.robyn.bitty.timeline.*
import com.robyn.bitty.utils.replaceFragment
import com.twitter.sdk.android.core.models.User
import kotlinx.android.synthetic.main.drawer_ac.*
import kotlinx.android.synthetic.main.drawer_header.*
import kotlinx.android.synthetic.main.main_ac.*

class DrawerActivity : AppCompatActivity(), DrawerContract.View,
    TimelineFragment.TimelineFragmentCallback,
    NavigationView.OnNavigationItemSelectedListener {

    override lateinit var mPresenter: DrawerContract.Presenter

    lateinit var mProfileImageView: ImageView

//    private var userImageUrl: URL? = null
//    private var userImageUrlString: String? = null

    private val dataSource = DataSource()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer_ac)

        // top bar
        setSupportActionBar(toolbar_main)
        if (supportActionBar != null) {
            supportActionBar?.title = "Home"
        }

        // Create the home timeline fragment that initially shown in this activity
        switchFragment(HOME_TIMELINE_TAG) // mCallback not init, that this fg's onAttach not called

        // bottom bar
        val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_home -> {
                        showActionbarNavIcon() // todo make it always shown

                        switchFragment(HOME_TIMELINE_TAG)

                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_compose -> {

                        mPresenter.composeTweet(this)

                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_search -> {

                        showActionbarNavIcon()

                        switchFragment(SEARCH_TIMELINE_TAG)

                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }
        bottom_nav_view.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        // drawers presenter
        mPresenter =
                DrawerPresenter(this, dataSource)

        mPresenter.verifyCredentials()

        // the drawer
        val drawer_toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar_main,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout?.addDrawerListener(drawer_toggle)
        drawer_toggle.syncState()

        drawer_nav_view?.setNavigationItemSelectedListener(this)
    }

    // This navigation listener is for the MenuItems in the drawer
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.drawer_login -> {
                startActivity(LoginActivity.newIntent(applicationContext))
            }

            R.id.drawer_feedback -> {
                sendFeedback()
            }
        }

        drawer_layout?.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {

        drawer_layout?.let {
            if (it.isDrawerOpen(GravityCompat.START)) {
                it.closeDrawer(GravityCompat.START)
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun customUI(user: User) {

        with(user) {

            // Load user info to drawer content
            user_name_drawer?.text = name
            screen_name_drawer?.text = atScreenName()
            follows_count?.text = friendsCount.toString()
            follower_count?.text = followersCount.toString()
            loadBannerImage(applicationContext, banner_image)
            loadProfileImage(applicationContext, profile_img_drawer)

            // Load user profile image as toolbar navigation icon
            val profileImage_onToolbar = toolbar_main.getChildAt(1) as ImageView
            loadProfileImage(applicationContext, profileImage_onToolbar, sizeMultiplier = 0.6f)
        }
    }

    override fun login() {
        val intent = LoginActivity.newIntent(this)
        startActivity(intent)
        networking_wrong_msg?.visibility = View.VISIBLE
    }

    override fun setActionbarSubtitle(title: String) {
        supportActionBar?.title = title
    }

    override fun loadProfileImage(urlString: String, compressionQuality: Int) {
        Glide.with(applicationContext).load(urlString)
            .apply(
                RequestOptions().encodeQuality(compressionQuality).circleCrop()
            )
            .into(mProfileImageView)
    }

    private fun showActionbarNavIcon(visibilityCode: Int = View.VISIBLE) {
        toolbar_main?.getChildAt(0)?.visibility = visibilityCode
    }

    private fun switchFragment(timelineCode: Int = 0) {

        // Check if timeline fragment of this type already exists
        val tagFragment = supportFragmentManager.findFragmentByTag(timelineCode.toString())

        // Reference of the fragment to be added to container
        val timelineFragment: TimelineFragment =
            if (tagFragment != null) {
                tagFragment as TimelineFragment
            } else {
                TimelineFragment.newInstance().also {
                    Runnable {
                        TimelinePresenter(it, dataSource, timelineCode)
                    }.run()
                }
            }

        // Fragment transaction ... commit!
        replaceFragment(
            timelineFragment,
            R.id.fragment_container_main_ac,
            timelineCode.toString()
        )
    }

    private fun sendFeedback() {

        val appName = BitApplication::class.java.simpleName
        val androidVersion = "Android Version: " + Build.VERSION.SDK_INT.toString() + "\n"
        val manufacturer = "Manufacturer: " + Build.MANUFACTURER + "\n"
        val model = "Model: " + Build.MODEL + "\n"
        val version =
            "App Version: " + BuildConfig.VERSION_CODE + BuildConfig.VERSION_NAME + "\n\n"

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "plain/text"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("feedback.dayplus@gmail.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback to " + appName)
        intent.putExtra(
            Intent.EXTRA_TEXT,
            androidVersion + manufacturer + model + version
                    + "======\n"
        )

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(intent, "Send Email"))
        }
    }

    companion object {
        private val TAG = DrawerActivity::class.java.simpleName

        const val HOME_TIMELINE_TAG = 0
        const val SEARCH_TIMELINE_TAG = 1

        fun newIntent(context: Context): Intent {
            return Intent(context, DrawerActivity::class.java)
        }
    }
}
