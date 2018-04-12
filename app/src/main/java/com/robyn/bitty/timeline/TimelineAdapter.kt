package com.robyn.bitty.timeline

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.robyn.bitty.R
import com.robyn.bitty.databinding.ItemBinding
import com.robyn.bitty.utils.makeSnackbar
import com.twitter.sdk.android.core.models.Tweet

/**
 * the mTweets passed in here already updated
 */
class TimelineAdapter(var mTweets: MutableList<Tweet>) :
    RecyclerView.Adapter<TimelineAdapter.TweetHolder>() {

    var mAdapter: TimelineAdapter? = null

    init {

        //mTweets.addAll(0, mTweets)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.item, parent, false)
        val holder = TweetHolder(binding)
        return holder
    }

    override fun onBindViewHolder(holder: TweetHolder, position: Int) {

        val tweet = mTweets[position]
        holder.bind(tweet)

//        fun bindTweet(tweet: Tweet) {
//
//            with(holderItemView) {
//                DataSource.INSTANCE.loadProfileImage(
//                    context, DataSource.INSTANCE.biggerProfileImageUrl(tweet.user.profileImageUrl),
//                    user_profile_image, 0
//                )
//                user_name.text = tweet.user.userName
//                user_screen_name.text = tweet.user.atScreenName()
//                post_time.text = DataSource.INSTANCE.createdAtTime(tweet.createdAt)
//                //tweet_text.text = tweet.text // how to link mask
//            }
//
//        }
//
//        // comprisete
//        fun bindRetweet(tweet: Tweet) {
//            val retweet = tweet.retweetedStatus
//
//            val userRetweeted = "${tweet.user.userName} retweeted"
//
//            holderItemView.retweet_by.visibility = View.VISIBLE
//
//            DataSource.INSTANCE.loadProfileImage(
//                context, DataSource.INSTANCE.biggerProfileImageUrl(retweet.user.profileImageUrl),
//                holderItemView.user_profile_image, 0
//            )
//
//            //dataSource.loadProfileImage(context, )
//
//            retweet.user.loadProfileImage(
//                context, holderItemView.user_profile_image
//            )
//
//            holderItemView.retweet_by.text = userRetweeted
//            holderItemView.user_name.text = retweet.user.userName
//            holderItemView.user_screen_name.text = retweet.user.atScreenName()
//            holderItemView.post_time.text = DataSource.INSTANCE.createdAtTime(retweet.createdAt)
//            holderItemView.tweet_text.text = retweet.text // how to link mask
//
//            playVideo(tweet, context, holderItemView.player_view)
//
//            //holderItemView.player_view.setPlayer(getPlayer(mContext))
//        }
//
//        fun bindQuote(tweet: Tweet) {
//            if (tweet.quotedStatus == null) return
//
//            val quote = tweet.quotedStatus
//            val retweet = tweet.retweetedStatus
//
//            holderItemView.quote_layout.visibility = View.VISIBLE
//
////            DataSource.INSTANCE.loadProfileImage(
////                mContext, DataSource.INSTANCE.biggerProfileImageUrl(tweet.user.profileImageUrl),
////                holderItemView.user_profile_image, 0
////            )
//
//            tweet.user.loadProfileImage(
//                holderItemView.context, holderItemView.user_profile_image
//            )
//
//            holderItemView.user_name.text = tweet.user.userName
//            holderItemView.user_screen_name.text = tweet.user.atScreenName()
//            holderItemView.post_time.text = DataSource.INSTANCE.createdAtTime(tweet.createdAt)
//            holderItemView.tweet_text.text = tweet.text // how to link mask
//
//            holderItemView.user_name_quote.text = quote.user.userName
//            holderItemView.user_screen_name_quote.text =
//                    quote.user.atScreenName()
//            holderItemView.tweet_text_quote.text = quote.text
//        }

        /**
         *  holder.itemView = tweet layout + tweet actions layout
         *
         *  tweet layout listeners and data binding:
         */

//        val onClickListenerItem = View.OnClickListener {
//
//            val intent = SoloActivity.newIntent(context, tweetId)
//            (context as? AppCompatActivity)?.startActivityForResult(
//                intent,
//                TIMELINE_ADAPTER_REQUEST_CODE
//            )
//            // todo on ac result, update this viewed tweet item, bind view/data again.
//        }
//
//        holderItemView.tweet_layout_in_list.setOnClickListener(onClickListenerItem)
//        holderItemView.my_item_layout.reply_layout.setOnClickListener(onClickListenerItem)

        // bind data

        /**
         * different layout design for
         * tweet / retweet / quote / reply to
         */
//        when {
//            tweet.quotedStatus != null -> bindQuote(tweet)
//            tweet.retweetedStatus != null -> bindRetweet(tweet)
//            else -> bindTweet(tweet)
//        }
//
//
//        holderItemView.retweet_layout.setOnClickListener(object : View.OnClickListener {
//            internal var client = TwitterCore.getInstance().apiClient
//            internal var statusesService = client.statusesService
//            override fun onClick(v: View) {
//                val builder = AlertDialog.Builder(context)
//                val dialView = LayoutInflater.from(context)
//                    .inflate(R.layout.dial_retweet_choice, null)
//                builder.setView(dialView).create()
//                val retweetDial = builder.show()
//
//                val retweetButton = dialView.findViewById<Button>(R.id.retweet)
//                retweetButton.setOnClickListener {
//                    val retweetCall = statusesService.retweet(tweetId, false)
//                    retweetCall.enqueue(object : Callback<Tweet>() {
//                        override fun success(result: Result<Tweet>) {
//                        }
//
//                        override fun failure(exception: TwitterException) {
//                        }
//                    })
//                    retweetDial.dismiss()
//                }
//
//                // TODO: 7/24/2017 append the url for quoting
//                val retweetQuoteButton = dialView.findViewById<View>(R.id.retweet_quote) as Button
//                retweetQuoteButton.setOnClickListener {
//                    val retweetQuoteCall = statusesService.retweet(tweetId, true)
//                    retweetQuoteCall.enqueue(object : Callback<Tweet>() {
//                        override fun success(result: Result<Tweet>) {
//                            val tweetUrl =
//                                "https://twitter.com/${tweet.inReplyToScreenName}/status/${tweet.idStr}"
//                        }
//
//                        override fun failure(exception: TwitterException) {
//                        }
//                    })
//                }
//            }
//        })
//
//        holderItemView.share_layout.setOnClickListener {
//            val shareIntent = Intent()
//            shareIntent.action = Intent.ACTION_SEND
//
//            // TODO: 7/23/2017 encode
//            val tweetUrl = "https://twitter.com/${tweet.inReplyToScreenName}/status/${tweet.idStr}"
//
//            shareIntent.putExtra(Intent.EXTRA_TEXT, "sharing from BittyForTwitter: " + tweetUrl)
//            shareIntent.type = "text/plain"
//            context.startActivity(
//                Intent.createChooser(
//                    shareIntent,
//                    context.resources.getText(R.string.app_name)
//                )
//            )
//            val mShareActionProvider = ShareActionProvider(context)
//            mShareActionProvider.setShareIntent(shareIntent)
//        }
//
//        favoAction(context, tweet, holderItemView.favo_image)
//
//        // bind data
//
//        fun displayNotes(tweet: Tweet) {
//            val notes = tweet.favoriteCount + tweet.retweetCount
//            if (notes != 0) {
//                holder.itemView.notesNum.text = notes.toString()
//                holder.itemView.notesText.visibility = View.VISIBLE
//            }
//        }
    }

    override fun getItemCount(): Int {
        return mTweets.size
    }

    inner class TweetHolder(var binding: ItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

//        private var mBinding: ItemInListBinding =
//            DataBindingUtil.inflate(inflater, R.layout.item, parent, false)

        fun bind(tweet: Tweet) {
            binding.tweet = tweet
            binding.executePendingBindings()
        }
    }

    /**
     * @param updateTweets the list to be added to the current list
     */
    fun updateRecyclerUI(updateTweets: List<Tweet>, recyclerView: RecyclerView) {

        if (updateTweets.isEmpty()) return
        mTweets.addAll(0, updateTweets)

        // todo rewrite
        if (mAdapter == null) {
            mAdapter = TimelineAdapter(mTweets)
            recyclerView.adapter = mAdapter
        } else {
            mAdapter?.notifyDataSetChanged()
        }

        //todo temp
        Log.d(TAG, "mtweets.size = ${mTweets.size}")
    }

    companion object {
        const val TAG = "TimelineAdapter"

        const val TIMELINE_ADAPTER_REQUEST_CODE = 0
    }
}