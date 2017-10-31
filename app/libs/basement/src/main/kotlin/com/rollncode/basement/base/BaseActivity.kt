package com.rollncode.basement.base

import android.support.annotation.*
import android.support.v7.app.*
import com.rollncode.basement.interfaces.*
import io.reactivex.disposables.*

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