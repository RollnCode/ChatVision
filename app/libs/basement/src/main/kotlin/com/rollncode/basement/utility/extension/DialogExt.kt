package com.rollncode.basement.utility.extension

import android.app.Activity
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.View

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.09.01
 */
fun Activity.showAlert(init: AlertDialog.Builder.() -> Unit): AlertDialog {
    val builder = AlertDialog.Builder(this)
    builder.init()

    return builder.show()
}

fun Fragment.showAlert(init: AlertDialog.Builder.() -> Unit)
        = activity?.showAlert(init)

fun View?.showSnackBar(text: CharSequence, duration: Int = Snackbar.LENGTH_LONG, block: (Snackbar.() -> Unit)? = null) {
    this?.parent ?: return

    val snackBar = Snackbar.make(this, text, duration)
    block?.let { snackBar.it() }
    snackBar.show()
}