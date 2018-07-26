package com.rollncode.basement.utility.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.PluralsRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.WindowManager

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.09.01
 */
fun Context.getColorCompat(@ColorRes colorId: Int) = ContextCompat.getColor(this, colorId)

fun Context.getColorStateListCompat(@ColorRes colorId: Int) = ContextCompat.getColorStateList(this, colorId)

fun Context.getDrawableCompat(@DrawableRes drawId: Int): Drawable? = ContextCompat.getDrawable(this, drawId)

fun Context?.startEmailApp() {
    this ?: return
    startIntent(Intent(Intent.ACTION_MAIN).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addCategory(Intent.CATEGORY_APP_EMAIL))
}

fun Context?.startIntent(intent: Intent): Boolean {
    if (this !is Activity || intent.resolveActivity(packageManager) == null) {
        return false
    }
    startActivity(intent)
    return true
}

fun Context.getPlurals(@PluralsRes id: Int, quantity: Int, vararg params: Any): String {
    return if (params.isEmpty()) {
        resources.getQuantityString(id, quantity, quantity)

    } else {
        resources.getQuantityString(id, quantity, *params)
    }
}

fun Fragment.getPlurals(@PluralsRes id: Int, quantity: Int, vararg params: Any)
        = context?.getPlurals(id, quantity, *params) ?: ""

fun Context.getScreenSize(): Point {
    val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val size = Point()
    display.getSize(size)
    return size
}