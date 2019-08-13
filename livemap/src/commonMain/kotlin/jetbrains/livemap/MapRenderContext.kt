package jetbrains.livemap

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.canvas.CanvasProvider
import jetbrains.datalore.visualization.base.canvas.Context2d
import jetbrains.livemap.core.rendering.primitives.RenderObject
import jetbrains.livemap.projections.ViewProjection

class MapRenderContext internal constructor(
    val viewProjection: ViewProjection,
    val canvasProvider: CanvasProvider
) {

    fun draw(origin: DoubleVector, renderObject: RenderObject, context: Context2d) {
        draw(context, origin, renderObject)
    }

    fun draw(context: Context2d, origin: DoubleVector, renderObject: RenderObject) {
        context.save()
        context.translate(origin.x, origin.y)
        renderObject.render(context)
        context.restore()
    }
}