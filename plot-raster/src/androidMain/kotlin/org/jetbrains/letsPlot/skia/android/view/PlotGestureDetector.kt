/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.android.view

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.Vector

class PlotGestureDetector(
    private val context: Context,
    private val svgPanel: SvgPanel
) {
    private val gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            val coord = translateMouseEvent(e)
            svgPanel.eventDispatcher?.dispatchMouseEvent(MouseEventSpec.MOUSE_PRESSED, MouseEvent.leftButton(coord))
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            val coord = translateMouseEvent(e)
            svgPanel.eventDispatcher?.dispatchMouseEvent(MouseEventSpec.MOUSE_MOVED, MouseEvent.noButton(coord)) // to show tooltip
            svgPanel.eventDispatcher?.dispatchMouseEvent(MouseEventSpec.MOUSE_CLICKED, MouseEvent.leftButton(coord))
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            val coord = translateMouseEvent(e)
            svgPanel.eventDispatcher?.dispatchMouseEvent(MouseEventSpec.MOUSE_DOUBLE_CLICKED, MouseEvent.leftButton(coord))
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            val coord = translateMouseEvent(e2)
            svgPanel.eventDispatcher?.dispatchMouseEvent(MouseEventSpec.MOUSE_MOVED, MouseEvent.leftButton(coord))
            return true
        }
    })

    fun translateMouseEvent(e: MotionEvent): Vector {
        val density = context.resources.displayMetrics.density
        val v = Vector((e.x / density).toInt(), (e.y / density).toInt())
        return v
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return true
    }

}
