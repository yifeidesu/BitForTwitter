package com.robyn.bitty.utils

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestOptions
import com.robyn.bitty.main.TimelineAdapter
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.models.User
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

import java.util.concurrent.Callable


/**
 * todo move to remote data source class
 *
 * functions for timelines
 *
 *
 * Created by yifei on 9/14/2017.
 */
val TAG = "TweetFun"

private var mMaxId: Long? = null
private var mMinId: Long? = null

var disposable: CompositeDisposable = CompositeDisposable()

object Fetch {

    var mTweet:MutableList<Tweet> = ArrayList<Tweet>()
    /**
     * get the observable to be used for Disposable
     *
     * returns an observable, that is a holder of a list of tweets
     *
     *
     *
     *
     *
     *
     */
    fun fetchNewObservable(sinceId: Long? = null, fetchNew: Boolean = true): Observable<List<Tweet>> {

        return Observable.defer(Callable<ObservableSource<List<Tweet>>> {
            try {
                return@Callable Observable.just(fetchList(sinceId, fetchNew))
            } catch (e: Exception) {
                Log.e(TAG, e.message)
                return@Callable null
            }
        })
    }

    fun fetchTweets(context: Context,
                    endId:Long? = null,
                    fetchNew: Boolean = true,
                    recyclerView:RecyclerView,
                    progressBar: ProgressBar) {
        myLog(TAG, "fun fetchTweet is called")
        var mTweetsUpdate: MutableList<Tweet> = ArrayList() // to pass the result from onnext to oncomplete

        val mAdapter = TimelineAdapter(context, mTweetsUpdate)
        val disposable = fetchNewObservable(endId, fetchNew)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()) // receive on main thread
                .subscribe(
                        { result ->
                            Log.i(TAG, "RESPONSE SIZE =  ${result.size}")
                            mTweetsUpdate = result.toMutableList()

                            //Toast.makeText(context, "${result.size} new tweets", Toast.LENGTH_SHORT).show()
                        },
                        { err -> Log.e(TAG, err.message) },
                        {
                            myLog(TAG, "observable.just")

                            if (mTweetsUpdate.size < 1) {

                                myLog(TAG, "size = 0")

                                return@subscribe
                            } else {
                                myLog(TAG, "size = ${mTweetsUpdate.size}")
                                if (mAdapter == null) return@subscribe
                                mAdapter.updateRecyclerUI(mTweetsUpdate, recyclerView)
                                Log.d(TAG, "dapter.updateRecyclerUI called, mtweetsupdate = ${mTweetsUpdate.size}")

                                if (fetchNew) {
                                    setMaxid(mTweetsUpdate[0].id)
                                } else {
                                    setMinid(mTweetsUpdate[mTweetsUpdate.size - 1].id)
                                }

                                progressBar.visibility = View.GONE
                            }

                        })
        com.robyn.bitty.utils.disposable.add(disposable)
    }

    /**
     * Make an http call, hometimeline(),  with twitter sdk status service,
     *
     * returns a list
     *
     *
     * Fetch a tweet list to update.
     * To be used w/ twitter api timeline methods, e.g,
     * Fetch new tweets; pull previous tweets; search; trends.
     */
    fun fetchList(endId:Long? = null, fetchNew: Boolean = true ):List<Tweet>? {
        val client = TwitterCore.getInstance().apiClient
        val statusesService = client.statusesService

        val call = if (fetchNew) {
            statusesService.homeTimeline( // http get method
                    null,
                    endId, // to Fetch new, current maxId = this.minId
                    null,
                    null,
                    false,
                    true,
                    true)

        } else {
            statusesService.homeTimeline(
                    null,
                    null,
                    endId,
                    null,
                    false,
                    true,
                    true)
        }
        return call.execute().body()
    }


    /**
     *
     */
    fun setMaxid(maxId: Long?) {
        mMaxId = maxId
        Log.i(TAG, "maxId = ${mMaxId}")
    }  // most recent
    fun setMinid(minId: Long?){
        mMinId = minId
        Log.i(TAG, "minId = ${mMinId}")
    }
}






//        mTweets.addAll(0, updateTweets)
//        mAdapter.notifyItemRangeInserted(0, updateTweets.size)


    //progress_bar?.visibility = View.GONE
//}





fun linkToId(url:String) : String {
    val arr = url.split("/")
    val size = arr.size
    return arr[size - 1]

}

/**
 *  build a joda time w/
 *  year mon day hour min
 *  if period <　24 hours
 *
 *  show 1, xxx min ago ; 2. xx hours ago
 *
 */
