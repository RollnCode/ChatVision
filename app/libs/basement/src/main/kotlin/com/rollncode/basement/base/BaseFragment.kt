package com.rollncode.basement.base

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import com.rollncode.basement.interfaces.AsyncMVPInterface
import com.rollncode.basement.interfaces.FragmentManagerInterface
import io.reactivex.disposables.CompositeDisposable

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

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, b: Bundle?): View?
            = inflater.inflate(getLayoutRes(), parent, false)

    @Suppress("RedundantOverride")
    override fun onViewCreated(v: View, b: Bundle?) {
        super.onViewCreated(v, b)
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        dispose()
    }

    @Suppress("RedundantOverride")
    override fun onSaveInstanceState(b: Bundle) {
        super.onSaveInstanceState(b)
    }

    protected open fun getMenuLayout(): Int = 0

    protected abstract fun getLayoutRes(): Int

    fun finish(affinity: Boolean = false)
            = if (affinity) activity?.finishAffinity() else activity?.supportFinishAfterTransition()
}