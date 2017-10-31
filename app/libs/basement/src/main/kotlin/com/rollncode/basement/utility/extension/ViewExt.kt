package com.rollncode.basement.utility.extension

import android.text.InputFilter
import android.view.View
import android.widget.TextView
import com.rollncode.basement.utility.Utils
import com.rollncode.basement.utility.setVisibility
import java.util.*
import java.util.concurrent.TimeUnit

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.09.01
 */
fun View.show(animated: Boolean = false) {
    if (animated) {
        alpha = 0F
        visibility = View.VISIBLE
        animate().alpha(1F).setDuration(TimeUnit.MILLISECONDS.toMillis(500)).start()

    } else {
        Utils.setVisibility(this, View.VISIBLE)
    }
}

fun View.hide(gone: Boolean = true)
        = Utils.setVisibility(this, if (gone) View.GONE else View.INVISIBLE)

inline val View.isVisible
    get() = visibility == View.VISIBLE

fun TextView.getString()
        = this.text.toString().trim()

fun TextView.addFilter(vararg filters: InputFilter) {
    val newFilters = mutableListOf<InputFilter>()
    if (this.filters != null && this.filters.isNotEmpty()) {
        Collections.addAll(newFilters, *this.filters)
    }
    Collections.addAll(newFilters, *filters)

    this.filters = newFilters.toTypedArray()
}