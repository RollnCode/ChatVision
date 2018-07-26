package base

import android.support.v4.app.FragmentManager
import com.rollncode.basement.base.BaseFragment
import io.reactivex.Flowable

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.10.24
 */
abstract class BFragment : BaseFragment() {
    override fun getSupportFragmentManager(): FragmentManager?
            = fragmentManager

    override fun setProgress(start: Boolean) {
        (activity as? BActivity)?.setProgress(start)
    }

    override fun onRequestError(error: Throwable) {
        (activity as? BActivity)?.onRequestError(error)
    }

    override fun getRetryFlowAction(): Flowable<Boolean>
            = (activity as? BActivity)?.getRetryFlowAction() ?: Flowable.just(false)
}