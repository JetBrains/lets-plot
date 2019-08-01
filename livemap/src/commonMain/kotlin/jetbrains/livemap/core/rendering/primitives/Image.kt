package jetbrains.livemap.core.rendering.primitives

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.canvas.Canvas.Snapshot
import jetbrains.datalore.visualization.base.canvas.Context2d

class Image(
    private val snapshot: Snapshot,
    private val origin: DoubleVector,
    private val dimension: DoubleVector
) : RenderBox {

    override fun origin(): DoubleVector {
        return origin
    }

    override fun dimension(): DoubleVector {
        return dimension
    }

    override fun render(ctx: Context2d) {
        ctx.drawImage(snapshot, 0.0, 0.0, dimension.x, dimension.y)
    }
}