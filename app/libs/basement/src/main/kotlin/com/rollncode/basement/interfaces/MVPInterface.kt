package com.rollncode.basement.interfaces

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.09.01
 */
interface MVPInterface {

    val compositeDisposable: CompositeDisposable

    fun addDisposable(disposable: Disposable)
            = compositeDisposable.add(disposable)

    fun dispose()
            = compositeDisposable.clear()
}