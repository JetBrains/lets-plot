/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.canvas.CanvasProvider
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.livemap.ClientPoint
import org.jetbrains.letsPlot.livemap.core.graphics.RenderObject
import org.jetbrains.letsPlot.livemap.mapengine.viewport.Viewport

open class MapRenderContext internal constructor(
    open val viewport: Viewport,
    val canvasProvider: CanvasProvider
) {

    fun draw(context: Context2d, origin: org.jetbrains.letsPlot.livemap.ClientPoint, renderObject: RenderObject) {
        draw(context, origin.x, origin.y, renderObject)
    }

    fun draw(context: Context2d, origin: DoubleVector, renderObject: RenderObject) {
        draw(context, origin.x, origin.y, renderObject)
    }

    private fun draw(context: Context2d, x: Double, y: Double, renderObject: RenderObject) {
        context.save()
        context.translate(x, y)
        renderObject.render(context)
        context.restore()
    }
}