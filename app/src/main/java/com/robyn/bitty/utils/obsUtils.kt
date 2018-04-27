package com.robyn.bitty.utils

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Observable<T>.schedule(): Observable<T> {

    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}