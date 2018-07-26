package com.rollncode.basement.utility

import android.view.MotionEvent

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 * @since 2017.09.22
 */
class ALog(private val tag: String = "aLog", private val showLogs: Boolean = false) {

    private val MAX_MESSAGE_LENGTH = 2 * 1024

    fun toLog(vararg values: Any?) {
        if (showLogs) {
            for (value in values) {
                when (value) {
                    is String      -> toLog(value)
                    is Throwable   -> toLog(value)
                    is MotionEvent -> toLog(value)

                    else           -> toLog("Unknown type of $value")
                }
            }
        }
    }

    private fun toLog(string: String) {
        val length = string.length
        if (length > MAX_MESSAGE_LENGTH) {
            toLog("\n>>>\t\tstring too long >>>")

            var start = 0
            var end = MAX_MESSAGE_LENGTH

            while (start < length) {
                toLog(string.substring(start, end))

                start = end
                end += MAX_MESSAGE_LENGTH

                if (end > length) {
                    end = length
                }
            }
            toLog("<<<\t\tstring too long <<<\n")

        } else {
            android.util.Log.d(tag, string)
        }
    }

    private fun toLog(e: Throwable) {
        e.printStackTrace()

        if (e.message?.isNotEmpty() == true) {
            toLog(e.message!!)
        }
        toLog(e.javaClass.name)

        for (element in e.stackTrace) {
            toLog(element.toString())
        }
    }

    private fun toLog(e: MotionEvent) {
        val action = when (e.action) {
            MotionEvent.ACTION_DOWN               -> "ACTION_DOWN"
            MotionEvent.ACTION_MOVE               -> "ACTION_MOVE"
            MotionEvent.ACTION_SCROLL             -> "ACTION_SCROLL"
            MotionEvent.ACTION_MASK               -> "ACTION_MASK"
            MotionEvent.ACTION_UP                 -> "ACTION_UP"
            MotionEvent.ACTION_CANCEL             -> "ACTION_CANCEL"
            MotionEvent.ACTION_POINTER_UP         -> "ACTION_POINTER_UP"
            MotionEvent.ACTION_BUTTON_PRESS       -> "ACTION_BUTTON_PRESS"
            MotionEvent.ACTION_BUTTON_RELEASE     -> "ACTION_BUTTON_RELEASE"
            MotionEvent.ACTION_HOVER_ENTER        -> "ACTION_HOVER_ENTER"
            MotionEvent.ACTION_HOVER_EXIT         -> "ACTION_HOVER_EXIT"
            MotionEvent.ACTION_HOVER_MOVE         -> "ACTION_HOVER_MOVE"
            MotionEvent.ACTION_OUTSIDE            -> "ACTION_OUTSIDE"
            MotionEvent.ACTION_POINTER_DOWN       -> "ACTION_POINTER_DOWN"
            MotionEvent.ACTION_POINTER_INDEX_MASK -> "ACTION_POINTER_INDEX_MASK"

            else                                  -> e.action.toString()
        }
        toLog("MotionEvent.$action")
    }
}