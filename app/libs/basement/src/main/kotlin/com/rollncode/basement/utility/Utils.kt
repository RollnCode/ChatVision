@file:Suppress("unused")

package com.rollncode.basement.utility

import android.os.HandlerThread
import android.os.Looper
import android.support.annotation.AnyThread
import android.support.annotation.MainThread
import android.support.annotation.StringRes
import android.support.annotation.WorkerThread
import android.support.design.widget.Snackbar
import android.view.View
import android.view.View.OnClickListener
import io.reactivex.BackpressureStrategy.LATEST
import io.reactivex.Flowable

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 * @since 2017.09.20
 */
class Utils {
    companion object
}

@AnyThread
fun Utils.Companion.newWorkerLooper(name: String, threadPriority: Int = Thread.MIN_PRIORITY): Looper
        = HandlerThread(name).apply { priority = threadPriority;start() }.looper

@MainThread
fun Utils.Companion.snackbarFlowAction(view: View?, @StringRes resId: Int, actionName: Int = android.R.string.ok): Flowable<Boolean> =
        Flowable.create({ subscriber ->
            if (view == null) {
                subscriber.onNext(false)

            } else {
                val snackBar = Snackbar.make(view, resId, Snackbar.LENGTH_INDEFINITE)
                snackBar.setAction(actionName) { subscriber.onNext(true) }
                snackBar.show()
            }
        }, LATEST)

@MainThread
fun Utils.Companion.setVisibility(v: View, visibility: Int) {
    if (v.visibility != visibility) {
        v.visibility = visibility
    }
}

@WorkerThread
fun Utils.Companion.threadSleep(millis: Long) {
    try {
        Thread.sleep(millis)

    } catch (e: InterruptedException) {
    }
}

@WorkerThread
fun Utils.Companion.waitUntilTimeout(start: Long, timeout: Long) {
    val difference = timeout - (System.currentTimeMillis() - start)
    if (difference > 0) {
        threadSleep(difference)
    }
}

@MainThread
fun Utils.Companion.changeVisibility(visibility: Int, vararg views: View) {
    for (view in views) {
        setVisibility(view, visibility)
    }
}

@AnyThread
fun Utils.Companion.fakeEmailOrPhone(email: Boolean = true): String
        = if (email) "zp12ser+${ARandom.getString()}@gmail.com" else "380670000000"

@MainThread
fun Utils.Companion.setOnClickListener(listener: OnClickListener?, vararg views: View) {
    for (v in views) {
        v.setOnClickListener(listener)
    }
}