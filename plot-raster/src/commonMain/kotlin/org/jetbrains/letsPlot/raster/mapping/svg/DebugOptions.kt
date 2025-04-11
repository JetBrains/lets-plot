/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.raster.shape.*
import kotlin.math.roundToInt


internal object DebugOptions {
    const val DEBUG_DRAWING_ENABLED: Boolean = false

    fun drawBoundingBoxes(rootElement: Pane, canvas: Canvas) {
        //val strokePaint = Paint().setStroke(true)
        //val fillPaint = Paint().setStroke(false)

        depthFirstTraversal(rootElement).forEach { el ->
            val color = when (el) {
                is Pane -> Color.CYAN
                is Group -> Color.YELLOW
                //is Text -> Color.GREEN
                //is TSpan -> Color.GREEN
                is Rectangle -> Color.BLUE
                //is Circle -> Color.RED
                is Line -> Color.RED
                else -> Color.LIGHT_GRAY
            }

            val fillColor = color.changeAlpha((255*0.02).roundToInt())
            canvas.context2d.setFillStyle(fillColor)
            canvas.context2d.fillRect(el.screenBounds.left, el.screenBounds.top, el.screenBounds.width, el.screenBounds.height)

            val strokeColor = color.changeAlpha((255*0.7).roundToInt())
            val strokeWidth = if(el is Container) 3f else 1f
            //strokeColor.pathEffect = if (el is Container) PathEffect.makeDash(floatArrayOf(3f, 8f), 0f) else null
            canvas.context2d.setStrokeStyle(strokeColor)
            canvas.context2d.strokeRect(el.screenBounds.left, el.screenBounds.top, el.screenBounds.width, el.screenBounds.height)
        }
    }
}
