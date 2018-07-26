package com.rollncode.basement.utility.draw

import android.graphics.*
import android.support.annotation.*
import android.view.*

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 * @since 2017.09.28
 */
abstract class BaseDrawer(@ColorInt color: Int, @Dimension val strokeWidth: Float) {

    val paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.strokeWidth = strokeWidth
            it.color = color
        }
    }

    fun onDraw(view: View, canvas: Canvas?) {
        val width = view.width
        val height = view.height

        if (width > 0 && height > 0 && canvas != null) {
            onDraw(view, canvas, width, height)
        }
    }

    abstract protected fun onDraw(view: View, canvas: Canvas, width: Int, height: Int)
}