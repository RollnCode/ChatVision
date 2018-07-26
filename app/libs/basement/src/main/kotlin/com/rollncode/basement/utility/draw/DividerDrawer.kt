package com.rollncode.basement.utility.draw

import android.content.*
import android.graphics.*
import android.support.annotation.*
import android.view.*
import com.rollncode.basement.utility.extension.*

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 * @since 2017.09.28
 */
class DividerDrawer(context: Context, @ColorRes color: Int, @DimenRes stroke: Int, @DimenRes leftOffset: Int = 0, @DimenRes rightOffset: Int = 0)
    : BaseDrawer(context.getColorCompat(color), context.resources.getDimension(stroke)) {

    private val offset = PointF(
            if (leftOffset == 0) 0F else context.resources.getDimension(leftOffset),
            if (rightOffset == 0) 0F else context.resources.getDimension(rightOffset)
                               )

    override fun onDraw(view: View, canvas: Canvas, width: Int, height: Int)
            = canvas.drawRect(offset.x, height - strokeWidth, width - offset.y, height.toFloat(), paint)
}