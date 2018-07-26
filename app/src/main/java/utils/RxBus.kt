package utils

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2018.05.16
 */
object RxBus {

    private val bus = PublishSubject.create<Any>()

    fun send(event: Any) {
        bus.onNext(event)
    }

    fun <T> observeEvent(type: Class<T>): Observable<T> =
            bus.ofType(type)
}

class NeedToUpdateEvent