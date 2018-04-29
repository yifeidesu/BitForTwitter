package com.robyn.bitty.data

import android.content.Context
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestOptions
import com.robyn.bitty.utils.myLog
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.models.Search
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.models.User
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import java.util.concurrent.Callable

/**
 * This class provides wrapper methods of the twitter service methods.
 * Subscribe the return value to receive data.
 */

class DataSource {

    private fun getTweetCall(id: Long): Call<Tweet> {
        val service = client.statusesService
        return service.show(id, null, null, null)
    }

    fun getTweetSingle(id: Long): Single<Tweet> {
        return Single.defer(Callable<SingleSource<Tweet?>> {
            try {
                return@Callable Single.just(getTweetCall(id).execute().body())
            } catch (e: Exception) {
                Log.e(TAG, e.message)
                return@Callable null
            }
        })
    }

    fun homeSingle(sinceId: Long? = null, maxId: Long? = null): Single<List<Tweet>?> {

        return Single.defer(Callable<SingleSource<List<Tweet>?>> {

            try {
                return@Callable Single.just(
                    homeCall(maxId, sinceId).execute().body()
                )
            } catch (e: Exception) {
                Log.e(TAG, e.message)
                return@Callable null
            }
        })
    }

     fun homeCall(
        sinceId: Long? = null,
        maxId: Long? = null
    ): Call<MutableList<Tweet>> {

        val statusesService = client.statusesService

        return statusesService.homeTimeline( // http get method
                null,
                sinceId, // to Fetch new, current maxId = this.minId
                maxId,
                null,
                false,
                true,
                true
            )
    }

     fun searchCall(
        q: String = "cat",
        endId: Long? = null,
        fetchNew: Boolean = true,
        lang: String = "en"
    ): Call<Search?> {

        val searchService = client.searchService

        val call = searchService.tweets( // http get method
            q,
            null, // to Fetch new, current maxId = this.minId
            lang,
            null,
            "popular",
            null,
            null,
            null,
            null,
            null
        )

        return call
    }

    fun searchSingle(q: String): Single<Search?> {
        return Single.defer(Callable<SingleSource<Search?>> {
            try {
                return@Callable Single.just(
                    searchCall(q).execute().body()
                )
            } catch (e: Exception) {
                Log.e(TAG, e.message)
                return@Callable null
            }
        })
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
        val timeCharArray = timeString.split(" ")
        if (timeCharArray.size < 5) return ""
        val year = timeCharArray[5]
        val month = timeCharArray[1]
        val day = timeCharArray[2]
        val date = " • $month $day, $year "

        return date
    }

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

    fun loadProfileImage(
        context: Context, // view
        stringUrl: String, // datasource
        imageView: ImageView, //view
        compressionQuality: Int // static
    ) {
        Glide.with(context).load(stringUrl)
            .apply(
                RequestOptions()
                    .encodeQuality(compressionQuality)
                    .circleCrop()
            )
            .into(imageView)
    }

    fun loadOriginalProfileImage(
        context: Context,
        imageUrlString: String,
        imageView: ImageView,
        compressionQuality: Int
    ) {
        try {
            Glide.with(context).load(originalProfileImageUrl(imageUrlString))
                .apply(
                    RequestOptions()
                        .encodeQuality(compressionQuality)
                        .circleCrop()
                )
                .into(imageView)
        } catch (ge: GlideException) {
            Log.e(TAG, ge.message)
            Glide.with(context).load(biggerProfileImageUrl(imageUrlString))
                .apply(
                    RequestOptions()
                        .encodeQuality(compressionQuality)
                        .circleCrop()
                )
                .into(imageView)
        }
    }

    private fun verifyCredentialsCall(): Call<User?> {
        val call = client
            .accountService
            .verifyCredentials(false, true, false)
        return call
    }

    /**
     * This method returns to an observable that can notify if the user is verified by emitting a bool
     * defer() returns an observable which will start emitting data when it gets subscribed.
     * in this case, the emission is a bool, indicates if the user is verified.
     *
     */
    fun verifyCredentialsObs(): Observable<User?> {

        val callable = Callable<ObservableSource<User?>> {
            try {
                return@Callable Observable.just(verifyCredentialsCall().execute().body())
            } catch (e: Exception) {
                myLog(this.toString(), e.toString())
                return@Callable null
            }
        }

        return Observable.defer(callable)
    }

    private fun favoriteCall(id: Long): Call<Tweet> {
        return client.favoriteService.create(id, null)
    }

    private fun destroyFavoriteCall(id: Long): Call<Tweet> {
        return client.favoriteService.destroy(id, null)
    }

    fun favoriteSingle(id: Long): Single<Tweet?> {
        return tweetSingle(favoriteCall(id))
    }

    fun unFavoriteSingle(id: Long): Single<Tweet?> {
        return tweetSingle(destroyFavoriteCall(id))
    }

    private fun tweetSingle(call: Call<Tweet>): Single<Tweet?> {
        val callable = Callable<SingleSource<Tweet?>> {
            try {
                return@Callable Single.just(call.execute().body())
            } catch (e: Exception) {
                Log.e(TAG, e.message)
                return@Callable null
            }
        }
        return Single.defer(callable)
    }

    fun <T> Single<T>.schedule(): Single<T> {
        return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    companion object {

        const val TAG = "DataSource"

        val INSTANCE = DataSource()

        private val client = TwitterCore.getInstance().apiClient
    }
}











