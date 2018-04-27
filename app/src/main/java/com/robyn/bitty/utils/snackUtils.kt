package com.robyn.bitty.utils

import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_SHORT
import android.view.View

fun makeSnackbar(view: View, resInt: Int, duration: Int = LENGTH_SHORT) {
    Snackbar.make(view, resInt, duration)
}

fun makeSnackbar(view: View, msg: String, duration: Int = LENGTH_SHORT) {
    Snackbar.make(view, msg, duration)
}