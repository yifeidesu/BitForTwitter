package com.robyn.bitty.utils

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

fun AppCompatActivity.replaceFragment(fragment: Fragment, containerViewId: Int, tag: String) {

    supportFragmentManager
        ?.beginTransaction()
        ?.replace(containerViewId, fragment, tag)
        ?.addToBackStack("timeline")
        ?.commit()
}

fun AppCompatActivity.replaceFragment(fragment: Fragment, containerViewId: Int) {

    supportFragmentManager
        ?.beginTransaction()
        ?.replace(containerViewId, fragment)
        ?.addToBackStack("timeline")
        ?.commit()
}