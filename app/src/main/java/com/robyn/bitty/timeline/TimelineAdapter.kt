package com.robyn.bitty.timeline

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.robyn.bitty.R
import com.robyn.bitty.databinding.ItemBinding
import com.robyn.bitty.utils.tweetToShow
import com.twitter.sdk.android.core.models.Tweet

/**
 * the mTweets passed in here already updated
 */
class TimelineAdapter(var mTweets: MutableList<Tweet>) :
    RecyclerView.Adapter<TimelineAdapter.TweetHolder>() {

    var mAdapter: TimelineAdapter? = null

    init {
        notifyDataSetChanged()
    }

    fun setTweets(list:List<Tweet>) {
        mTweets = list as ArrayList
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

    }

    override fun getItemCount(): Int {
        return mTweets.size
    }

    inner class TweetHolder(var binding: ItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tweet: Tweet) {
            binding.tweet = tweet
            binding.executePendingBindings()
        }
    }

    /**
     * @param updatedTweets the list to be added to the current list
     */
    fun updateRecyclerUI(updatedTweets: List<Tweet>) {

        if (updatedTweets.isEmpty()) return

        mTweets = updatedTweets as ArrayList
        notifyDataSetChanged()

        // todo rewrite
//        if (mAdapter == null) {
//            mAdapter = TimelineAdapter(mTweets)
//            recyclerView.adapter = mAdapter
//        }

//        mAdapter?.setTweets(updatedTweets)
//        mAdapter?.notifyDataSetChanged()

        Log.d(TAG, "updateRecyclerUI new list size = ${mTweets.size}")
    }


    companion object {
        const val TAG = "TimelineAdapter"

        const val TIMELINE_ADAPTER_REQUEST_CODE = 0
    }
}