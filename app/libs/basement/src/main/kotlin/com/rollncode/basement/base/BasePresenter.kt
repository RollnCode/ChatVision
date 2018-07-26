package com.rollncode.basement.base

import com.rollncode.basement.interfaces.AsyncMVPInterface
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers

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

    private fun <T> Single<T>.attachTransformer(): Single<T>
            = this.compose(createRequestTransformer<T>())

    protected fun <T> execute(request: Single<T>, success: (T) -> Unit, error: (Throwable) -> Unit = { mvp.onRequestError(it) }) {
        mvp.addDisposable(request.attachTransformer()
            .subscribe({ success(it) }, { error(it) }))
    }

    protected fun execute(request: Completable, success: () -> Unit, error: (Throwable) -> Unit = { mvp.onRequestError(it) }) {
        mvp.addDisposable(request.toSingleDefault(Unit).attachTransformer()
            .subscribe({ success() }, { error(it) }))
    }

    protected abstract fun <T> createRetryPolicy(action: Flowable<Boolean>): SingleTransformer<T, T>
}