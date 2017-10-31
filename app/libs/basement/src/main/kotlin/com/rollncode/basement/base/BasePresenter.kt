package com.rollncode.basement.base

import com.rollncode.basement.interfaces.*
import io.reactivex.*
import io.reactivex.android.schedulers.*

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.09.04
 */
abstract class BasePresenter(private val mvp: AsyncMVPInterface) {

    private fun <T> createRequestTransformer() = SingleTransformer<T, T> { upstream ->
        upstream
            .compose(createRetryPolicy<T>(mvp.getRetryFlowAction()))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mvp.setProgress() }
            .doFinally { mvp.setProgress(false) }
    }

    protected fun <T> Single<T>.attachTransformer()
            = this.compose(createRequestTransformer<T>())!!

    protected fun <T> execute(request: Single<T>, success: (T) -> Unit, error: (Throwable) -> Unit = { mvp.onRequestError(it) }) {
        mvp.add(request.attachTransformer()
            .subscribe({ success(it) }, { error(it) }))
    }

    protected abstract fun <T> createRetryPolicy(action: Flowable<Boolean>): SingleTransformer<T, T>
}