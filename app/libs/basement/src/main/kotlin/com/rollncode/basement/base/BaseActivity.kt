package com.rollncode.basement.base

import android.support.annotation.CallSuper
import android.support.v7.app.AppCompatActivity
import com.rollncode.basement.interfaces.AsyncMVPInterface
import com.rollncode.basement.interfaces.FragmentManagerInterface
import com.rollncode.basement.interfaces.OnBackPressedListener
import io.reactivex.disposables.CompositeDisposable

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.09.01
 */
abstract class BaseActivity : AppCompatActivity(), AsyncMVPInterface, FragmentManagerInterface {

    override val compositeDisposable = CompositeDisposable()

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        dispose()
    }

    override fun onBackPressed() {
        val fragment = getFragmentFromContainer()
        if (fragment !is OnBackPressedListener || fragment.onBackPressed()) {
            super.onBackPressed()
        }
    }
}