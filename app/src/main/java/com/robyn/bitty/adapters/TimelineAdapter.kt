package com.robyn.bitty.adapters

import android.content.Context
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.ShareActionProvider
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.robyn.bitty.R
import com.robyn.bitty.favoAction
import com.robyn.bitty.activities.TweetSoloActivity
import com.robyn.bitty.atScreenName
import com.robyn.bitty.biggerProfileImageUrl
import com.robyn.bitty.createdAtTime
import com.robyn.bitty.loadProfileImage
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_tweet_actions.view.*
import kotlinx.android.synthetic.main.item_in_list.view.*
import kotlinx.android.synthetic.main.quote_layout.view.*
import kotlinx.android.synthetic.main.tweet_layout_in_list.view.*

/**
 * Created by yifei on 9/19/2017.
 */

/**
 * the tweets passed in here already updated
 */
class TimelineAdapter(context: Context, tweets: MutableList<Tweet>) :
        RecyclerView.Adapter<TimelineAdapter.HomeHolder>() {

    var mAdapter: TimelineAdapter? = null

    val TAG = "TimelineAdapter"

    val mContext = context

    var composite: CompositeDisposable = CompositeDisposable()

    val mTweets:MutableList<Tweet> = ArrayList<Tweet>()

    // private val mTweetToAdapter: MutableList<Tweet> = mTweets
    // private val mOnClickListener: View.OnClickListener() {}

    init {
        mTweets.addAll(0, tweets)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHolder {
        val inflater = LayoutInflater.from(parent.context)
        return HomeHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: HomeHolder, position: Int) {

        // remove previous view if the holder is not empty,
        // otherwise holder shows multiple tweets in one single holder
//            if (holder.itemView.my_item_layout.childCount != 0) {
//                holder.itemView.my_item_layout.removeAllViews()
//            }

        val holderItemView = holder.itemView

        val tweet = mTweets[position] // position in the recyclerview, not the list?!
        val tweetId = tweet.getId()

        Log.i(TAG, tweetId.toString())

        fun bindTweet(tweet: Tweet) {

//                with (holderItemView) {
//                    loadProfileImage(context, biggerProfileImageUrl(tweet.user.profileImageUrl),
//                            holderItemView.user_profile_image, 0)
//                    holderItemView.user_name.text = tweet.user.name
//                    holderItemView.user_screen_name.text = atScreenName(tweet.user)
//                    holderItemView.post_time.text = createdAtTime(tweet.createdAt)
//                    holderItemView.tweet_text.text = tweet.text // how to link mask
//                }

            with (holderItemView) {
                loadProfileImage(context, biggerProfileImageUrl(tweet.user.profileImageUrl),
                        user_profile_image, 0)
                user_name.text = tweet.user.name
                user_screen_name.text = atScreenName(tweet.user)
                post_time.text = createdAtTime(tweet.createdAt)
                tweet_text.text = tweet.text // how to link mask
            }
        }

        fun bindRetweet(tweet: Tweet) {
            val retweet = tweet.retweetedStatus

            val userRetweeted = "${tweet.user.name} retweeted"

            holderItemView.retweet_by.visibility = View.VISIBLE

            loadProfileImage(mContext, biggerProfileImageUrl(retweet.user.profileImageUrl),
                    holderItemView.user_profile_image, 0)

            holderItemView.retweet_by.text = userRetweeted
            holderItemView.user_name.text = retweet.user.name
            holderItemView.user_screen_name.text = atScreenName(retweet.user)
            holderItemView.post_time.text = createdAtTime(retweet.createdAt)
            holderItemView.tweet_text.text = retweet.text // how to link mask

        }

        fun bindQuote(tweet: Tweet) {
            if (tweet.quotedStatus == null) return

            val quote = tweet.quotedStatus
            val retweet = tweet.retweetedStatus

            holderItemView.quote_layout.visibility = View.VISIBLE

            loadProfileImage(mContext, biggerProfileImageUrl(tweet.user.profileImageUrl),
                    holderItemView.user_profile_image, 0)
            holderItemView.user_name.text = tweet.user.name
            holderItemView.user_screen_name.text = atScreenName(tweet.user)
            holderItemView.post_time.text = createdAtTime(tweet.createdAt)
            holderItemView.tweet_text.text = tweet.text // how to link mask

            holderItemView.user_name_quote.text = quote.user.name
            holderItemView.user_screen_name_quote.text = atScreenName(quote.user)
            holderItemView.tweet_text_quote.text = quote.text

        }


        /**
         *  holder.itemView = tweet layout + tweet actions layout
         *
         *  tweet layout listeners and data binding:
         */

        val onClickListenerItem = View.OnClickListener {
            Toast.makeText(mContext, "show tweet listner called", Toast.LENGTH_SHORT).show()
            Log.i(TAG, "onClickShowTweetListener invoked")

            mContext.startActivity(TweetSoloActivity.newIntent(
                    mContext,
                    tweet.id,
                    tweet.favorited,
                    tweet.user.name,
                    tweet.user.screenName,
                    tweet.user.profileImageUrl,
                    tweet.text,
                    tweet.createdAt)) }

        holderItemView.tweet_layout_in_list.setOnClickListener(onClickListenerItem)
        holderItemView.my_item_layout.reply_layout.setOnClickListener(onClickListenerItem)

        // bind data

        /**
         * different layout design for
         * tweet / retweet / quote / reply to
         */
        when {
            tweet.quotedStatus != null -> bindQuote(tweet)
            tweet.retweetedStatus != null -> bindRetweet(tweet)
            else -> bindTweet(tweet)
        }

        /**
         * setup listeners for tweet actions
         */
        fun setupActionListeners() {

        }

        holderItemView.retweet_layout.setOnClickListener(object : View.OnClickListener {
            internal var client = TwitterCore.getInstance().apiClient
            internal var statusesService = client.statusesService

            override fun onClick(v: View) {
                val builder = AlertDialog.Builder(mContext)
                val dialView = LayoutInflater.from(mContext)
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

        holderItemView.share_layout.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND

            // TODO: 7/23/2017 encode
            val tweetUrl = "https://twitter.com/${tweet.inReplyToScreenName}/status/${tweet.idStr}"

            shareIntent.putExtra(Intent.EXTRA_TEXT, "sharing from BittyForTwitter: " + tweetUrl)
            shareIntent.type = "text/plain"
            mContext.startActivity(Intent.createChooser(shareIntent,
                    mContext.resources.getText(R.string.app_name)))
            val mShareActionProvider = ShareActionProvider(mContext)
            mShareActionProvider.setShareIntent(shareIntent)
        }



        favoAction(mContext, tweet, holderItemView.favo_image)

        // bind data



        fun displayNotes(tweet: Tweet) {
            val notes = tweet.favoriteCount + tweet.retweetCount
            if (notes != 0) {
                holder.itemView.notesNum.text = notes.toString()
                holder.itemView.notesText.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return mTweets.size
    }

    inner class HomeHolder(inflater: LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_in_list, parent, false)) {

        init {

            itemView.setOnClickListener(View.OnClickListener {
                // how to get tweet id before data binding?
            })
        }


    }

    /**
     * @param updateTweets the list to be added to the current list
     */
    fun updateRecyclerUI(updateTweets:List<Tweet>, homeTimeline: RecyclerView) {
        val TAG = "updateRecyclerUI"
        Log.i(TAG, "updateTweets.size = ${updateTweets.size}")

        if (updateTweets.isEmpty()) return
        mTweets.addAll(0,updateTweets)

        if (mAdapter == null) {
            //mTweet = updateTweets.toMutableList()
            mAdapter = TimelineAdapter(mContext, mTweets)
            homeTimeline.adapter = mAdapter
            Log.i(TAG, "madapter created")
        } else {
            //mTweets.addAll(0, updateTweets)
            mAdapter!!.notifyDataSetChanged()
            Log.i(TAG, "madapter.notifyitemrangeinserted(), madapter.itemcount = ${mAdapter!!.itemCount}")
        }
    }


        /**
     *
     *
     *  current maxId = new list's sinceId
     */
//    fun fetchTweets(endId:Long? = null, fetchNew: Boolean = true) {
//        var mTweetsUpdate: MutableList<Tweet> = ArrayList() // to pass the result from onnext to oncomplete
//
//        val disposable = fetchNewObservable(endId, fetchNew)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread()) // receive on main thread
//                .subscribe(
//                        { result ->
//                            Log.i(TAG, "RESPONSE SIZE =  ${result.size}")
//                            mTweetsUpdate = result.toMutableList()
//
//                            Toast.makeText(mContext, "${result.size} new tweets", Toast.LENGTH_SHORT).show()
//                        },
//                        { err -> Log.e(com.robyn.bitty.ui.getTAG, err.message) },
//                        {
//                            myLog(TAG, "observable.just")
//
//                            if (mTweetsUpdate.size < 1) {
//                                return@subscribe
//                            } else {
//                                updateRecyclerUI(mTweetsUpdate)
//
//                                if (fetchNew) {
//                                    setMaxid(mTweetsUpdate[0].id)
//                                } else {
//                                    setMinid(mTweetsUpdate[mTweetsUpdate.size - 1].id)
//                                }
//                            }
//                        })
//        composite.add(disposable)
//    }




}