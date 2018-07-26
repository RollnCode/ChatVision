package base

import com.quickblox.core.exception.QBResponseException
import com.rollncode.basement.base.BasePresenter
import com.rollncode.basement.interfaces.AsyncMVPInterface
import io.reactivex.Flowable
import io.reactivex.SingleTransformer
import io.reactivex.functions.BiFunction
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit.SECONDS

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.10.24
 */
abstract class BPresenter(mvp: AsyncMVPInterface) : BasePresenter(mvp) {

    final override fun <T> createRetryPolicy(action: Flowable<Boolean>) = SingleTransformer<T, T> { single ->
        single.retryWhen { flow ->
            flow.zipWith(Flowable.range(1, 3), BiFunction<Throwable, Int, Int> { _, i -> i })
                .flatMap { Flowable.timer(5L * it, SECONDS) }
        }
        single.retryWhen { flow ->
            flow.flatMap {
                    when (it) {
                        is QBResponseException    -> Flowable.error(it)
                        is IOException            -> action.flatMap { if (it) Flowable.just(it) else Flowable.empty<T>() }
                        is SocketTimeoutException -> Flowable.empty<T>()
                        else                      -> Flowable.error(it)
                    }
                }
        }
    }
}