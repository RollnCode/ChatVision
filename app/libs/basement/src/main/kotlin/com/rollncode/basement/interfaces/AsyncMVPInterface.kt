package com.rollncode.basement.interfaces

import android.support.annotation.*
import io.reactivex.*

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.09.01
 */
interface AsyncMVPInterface : MVPInterface {

    @AnyThread
    fun setProgress(start: Boolean = true)

    @MainThread
    fun onRequestError(error: Throwable)

    @MainThread
    fun getRetryFlowAction(): Flowable<Boolean>
}