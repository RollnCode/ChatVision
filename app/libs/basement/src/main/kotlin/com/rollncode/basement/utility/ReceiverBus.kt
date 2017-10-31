package com.rollncode.basement.utility

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.annotation.AnyThread
import android.support.annotation.IdRes
import android.support.annotation.MainThread
import android.support.v4.util.ArraySet
import android.support.v4.util.SparseArrayCompat
import com.rollncode.basement.BuildConfig
import com.rollncode.basement.interfaces.ObjectsReceiver
import com.rollncode.basement.interfaces.TestImplemented

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 * @since 2017.08.24
 */
@TestImplemented("test.ReceiverBusTest")
object ReceiverBus {

    private val receivers = SparseArrayCompat<WReceiver>()
    private val eventReceivers = SparseArrayCompat<MutableSet<Int>>()
    private val mainHandler by lazy { MainHandler() }

    fun subscribe(receiver: ObjectsReceiver, vararg events: Int) {
        val id = receiver.hashCode()
        if (events.isEmpty()) {
            throw IllegalStateException("empty events is not possible")
        }
        receivers.put(id, WReceiver(receiver))

        var set: MutableSet<Int>?
        for (event in events) {
            set = eventReceivers.get(event)
            if (set == null) {
                set = ArraySet<Int>()
                eventReceivers.put(event, set)
            }
            set.add(id)
        }
    }

    fun unSubscribe(receiver: ObjectsReceiver, vararg events: Int) {
        val id = receiver.hashCode()
        if (events.isEmpty()) {
            if (BuildConfig.DEBUG) {
                throw IllegalStateException("UnSubscribe from exact events!")
            }
            receivers.remove(id)

            var i = 0
            while (i < eventReceivers.size()) {
                eventReceivers.valueAt(i++)?.remove(id)
            }

        } else {
            for (event in events) {
                eventReceivers.get(event)?.remove(id)
            }
        }
    }

    @SuppressLint("WrongThread")
    @AnyThread
    fun notify(@IdRes event: Int, vararg objects: Any) {
        if (isWorkerThread()) {
            val msg = mainHandler.obtainMessage()
            msg.obj = arrayOf(event, objects)

            mainHandler.sendMessage(msg)
            return
        }
        val set = eventReceivers.get(event)
        if (set == null || set.size == 0) {
            return
        }
        val iterator = set.iterator()
        while (iterator.hasNext()) {
            if (!notify(receivers.get(iterator.next()), event, *objects)) {
                iterator.remove()
            }
        }
    }

    @MainThread
    private fun notify(wReceiver: WReceiver?, @IdRes event: Int, vararg objects: Any): Boolean {
        val receiver = wReceiver?.get() ?: return false
        receiver.onObjectsReceive(event, *objects)

        return true
    }

    /**
     * Done especially for *ReceiverBusTest*
     */
    private fun isWorkerThread(): Boolean {
        try {
            return Looper.myLooper() != Looper.getMainLooper()

        } catch (e: RuntimeException) {
            return false
        }
    }
}

private class MainHandler : Handler(Looper.getMainLooper()) {

    override fun handleMessage(msg: Message?) {
        if (msg == null) {
            return
        }
        val values = msg.obj as Array<*>

        val event: Int = values[0] as Int
        @Suppress("UNCHECKED_CAST")
        val objects: Array<Any> = values[1] as Array<Any>

        ReceiverBus.notify(event, *objects)
    }
}