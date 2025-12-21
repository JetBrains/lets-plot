/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.raster.shape.*
import kotlin.math.roundToInt


internal object DebugOptions {
    const val DEBUG_DRAWING_ENABLED: Boolean = false

    fun drawBoundingBoxes(rootElement: Pane, ctx: Context2d) {
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
            val strokeColor = color.changeAlpha((255*0.7).roundToInt())
            ctx.setFillStyle(fillColor)
            ctx.setStrokeStyle(strokeColor)

            val screenBounds = el.bBoxGlobal
            ctx.fillRect(screenBounds.left, screenBounds.top, screenBounds.width, screenBounds.height)
            ctx.strokeRect(screenBounds.left, screenBounds.top, screenBounds.width, screenBounds.height)
        }
    }
}
