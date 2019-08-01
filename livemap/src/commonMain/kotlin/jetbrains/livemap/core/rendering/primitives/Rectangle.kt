package jetbrains.livemap.core.rendering.primitives

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.canvas.Context2d

class Rectangle : RenderBox {
    var rect: DoubleRectangle = DoubleRectangle(0.0, 0.0, 0.0, 0.0)
    var color: Color? = null

    override fun render(ctx: Context2d) {
        color?.let { ctx.setFillColor(it.toCssColor()) }

        ctx.fillRect(
            rect.left,
            rect.top,
            rect.width,
            rect.height
        )
    }

    override fun origin(): DoubleVector = rect.origin

    override fun dimension(): DoubleVector = rect.dimension
}