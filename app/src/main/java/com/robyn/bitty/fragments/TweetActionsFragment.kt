//package com.robyn.bitty.ui.timelines
//
//import android.content.Intent
//import android.os.Bundle
//import android.support.v4.app.Fragment
//import android.support.v7.widget.ShareActionProvider
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.LinearLayout
//import android.widget.Toast
//
//import com.robyn.bitty.ColorToggle
//import com.robyn.bitty.R
//import com.robyn.bitty.ui.ReplyActivity
//import com.twitter.sdk.android.core.Callback
//import com.twitter.sdk.android.core.Result
//import com.twitter.sdk.android.core.TwitterApiClient
//import com.twitter.sdk.android.core.TwitterCore
//import com.twitter.sdk.android.core.TwitterException
//import com.twitter.sdk.android.core.models.Tweet
//import com.twitter.sdk.android.core.services.StatusesService
//
//import butterknife.BindView
//import butterknife.ButterKnife
//import kotlinx.android.synthetic.main.fragment_tweet_actions.*
//import retrofit2.Call
//
///**
// * actions in ShowTweetActivity
// * Created by yifei on 7/31/2017.
// */
//
//class TweetActionsFragment : Fragment() {
//
//    private var mTweetId: Long = 0
//    private var mScreenName: String? = null
//
//    private val mTweet: Tweet? = null
//    private var mIsFavoed: Boolean = false
//
////    @BindView(R.id.reply_layout) internal var mReplyLayout: LinearLayout? = null
////    @BindView(R.id.retweet_layout) internal var mRetweetLayout: LinearLayout? = null
////    @BindView(R.id.favo_layout) internal var mFavoLayout: LinearLayout? = null
////    @BindView(R.id.share_layout) internal var mShareLayout: LinearLayout? = null
////    @BindView(R.id.favo) internal var mFavoImage: ImageView? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        mTweetId = arguments.getLong(ARGS_TWEET_ID)
//        mIsFavoed = arguments.getBoolean(ARGS_ISFAVOED)
//        mScreenName = arguments.getString(ARGS_SCREEN_NAME)
//
//    }
//
//
//    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val view = inflater!!.inflate(R.layout.fragment_tweet_actions, container, false)
//        ButterKnife.bind(this, view)
//
//        ColorToggle.showHeartColor(mIsFavoed, favo_image, context)
//
//        /**
//         * Four tweet action buttons in total
//         *
//         * 1/4 Reply
//         */
//        mReplyLayout!!.setOnClickListener {
//            startActivity(ReplyActivity.newIntent(context,
//                    mTweetId, mScreenName)) // mTweetId = the update's in reply to status id
//        }
//
//        /**
//         * 2/4 Retweet
//         */
//        mRetweetLayout!!.setOnClickListener(object : View.OnClickListener {
//            internal var client = TwitterCore.getInstance().apiClient
//            internal var statusesService = client.statusesService
//
//            override fun onClick(v: View) {
//                val retweetCall = statusesService.retweet(mTweetId, false)
//                retweetCall.enqueue(object : Callback<Tweet>() {
//                    override fun success(result: Result<Tweet>) {
//                        Toast.makeText(context,
//                                "Retweet successfully!", Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun failure(exception: TwitterException) {
//                        Toast.makeText(context,
//                                "Retweet failed!", Toast.LENGTH_SHORT).show()
//                    }
//                })
//
//                // TODO: 8/2/2017 next version: replace w/ the following dial: retweet + quote
//                //                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                //                final View dialView = LayoutInflater.from(getActivity())
//                //                        .inflate(R.layout.dial_retweet_choice, null);
//                //                builder.setView(dialView).create();
//                //                final AlertDialog retweetDial = builder.show();
//                //
//                //                Button retweetButton = dialView.findViewById(R.id.retweet);
//                //                retweetButton.setOnClickListener(new View.OnClickListener() {
//                //                    @Override
//                //                    public void onClick(View v) {
//                //                        Call<Tweet> retweetCall = statusesService.retweet(mTweetId, false);
//                //                        retweetCall.enqueue(new Callback<Tweet>() {
//                //                            @Override
//                //                            public void success(Result<Tweet> result) {
//                //                                Toast.makeText(getContext(),
//                //                                        "Retweet successfully!", Toast.LENGTH_SHORT).show();
//                //                            }
//                //
//                //                            @Override
//                //                            public void failure(TwitterException exception) {
//                //                                Toast.makeText(getContext(),
//                //                                        "Retweet failed!", Toast.LENGTH_SHORT).show();
//                //                            }
//                //                        });
//                //                        retweetDial.dismiss();
//                //                    }
//                //                });
//                //
//                //                final Button quoteButton = (Button) dialView.findViewById(R.id.retweet_quote);
//                //                quoteButton.setOnClickListener(new View.OnClickListener() {
//                //                    @Override
//                //                    public void onClick(View v) {
//                //                        Call<Tweet> retweetQuoteCall = statusesService.(mTweetId,true);
//                //
//                //                        retweetQuoteCall.enqueue(new Callback<Tweet>() {
//                //                            @Override
//                //                            public void success(Result<Tweet> result) {
//                //                                String tweetUrl = "https://twitter.com/"
//                //                                        + mScreenName + "/status/" + String.valueOf(mTweetId);
//                //                            }
//                //
//                //                            @Override
//                //                            public void failure(TwitterException exception) {
//                //
//                //                            }
//                //                        });
//                //
//                //                    }
//                //                });
//            }
//        })
//
//        /**
//         * 3/4 Favo
//         */
//
//        mFavoLayout!!.setOnClickListener {
//            ColorToggle.toggleHeartColor(
//                    mTweetId,
//                    mFavoImage!!, context)
//        }
//
//        /**
//         * 4/4 Share
//         */
//        mShareLayout!!.setOnClickListener {
//            val shareIntent = Intent()
//            shareIntent.action = Intent.ACTION_SEND
//
//            // TODO: 7/23/2017 encode
//            val tweetUrl = "https://twitter.com/$mScreenName/status/$mTweetId"
//
//            shareIntent.putExtra(Intent.EXTRA_TEXT, "sharing from BittyForTwitter: " + tweetUrl)
//            shareIntent.type = "text/plain"
//            startActivity(Intent.createChooser(shareIntent, resources.getText(R.string.app_name)))
//            val mShareActionProvider = ShareActionProvider(context)
//            mShareActionProvider.setShareIntent(shareIntent)
//        }
//
//        return view
//    }
//
//    companion object {
//        private val ARGS_TWEET_ID = "args_tweet_id"
//        private val ARGS_SCREEN_NAME = "args_screen_name"
//        private val ARGS_ISFAVOED = "args_isfavoed"
//
//        fun newInstance(
//                tweetId: Long, isFavoed: Boolean, screenName: String): TweetActionsFragment {
//
//            val args = Bundle()
//            args.putLong(ARGS_TWEET_ID, tweetId)
//            args.putBoolean(ARGS_ISFAVOED, isFavoed)
//            args.putString(ARGS_SCREEN_NAME, screenName)
//
//            val fragment = TweetActionsFragment()
//            fragment.arguments = args
//            return fragment
//        }
//    }
//}
