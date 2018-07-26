package com.rollncode.basement.utility.extension

import android.content.res.TypedArray
import android.support.annotation.StyleableRes
import java.util.*

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 * @since 2017.10.12
 */

fun Calendar.reset(startOfDay: Boolean = true) {
    if (startOfDay) {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

    } else {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }
}

fun String.onlyLetters(): String
        = this.filter { it.isLetter() }

fun TypedArray.getStringOpt(@StyleableRes index: Int, opt: String = ""): String
        = getTextOpt(index, opt).toString()

fun TypedArray.getTextOpt(@StyleableRes index: Int, opt: CharSequence = ""): CharSequence {
    return try {
        this.getText(index)

    } catch (e: IllegalStateException) {
        opt
    }
}

fun TypedArray.read(block: TypedArray.() -> Unit) {
    try {
        block()

    } finally {
        recycle()
    }
}