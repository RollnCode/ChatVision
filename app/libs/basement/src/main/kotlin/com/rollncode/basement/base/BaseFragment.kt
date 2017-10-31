package com.rollncode.basement.base

import android.os.*
import android.support.annotation.*
import android.support.v4.app.*
import android.view.*
import com.rollncode.basement.interfaces.*
import io.reactivex.disposables.*

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.09.01
 */
abstract class BaseFragment : Fragment(), AsyncMVPInterface, FragmentManagerInterface {

    override val compositeDisposable = CompositeDisposable()

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        super.setHasOptionsMenu(getMenuLayout() != 0)
    }

    @CallSuper
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(getMenuLayout(), menu)
    }

    override fun onCreateView(inflater: LayoutInflater?, parent: ViewGroup?, b: Bundle?): View?
            = inflater!!.inflate(getLayoutRes(), parent, false)

    @Suppress("RedundantOverride")
    override fun onViewCreated(v: View?, b: Bundle?) {
        super.onViewCreated(v, b)
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        dispose()
    }

    @Suppress("RedundantOverride")
    override fun onSaveInstanceState(b: Bundle?) {
        super.onSaveInstanceState(b)
    }

    open protected fun getMenuLayout(): Int = 0

    abstract protected fun getLayoutRes(): Int

    fun finish(affinity: Boolean = false)
            = if (affinity) activity?.finishAffinity() else activity?.supportFinishAfterTransition()
}