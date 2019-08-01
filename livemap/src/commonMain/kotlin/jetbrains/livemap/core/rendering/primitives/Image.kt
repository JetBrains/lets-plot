package jetbrains.livemap.core.rendering.primitives

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.canvas.Canvas.Snapshot
import jetbrains.datalore.visualization.base.canvas.Context2d

class Image(
    private val snapshot: Snapshot,
    override val origin: DoubleVector,
    override val dimension: DoubleVector
) : RenderBox {

    override fun render(ctx: Context2d) {
        ctx.drawImage(snapshot, 0.0, 0.0, dimension.x, dimension.y)
    }
}