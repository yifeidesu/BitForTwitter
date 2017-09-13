package com.robyn.bitty.ui.timelines

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.ShareActionProvider
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import com.robyn.bitty.ColorToggle
import com.robyn.bitty.MyAsyncTasks.RefreshTask
import com.robyn.bitty.R
import com.robyn.bitty.ui.ShowTweetActivity
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.TweetView

import butterknife.BindView
import butterknife.ButterKnife

import android.support.v7.widget.RecyclerView.*
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.Callable


/**
 * home timeline
 *
 * Created by yifei on 7/18/2017.
 */

class HomeTimelineFragment : Fragment(), RefreshTask.RefreshResponse {
    val TAG_SIZE = "observable"
    val TAG_TWEETS_SIZE = "tweets.size"

    private var mTweets: MutableList<Tweet> = ArrayList()
    private var mTweetsUpdate: MutableList<Tweet> = ArrayList()
    lateinit var mRecyclerViewHome: RecyclerView
    private var mAdapter: HomeAdapter? = null

    private var mostRecentId: Long = 0
    private var leastRecentId: Long = 0
    private val mProfileImgUrlString: String? = null

    private var
            mButtonLoadMore: Button? = null
    //private ProgressBar mProgressBar;

    @BindView(R.id.progress_bar)
    lateinit var mProgressBar: ProgressBar


