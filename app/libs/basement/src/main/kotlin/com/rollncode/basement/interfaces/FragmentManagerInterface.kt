package com.rollncode.basement.interfaces

import android.support.v4.app.*

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 * @since 2017.09.22
 */
interface FragmentManagerInterface {

    fun getContainerId(): Int = 0

    fun getSupportFragmentManager(): FragmentManager

    fun start(fragment: Fragment, addToBackStack: Boolean = true, block: (FragmentTransaction.() -> Unit)? = null) {
        val manager = getSupportFragmentManager()
        if (!addToBackStack) {
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        val transaction = manager.beginTransaction()

        val tag = fragment.javaClass.name
        if (addToBackStack) {
            transaction.addToBackStack(tag)
        }
        if (block != null) {
            transaction.block()
        }
        transaction
            .replace(getContainerId(), fragment, tag)
            .commit()
    }

    fun getFragmentFromContainer(): Fragment?
            = getSupportFragmentManager().findFragmentById(getContainerId())
}