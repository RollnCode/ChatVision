package com.rollncode.basement.utility

import com.rollncode.basement.interfaces.*
import java.lang.ref.*

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 * @since 2017.08.24
 */
@TestImplemented("test.ObjectReceiverTest")
class WReceiver(referent: ObjectsReceiver?) : WeakReference<ObjectsReceiver>(referent), ObjectsReceiver {

    private val hashCode = referent?.hashCode() ?: 0
    private val string = referent?.toString() ?: ""

    override fun onObjectsReceive(event: Int, vararg objects: Any) {
        get()?.onObjectsReceive(event, *objects)
    }

    override fun hashCode() = hashCode
    override fun toString() = string
    override fun equals(other: Any?) = when {
        other == null && hashCode == 0 && string == ""                              -> true
        other != null && hashCode == other.hashCode() && string == other.toString() -> true

        else                                                                        -> false
    }
}