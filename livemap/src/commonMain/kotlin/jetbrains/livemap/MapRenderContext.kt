/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.canvas.CanvasProvider
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.camera.Viewport
import jetbrains.livemap.core.rendering.primitives.RenderObject
import jetbrains.livemap.projections.ClientPoint
import kotlin.math.floor

open class MapRenderContext internal constructor(
    open val viewport: Viewport,
    val canvasProvider: CanvasProvider
) {

    fun draw(context: Context2d, origin: ClientPoint, renderObject: RenderObject) {
        draw(context, origin.x, origin.y, renderObject)
    }

    fun draw(context: Context2d, origin: DoubleVector, renderObject: RenderObject) {
        draw(context, origin.x, origin.y, renderObject)
    }

    fun draw(context: Context2d, x: Double, y: Double, renderObject: RenderObject) {
        context.save()
        context.translate(floor(x), floor(y))
        renderObject.render(context)
        context.restore()
    }
}