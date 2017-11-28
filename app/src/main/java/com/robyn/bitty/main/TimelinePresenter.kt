package com.robyn.bitty.main

import android.util.Log
import com.robyn.bitty.data.RemoteDataSource
import com.robyn.bitty.myLog
import io.reactivex.Observable
import io.reactivex.ObservableSource
import java.util.concurrent.Callable

/**
 * Created by yifei on 11/27/2017.
 */
class TimelinePresenter(dataSource:RemoteDataSource):TimelineContract.Presenter {

    override fun start() {
    }

    private fun isVertified(): Observable<Boolean> {
        return Observable.defer(Callable<ObservableSource<Boolean>> {
            try {
                return@Callable Observable.just(checkAuth())
            } catch (e: Exception) {
                myLog(this.toString(), e.toString())
                return@Callable null
            }
        })
    }




}