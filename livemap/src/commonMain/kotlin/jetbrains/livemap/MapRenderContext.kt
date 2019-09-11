package jetbrains.livemap

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.canvas.CanvasProvider
import jetbrains.datalore.visualization.base.canvas.Context2d
import jetbrains.livemap.core.rendering.primitives.RenderObject
import jetbrains.livemap.projections.ClientPoint
import jetbrains.livemap.projections.ViewProjection

open class MapRenderContext internal constructor(
    open val viewProjection: ViewProjection,
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
        context.translate(x, y)
        renderObject.render(context)
        context.restore()
    }
}