    /**
     *  update tweet list based on response;
     *
     *  To avoid the tweet list to long, remove the tweets at the end of the list.
     */
    fun updateTweets() {
        mTweets.addAll(0, mTweetsUpdate)
        if (mTweets.size > 50) {
            for (i in 50..mTweets.size) {
                mTweets.removeAt(i)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        //val param = RefreshTask.MyParams(context, this)
        //mTweetsUpdate = RefreshTask().execute(param).get().toMutableList()

        getObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()) // receive on main thread
                .subscribe(
                        {result ->
                            Log.i(TAG_SIZE, "RESPONSE SIZE =  ${result.size}")
                            mTweetsUpdate = result.toMutableList()
                            updateTweets() },
                        {err -> Log.e(TAG_SIZE, err.message)},
                        {
                            updateRecyclerUI()
                        })

        //updateRecyclerUI()

        Log.i(TAG, "home line oncreate, update tweets.size = ${mTweetsUpdate.size}")
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun fetchComplete(list: List<Tweet>) {
        mTweets.addAll(0, list.toMutableList())

        Log.i(TAG, "got ${list.size} tweets")
        Log.i(TAG, "got ${mTweets.size} tweets")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_hometimeline, container, false)
        ButterKnife.bind(this, view)

        mProgressBar = view.findViewById(R.id.progress_bar)

        // setup recyclerView
        mRecyclerViewHome = view.findViewById(R.id.home_timeline)
        val layoutManager = LinearLayoutManager(activity)

        mRecyclerViewHome.layoutManager = layoutManager

        mRecyclerViewHome.itemAnimator = null
        //UpdateUITask().execute()
        updateRecyclerUI()

        // set divider for recyclerView items
        val dividerItemDecoration = DividerItemDecoration(mRecyclerViewHome.context,
                layoutManager.orientation)
        mRecyclerViewHome.addItemDecoration(dividerItemDecoration)

        // setup swipe refresh layout
        val swipeLayout: SwipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.refresh_layout)
        swipeLayout.setOnRefreshListener {
            RefreshTask().execute()
            swipeLayout.isRefreshing = false
        }

        // TODO recyclerview set on scroll listener for scroll down to load prev tweets

        mButtonLoadMore = view.findViewById(R.id.load_more)
        mButtonLoadMore!!.visibility = View.GONE
        mButtonLoadMore!!.setOnClickListener {
            //mPullPrevTask.execute();
            mButtonLoadMore!!.visibility = View.GONE
            val m = LinearLayoutManager(activity)
            // TODO: 7/24/2017 scroll a bit upward when update finish
            Log.i(TAG, "PullPrevTask executed")
        }

        mRecyclerViewHome.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()
//                val pullPrevTask = PullPrevTask()
//
//                if (pastVisibleItems + visibleItemCount >= totalItemCount && pullPrevTask.status == AsyncTask.Status.PENDING) {
//
//                    try {
//                        pullPrevTask.execute()
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//
//                    Log.i(TAG, "reached bottom + pull task called")
//                }
            }
        })

        // TODO: 7/21/2017 double tap to go back to top

        if (mProgressBar!=null) {
            mProgressBar.visibility = View.GONE
        }
        return view
    }

    /**
     * the tweets passed in here should be updated w/ response already
     */
     inner class HomeAdapter(tweets: MutableList<Tweet>) : RecyclerView.Adapter<HomeAdapter.HomeHolder>() {

         private val mTweets = tweets



        init {
            notifyDataSetChanged() // ?


        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHolder {
            val inflater = LayoutInflater.from(context)
            return HomeHolder(inflater, parent)
        }

        override fun onBindViewHolder(holder: HomeHolder, position: Int) {

            // remove previous view if the holder is not empty,
            // otherwise holder shows multiple tweets in one single holder
            if (holder.myTweetLayout.childCount != 0) {
                holder.myTweetLayout.removeAllViews()
            }

            val tweet = mTweets[position]
            val tweetId = tweet.getId()
            val tweetView = TweetView(context, tweet)

            val notes = tweet.favoriteCount + tweet.retweetCount
            if (notes != 0) {
                holder.notesNum!!.text = notes.toString()
                holder.notesTextView!!.visibility = View.VISIBLE
            }

            // remove the top_right twitter icon
            tweetView.removeViewAt(4)
            holder.myTweetLayout
                    .addView(tweetView)

            // to remove defualt listener comes w/ the tweetView object
            holder.myTweetLayout.getChildAt(0).setOnClickListener(null)

            val onClickShowTweetListener = View.OnClickListener {
                Log.i(TAG, "onClickShowTweetListener invoked")
                startActivity(ShowTweetActivity.newIntent(context, tweetId))
            }

            holder.myTweetLayout.getChildAt(0).setOnClickListener(onClickShowTweetListener)

            holder.replyLayout!!.setOnClickListener(onClickShowTweetListener)

            holder.retweetLayout!!.setOnClickListener(object : View.OnClickListener {
                internal var client = TwitterCore.getInstance().apiClient
                internal var statusesService = client.statusesService

                override fun onClick(v: View) {
                    val builder = AlertDialog.Builder(activity)
                    val dialView = LayoutInflater.from(activity)
                            .inflate(R.layout.dial_retweet_choice, null)
                    builder.setView(dialView).create()
                    val retweetDial = builder.show()

                    val retweetButton = dialView.findViewById<Button>(R.id.retweet)
                    retweetButton.setOnClickListener {
                        val retweetCall = statusesService.retweet(tweetId, false)
                        retweetCall.enqueue(object : Callback<Tweet>() {
                            override fun success(result: Result<Tweet>) {

                            }

                            override fun failure(exception: TwitterException) {

                            }
                        })
                        retweetDial.dismiss()
                    }

                    // TODO: 7/24/2017 append the url for quoting
                    val retweetQuoteButton = dialView.findViewById<View>(R.id.retweet_quote) as Button
                    retweetQuoteButton.setOnClickListener {
                        val retweetQuoteCall = statusesService.retweet(tweetId, true)
                        retweetQuoteCall.enqueue(object : Callback<Tweet>() {
                            override fun success(result: Result<Tweet>) {
                                val tweetUrl = "https://twitter.com/${tweet.inReplyToScreenName}/status/${tweet.idStr}"
                            }

                            override fun failure(exception: TwitterException) {

                            }
                        })
                    }
                }
            })

            holder.shareLayout.setOnClickListener {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND

                // TODO: 7/23/2017 encode
                val tweetUrl = "https://twitter.com/${tweet.inReplyToScreenName}/status/${tweet.idStr}"

                shareIntent.putExtra(Intent.EXTRA_TEXT, "sharing from BittyForTwitter: " + tweetUrl)
                shareIntent.type = "text/plain"
                startActivity(Intent.createChooser(shareIntent, resources.getText(R.string.app_name)))
                val mShareActionProvider = ShareActionProvider(context)
                mShareActionProvider.setShareIntent(shareIntent)
            }

            ColorToggle.showHeartColor(tweet.favorited, holder.favoImage, context)

            holder.favoLayout.setOnClickListener {
                ColorToggle.toggleHeartColor(tweetId, holder.favoImage, context)
                Log.i(TAG, "toggle color called")
            }
        }

        override fun getItemCount(): Int {
            return mTweets.size
        }

        inner class HomeHolder(inflater: LayoutInflater, parent: ViewGroup)
            : ViewHolder(inflater.inflate(R.layout.item, parent, false)), View.OnClickListener {

            /**
             *  duplicate Resource id statements, but works!
             */
            @BindView (R.id.my_tweet_layout)
            var myTweetLayout: LinearLayout = itemView.findViewById(R.id.my_tweet_layout)
            @BindView(R.id.favo)
            var favoImage: ImageView = itemView.findViewById(R.id.favo)
            @BindView(R.id.reply_layout)
            var replyLayout: LinearLayout = itemView.findViewById(R.id.reply_layout)
            @BindView(R.id.retweet_layout)
            var retweetLayout: LinearLayout = itemView.findViewById(R.id.retweet_layout)
            @BindView(R.id.favo_layout)
            var favoLayout: LinearLayout = itemView.findViewById(R.id.favo_layout)
            @BindView(R.id.share_layout)
            var shareLayout: LinearLayout = itemView.findViewById(R.id.share_layout)
            @BindView(R.id.notesNum)
            var notesNum: TextView = itemView.findViewById(R.id.notesNum)
            @BindView(R.id.notesText)
            var notesTextView: TextView = itemView.findViewById(R.id.notesText)

            init {
                ButterKnife.bind(this, itemView)
                itemView.setOnClickListener(this)

            }

            override fun onClick(view: View) {
                Toast.makeText(context, "my item clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }


    /**
     * when pull refresh, perform this task to get new tweets
     */
    private inner class RefreshTask : AsyncTask<Void, Void, List<Tweet>>() {
        override fun doInBackground(vararg voids: Void): List<Tweet>? {
            Log.i(TAG, "RefreshTask doInBackground called")

            return fetchList()
        }

        override fun onPostExecute(tweets: List<Tweet>) {
            super.onPostExecute(tweets)
            Log.i(TAG, "RefreshTask onPostExecute called")
            mProgressBar!!.visibility = View.GONE
            cancel(false)
        }
    }

    /* rx */

    fun getObservable(): Observable<List<Tweet>> {
        return Observable.defer(Callable<ObservableSource<List<Tweet>>> {
            try {
                return@Callable Observable.just(fetchList())
            } catch (e: Exception) {
                Log.e(TAG, e.message)
                return@Callable null
            }
        })
    }

    private fun HomeTimelineFragment.fetchList(): MutableList<Tweet> {
        val TAG_REQUEST_TWEETS = " hometimeline call"
        val client = TwitterCore.getInstance().apiClient
        val statusesService = client.statusesService
        val call = statusesService.homeTimeline(
                null,
                null,
                null, // last call's sinceid = current call's max(least recent) id
                false,
                false,
                true,
                true)
        call.enqueue(object : Callback<List<Tweet>>() {
            override fun success(result: Result<List<Tweet>>) {
                Log.i(TAG + TAG_REQUEST_TWEETS,
                        "RefreshTask call success result.size = " + result.data.size)

                if (result.data.isEmpty()) {
                    Toast.makeText(context, "no new tweets o_O", Toast.LENGTH_LONG).show()
                } else {
                    mTweetsUpdate = result.data.toMutableList()
                    mTweets.addAll(0, mTweetsUpdate)

//                    if (false) {
//                        mTweets = result.data.toMutableList()
//                    } else {
//                        mTweetsUpdate = result.data.toMutableList()
//                        mTweets!!.addAll(0, mTweetsUpdate) // insert from the beginning
//                    }
                    //UpdateUITask().execute()
                    updateRecyclerUI()
                    setMostRecentId()
                    //setLeastRecentId()
                }
            }

            override fun failure(exception: TwitterException) {
                Toast.makeText(context,
                        "Networking wrong", Toast.LENGTH_LONG).show()
                Log.i(TAG + TAG_REQUEST_TWEETS,
                        exception.message)
            }
        })

        return mTweets
    }

    private fun setMostRecentId() {
        mostRecentId = mTweetsUpdate[0].id
    }

    private fun setLeastRecentId() {
        //leastRecentId = mTweetsUpdate[.size - 1].id
    }

    /**
     * Call this asynctask to get previous home timeline tweets
     */
//    private inner class PullPrevTask : AsyncTask<Void, Void, List<Tweet>>() {
//        override fun doInBackground(vararg voids: Void): List<Tweet>? {
//
//            val client = TwitterCore.getInstance().apiClient
//            val statusesService = client.statusesService
//            val call = statusesService.homeTimeline(null, null,
//                    if (leastRecentId == 0) null else leastRecentId - 1,
//                    false, true, true, true)
//            call.enqueue(object : Callback<List<Tweet>>() {
//                override fun success(result: Result<List<Tweet>>) {
//
//                    if (result.data.size == 0) {
//                        Toast.makeText(context, "no new tweets o_O", Toast.LENGTH_LONG).show()
//                    } else {
//                        if (mTweets == null) {
//                            mTweets = result.data
//                        } else {
//                            mTweetsUpdate = result.data
//                            mTweets!!.addAll(mTweetsUpdate) // insert starting from the end of the list
//                        }
//                        UpdateUITask().execute()
//                        setMostRecentId()
//                        setLeastRecentId()
//                    }
//                }
//
//                override fun failure(exception: TwitterException) {
//                    Toast.makeText(context, "Tweets arriving in 15 min", Toast.LENGTH_LONG).show()
//                    Log.i(TAG, "call for hometimeline failed --> " + exception.message)
//                    cancel(false)
//                }
//            })
//
//            return mTweets
//        }
//
//        override fun onPostExecute(tweets: List<Tweet>) {
//            super.onPostExecute(tweets)
//            Log.i(TAG, "PullPrevTask onPostExecute called")
//            try {
//                Thread.sleep(2000)
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }
//
//            cancel(true)
//        }
//    }
//
//    class UpdateUITask : AsyncTask<Void, Void, Void>() {
//        internal var isNewAdapter = true
//        override fun doInBackground(vararg voids: Void): Void? {
//            if (mAdapter == null && mTweets != null) {
//                mAdapter = HomeAdapter(mTweets!!.toList())
//            } else {
//                isNewAdapter = false
//            }
//            return null
//        }
//
//        override fun onPostExecute(aVoid: Void) {
//            super.onPostExecute(aVoid)
//            if (isNewAdapter) {
//                mRecyclerViewHome!!.adapter = mAdapter
//            } else {
//                mAdapter!!.notifyItemRangeChanged(0, mTweets!!.size)
//            }
//            cancel(false)
//        }
//    }

    fun updateRecyclerUI() {

        if (mTweets.isEmpty()) return
        if (mAdapter == null) {
            mAdapter = HomeAdapter(mTweets)
            mRecyclerViewHome.adapter = mAdapter
        } else {
            mAdapter!!.notifyItemRangeChanged(0, mTweets.size)
        }

        mProgressBar?.visibility = View.GONE
    }

//    private fun setMostRecentId() {
//        mostRecentId = mTweets!![0].id
//    }
//
//    private fun setLeastRecentId() {
//        leastRecentId = mTweets!![mTweets!!.size - 1].id
//    }

    companion object {
        private val TAG = HomeTimelineFragment::class.java.simpleName
        private val ARG_PROFILE_IMG_URL = "arg_profile_img_url"

        fun newInstance(): HomeTimelineFragment {

            val args = Bundle()
            //args.putString(ARG_PROFILE_IMG_URL, );

            val fragment = HomeTimelineFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
