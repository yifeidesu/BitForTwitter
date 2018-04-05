package com.robyn.bitty.timeline

import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.ShareActionProvider
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.robyn.bitty.R
import com.robyn.bitty.data.DataSource
import com.robyn.bitty.detail.TweetSoloActivity
import com.robyn.bitty.utils.tweetUtils.favoAction
import com.robyn.bitty.utils.tweetUtils.playVideo
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import kotlinx.android.synthetic.main.actions_fg.view.*
import kotlinx.android.synthetic.main.item_in_list.view.*
import kotlinx.android.synthetic.main.quote_layout.view.*
import kotlinx.android.synthetic.main.tweet_layout_in_list.view.*

/**
 * the mTweets passed in here already updated
 */
class TimelineAdapter(var mTweets: MutableList<Tweet>) :
    RecyclerView.Adapter<TimelineAdapter.HomeHolder>() { // todo context move to presenter

    var mAdapter: TimelineAdapter? = null

    //val mContext = context

    // mTweets = ArrayList<Tweet>()
    var mTweetsUpdate = ArrayList<Tweet>()

    init {
        mTweets.addAll(0, mTweets)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHolder {
        val inflater = LayoutInflater.from(parent.context)
        return HomeHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: HomeHolder, position: Int) {

        val holderItemView = holder.itemView

        val context = holderItemView.context

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

            with(holderItemView) {
                DataSource.INSTANCE.loadProfileImage(
                    context, DataSource.INSTANCE.biggerProfileImageUrl(tweet.user.profileImageUrl),
                    user_profile_image, 0
                )
                user_name.text = tweet.user.name
                user_screen_name.text = tweet.user.atScreenName()
                post_time.text = DataSource.INSTANCE.createdAtTime(tweet.createdAt)
                tweet_text.text = tweet.text // how to link mask
            }
        }

        // comprisete
        fun bindRetweet(tweet: Tweet) {
            val retweet = tweet.retweetedStatus

            val userRetweeted = "${tweet.user.name} retweeted"

            holderItemView.retweet_by.visibility = View.VISIBLE

            DataSource.INSTANCE.loadProfileImage(
                context, DataSource.INSTANCE.biggerProfileImageUrl(retweet.user.profileImageUrl),
                holderItemView.user_profile_image, 0
            )

            //dataSource.loadProfileImage(context, )

            retweet.user.setProfileImageToImageView(
                context, holderItemView.user_profile_image
            )

            holderItemView.retweet_by.text = userRetweeted
            holderItemView.user_name.text = retweet.user.name
            holderItemView.user_screen_name.text = retweet.user.atScreenName()
            holderItemView.post_time.text = DataSource.INSTANCE.createdAtTime(retweet.createdAt)
            holderItemView.tweet_text.text = retweet.text // how to link mask

            playVideo(tweet, context, holderItemView.player_view)

            //holderItemView.player_view.setPlayer(getPlayer(mContext))
        }

        fun bindQuote(tweet: Tweet) {
            if (tweet.quotedStatus == null) return

            val quote = tweet.quotedStatus
            val retweet = tweet.retweetedStatus

            holderItemView.quote_layout.visibility = View.VISIBLE

//            DataSource.INSTANCE.loadProfileImage(
//                mContext, DataSource.INSTANCE.biggerProfileImageUrl(tweet.user.profileImageUrl),
//                holderItemView.user_profile_image, 0
//            )

            tweet.user.setProfileImageToImageView(
                holderItemView.context, holderItemView.user_profile_image
            )

            holderItemView.user_name.text = tweet.user.name
            holderItemView.user_screen_name.text = tweet.user.atScreenName()
            holderItemView.post_time.text = DataSource.INSTANCE.createdAtTime(tweet.createdAt)
            holderItemView.tweet_text.text = tweet.text // how to link mask

            holderItemView.user_name_quote.text = quote.user.name
            holderItemView.user_screen_name_quote.text =
                    quote.user.atScreenName()
            holderItemView.tweet_text_quote.text = quote.text
        }

        /**
         *  holder.itemView = tweet layout + tweet actions layout
         *
         *  tweet layout listeners and data binding:
         */

        val onClickListenerItem = View.OnClickListener {

            context.startActivity(
                TweetSoloActivity.newIntent(
                    context,
                    tweet.id,
                    tweet.favorited,
                    tweet.user.name,
                    tweet.user.screenName,
                    tweet.user.profileImageUrl,
                    tweet.text,
                    tweet.createdAt
                )
            )
        }

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
                val builder = AlertDialog.Builder(context)
                val dialView = LayoutInflater.from(context)
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
                            val tweetUrl =
                                "https://twitter.com/${tweet.inReplyToScreenName}/status/${tweet.idStr}"
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
            context.startActivity(
                Intent.createChooser(
                    shareIntent,
                    context.resources.getText(R.string.app_name)
                )
            )
            val mShareActionProvider = ShareActionProvider(context)
            mShareActionProvider.setShareIntent(shareIntent)
        }



        favoAction(context, tweet, holderItemView.favo_image)

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

    inner class HomeHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_in_list, parent, false)) {

        init {

            itemView.setOnClickListener(View.OnClickListener {
                // how to get tweet id before data binding?
            })
        }
    }

    /**
     * @param updateTweets the list to be added to the current list
     */
    fun updateRecyclerUI(updateTweets: List<Tweet>, homeTimeline: RecyclerView) {

        if (updateTweets.isEmpty()) return
        mTweets.addAll(0, updateTweets)

        // todo rewrite
        if (mAdapter == null) {
            mAdapter = TimelineAdapter(mTweets)
            homeTimeline.adapter = mAdapter
        } else {
            mAdapter!!.notifyDataSetChanged()
        }
    }

    fun updateRecyclerUI(updateTweets: List<Tweet>) {

        if (updateTweets.isEmpty()) return
        mTweets.addAll(0, updateTweets)

        this.notifyDataSetChanged()
    }

    companion object {
        const val TAG = "TimelineAdapter"
    }

    /**
     *
     *
     *  current maxId = new list's sinceId
     */
//    fun fetchTweets(endId:Long? = null, fetchNew: Boolean = true) {
//        var mTweetsUpdate: MutableList<Tweet> = ArrayList() // to pass the result from onnext to oncomplete
//
//        val mCompositeDisposable = fetchNewObservable(endId, fetchNew)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread()) // receive on main thread
//                .subscribe(
//                        { result ->
//                            Log.i(TAG, "RESPONSE SIZE =  ${result.size}")
//                            mTweetsUpdate = result.toMutableList()
//
//                            Toast.makeText(context, "${result.size} new mTweets", Toast.LENGTH_SHORT).show()
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
//        mCompositeDisposable.add(mCompositeDisposable)
//    }

}