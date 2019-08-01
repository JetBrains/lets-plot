package jetbrains.livemap.core.rendering.primitives

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.canvas.Canvas.Snapshot
import jetbrains.datalore.visualization.base.canvas.Context2d

class MutableImage(private val origin: DoubleVector, private val dimension: DoubleVector) : RenderBox {
    private var snapshot: Snapshot? = null

    override fun origin(): DoubleVector {
        return origin
    }

    override fun dimension(): DoubleVector {
        return dimension
    }

    override fun render(ctx: Context2d) {
        snapshot?.let { ctx.drawImage(it, 0.0, 0.0, dimension.x, dimension.y) }
    }
}