fun createdAtTime(timeString: String): String {
    Log.i(TAG, timeString)
    val arr = timeString.split(" ")
    Log.i(TAG, "arr = ${arr.toString()} size = ${arr.size}")
    if (arr.size < 5) return ""
    val year = arr[5]
    val month = arr[1]
    val day = arr[2]
    val date = " • $month $day, $year "

    return date
}

/**
 * Add prefix "@" for either a user: User or a name: String
 */
fun atScreenName(user: User) = "@${user.screenName}"

fun atScreenName(screenName: String) = "@$screenName"


/**
 * Loading images funs: profile image, entity image
 */

/**
 * url for higher resolution
 */
fun biggerProfileImageUrl(urlNormal: String): String = urlNormal
        .replace("normal.", "bigger.")

fun originalProfileImageUrl(urlNormal: String): String {
    val url = urlNormal
            .replace("_normal.", ".")
    //Log.d(TAG, url)
    return url
}

/**
 * As toolbar navigation icon
 */
fun loadProfileImage(context: Context, user: User, imageView: ImageView, sizeMultiplier: Float = 1f) {
    Log.i(TAG, "loadProfileImage url = ${user.profileImageUrl}")
    Glide.with(context).load(user.profileImageUrl)
            .apply(RequestOptions()
                    .encodeQuality(0) // 0 = no compression
                    .sizeMultiplier(sizeMultiplier)
                    .circleCrop())
            .into(imageView)
}

fun loadProfileImage(context: Context, stringUrl: String, imageView: ImageView, compressionQuality: Int) {
    Log.i(TAG, "loadProfileImage url = $stringUrl")
    Glide.with(context).load(stringUrl)
            .apply(RequestOptions()
                    .encodeQuality(compressionQuality)
                    .circleCrop())
            .into(imageView)
}

fun loadOriginalProfileImage(context: Context, user: User, imageView: ImageView, compressionQuality: Int) {
    Log.i(TAG,"loadOriginalProfileImage url = ${user.profileImageUrl}")
    try {
        Glide.with(context).load(originalProfileImageUrl(user.profileImageUrl))
                .apply(RequestOptions()
                        .encodeQuality(compressionQuality)
                        .circleCrop())
                .into(imageView)
    } catch (ge: GlideException) {
        Log.e(TAG, ge.message)
        Glide.with(context).load(biggerProfileImageUrl(user.profileImageUrl))
                .apply(RequestOptions()
                        .encodeQuality(compressionQuality)
                        .circleCrop())
                .into(imageView)
    }
}

fun loadOriginalProfileImage(context: Context, imageUrlString: String, imageView: ImageView, compressionQuality: Int) {
    Log.i(TAG, "loadOriginalProfileImage url = $imageUrlString")
    try {
        Glide.with(context).load(originalProfileImageUrl(imageUrlString))
                .apply(RequestOptions()
                        .encodeQuality(compressionQuality)
                        .circleCrop())
                .into(imageView)
    } catch (ge: GlideException) {
        Log.e(TAG, ge.message)
        Glide.with(context).load(biggerProfileImageUrl(imageUrlString))
                .apply(RequestOptions()
                        .encodeQuality(compressionQuality)
                        .circleCrop())
                .into(imageView)
    }
}

fun loadBannerImage(context: Context, user: User, imageView: ImageView) {
    Glide.with(context).load(user.profileBannerUrl)
            .into(imageView)
}


object DebugLog {
    val DEBUG = true
    fun log(message: String) {
        if (DEBUG) {
            val fullClassName = Thread.currentThread().stackTrace[2].className
            val className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
            val methodName = Thread.currentThread().stackTrace[2].methodName
            val lineNumber = Thread.currentThread().stackTrace[2].lineNumber
            Log.d("$className.$methodName():$lineNumber", message)
        }
    }
}

fun logWholeTrace(tag:String, msg: String?) {
    val fullClassName = Thread.currentThread().stackTrace[2].className
    val className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
    val methodName: String? = Thread.currentThread().stackTrace[2].methodName
    val lineNumber = Thread.currentThread().stackTrace[2].lineNumber
    Log.d(tag, "$className $methodName $lineNumber $msg")
    Thread.currentThread().stackTrace.forEach {
        Log.d(tag, it.toString())
    }
}

fun myLog(tag: String = "myLog", msg: String) {
    val fullClassName = Thread.currentThread().stackTrace[2].className
    val className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
    val methodName: String? = Thread.currentThread().stackTrace[2].methodName
    val lineNumber = Thread.currentThread().stackTrace[2].lineNumber
    val usage = Thread.currentThread().stackTrace[3]
    Log.d(tag, "fun $methodName() is in class $className at line $lineNumber\nusage = $usage\nmsg = $msg")
}






