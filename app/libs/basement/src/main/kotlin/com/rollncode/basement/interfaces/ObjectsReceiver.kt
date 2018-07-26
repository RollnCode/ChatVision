package com.rollncode.basement.interfaces

import android.support.annotation.*

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 * @since 2017.08.24
 */
@TestImplemented("test.ObjectReceiverTest")
interface ObjectsReceiver {
    fun onObjectsReceive(@IdRes event: Int, vararg objects: Any)
}