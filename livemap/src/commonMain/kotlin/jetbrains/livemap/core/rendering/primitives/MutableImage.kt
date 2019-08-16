package jetbrains.livemap.core.rendering.primitives

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.canvas.Canvas.Snapshot
import jetbrains.datalore.visualization.base.canvas.Context2d

class MutableImage(override val origin: DoubleVector, override val dimension: DoubleVector) : RenderBox {
    var snapshot: Snapshot? = null

    override fun render(ctx: Context2d) {
        snapshot?.let { ctx.drawImage(it, 0.0, 0.0, dimension.x, dimension.y) }
    }
}