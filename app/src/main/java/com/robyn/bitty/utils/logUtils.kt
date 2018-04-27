package com.robyn.bitty.utils

import android.util.Log

fun log(message: String) {
    val fullClassName = Thread.currentThread().stackTrace[2].className
    val className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
    val methodName = Thread.currentThread().stackTrace[2].methodName
    val lineNumber = Thread.currentThread().stackTrace[2].lineNumber
    Log.d("$className.$methodName():$lineNumber", message)
}

fun logWholeTrace(tag: String, msg: String?) {
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
    Log.d(
        tag,
        "fun $methodName() is in class $className at line $lineNumber\nusage = $usage\nmsg = $msg"
    )
}